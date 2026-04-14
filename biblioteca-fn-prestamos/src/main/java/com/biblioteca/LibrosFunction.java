package com.biblioteca;

import com.biblioteca.model.Libro;
import com.biblioteca.oracle.OracleConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibrosFunction {

    private static final ObjectMapper mapper = new ObjectMapper();

    // ----------------------------------------------------------------
    // GET /api/libros
    // ----------------------------------------------------------------
    @FunctionName("GetAllLibros")
    public HttpResponseMessage getAll(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            route = "libros",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext context
    ) {
        context.getLogger().info("GET /api/libros");
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, TITULO, AUTOR, ISBN, DISPONIBLE FROM LIBROS ORDER BY ID");
             ResultSet rs = ps.executeQuery()) {

            List<Libro> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return ok(request, list);

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/libros/{id}
    // ----------------------------------------------------------------
    @FunctionName("GetLibroById")
    public HttpResponseMessage getById(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            route = "libros/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("GET /api/libros/" + id);
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, TITULO, AUTOR, ISBN, DISPONIBLE FROM LIBROS WHERE ID = ?")) {

            ps.setLong(1, Long.parseLong(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return notFound(request, "Libro no encontrado con id: " + id);
                }
                return ok(request, mapRow(rs));
            }

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // POST /api/libros
    // ----------------------------------------------------------------
    @FunctionName("CreateLibro")
    public HttpResponseMessage create(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.POST},
            route = "libros",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext context
    ) {
        context.getLogger().info("POST /api/libros");
        try {
            String body = request.getBody().orElse("{}");
            Libro input = mapper.readValue(body, Libro.class);

            try (Connection conn = OracleConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO LIBROS (TITULO, AUTOR, ISBN, DISPONIBLE) VALUES (?, ?, ?, ?)",
                     new String[]{"ID"})) {

                ps.setString(1, input.getTitulo());
                ps.setString(2, input.getAutor());
                ps.setString(3, input.getIsbn());
                ps.setInt(4, Boolean.TRUE.equals(input.getDisponible()) ? 1 : 0);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        input.setId(keys.getLong(1));
                    }
                }
            }

            return request.createResponseBuilder(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(mapper.writeValueAsString(input))
                .build();

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // PUT /api/libros/{id}
    // ----------------------------------------------------------------
    @FunctionName("UpdateLibro")
    public HttpResponseMessage update(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.PUT},
            route = "libros/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("PUT /api/libros/" + id);
        try {
            String body = request.getBody().orElse("{}");
            Libro input = mapper.readValue(body, Libro.class);
            input.setId(Long.parseLong(id));

            try (Connection conn = OracleConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE LIBROS SET TITULO=?, AUTOR=?, ISBN=?, DISPONIBLE=? WHERE ID=?")) {

                ps.setString(1, input.getTitulo());
                ps.setString(2, input.getAutor());
                ps.setString(3, input.getIsbn());
                ps.setInt(4, Boolean.TRUE.equals(input.getDisponible()) ? 1 : 0);
                ps.setLong(5, input.getId());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    return notFound(request, "Libro no encontrado con id: " + id);
                }
            }

            return ok(request, input);

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // DELETE /api/libros/{id}
    // ----------------------------------------------------------------
    @FunctionName("DeleteLibro")
    public HttpResponseMessage delete(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.DELETE},
            route = "libros/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("DELETE /api/libros/" + id);
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM LIBROS WHERE ID=?")) {

            ps.setLong(1, Long.parseLong(id));
            int rows = ps.executeUpdate();

            if (rows == 0) {
                return notFound(request, "Libro no encontrado con id: " + id);
            }

            return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Libro mapRow(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setId(rs.getLong("ID"));
        l.setTitulo(rs.getString("TITULO"));
        l.setAutor(rs.getString("AUTOR"));
        l.setIsbn(rs.getString("ISBN"));
        l.setDisponible(rs.getInt("DISPONIBLE") == 1);
        return l;
    }

    private HttpResponseMessage ok(HttpRequestMessage<Optional<String>> req, Object body) throws Exception {
        return req.createResponseBuilder(HttpStatus.OK)
            .header("Content-Type", "application/json")
            .body(mapper.writeValueAsString(body))
            .build();
    }

    private HttpResponseMessage notFound(HttpRequestMessage<Optional<String>> req, String msg) {
        return req.createResponseBuilder(HttpStatus.NOT_FOUND)
            .header("Content-Type", "application/json")
            .body("{\"error\":\"" + msg + "\"}")
            .build();
    }

    private HttpResponseMessage error(HttpRequestMessage<Optional<String>> req,
                                       ExecutionContext ctx, Exception e) {
        ctx.getLogger().severe("Error: " + e.getMessage());
        return req.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("Content-Type", "application/json")
            .body("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}")
            .build();
    }
}
