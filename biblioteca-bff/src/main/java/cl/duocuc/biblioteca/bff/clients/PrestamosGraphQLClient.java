package cl.duocuc.biblioteca.bff.clients;

import cl.duocuc.biblioteca.bff.models.PrestamoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrestamosGraphQLClient {

    private final RestTemplate restTemplate;
    private final String graphqlUrl;

    public PrestamosGraphQLClient(RestTemplateBuilder builder,
                                   @Value("${fn.prestamos.url}") String prestamosUrl) {
        this.restTemplate = builder.build();
        this.graphqlUrl = prestamosUrl + "/api/graphql";
    }

    public List<PrestamoDTO> getPrestamos() {
        String query = "{ getPrestamos { id usuarioId libroId fechaPrestamo fechaDevolucion estado } }";
        Map<?, ?> data = executeQuery(query);
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("getPrestamos");
        return items.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PrestamoDTO getPrestamoById(Long id) {
        String query = "{ getPrestamoById(id: \"" + id + "\") { id usuarioId libroId fechaPrestamo fechaDevolucion estado } }";
        Map<?, ?> data = executeQuery(query);
        Object item = data.get("getPrestamoById");
        if (item == null) return null;
        return toDTO((Map<String, Object>) item);
    }

    public PrestamoDTO createPrestamo(PrestamoDTO prestamo) {
        String mutation = "mutation { createPrestamo(usuarioId: \"" + prestamo.getUsuarioId() +
            "\", libroId: \"" + prestamo.getLibroId() +
            "\") { id usuarioId libroId fechaPrestamo fechaDevolucion estado } }";
        Map<?, ?> data = executeQuery(mutation);
        return toDTO((Map<String, Object>) data.get("createPrestamo"));
    }

    public PrestamoDTO updatePrestamo(Long id, PrestamoDTO prestamo) {
        StringBuilder sb = new StringBuilder("mutation { updatePrestamo(id: \"").append(id).append("\"");
        if (prestamo.getEstado() != null)
            sb.append(", estado: \"").append(esc(prestamo.getEstado())).append("\"");
        if (prestamo.getFechaDevolucion() != null)
            sb.append(", fechaDevolucion: \"").append(esc(prestamo.getFechaDevolucion())).append("\"");
        sb.append(") { id usuarioId libroId fechaPrestamo fechaDevolucion estado } }");
        Map<?, ?> data = executeQuery(sb.toString());
        return toDTO((Map<String, Object>) data.get("updatePrestamo"));
    }

    public void deletePrestamo(Long id) {
        String mutation = "mutation { deletePrestamo(id: \"" + id + "\") }";
        executeQuery(mutation);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> executeQuery(String query) {
        Map<String, Object> body = Map.of("query", query);
        ResponseEntity<Map> response = restTemplate.postForEntity(graphqlUrl, body, Map.class);
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) throw new RuntimeException("Respuesta vacia del servicio GraphQL de prestamos");
        if (responseBody.containsKey("errors")) {
            throw new RuntimeException("Error GraphQL prestamos: " + responseBody.get("errors"));
        }
        return (Map<String, Object>) responseBody.get("data");
    }

    private PrestamoDTO toDTO(Map<String, Object> map) {
        if (map == null) return null;
        PrestamoDTO dto = new PrestamoDTO();
        dto.setId(map.get("id") != null ? Long.parseLong(String.valueOf(map.get("id"))) : null);
        dto.setUsuarioId(map.get("usuarioId") != null ? Long.parseLong(String.valueOf(map.get("usuarioId"))) : null);
        dto.setLibroId(map.get("libroId") != null ? Long.parseLong(String.valueOf(map.get("libroId"))) : null);
        dto.setFechaPrestamo((String) map.get("fechaPrestamo"));
        dto.setFechaDevolucion((String) map.get("fechaDevolucion"));
        dto.setEstado((String) map.get("estado"));
        return dto;
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
