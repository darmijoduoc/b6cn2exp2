package cl.duocuc.biblioteca.bff.controllers;

import cl.duocuc.biblioteca.bff.clients.ReservasClient;
import cl.duocuc.biblioteca.bff.models.ReservaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class ReservasController {

    private final ReservasClient client;

    public ReservasController(ReservasClient client) {
        this.client = client;
    }

    @GetMapping("/reservas")
    public ResponseEntity<?> getAll() {
        try {
            List<ReservaDTO> reservas = client.getAll();
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(client.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/reservas")
    public ResponseEntity<?> create(@RequestBody ReservaDTO reserva) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(client.create(reserva));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/reservas/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReservaDTO reserva) {
        try {
            return ResponseEntity.ok(client.update(id, reserva));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/reservas/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            client.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
