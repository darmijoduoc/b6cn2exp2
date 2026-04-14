package cl.duocuc.biblioteca.bff.clients;

import cl.duocuc.biblioteca.bff.config.FeignConfig;
import cl.duocuc.biblioteca.bff.models.ReservaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "reservas-fn", url = "${fn.reservas.url}", configuration = FeignConfig.class)
public interface ReservasClient {

    @GetMapping("/api/reservas")
    List<ReservaDTO> getAll();

    @GetMapping("/api/reservas/{id}")
    ReservaDTO getById(@PathVariable("id") Long id);

    @PostMapping("/api/reservas")
    ReservaDTO create(@RequestBody ReservaDTO reserva);

    @PutMapping("/api/reservas/{id}")
    ReservaDTO update(@PathVariable("id") Long id, @RequestBody ReservaDTO reserva);

    @DeleteMapping("/api/reservas/{id}")
    void delete(@PathVariable("id") Long id);
}
