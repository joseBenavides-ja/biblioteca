package cl.untec.biblioteca.model.dao;

import cl.untec.biblioteca.config.ConexionDB;
import cl.untec.biblioteca.model.dto.Movimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Guarda y consulta el historial operativo del sistema.
public class MovimientoDAO {

    // Registra eventos de ALTA, PRESTAMO y DEVOLUCION para auditoria.
    public void registrarMovimiento(Integer libroId, String tituloLibro, String serialLibro, String tipo,
                                    int cantidad, String usuario, String rutReferencia, String observacion) {

        String sql = "INSERT INTO movimientos (libro_id, titulo_libro, serial_libro, tipo, cantidad, usuario, rut_referencia, observacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (libroId == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, libroId);
            }

            ps.setString(2, tituloLibro);
            ps.setString(3, serialLibro);
            ps.setString(4, tipo);
            ps.setInt(5, cantidad);
            ps.setString(6, usuario);
            ps.setString(7, rutReferencia);
            ps.setString(8, observacion);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error al registrar movimiento", e);
        }
    }

    // Devuelve los movimientos mas recientes para la tabla de historial.
    public List<Movimiento> listarRecientes(int limite) {
        List<Movimiento> movimientos = new ArrayList<>();

        String sql = "SELECT id, libro_id, titulo_libro, serial_libro, tipo, cantidad, usuario, rut_referencia, fecha, observacion " +
                     "FROM movimientos ORDER BY fecha DESC LIMIT ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Movimiento m = new Movimiento();
                    m.setId(rs.getInt("id"));

                    int libroId = rs.getInt("libro_id");
                    if (rs.wasNull()) {
                        m.setLibroId(null);
                    } else {
                        m.setLibroId(libroId);
                    }

                    m.setTituloLibro(rs.getString("titulo_libro"));
                    m.setSerialLibro(rs.getString("serial_libro"));
                    m.setTipo(rs.getString("tipo"));
                    m.setCantidad(rs.getInt("cantidad"));
                    m.setUsuario(rs.getString("usuario"));
                    m.setRutReferencia(rs.getString("rut_referencia"));
                    m.setFecha(rs.getTimestamp("fecha"));
                    m.setObservacion(rs.getString("observacion"));
                    movimientos.add(m);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al listar movimientos", e);
        }

        return movimientos;
    }
}