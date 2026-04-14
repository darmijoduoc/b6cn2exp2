package cl.duocuc.biblioteca.bff.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {
    private Long id;
    private Long usuarioId;
    private Long libroId;
    private String fechaReserva;
    private String estado;
}
