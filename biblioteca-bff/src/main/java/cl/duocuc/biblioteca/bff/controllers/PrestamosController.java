package cl.duocuc.biblioteca.bff.controllers;

import cl.duocuc.biblioteca.bff.clients.PrestamosGraphQLClient;
import cl.duocuc.biblioteca.bff.models.PrestamoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class PrestamosController {

    private final PrestamosGraphQLClient client;

    public PrestamosController(PrestamosGraphQLClient client) {
        this.client = client;
    }

    @GetMapping("/prestamos")
    public ResponseEntity<?> getAll() {
        try {
            List<PrestamoDTO> prestamos = client.getPrestamos();
            return ResponseEntity.ok(prestamos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/prestamos/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            PrestamoDTO prestamo = client.getPrestamoById(id);
            if (prestamo == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(prestamo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/prestamos")
    public ResponseEntity<?> create(@RequestBody PrestamoDTO prestamo) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(client.createPrestamo(prestamo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/prestamos/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PrestamoDTO prestamo) {
        try {
            PrestamoDTO updated = client.updatePrestamo(id, prestamo);
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/prestamos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            client.deletePrestamo(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
