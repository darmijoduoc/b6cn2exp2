package cl.duocuc.biblioteca.bff.clients;

import cl.duocuc.biblioteca.bff.config.FeignConfig;
import cl.duocuc.biblioteca.bff.models.LibroDTO;
import cl.duocuc.biblioteca.bff.models.PrestamoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "prestamos-fn", url = "${fn.prestamos.url}", configuration = FeignConfig.class)
public interface PrestamosClient {

    // --- Libros ---

    @GetMapping("/api/libros")
    List<LibroDTO> getAllLibros();

    @GetMapping("/api/libros/{id}")
    LibroDTO getLibroById(@PathVariable("id") Long id);

    @PostMapping("/api/libros")
    LibroDTO createLibro(@RequestBody LibroDTO libro);

    @PutMapping("/api/libros/{id}")
    LibroDTO updateLibro(@PathVariable("id") Long id, @RequestBody LibroDTO libro);

    @DeleteMapping("/api/libros/{id}")
    void deleteLibro(@PathVariable("id") Long id);

    // --- Prestamos ---

    @GetMapping("/api/prestamos")
    List<PrestamoDTO> getAllPrestamos();

    @GetMapping("/api/prestamos/{id}")
    PrestamoDTO getPrestamoById(@PathVariable("id") Long id);

    @PostMapping("/api/prestamos")
    PrestamoDTO createPrestamo(@RequestBody PrestamoDTO prestamo);

    @PutMapping("/api/prestamos/{id}")
    PrestamoDTO updatePrestamo(@PathVariable("id") Long id, @RequestBody PrestamoDTO prestamo);

    @DeleteMapping("/api/prestamos/{id}")
    void deletePrestamo(@PathVariable("id") Long id);
}
