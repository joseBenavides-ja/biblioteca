package cl.untec.biblioteca.model.dto;

// DTO que representa un libro y su estado de stock en el sistema.
public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String serialInterno;
    private int stockTotal;
    private int stockDisponible;

    public Libro() {
    }

    public Libro(int id, String titulo, String autor, String serialInterno, int stockTotal, int stockDisponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.serialInterno = serialInterno;
        this.stockTotal = stockTotal;
        this.stockDisponible = stockDisponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }    

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }    

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }    

    public String getSerialInterno() {
        return serialInterno;
    }

    public void setSerialInterno(String serialInterno) {
        this.serialInterno = serialInterno;
    }

    public int getStockTotal() {
        return stockTotal;
    }

    public void setStockTotal(int stockTotal) {
        this.stockTotal = stockTotal;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public void setStockDisponible(int stockDisponible) {
        this.stockDisponible = stockDisponible;
    }
}