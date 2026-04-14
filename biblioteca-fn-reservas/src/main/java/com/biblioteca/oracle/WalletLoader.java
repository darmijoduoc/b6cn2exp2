package com.biblioteca.oracle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Comparator;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class WalletLoader {

    private static volatile Path walletDir;

    private WalletLoader() {}

    public static Path ensureWalletExtracted() throws IOException {
        if (walletDir != null && Files.exists(walletDir.resolve("tnsnames.ora"))) {
            return walletDir;
        }

        synchronized (WalletLoader.class) {
            if (walletDir != null && Files.exists(walletDir.resolve("tnsnames.ora"))) {
                return walletDir;
            }

            String walletBase64 = System.getenv("OCI_WALLET_BASE64");
            if (walletBase64 == null || walletBase64.isBlank()) {
                throw new IllegalStateException("Falta la variable OCI_WALLET_BASE64");
            }

            byte[] zipBytes = Base64.getDecoder().decode(walletBase64);

            Path tempRoot = Paths.get(System.getProperty("java.io.tmpdir"), "oci-wallet-" + UUID.randomUUID());
            Files.createDirectories(tempRoot);

            unzip(zipBytes, tempRoot);

            if (!Files.exists(tempRoot.resolve("tnsnames.ora"))) {
                throw new IllegalStateException("No se encontro tnsnames.ora dentro del wallet");
            }

            walletDir = tempRoot;
            return walletDir;
        }
    }

    private static void unzip(byte[] zipBytes, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path out = targetDir.resolve(entry.getName()).normalize();

                if (!out.startsWith(targetDir)) {
                    throw new SecurityException("ZIP invalido: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(out);
                } else {
                    Files.createDirectories(out.getParent());
                    try (OutputStream os = Files.newOutputStream(out)) {
                        zis.transferTo(os);
                    }
                }

                zis.closeEntry();
            }
        }
    }

    public static void cleanupQuietly() {
        if (walletDir == null) return;
        try {
            Files.walk(walletDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                });
        } catch (IOException ignored) {}
    }
}
