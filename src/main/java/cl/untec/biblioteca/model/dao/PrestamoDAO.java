package cl.untec.biblioteca.model.dao;

import cl.untec.biblioteca.config.ConexionDB;
import cl.untec.biblioteca.model.dto.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Maneja el ciclo de vida de prestamos (alta, busqueda activa y cierre).
public class PrestamoDAO {

    // Registra el prestamo en estado ACTIVO al momento de entregar el ejemplar.
    public void registrarPrestamo(int libroId, String tituloLibro, String serialLibro,
                                  String nombrePrestatario, String rutPrestatario) {

        String sql = "INSERT INTO prestamos (libro_id, titulo_libro, serial_libro, nombre_prestatario, rut_prestatario, estado) " +
                     "VALUES (?, ?, ?, ?, ?, 'ACTIVO')";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, libroId);
            ps.setString(2, tituloLibro);
            ps.setString(3, serialLibro);
            ps.setString(4, nombrePrestatario);
            ps.setString(5, rutPrestatario);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error al registrar prestamo", e);
        }
    }

    // Recupera el primer prestamo activo de un libro para un RUT especifico.
    public Prestamo buscarPrestamoActivoPorLibroYRut(int libroId, String rutPrestatario) {
        String sql = "SELECT id, libro_id, titulo_libro, serial_libro, nombre_prestatario, rut_prestatario, " +
                     "fecha_prestamo, fecha_devolucion, estado, rut_devolucion, observacion_devolucion " +
                     "FROM prestamos " +
                     "WHERE libro_id = ? AND rut_prestatario = ? AND estado = 'ACTIVO' " +
                     "ORDER BY fecha_prestamo ASC LIMIT 1";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, libroId);
            ps.setString(2, rutPrestatario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.setLibroId(rs.getInt("libro_id"));
                    p.setTituloLibro(rs.getString("titulo_libro"));
                    p.setSerialLibro(rs.getString("serial_libro"));
                    p.setNombrePrestatario(rs.getString("nombre_prestatario"));
                    p.setRutPrestatario(rs.getString("rut_prestatario"));
                    p.setFechaPrestamo(rs.getTimestamp("fecha_prestamo"));
                    p.setFechaDevolucion(rs.getTimestamp("fecha_devolucion"));
                    p.setEstado(rs.getString("estado"));
                    p.setRutDevolucion(rs.getString("rut_devolucion"));
                    p.setObservacionDevolucion(rs.getString("observacion_devolucion"));
                    return p;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al buscar prestamo activo", e);
        }

        return null;
    }

    // Marca un prestamo como DEVUELTO y almacena trazas de la devolucion.
    public void cerrarPrestamo(int prestamoId, String rutDevolucion, String observacion) {
        String sql = "UPDATE prestamos " +
                     "SET estado = 'DEVUELTO', fecha_devolucion = CURRENT_TIMESTAMP, rut_devolucion = ?, observacion_devolucion = ? " +
                     "WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rutDevolucion);
            ps.setString(2, observacion);
            ps.setInt(3, prestamoId);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar prestamo", e);
        }
    }
}