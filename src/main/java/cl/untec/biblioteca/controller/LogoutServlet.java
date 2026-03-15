package cl.untec.biblioteca.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Cierra sesion del usuario autenticado y lo devuelve al login.
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            // Elimina completamente los datos de autenticacion en servidor.
            session.invalidate();
        }

        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }
}