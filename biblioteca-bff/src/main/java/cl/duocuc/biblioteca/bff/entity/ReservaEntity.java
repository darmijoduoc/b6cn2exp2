package cl.duocuc.biblioteca.bff.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "RESERVAS")
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUARIO_ID", nullable = false)
    private Long usuarioId;

    @Column(name = "LIBRO_ID", nullable = false)
    private Long libroId;

    @Column(name = "FECHA_RESERVA", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fechaReserva;

    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado = "PENDIENTE";
}
