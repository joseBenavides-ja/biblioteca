package cl.untec.biblioteca.model.dao;

import cl.untec.biblioteca.config.ConexionDB;
import cl.untec.biblioteca.model.dto.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

// Encapsula operaciones CRUD y de stock para libros.
public class LibroDAO {

    // Obtiene todo el catalogo ordenado por id para mostrarlo en dashboard.
    public List<Libro> listarTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, serial_interno, stock_total, stock_disponible FROM libros ORDER BY id";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setId(rs.getInt("id"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setSerialInterno(rs.getString("serial_interno"));
                libro.setStockTotal(rs.getInt("stock_total"));
                libro.setStockDisponible(rs.getInt("stock_disponible"));
                libros.add(libro);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al listar libros", e);
        }

        return libros;
    }

    // Busca un libro puntual por identificador.
    public Libro buscarPorId(int id) {
        String sql = "SELECT id, titulo, autor, serial_interno, stock_total, stock_disponible FROM libros WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id"));
                    libro.setTitulo(rs.getString("titulo"));
                    libro.setAutor(rs.getString("autor"));
                    libro.setSerialInterno(rs.getString("serial_interno"));
                    libro.setStockTotal(rs.getInt("stock_total"));
                    libro.setStockDisponible(rs.getInt("stock_disponible"));
                    return libro;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar libro por id", e);
        }

        return null;
    }

    // Inserta un nuevo libro con su stock inicial.
    public void insertar(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, serial_interno, stock_total, stock_disponible) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getSerialInterno());
            ps.setInt(4, libro.getStockTotal());
            ps.setInt(5, libro.getStockDisponible());
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error al insertar libro", e);
        }
    }

    // Descuenta una unidad solo si hay disponibilidad.
    public boolean prestarLibro(int id) {
        String sql = "UPDATE libros SET stock_disponible = stock_disponible - 1 WHERE id = ? AND stock_disponible > 0";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al prestar libro", e);
        }
    }

    // Repone una unidad sin superar el stock total del titulo.
    public boolean regresarLibro(int id) {
        String sql = "UPDATE libros SET stock_disponible = stock_disponible + 1 WHERE id = ? AND stock_disponible < stock_total";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException("Error al regresar libro", e);
        }
    }
}