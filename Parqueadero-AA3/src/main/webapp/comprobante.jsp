<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<div class="ok">Pago registrado correctamente. La salida del vehículo quedó registrada y el espacio fue liberado.</div>

<h2>Comprobante de pago N.° <c:out value="${c.id}"/></h2>

<table class="detalle">
    <tr><th>Placa</th><td><c:out value="${c.placa}"/></td></tr>
    <tr><th>Tipo de vehículo</th><td><c:out value="${c.tipo}"/></td></tr>
    <tr><th>Espacio</th><td><c:out value="${c.espacio}"/></td></tr>
    <tr><th>Fecha y hora de ingreso</th><td><c:out value="${c.ingreso}"/></td></tr>
    <tr><th>Fecha y hora de salida</th><td><c:out value="${c.salida}"/></td></tr>
    <tr><th>Horas cobradas</th><td><c:out value="${c.horas}"/></td></tr>
    <tr class="total"><th>Valor total</th><td>$ <c:out value="${c.total}"/></td></tr>
    <tr><th>Valor recibido</th><td>$ <c:out value="${c.recibido}"/></td></tr>
    <tr><th>Cambio</th><td>$ <c:out value="${c.cambio}"/></td></tr>
    <tr><th>Método de pago</th><td><c:out value="${c.metodo}"/></td></tr>
    <tr><th>Fecha del pago</th><td><c:out value="${c.pago}"/></td></tr>
</table>

<p>
    <button type="button" onclick="window.print()">Imprimir comprobante</button>
    <a class="boton-secundario" href="${pageContext.request.contextPath}/inicio">Volver al inicio</a>
</p>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
