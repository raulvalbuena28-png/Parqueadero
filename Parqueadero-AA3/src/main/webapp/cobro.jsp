<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h2>Cobro del parqueadero</h2>

<c:if test="${not empty errorBD}"><div class="error"><c:out value="${errorBD}"/></div></c:if>
<c:if test="${not empty errores}">
    <div class="error"><ul><c:forEach var="er" items="${errores}"><li><c:out value="${er}"/></li></c:forEach></ul></div>
</c:if>

<table class="detalle">
    <tr><th>Placa</th><td><c:out value="${ingreso.placa}"/></td></tr>
    <tr><th>Tipo de vehículo</th><td><c:out value="${ingreso.tipo}"/></td></tr>
    <tr><th>Espacio</th><td><c:out value="${ingreso.codigoEspacio}"/></td></tr>
    <tr><th>Fecha y hora de ingreso</th><td><c:out value="${ingreso.fechaIngresoTexto}"/></td></tr>
    <tr><th>Fecha y hora de salida</th><td><c:out value="${fechaSalidaTxt}"/></td></tr>
    <tr><th>Tiempo de permanencia</th><td><c:out value="${permanencia}"/></td></tr>
    <tr><th>Tarifa por hora</th><td>$ <c:out value="${ingreso.tarifaHoraTexto}"/></td></tr>
    <tr><th>Horas cobradas</th><td><c:out value="${horas}"/></td></tr>
    <tr class="total"><th>VALOR A PAGAR</th><td>$ <c:out value="${total}"/></td></tr>
</table>

<form method="post" action="${pageContext.request.contextPath}/cobro" novalidate>
    <input type="hidden" name="idIngreso" value="<c:out value='${ingreso.idIngreso}'/>">
    <input type="hidden" name="fechaSalida" value="<c:out value='${fechaSalidaTxt}'/>">

    <label for="valorRecibido">Valor recibido</label>
    <input type="text" id="valorRecibido" name="valorRecibido" value="<c:out value='${valorRecibido}'/>"
           placeholder="Ejemplo: 10000" autocomplete="off">

    <label for="metodo">Método de pago</label>
    <select id="metodo" name="metodo">
        <option value="">-- Seleccione --</option>
        <option value="EFECTIVO" ${metodo == 'EFECTIVO' ? 'selected' : ''}>Efectivo</option>
        <option value="TARJETA" ${metodo == 'TARJETA' ? 'selected' : ''}>Tarjeta</option>
        <option value="TRANSFERENCIA" ${metodo == 'TRANSFERENCIA' ? 'selected' : ''}>Transferencia</option>
    </select>

    <button type="submit">Registrar pago y salida</button>
    <a class="boton-secundario" href="${pageContext.request.contextPath}/salida">Cancelar</a>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
