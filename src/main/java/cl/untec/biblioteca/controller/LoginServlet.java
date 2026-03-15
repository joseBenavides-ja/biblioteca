package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.config.DatabaseInitializer;
import cl.untec.biblioteca.model.dao.UsuarioDAO;
import cl.untec.biblioteca.model.dto.Usuario;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Gestiona autenticacion basica y carga de datos de sesion.
public class LoginServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO;

    @Override
    public void init() throws ServletException {
        // Se asegura de que la BD y tablas existan antes de recibir solicitudes.
        DatabaseInitializer.initialize();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        if (usuario == null || usuario.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            // Validacion minima para evitar consultas innecesarias.
            request.setAttribute("error", "Debes completar usuario y contrasena");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }

        Usuario usuarioAutenticado = usuarioDAO.autenticar(usuario, password);

        if (usuarioAutenticado != null) {
            HttpSession session = request.getSession();
            // Estos datos luego se usan en dashboard y control de sesion.
            session.setAttribute("usuarioLogueado", usuarioAutenticado.getUsername());
            session.setAttribute("nombreUsuario", usuarioAutenticado.getNombre());
            session.setAttribute("rolUsuario", usuarioAutenticado.getRol());

            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Credenciales incorrectas");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}