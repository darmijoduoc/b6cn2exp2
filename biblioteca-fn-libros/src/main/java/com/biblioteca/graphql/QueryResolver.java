package com.biblioteca.graphql;

import com.biblioteca.model.Libro;
import com.biblioteca.oracle.OracleConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QueryResolver {

    public List<Libro> getLibros() {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, TITULO, AUTOR, ISBN, DISPONIBLE FROM LIBROS ORDER BY ID");
             ResultSet rs = ps.executeQuery()) {

            List<Libro> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener libros: " + e.getMessage(), e);
        }
    }

    public Libro getLibroById(String id) {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, TITULO, AUTOR, ISBN, DISPONIBLE FROM LIBROS WHERE ID = ?")) {

            ps.setLong(1, Long.parseLong(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return mapRow(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener libro: " + e.getMessage(), e);
        }
    }

    private Libro mapRow(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setId(rs.getLong("ID"));
        l.setTitulo(rs.getString("TITULO"));
        l.setAutor(rs.getString("AUTOR"));
        l.setIsbn(rs.getString("ISBN"));
        l.setDisponible(rs.getInt("DISPONIBLE") == 1);
        return l;
    }
}
