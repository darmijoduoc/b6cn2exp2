package cl.duocuc.biblioteca.bff.repository;

import cl.duocuc.biblioteca.bff.entity.PrestamoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoRepository extends JpaRepository<PrestamoEntity, Long> {
}
