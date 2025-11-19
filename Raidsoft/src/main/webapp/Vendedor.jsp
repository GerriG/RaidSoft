<%-- 
    Document   : Vendedor
    Created on : 1 oct 2025, 7:19:25 p. m.
    Author     : Adonay
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");
    if (usuario == null || !"vendedor".equals(rol)) {
        response.sendRedirect("Login.jsp");
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Vendedor</title>
    <link rel="stylesheet" href="css/Vendedor.css">
</head>
<body>
    <div class="vendedor-card">
        <h2>💼 Bienvenido Vendedor: <%= usuario %></h2>
        <a class="logout-btn" href="LogoutServlet">Cerrar Sesión</a>
    </div>
</body>
</html>
