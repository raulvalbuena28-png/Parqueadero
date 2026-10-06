<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.text.SimpleDateFormat, modelo.Registro" %>
<%!
    // Declaración JSP: formato de fecha reutilizable en toda la página
    private String formatear(java.sql.Timestamp t) {
        if (t == null) {
            return "-";
        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(t);
    }
%>
<%
    // Scriptlet: datos que envió el servlet con request.setAttribute
    List<Registro> registros = (List<Registro>) request.getAttribute("registros");
    String mensaje = (String) request.getAttribute("mensaje");
    String error = (String) request.getAttribute("error");
    String filtro = (String) request.getAttribute("filtroPlaca");
    if (filtro == null) {
        filtro = "";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Parqueadero - Registros</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
    <jsp:include page="encabezado.jsp" />
    <main>
        <h2>Registros del parqueadero</h2>

        <% if (mensaje != null) { %>
            <div class="aviso ok"><%= mensaje %></div>
        <% } %>
        <% if (error != null) { %>
            <div class="aviso error"><%= error %></div>
        <% } %>

        <form class="filtro" action="parqueadero" method="get">
            <input type="hidden" name="accion" value="listar">
            <div>
                <label for="placa">Buscar por placa</label>
                <input type="text" id="placa" name="placa" maxlength="7" value="<%= filtro %>">
            </div>
            <button type="submit">Buscar</button>
        </form>

        <% if (registros == null || registros.isEmpty()) { %>
            <p>No hay registros para mostrar. Registre un ingreso desde el <a href="index.html">inicio</a>.</p>
        <% } else { %>
            <div class="tabla-scroll">
            <table>
                <thead>
                    <tr>
                        <th>Placa</th>
                        <th>Tipo</th>
                        <th>Ingreso</th>
                        <th>Salida</th>
                        <th>Valor</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                <% for (Registro r : registros) { %>
                    <tr>
                        <td><%= r.getPlaca() %></td>
                        <td><%= r.getTipoVehiculo().equals("CARRO") ? "Carro" : "Moto" %></td>
                        <td><%= formatear(r.getFechaIngreso()) %></td>
                        <td><%= formatear(r.getFechaSalida()) %></td>
                        <td><%= r.isActivo() ? "-" : String.format("$%,.0f", r.getValorPagado()) %></td>
                        <td>
                            <% if (r.isActivo()) { %>
                                <span class="estado-dentro">Dentro</span>
                            <% } else { %>
                                <a class="estado-salio" href="parqueadero?accion=comprobante&id=<%= r.getId() %>">Ver comprobante</a>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
            </div>
        <% } %>
    </main>
</body>
</html>
