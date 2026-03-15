<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%-- Vista principal del sistema: resumen, operacion de libros e historial. --%>

<c:if test="${empty sessionScope.usuarioLogueado}">
    <%-- Si no hay sesion valida, se protege la pagina y vuelve al login. --%>
    <c:redirect url="/index.jsp" />
</c:if>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel principal - Biblioteca Digital UNTEC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="page-bg">
    <header class="topbar">
        <div class="brand">
            <h1>Biblioteca Digital UNTEC</h1>
            <p>Sistema de gestion bibliotecaria</p>
        </div>

        <div class="topbar-actions">
            <span class="welcome-user">
                Bienvenido, <strong><c:out value="${sessionScope.nombreUsuario}" /></strong>
            </span>
            <a href="${pageContext.request.contextPath}/logout" class="btn btn-danger btn-sm">Cerrar sesion</a>
        </div>
    </header>

    <main class="panel-wrapper">
        <c:if test="${not empty sessionScope.mensajeOk}">
            <div class="alert alert-ok">
                <c:out value="${sessionScope.mensajeOk}" />
            </div>
            <c:remove var="mensajeOk" scope="session" />
        </c:if>

        <c:if test="${not empty sessionScope.mensajeError}">
            <div class="alert alert-error">
                <c:out value="${sessionScope.mensajeError}" />
            </div>
            <c:remove var="mensajeError" scope="session" />
        </c:if>

        <section class="stats-grid">
            <%-- Indicadores globales calculados por DashboardServlet. --%>
            <div class="stat-card">
                <span class="stat-label">Titulos</span>
                <span class="stat-value"><c:out value="${totalTitulos}" /></span>
            </div>

            <div class="stat-card">
                <span class="stat-label">Ejemplares</span>
                <span class="stat-value"><c:out value="${totalEjemplares}" /></span>
            </div>

            <div class="stat-card">
                <span class="stat-label">Disponibles</span>
                <span class="stat-value success"><c:out value="${disponibles}" /></span>
            </div>

            <div class="stat-card">
                <span class="stat-label">Prestados</span>
                <span class="stat-value warning"><c:out value="${prestados}" /></span>
            </div>
        </section>

        <section class="content-grid">
            <div class="card">
                <%-- Formulario de alta de libros: envia accion=agregar al servlet. --%>
                <h2>Registrar nuevo libro</h2>

                <form action="${pageContext.request.contextPath}/dashboard" method="post" class="form-stack">
                    <input type="hidden" name="accion" value="agregar">

                    <div class="field">
                        <label for="titulo">Titulo</label>
                        <input type="text" id="titulo" name="titulo" placeholder="Ej: Arquitectura Limpia">
                    </div>

                    <div class="field">
                        <label for="autor">Autor</label>
                        <input type="text" id="autor" name="autor" placeholder="Ej: Robert C. Martin">
                    </div>

                    <div class="field">
                        <label for="serialInterno">Serial interno</label>
                        <input type="text" id="serialInterno" name="serialInterno" maxlength="7" placeholder="7 digitos">
                    </div>

                    <div class="field">
                        <label for="stockTotal">Stock inicial</label>
                        <input type="number" id="stockTotal" name="stockTotal" min="1" placeholder="Ej: 5">
                    </div>

                    <button type="submit" class="btn btn-primary">Guardar libro</button>
                </form>
            </div>

            <div class="card">
                <%-- Tabla principal del catalogo con acciones de prestar y regresar. --%>
                <h2>Libros registrados</h2>

                <c:if test="${empty libros}">
                    <p>No hay libros para mostrar.</p>
                </c:if>

                <c:if test="${not empty libros}">
                    <div class="table-wrapper">
                        <table class="books-table">
                            <thead>
                                <tr>
                                    <th>Titulo</th>
                                    <th>Autor</th>
                                    <th>Serial</th>
                                    <th>Total</th>
                                    <th>Disponibles</th>
                                    <th>Prestados</th>
                                    <th>Accion</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="libro" items="${libros}">
                                    <tr>
                                        <td><strong><c:out value="${libro.titulo}" /></strong></td>
                                        <td><c:out value="${libro.autor}" /></td>
                                        <td><c:out value="${libro.serialInterno}" /></td>
                                        <td><c:out value="${libro.stockTotal}" /></td>
                                        <td><c:out value="${libro.stockDisponible}" /></td>
                                        <td><c:out value="${libro.stockTotal - libro.stockDisponible}" /></td>
                                        <td>
                                            <div class="action-stack">
                                                <button type="button"
                                                        class="btn btn-secondary btn-sm"
                                                        onclick="abrirModalPrestamo('${libro.id}', '${libro.titulo}', '${libro.serialInterno}', '${libro.stockDisponible}')"
                                                        <c:if test="${libro.stockDisponible == 0}">disabled</c:if>>
                                                    Prestar
                                                </button>

                                                <button type="button"
                                                        class="btn btn-outline btn-sm"
                                                        onclick="abrirModalRegreso('${libro.id}', '${libro.titulo}', '${libro.serialInterno}', '${libro.stockTotal}', '${libro.stockDisponible}')"
                                                        <c:if test="${libro.stockDisponible == libro.stockTotal}">disabled</c:if>>
                                                    Regresar
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </section>

        <section class="history-section">
            <div class="card">
                <%-- Historial operacional generado desde la tabla movimientos. --%>
                <h2>Historial reciente</h2>

                <c:if test="${empty movimientos}">
                    <p>No hay movimientos registrados aun.</p>
                </c:if>

                <c:if test="${not empty movimientos}">
                    <div class="table-wrapper">
                        <table class="books-table">
                            <thead>
                                <tr>
                                    <th>Fecha</th>
                                    <th>Tipo</th>
                                    <th>Libro</th>
                                    <th>Serial</th>
                                    <th>Cantidad</th>
                                    <th>Rut</th>
                                    <th>Usuario sistema</th>
                                    <th>Observacion</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="mov" items="${movimientos}">
                                    <tr>
                                        <td><fmt:formatDate value="${mov.fecha}" pattern="dd-MM-yyyy HH:mm" /></td>
                                        <td>
                                            <span class="history-badge history-${mov.tipo}">
                                                <c:out value="${mov.tipo}" />
                                            </span>
                                        </td>
                                        <td><c:out value="${mov.tituloLibro}" /></td>
                                        <td><c:out value="${mov.serialLibro}" /></td>
                                        <td><c:out value="${mov.cantidad}" /></td>
                                        <td><c:out value="${mov.rutReferencia}" /></td>
                                        <td><c:out value="${mov.usuario}" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty mov.observacion}">
                                                    <c:out value="${mov.observacion}" />
                                                </c:when>
                                                <c:otherwise>-</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
        </section>
    </main>

    <!-- Modal prestar -->
    <div id="modalPrestamo" class="modal-overlay">
        <div class="modal-card">
            <div class="modal-header">
                <h3>Registrar prestamo</h3>
                <button type="button" class="modal-close" onclick="cerrarModal('modalPrestamo')">&times;</button>
            </div>

            <form action="${pageContext.request.contextPath}/dashboard" method="post" class="form-stack">
                <input type="hidden" name="accion" value="prestar">
                <input type="hidden" id="prestamoLibroId" name="id">

                <div class="field">
                    <label>Titulo</label>
                    <input type="text" id="prestamoTitulo" readonly>
                </div>

                <div class="field">
                    <label>Serial</label>
                    <input type="text" id="prestamoSerial" readonly>
                </div>

                <div class="field">
                    <label for="nombreLector">Nombre</label>
                    <input type="text" id="nombreLector" name="nombreLector" placeholder="Nombre de quien recibe">
                </div>

                <div class="field">
                    <label for="rutLector">Rut</label>
                    <input type="text" id="rutLector" name="rutLector" placeholder="Rut de quien recibe">
                </div>

                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" onclick="cerrarModal('modalPrestamo')">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Confirmar prestamo</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal regresar -->
    <div id="modalRegreso" class="modal-overlay">
        <div class="modal-card">
            <div class="modal-header">
                <h3>Registrar regreso</h3>
                <button type="button" class="modal-close" onclick="cerrarModal('modalRegreso')">&times;</button>
            </div>

            <form action="${pageContext.request.contextPath}/dashboard" method="post" class="form-stack">
                <input type="hidden" name="accion" value="regresar">
                <input type="hidden" id="regresoLibroId" name="id">

                <div class="field">
                    <label>Titulo</label>
                    <input type="text" id="regresoTitulo" readonly>
                </div>

                <div class="field">
                    <label>Serial</label>
                    <input type="text" id="regresoSerial" readonly>
                </div>

                <div class="field">
                    <label for="rutDevolucion">Rut de quien devuelve</label>
                    <input type="text" id="rutDevolucion" name="rutDevolucion" placeholder="Rut del regreso">
                </div>

                <div class="field">
                    <label for="observacionRegreso">Observaciones de regreso</label>
                    <textarea id="observacionRegreso" name="observacionRegreso" rows="4" placeholder="Ej: Libro entregado en buen estado"></textarea>
                </div>

                <div class="modal-actions">
                    <button type="button" class="btn btn-outline" onclick="cerrarModal('modalRegreso')">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Confirmar regreso</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Abre modal de prestamo y precarga datos del libro seleccionado.
        function abrirModalPrestamo(id, titulo, serial, disponible) {
            if (parseInt(disponible, 10) <= 0) {
                return;
            }

            document.getElementById('prestamoLibroId').value = id;
            document.getElementById('prestamoTitulo').value = titulo;
            document.getElementById('prestamoSerial').value = serial;
            document.getElementById('nombreLector').value = '';
            document.getElementById('rutLector').value = '';
            document.getElementById('modalPrestamo').classList.add('active');
        }

        // Abre modal de regreso para registrar la devolucion de un ejemplar.
        function abrirModalRegreso(id, titulo, serial, total, disponible) {
            if (parseInt(disponible, 10) >= parseInt(total, 10)) {
                return;
            }

            document.getElementById('regresoLibroId').value = id;
            document.getElementById('regresoTitulo').value = titulo;
            document.getElementById('regresoSerial').value = serial;
            document.getElementById('rutDevolucion').value = '';
            document.getElementById('observacionRegreso').value = '';
            document.getElementById('modalRegreso').classList.add('active');
        }

        // Cierra cualquier modal por id para reutilizar la misma logica.
        function cerrarModal(id) {
            document.getElementById(id).classList.remove('active');
        }

        // Permite cerrar modales al hacer clic fuera de la tarjeta.
        window.addEventListener('click', function (event) {
            const modalPrestamo = document.getElementById('modalPrestamo');
            const modalRegreso = document.getElementById('modalRegreso');

            if (event.target === modalPrestamo) {
                cerrarModal('modalPrestamo');
            }

            if (event.target === modalRegreso) {
                cerrarModal('modalRegreso');
            }
        });
    </script>
</body>
</html>