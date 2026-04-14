package com.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Prestamo {

    private Long id;
    private Long usuarioId;
    private Long libroId;
    private String fechaPrestamo;
    private String fechaDevolucion;
    private String estado;

    public Prestamo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getLibroId() { return libroId; }
    public void setLibroId(Long libroId) { this.libroId = libroId; }

    public String getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(String fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public String getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(String fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
