package cl.untec.biblioteca.controller;

import cl.untec.biblioteca.config.DatabaseInitializer;
import cl.untec.biblioteca.model.dao.LibroDAO;
import cl.untec.biblioteca.model.dao.MovimientoDAO;
import cl.untec.biblioteca.model.dao.PrestamoDAO;
import cl.untec.biblioteca.model.dto.Libro;
import cl.untec.biblioteca.model.dto.Prestamo;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Coordina la vista principal: estadisticas, altas, prestamos y devoluciones.
public class DashboardServlet extends HttpServlet {

    private LibroDAO libroDAO;
    private MovimientoDAO movimientoDAO;
    private PrestamoDAO prestamoDAO;

    @Override
    public void init() throws ServletException {
        // Garantiza que la estructura de datos exista antes de operar con DAO.
        DatabaseInitializer.initialize();
        libroDAO = new LibroDAO();
        movimientoDAO = new MovimientoDAO();
        prestamoDAO = new PrestamoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = obtenerSesionAutenticada(request);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        // Calcula indicadores para tarjetas de resumen en la JSP.
        List<Libro> libros = libroDAO.listarTodos();

        int totalTitulos = libros.size();
        int totalEjemplares = 0;
        int disponibles = 0;
        int prestados = 0;

        for (Libro libro : libros) {
            totalEjemplares += libro.getStockTotal();
            disponibles += libro.getStockDisponible();
            prestados += (libro.getStockTotal() - libro.getStockDisponible());
        }

        request.setAttribute("libros", libros);
        request.setAttribute("totalTitulos", totalTitulos);
        request.setAttribute("totalEjemplares", totalEjemplares);
        request.setAttribute("disponibles", disponibles);
        request.setAttribute("prestados", prestados);
        request.setAttribute("movimientos", movimientoDAO.listarRecientes(12));

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = obtenerSesionAutenticada(request);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        // Enruta la accion del formulario a su caso de uso correspondiente.
        if (accion == null) {
            session.setAttribute("mensajeError", "Accion no valida.");
        } else {
            switch (accion) {
                case "agregar":
                    agregarLibro(request, session);
                    break;
                case "prestar":
                    prestarLibro(request, session);
                    break;
                case "regresar":
                    regresarLibro(request, session);
                    break;
                default:
                    session.setAttribute("mensajeError", "Accion no valida.");
                    break;
            }
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    // Valida datos de entrada y crea un nuevo libro con su movimiento de ALTA.
    private void agregarLibro(HttpServletRequest request, HttpSession session) {
        String titulo = normalizar(request.getParameter("titulo"));
        String autor = normalizar(request.getParameter("autor"));
        String serialInterno = normalizar(request.getParameter("serialInterno"));
        String stockTotalStr = normalizar(request.getParameter("stockTotal"));

        if (titulo.isEmpty() || autor.isEmpty() || serialInterno.isEmpty() || stockTotalStr.isEmpty()) {
            session.setAttribute("mensajeError", "Debes completar todos los campos.");
            return;
        }

        if (!serialInterno.matches("\\d{7}")) {
            session.setAttribute("mensajeError", "El serial interno debe tener exactamente 7 digitos.");
            return;
        }

        int stockTotal;
        try {
            stockTotal = Integer.parseInt(stockTotalStr);
            if (stockTotal <= 0) {
                session.setAttribute("mensajeError", "El stock debe ser mayor que cero.");
                return;
            }
        } catch (NumberFormatException e) {
            session.setAttribute("mensajeError", "El stock debe ser numerico.");
            return;
        }

        try {
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setSerialInterno(serialInterno);
            libro.setStockTotal(stockTotal);
            libro.setStockDisponible(stockTotal);

            libroDAO.insertar(libro);

            movimientoDAO.registrarMovimiento(
                null,
                libro.getTitulo(),
                libro.getSerialInterno(),
                "ALTA",
                stockTotal,
                obtenerUsuarioSesion(session),
                "-",
                "Registro inicial del libro"
            );

            session.setAttribute("mensajeOk", "Libro agregado correctamente.");
        } catch (Exception e) {
            session.setAttribute("mensajeError", "No se pudo guardar el libro. Revisa que el serial no este repetido.");
        }
    }

    // Descuenta stock, registra prestamo y deja trazabilidad en movimientos.
    private void prestarLibro(HttpServletRequest request, HttpSession session) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nombreLector = normalizar(request.getParameter("nombreLector"));
            String rutLector = normalizar(request.getParameter("rutLector"));

            if (nombreLector.isEmpty() || rutLector.isEmpty()) {
                session.setAttribute("mensajeError", "Debes ingresar nombre y rut para el prestamo.");
                return;
            }

            Libro libro = libroDAO.buscarPorId(id);

            if (libro == null) {
                session.setAttribute("mensajeError", "Libro no encontrado.");
                return;
            }

            boolean actualizado = libroDAO.prestarLibro(id);

            if (actualizado) {
                prestamoDAO.registrarPrestamo(
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getSerialInterno(),
                    nombreLector,
                    rutLector
                );

                movimientoDAO.registrarMovimiento(
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getSerialInterno(),
                    "PRESTAMO",
                    1,
                    obtenerUsuarioSesion(session),
                    rutLector,
                    "Prestado a " + nombreLector
                );

                session.setAttribute("mensajeOk", "Prestamo registrado correctamente.");
            } else {
                session.setAttribute("mensajeError", "No hay stock disponible para prestar.");
            }
        } catch (Exception e) {
            session.setAttribute("mensajeError", "No se pudo registrar el prestamo.");
        }
    }

    // Registra devolucion, cierra prestamo activo y repone stock disponible.
    private void regresarLibro(HttpServletRequest request, HttpSession session) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String rutDevolucion = normalizar(request.getParameter("rutDevolucion"));
            String observacionRegreso = normalizar(request.getParameter("observacionRegreso"));

            if (rutDevolucion.isEmpty()) {
                session.setAttribute("mensajeError", "Debes ingresar el rut para registrar el regreso.");
                return;
            }

            Libro libro = libroDAO.buscarPorId(id);

            if (libro == null) {
                session.setAttribute("mensajeError", "Libro no encontrado.");
                return;
            }

            Prestamo prestamoActivo = prestamoDAO.buscarPrestamoActivoPorLibroYRut(id, rutDevolucion);

            if (prestamoActivo == null) {
                session.setAttribute("mensajeError", "No existe un prestamo activo para ese serial y rut.");
                return;
            }

            boolean actualizado = libroDAO.regresarLibro(id);

            if (actualizado) {
                prestamoDAO.cerrarPrestamo(
                    prestamoActivo.getId(),
                    rutDevolucion,
                    observacionRegreso
                );

                String observacionMovimiento = "Regreso de ejemplar";
                if (!observacionRegreso.isEmpty()) {
                    observacionMovimiento += " | Obs: " + observacionRegreso;
                }

                movimientoDAO.registrarMovimiento(
                    libro.getId(),
                    libro.getTitulo(),
                    libro.getSerialInterno(),
                    "DEVOLUCION",
                    1,
                    obtenerUsuarioSesion(session),
                    prestamoActivo.getRutPrestatario(),
                    observacionMovimiento
                );

                session.setAttribute("mensajeOk", "Regreso registrado correctamente.");
            } else {
                session.setAttribute("mensajeError", "El stock ya esta completo.");
            }
        } catch (Exception e) {
            session.setAttribute("mensajeError", "No se pudo registrar el regreso.");
        }
    }

    // Prioriza el nombre visible del usuario y usa username como respaldo.
    private String obtenerUsuarioSesion(HttpSession session) {
        Object nombre = session.getAttribute("nombreUsuario");
        if (nombre != null) {
            return nombre.toString();
        }

        Object user = session.getAttribute("usuarioLogueado");
        return user != null ? user.toString() : "Sistema";
    }

    private HttpSession obtenerSesionAutenticada(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            return null;
        }
        return session;
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}