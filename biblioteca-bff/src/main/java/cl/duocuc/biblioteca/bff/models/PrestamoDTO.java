package cl.duocuc.biblioteca.bff.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrestamoDTO {
    private Long id;
    private Long usuarioId;
    private Long libroId;
    private String fechaPrestamo;
    private String fechaDevolucion;
    private String estado;
}
