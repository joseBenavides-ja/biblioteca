package cl.untec.biblioteca.model.dao;

import cl.untec.biblioteca.config.ConexionDB;
import cl.untec.biblioteca.model.dto.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Acceso a datos de usuarios para autenticacion.
public class UsuarioDAO {

    // Retorna el usuario cuando las credenciales coinciden; si no, devuelve null.
    public Usuario autenticar(String username, String password) {
        String sql = "SELECT id, username, password, nombre, rol FROM usuarios WHERE username = ? AND password = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setRol(rs.getString("rol"));
                    return usuario;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al autenticar usuario", e);
        }

        return null;
    }
}