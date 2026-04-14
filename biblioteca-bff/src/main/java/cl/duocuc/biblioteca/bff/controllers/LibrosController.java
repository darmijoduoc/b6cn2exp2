package cl.duocuc.biblioteca.bff.controllers;

import cl.duocuc.biblioteca.bff.clients.LibrosGraphQLClient;
import cl.duocuc.biblioteca.bff.models.LibroDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class LibrosController {

    private final LibrosGraphQLClient client;

    public LibrosController(LibrosGraphQLClient client) {
        this.client = client;
    }

    @GetMapping("/libros")
    public ResponseEntity<?> getAll() {
        try {
            List<LibroDTO> libros = client.getLibros();
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/libros/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            LibroDTO libro = client.getLibroById(id);
            if (libro == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(libro);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/libros")
    public ResponseEntity<?> create(@RequestBody LibroDTO libro) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(client.createLibro(libro));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/libros/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody LibroDTO libro) {
        try {
            LibroDTO updated = client.updateLibro(id, libro);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/libros/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            client.deleteLibro(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
