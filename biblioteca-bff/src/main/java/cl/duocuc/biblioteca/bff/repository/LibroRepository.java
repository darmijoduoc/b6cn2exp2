package cl.duocuc.biblioteca.bff.repository;

import cl.duocuc.biblioteca.bff.entity.LibroEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<LibroEntity, Long> {
}
