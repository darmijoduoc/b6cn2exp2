package cl.duocuc.biblioteca.bff.clients;

import cl.duocuc.biblioteca.bff.config.FeignConfig;
import cl.duocuc.biblioteca.bff.models.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "usuarios-fn", url = "${fn.usuarios.url}", configuration = FeignConfig.class)
public interface UsuariosClient {

    @GetMapping("/api/usuarios")
    List<UsuarioDTO> getAll();

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO getById(@PathVariable("id") Long id);

    @PostMapping("/api/usuarios")
    UsuarioDTO create(@RequestBody UsuarioDTO usuario);

    @PutMapping("/api/usuarios/{id}")
    UsuarioDTO update(@PathVariable("id") Long id, @RequestBody UsuarioDTO usuario);

    @DeleteMapping("/api/usuarios/{id}")
    void delete(@PathVariable("id") Long id);
}
