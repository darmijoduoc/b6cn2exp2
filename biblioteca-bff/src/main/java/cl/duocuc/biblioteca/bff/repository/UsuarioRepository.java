package cl.duocuc.biblioteca.bff.repository;

import cl.duocuc.biblioteca.bff.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
}
