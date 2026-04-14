package com.biblioteca;

import com.biblioteca.model.Usuario;
import com.biblioteca.oracle.OracleConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuariosFunction {

    private static final ObjectMapper mapper = new ObjectMapper();

    // ----------------------------------------------------------------
    // GET /api/usuarios  — listar todos
    // ----------------------------------------------------------------
    @FunctionName("GetAllUsuarios")
    public HttpResponseMessage getAll(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            route = "usuarios",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext context
    ) {
        context.getLogger().info("GET /api/usuarios");
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, NOMBRE, APELLIDO, EMAIL, TELEFONO FROM USUARIOS ORDER BY ID");
             ResultSet rs = ps.executeQuery()) {

            List<Usuario> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return ok(request, list);

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/usuarios/{id}  — obtener por id
    // ----------------------------------------------------------------
    @FunctionName("GetUsuarioById")
    public HttpResponseMessage getById(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            route = "usuarios/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("GET /api/usuarios/" + id);
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, NOMBRE, APELLIDO, EMAIL, TELEFONO FROM USUARIOS WHERE ID = ?")) {

            ps.setLong(1, Long.parseLong(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return notFound(request, "Usuario no encontrado con id: " + id);
                }
                return ok(request, mapRow(rs));
            }

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // POST /api/usuarios  — crear
    // ----------------------------------------------------------------
    @FunctionName("CreateUsuario")
    public HttpResponseMessage create(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.POST},
            route = "usuarios",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext context
    ) {
        context.getLogger().info("POST /api/usuarios");
        try {
            String body = request.getBody().orElse("{}");
            Usuario input = mapper.readValue(body, Usuario.class);

            try (Connection conn = OracleConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO USUARIOS (NOMBRE, APELLIDO, EMAIL, TELEFONO) VALUES (?, ?, ?, ?)",
                     new String[]{"ID"})) {

                ps.setString(1, input.getNombre());
                ps.setString(2, input.getApellido());
                ps.setString(3, input.getEmail());
                ps.setString(4, input.getTelefono());
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
    // PUT /api/usuarios/{id}  — actualizar
    // ----------------------------------------------------------------
    @FunctionName("UpdateUsuario")
    public HttpResponseMessage update(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.PUT},
            route = "usuarios/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("PUT /api/usuarios/" + id);
        try {
            String body = request.getBody().orElse("{}");
            Usuario input = mapper.readValue(body, Usuario.class);
            input.setId(Long.parseLong(id));

            try (Connection conn = OracleConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE USUARIOS SET NOMBRE=?, APELLIDO=?, EMAIL=?, TELEFONO=? WHERE ID=?")) {

                ps.setString(1, input.getNombre());
                ps.setString(2, input.getApellido());
                ps.setString(3, input.getEmail());
                ps.setString(4, input.getTelefono());
                ps.setLong(5, input.getId());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    return notFound(request, "Usuario no encontrado con id: " + id);
                }
            }

            return ok(request, input);

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // DELETE /api/usuarios/{id}  — eliminar
    // ----------------------------------------------------------------
    @FunctionName("DeleteUsuario")
    public HttpResponseMessage delete(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.DELETE},
            route = "usuarios/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("DELETE /api/usuarios/" + id);
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM USUARIOS WHERE ID=?")) {

            ps.setLong(1, Long.parseLong(id));
            int rows = ps.executeUpdate();

            if (rows == 0) {
                return notFound(request, "Usuario no encontrado con id: " + id);
            }

            return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("ID"));
        u.setNombre(rs.getString("NOMBRE"));
        u.setApellido(rs.getString("APELLIDO"));
        u.setEmail(rs.getString("EMAIL"));
        u.setTelefono(rs.getString("TELEFONO"));
        return u;
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
