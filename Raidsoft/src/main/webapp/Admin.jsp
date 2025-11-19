<%-- 
    Document   : Admin
    Created on : 1 oct 2025, 7:19:16 p. m.
    Author     : Adonay
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String rol = (String) session.getAttribute("rol");
    if (usuario == null || !"admin".equals(rol)) {
        response.sendRedirect("Login.jsp");
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel Admin</title>
    <link rel="stylesheet" href="css/Admin.css">
</head>
<body>
    <div class="admin-card">
        <h2>👑 Bienvenido Administrador: <%= usuario %></h2>
        <a class="logout-btn" href="LogoutServlet">Cerrar Sesión</a>
    </div>
</body>
</html>
