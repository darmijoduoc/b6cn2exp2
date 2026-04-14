package cl.duocuc.biblioteca.bff.controllers;

import cl.duocuc.biblioteca.bff.clients.UsuariosClient;
import cl.duocuc.biblioteca.bff.models.UsuarioDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
public class UsuariosController {

    private final UsuariosClient client;

    public UsuariosController(UsuariosClient client) {
        this.client = client;
    }

    @GetMapping("/usuarios")
    public ResponseEntity<?> getAll() {
        try {
            List<UsuarioDTO> usuarios = client.getAll();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(client.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> create(@RequestBody UsuarioDTO usuario) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(client.create(usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO usuario) {
        try {
            return ResponseEntity.ok(client.update(id, usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @DeleteMapping("/usuarios/{id}")
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
