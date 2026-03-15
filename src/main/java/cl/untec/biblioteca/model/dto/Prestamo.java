package cl.untec.biblioteca.model.dto;

import java.sql.Timestamp;

// DTO que modela un prestamo de libro, activo o devuelto.
public class Prestamo {

    private int id;
    private int libroId;
    private String tituloLibro;
    private String serialLibro;
    private String nombrePrestatario;
    private String rutPrestatario;
    private Timestamp fechaPrestamo;
    private Timestamp fechaDevolucion;
    private String estado;
    private String rutDevolucion;
    private String observacionDevolucion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public int getLibroId() {
        return libroId;
    }

    public void setLibroId(int libroId) {
        this.libroId = libroId;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getSerialLibro() {
        return serialLibro;
    }

    public void setSerialLibro(String serialLibro) {
        this.serialLibro = serialLibro;
    }

    public String getNombrePrestatario() {
        return nombrePrestatario;
    }

    public void setNombrePrestatario(String nombrePrestatario) {
        this.nombrePrestatario = nombrePrestatario;
    }

    public String getRutPrestatario() {
        return rutPrestatario;
    }

    public void setRutPrestatario(String rutPrestatario) {
        this.rutPrestatario = rutPrestatario;
    }

    public Timestamp getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(Timestamp fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public Timestamp getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Timestamp fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRutDevolucion() {
        return rutDevolucion;
    }

    public void setRutDevolucion(String rutDevolucion) {
        this.rutDevolucion = rutDevolucion;
    }

    public String getObservacionDevolucion() {
        return observacionDevolucion;
    }

    public void setObservacionDevolucion(String observacionDevolucion) {
        this.observacionDevolucion = observacionDevolucion;
    }
}