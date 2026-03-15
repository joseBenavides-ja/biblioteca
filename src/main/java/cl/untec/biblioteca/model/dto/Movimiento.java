package cl.untec.biblioteca.model.dto;

import java.sql.Timestamp;

// DTO para mostrar trazabilidad de operaciones realizadas sobre libros.
public class Movimiento {

    private int id;
    private Integer libroId;
    private String tituloLibro;
    private String serialLibro;
    private String tipo;
    private int cantidad;
    private String usuario;
    private String rutReferencia;
    private Timestamp fecha;
    private String observacion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getLibroId() {
        return libroId;
    }

    public void setLibroId(Integer libroId) {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRutReferencia() {
        return rutReferencia;
    }

    public void setRutReferencia(String rutReferencia) {
        this.rutReferencia = rutReferencia;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}