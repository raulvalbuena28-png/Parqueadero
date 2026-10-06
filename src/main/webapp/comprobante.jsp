<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.text.SimpleDateFormat, modelo.Registro" %>
<%
    Registro r = (Registro) request.getAttribute("registro");
    SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Parqueadero - Comprobante</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
    <jsp:include page="encabezado.jsp" />
    <main>
        <% if (r == null) { %>
            <div class="aviso error">No se encontró el registro.</div>
        <% } else { %>
            <h2>Comprobante de salida</h2>
            <div class="comprobante">
                <dl>
                    <dt>Placa</dt>
                    <dd><%= r.getPlaca() %></dd>
                    <dt>Tipo de vehículo</dt>
                    <dd><%= r.getTipoVehiculo().equals("CARRO") ? "Carro" : "Moto" %></dd>
                    <dt>Hora de ingreso</dt>
                    <dd><%= formato.format(r.getFechaIngreso()) %></dd>
                    <dt>Hora de salida</dt>
                    <dd><%= r.getFechaSalida() == null ? "-" : formato.format(r.getFechaSalida()) %></dd>
                    <dt>Total a pagar</dt>
                    <dd class="total"><%= String.format("$%,.0f", r.getValorPagado()) %></dd>
                </dl>
            </div>
            <a class="boton" href="parqueadero?accion=listar">Volver a los registros</a>
        <% } %>
    </main>
</body>
</html>
