package com.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reserva {

    private Long id;
    private Long usuarioId;
    private Long libroId;
    private String fechaReserva;
    private String estado; // PENDIENTE, COMPLETADA, CANCELADA

    public Reserva() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLibroId() { return libroId; }
    public void setLibroId(Long libroId) { this.libroId = libroId; }

    public String getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(String fechaReserva) { this.fechaReserva = fechaReserva; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
