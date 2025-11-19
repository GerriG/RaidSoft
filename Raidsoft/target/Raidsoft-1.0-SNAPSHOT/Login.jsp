<%-- 
    Document   : Login
    Created on : 1 oct 2025, 7:15:27 p. m.
    Author     : Adonay
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link rel="stylesheet" href="css/Styles.css">
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body>
    <form action="LoginServlet" method="post">
        <h2 class="titulo-login">🔐 Iniciar sesión</h2>
        
        <label>Usuario</label>
        <input type="text" name="usuario" required>

        <label>Contraseña</label>
        <input type="password" name="clave" required>

        <div class="g-recaptcha" data-sitekey="6Lcu1t0rAAAAACp4SWn-3KRYbhSZ23Z0TJXopPxY"></div>

        <input type="submit" value="Ingresar">

        <p>${error != null ? error : ""}</p>
    </form>
</body>
</html>