package cl.duocuc.biblioteca.bff.clients;

import cl.duocuc.biblioteca.bff.models.LibroDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibrosGraphQLClient {

    private final RestTemplate restTemplate;
    private final String graphqlUrl;

    public LibrosGraphQLClient(RestTemplateBuilder builder,
                                @Value("${fn.libros.url}") String librosUrl) {
        this.restTemplate = builder.build();
        this.graphqlUrl = librosUrl + "/api/graphql";
    }

    public List<LibroDTO> getLibros() {
        String query = "{ getLibros { id titulo autor isbn disponible } }";
        Map<?, ?> data = executeQuery(query, "getLibros");
        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("getLibros");
        return items.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LibroDTO getLibroById(Long id) {
        String query = "{ getLibroById(id: \"" + id + "\") { id titulo autor isbn disponible } }";
        Map<?, ?> data = executeQuery(query, "getLibroById");
        Object item = data.get("getLibroById");
        if (item == null) return null;
        return toDTO((Map<String, Object>) item);
    }

    public LibroDTO createLibro(LibroDTO libro) {
        String mutation = "mutation { createLibro(titulo: \"" + esc(libro.getTitulo()) +
            "\", autor: \"" + esc(libro.getAutor()) +
            "\", isbn: \"" + esc(libro.getIsbn()) +
            "\") { id titulo autor isbn disponible } }";
        Map<?, ?> data = executeQuery(mutation, "createLibro");
        return toDTO((Map<String, Object>) data.get("createLibro"));
    }

    public LibroDTO updateLibro(Long id, LibroDTO libro) {
        StringBuilder sb = new StringBuilder("mutation { updateLibro(id: \"").append(id).append("\"");
        if (libro.getTitulo() != null)    sb.append(", titulo: \"").append(esc(libro.getTitulo())).append("\"");
        if (libro.getAutor() != null)     sb.append(", autor: \"").append(esc(libro.getAutor())).append("\"");
        if (libro.getIsbn() != null)      sb.append(", isbn: \"").append(esc(libro.getIsbn())).append("\"");
        if (libro.getDisponible() != null) sb.append(", disponible: ").append(libro.getDisponible());
        sb.append(") { id titulo autor isbn disponible } }");
        Map<?, ?> data = executeQuery(sb.toString(), "updateLibro");
        return toDTO((Map<String, Object>) data.get("updateLibro"));
    }

    public void deleteLibro(Long id) {
        String mutation = "mutation { deleteLibro(id: \"" + id + "\") }";
        executeQuery(mutation, "deleteLibro");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> executeQuery(String query, String operationName) {
        Map<String, Object> body = Map.of("query", query);
        ResponseEntity<Map> response = restTemplate.postForEntity(graphqlUrl, body, Map.class);
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) throw new RuntimeException("Respuesta vacia del servicio GraphQL de libros");
        if (responseBody.containsKey("errors")) {
            throw new RuntimeException("Error GraphQL libros: " + responseBody.get("errors"));
        }
        return (Map<String, Object>) responseBody.get("data");
    }

    private LibroDTO toDTO(Map<String, Object> map) {
        if (map == null) return null;
        LibroDTO dto = new LibroDTO();
        dto.setId(map.get("id") != null ? Long.parseLong(String.valueOf(map.get("id"))) : null);
        dto.setTitulo((String) map.get("titulo"));
        dto.setAutor((String) map.get("autor"));
        dto.setIsbn((String) map.get("isbn"));
        dto.setDisponible((Boolean) map.get("disponible"));
        return dto;
    }

    private String esc(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}
