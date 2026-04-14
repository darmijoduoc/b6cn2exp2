package com.biblioteca.graphql;

import com.biblioteca.model.Prestamo;
import com.biblioteca.oracle.OracleConnectionFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class QueryResolver {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    public List<Prestamo> getPrestamos() {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, USUARIO_ID, LIBRO_ID, FECHA_PRESTAMO, FECHA_DEVOLUCION, ESTADO " +
                 "FROM PRESTAMOS ORDER BY ID");
             ResultSet rs = ps.executeQuery()) {

            List<Prestamo> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prestamos: " + e.getMessage(), e);
        }
    }

    public Prestamo getPrestamoById(String id) {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, USUARIO_ID, LIBRO_ID, FECHA_PRESTAMO, FECHA_DEVOLUCION, ESTADO " +
                 "FROM PRESTAMOS WHERE ID = ?")) {

            ps.setLong(1, Long.parseLong(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener prestamo: " + e.getMessage(), e);
        }
    }

    private Prestamo mapRow(ResultSet rs) throws SQLException {
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
