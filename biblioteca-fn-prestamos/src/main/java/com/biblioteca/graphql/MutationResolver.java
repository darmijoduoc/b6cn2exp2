package com.biblioteca.graphql;

import com.biblioteca.model.Prestamo;
import com.biblioteca.oracle.OracleConnectionFactory;

import java.sql.*;
import java.text.SimpleDateFormat;

public class MutationResolver {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    public Prestamo createPrestamo(String usuarioId, String libroId) {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO PRESTAMOS (USUARIO_ID, LIBRO_ID, FECHA_PRESTAMO, ESTADO) " +
                 "VALUES (?, ?, SYSDATE, 'ACTIVO')",
                 new String[]{"ID"})) {

            ps.setLong(1, Long.parseLong(usuarioId));
            ps.setLong(2, Long.parseLong(libroId));
            ps.executeUpdate();

            Prestamo p = new Prestamo();
            p.setUsuarioId(Long.parseLong(usuarioId));
            p.setLibroId(Long.parseLong(libroId));
            p.setEstado("ACTIVO");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

            // Marcar libro como no disponible
            try (PreparedStatement upd = conn.prepareStatement(
                     "UPDATE LIBROS SET DISPONIBLE=0 WHERE ID=?")) {
                upd.setLong(1, Long.parseLong(libroId));
                upd.executeUpdate();
            }

            return p;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear prestamo: " + e.getMessage(), e);
        }
    }

    public Prestamo updatePrestamo(String id, String estado, String fechaDevolucion) {
        try (Connection conn = OracleConnectionFactory.getConnection()) {

            long libroId = 0;
            try (PreparedStatement sel = conn.prepareStatement(
                     "SELECT LIBRO_ID FROM PRESTAMOS WHERE ID=?")) {
                sel.setLong(1, Long.parseLong(id));
                try (ResultSet rs = sel.executeQuery()) {
                    if (rs.next()) libroId = rs.getLong("LIBRO_ID");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                     "UPDATE PRESTAMOS SET ESTADO=?, " +
                     "FECHA_DEVOLUCION=(CASE WHEN ? IS NOT NULL THEN TO_DATE(?, 'YYYY-MM-DD') ELSE FECHA_DEVOLUCION END) " +
                     "WHERE ID=?")) {

                ps.setString(1, estado);
                ps.setString(2, fechaDevolucion);
                ps.setString(3, fechaDevolucion);
                ps.setLong(4, Long.parseLong(id));

                int rows = ps.executeUpdate();
                if (rows == 0) return null;
            }

            if ("DEVUELTO".equalsIgnoreCase(estado) && libroId > 0) {
                try (PreparedStatement upd = conn.prepareStatement(
                         "UPDATE LIBROS SET DISPONIBLE=1 WHERE ID=?")) {
                    upd.setLong(1, libroId);
                    upd.executeUpdate();
                }
            }

            try (PreparedStatement sel = conn.prepareStatement(
                     "SELECT ID, USUARIO_ID, LIBRO_ID, FECHA_PRESTAMO, FECHA_DEVOLUCION, ESTADO " +
                     "FROM PRESTAMOS WHERE ID=?")) {
                sel.setLong(1, Long.parseLong(id));
                try (ResultSet rs = sel.executeQuery()) {
                    if (!rs.next()) return null;
                    Prestamo p = new Prestamo();
                    p.setId(rs.getLong("ID"));
                    p.setUsuarioId(rs.getLong("USUARIO_ID"));
                    p.setLibroId(rs.getLong("LIBRO_ID"));
                    Date fp = rs.getDate("FECHA_PRESTAMO");
                    if (fp != null) p.setFechaPrestamo(DATE_FMT.format(fp));
                    Date fd = rs.getDate("FECHA_DEVOLUCION");
                    if (fd != null) p.setFechaDevolucion(DATE_FMT.format(fd));
                    p.setEstado(rs.getString("ESTADO"));
                    return p;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar prestamo: " + e.getMessage(), e);
        }
    }

    public Boolean deletePrestamo(String id) {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM PRESTAMOS WHERE ID=?")) {

            ps.setLong(1, Long.parseLong(id));
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar prestamo: " + e.getMessage(), e);
        }
    }
}
