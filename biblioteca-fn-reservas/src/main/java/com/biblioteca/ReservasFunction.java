package com.biblioteca;

import com.biblioteca.model.Reserva;
import com.biblioteca.oracle.OracleConnectionFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReservasFunction {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    // ----------------------------------------------------------------
    // GET /api/reservas
    // ----------------------------------------------------------------
    @FunctionName("GetAllReservas")
    public HttpResponseMessage getAll(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            route = "reservas",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext context
    ) {
        context.getLogger().info("GET /api/reservas");
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, USUARIO_ID, LIBRO_ID, FECHA_RESERVA, ESTADO " +
                 "FROM RESERVAS ORDER BY ID");
             ResultSet rs = ps.executeQuery()) {

            List<Reserva> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return ok(request, list);

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/reservas/{id}
    // ----------------------------------------------------------------
    @FunctionName("GetReservaById")
    public HttpResponseMessage getById(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.GET},
            route = "reservas/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("GET /api/reservas/" + id);
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT ID, USUARIO_ID, LIBRO_ID, FECHA_RESERVA, ESTADO " +
                 "FROM RESERVAS WHERE ID = ?")) {

            ps.setLong(1, Long.parseLong(id));
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return notFound(request, "Reserva no encontrada con id: " + id);
                }
                return ok(request, mapRow(rs));
            }

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // POST /api/reservas
    // ----------------------------------------------------------------
    @FunctionName("CreateReserva")
    public HttpResponseMessage create(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.POST},
            route = "reservas",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        ExecutionContext context
    ) {
        context.getLogger().info("POST /api/reservas");
        try {
            String body = request.getBody().orElse("{}");
            Reserva input = mapper.readValue(body, Reserva.class);

            if (input.getEstado() == null) {
                input.setEstado("PENDIENTE");
            }

            try (Connection conn = OracleConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO RESERVAS (USUARIO_ID, LIBRO_ID, FECHA_RESERVA, ESTADO) " +
                     "VALUES (?, ?, SYSDATE, ?)",
                     new String[]{"ID"})) {

                ps.setLong(1, input.getUsuarioId());
                ps.setLong(2, input.getLibroId());
                ps.setString(3, input.getEstado());
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
    // PUT /api/reservas/{id}  — actualizar estado
    // ----------------------------------------------------------------
    @FunctionName("UpdateReserva")
    public HttpResponseMessage update(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.PUT},
            route = "reservas/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("PUT /api/reservas/" + id);
        try {
            String body = request.getBody().orElse("{}");
            Reserva input = mapper.readValue(body, Reserva.class);
            input.setId(Long.parseLong(id));

            try (Connection conn = OracleConnectionFactory.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE RESERVAS SET ESTADO=? WHERE ID=?")) {

                ps.setString(1, input.getEstado());
                ps.setLong(2, input.getId());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    return notFound(request, "Reserva no encontrada con id: " + id);
                }
            }

            return ok(request, input);

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // DELETE /api/reservas/{id}
    // ----------------------------------------------------------------
    @FunctionName("DeleteReserva")
    public HttpResponseMessage delete(
        @HttpTrigger(name = "req",
            methods = {HttpMethod.DELETE},
            route = "reservas/{id}",
            authLevel = AuthorizationLevel.ANONYMOUS)
        HttpRequestMessage<Optional<String>> request,
        @BindingName("id") String id,
        ExecutionContext context
    ) {
        context.getLogger().info("DELETE /api/reservas/" + id);
        try (Connection conn = OracleConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM RESERVAS WHERE ID=?")) {

            ps.setLong(1, Long.parseLong(id));
            int rows = ps.executeUpdate();

            if (rows == 0) {
                return notFound(request, "Reserva no encontrada con id: " + id);
            }

            return request.createResponseBuilder(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            return error(request, context, e);
        }
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Reserva mapRow(ResultSet rs) throws SQLException {
        Reserva r = new Reserva();
        r.setId(rs.getLong("ID"));
        r.setUsuarioId(rs.getLong("USUARIO_ID"));
        r.setLibroId(rs.getLong("LIBRO_ID"));

        Date fr = rs.getDate("FECHA_RESERVA");
        if (fr != null) r.setFechaReserva(DATE_FMT.format(fr));

        r.setEstado(rs.getString("ESTADO"));
        return r;
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
