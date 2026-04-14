package cl.duocuc.biblioteca.bff.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "LIBROS")
public class LibroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 200)
    private String titulo;

    @Column(name = "AUTOR", nullable = false, length = 200)
    private String autor;

    @Column(name = "ISBN", nullable = false, unique = true, length = 50)
    private String isbn;

    @Column(name = "DISPONIBLE", nullable = false)
    private Integer disponible = 1;
}
