package cl.duocuc.biblioteca.bff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "PRESTAMOS")
public class PrestamoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

    @Column(name = "LIBRO_ID", nullable = false)
    private Long libroId;

    @Column(name = "FECHA_PRESTAMO", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaPrestamo;

    @Column(name = "FECHA_DEVOLUCION")
    @Temporal(TemporalType.DATE)
    private Date fechaDevolucion;

    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado = "ACTIVO";
}
