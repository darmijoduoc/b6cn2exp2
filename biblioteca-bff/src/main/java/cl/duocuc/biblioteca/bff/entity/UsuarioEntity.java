package cl.duocuc.biblioteca.bff.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "USUARIOS")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @Column(name = "APELLIDO", nullable = false, length = 100)
    private String apellido;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;
}
