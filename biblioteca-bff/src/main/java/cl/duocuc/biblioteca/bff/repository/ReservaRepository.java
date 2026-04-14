package cl.duocuc.biblioteca.bff.repository;

import cl.duocuc.biblioteca.bff.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {
}
