package com.biblioteca.graphql;

import com.biblioteca.model.Libro;
import com.biblioteca.oracle.OracleConnectionFactory;

import java.sql.*;

public class MutationResolver {

    public Libro createLibro(String titulo, String autor, String isbn) {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO LIBROS (TITULO, AUTOR, ISBN, DISPONIBLE) VALUES (?, ?, ?, 1)",
                 new String[]{"ID"})) {

            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setString(3, isbn);
            ps.executeUpdate();

            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setIsbn(isbn);
            libro.setDisponible(true);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) libro.setId(keys.getLong(1));
            }
            return libro;

        } catch (Exception e) {
            throw new RuntimeException("Error al crear libro: " + e.getMessage(), e);
        }
    }

    public Libro updateLibro(String id, String titulo, String autor, String isbn, Boolean disponible) {
        try (Connection conn = OracleConnectionFactory.getConnection()) {

            try (PreparedStatement ps = conn.prepareStatement(
                     "UPDATE LIBROS SET " +
                     "TITULO = COALESCE(?, TITULO), " +
                     "AUTOR = COALESCE(?, AUTOR), " +
                     "ISBN = COALESCE(?, ISBN), " +
                     "DISPONIBLE = COALESCE(?, DISPONIBLE) " +
                     "WHERE ID = ?")) {

                ps.setString(1, titulo);
                ps.setString(2, autor);
                ps.setString(3, isbn);
                if (disponible != null) {
                    ps.setInt(4, disponible ? 1 : 0);
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                ps.setLong(5, Long.parseLong(id));

                int rows = ps.executeUpdate();
                if (rows == 0) return null;
            }

            try (PreparedStatement sel = conn.prepareStatement(
                     "SELECT ID, TITULO, AUTOR, ISBN, DISPONIBLE FROM LIBROS WHERE ID = ?")) {
                sel.setLong(1, Long.parseLong(id));
                try (ResultSet rs = sel.executeQuery()) {
                    if (!rs.next()) return null;
                    Libro l = new Libro();
                    l.setId(rs.getLong("ID"));
                    l.setTitulo(rs.getString("TITULO"));
                    l.setAutor(rs.getString("AUTOR"));
                    l.setIsbn(rs.getString("ISBN"));
                    l.setDisponible(rs.getInt("DISPONIBLE") == 1);
                    return l;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar libro: " + e.getMessage(), e);
        }
    }

    public Boolean deleteLibro(String id) {
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM LIBROS WHERE ID = ?")) {

            ps.setLong(1, Long.parseLong(id));
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar libro: " + e.getMessage(), e);
        }
    }
}
