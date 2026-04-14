package com.biblioteca.oracle;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

public final class OracleConnectionFactory {

    private OracleConnectionFactory() {}

    public static Connection getConnection() throws Exception {
        String user     = requireEnv("DB_USER");
        String password = requireEnv("DB_PASSWORD");
        String tnsAlias = requireEnv("DB_TNS_ALIAS");

        Path walletPath = WalletLoader.ensureWalletExtracted();

        String tnsAdmin = walletPath.toAbsolutePath().toString().replace("\\", "/");
        String url = "jdbc:oracle:thin:@" + tnsAlias + "?TNS_ADMIN=" + tnsAdmin;

        System.out.println("[fn-reservas] URL JDBC: " + url);

        return DriverManager.getConnection(url, user, password);
    }

    private static String requireEnv(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Falta la variable de entorno: " + key);
        }
        return value;
    }
}
