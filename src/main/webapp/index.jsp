<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Pantalla de entrada: recibe credenciales y muestra errores de validacion. --%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Login - Biblioteca Digital UNTEC</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
</head>
<body class="page-bg">
    <div class="auth-wrapper">
        <div class="auth-card">
            <div class="auth-header">
                <h1>Biblioteca Digital UNTEC</h1>
                <p>Inicio de sesion</p>
            </div>

            <c:if test="${not empty error}">
                <%-- Mensaje devuelto por LoginServlet cuando faltan datos o credenciales invalidas. --%>
                <div class="alert alert-error">
                    <c:out value="${error}" />
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/login" method="post" class="form-stack">
                <div class="field">
                    <label for="usuario">Usuario</label>
                    <input type="text" id="usuario" name="usuario" placeholder="Ingrese su usuario">
                </div>

                <div class="field">
                    <label for="password">Contrasena</label>
                    <input type="password" id="password" name="password" placeholder="Ingrese su contrasena">
                </div>

                <button type="submit" class="btn btn-primary">Ingresar</button>
            </form>

            <div class="helper-box">
                <p><strong>Usuario de prueba:</strong> admin</p>
                <p><strong>Clave de prueba:</strong> 1234</p>
            </div>
        </div>
    </div>
</body>
</html>