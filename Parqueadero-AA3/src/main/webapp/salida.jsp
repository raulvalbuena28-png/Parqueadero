<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h2>Registrar salida de un vehículo</h2>

<c:if test="${not empty mensaje}"><div class="ok"><c:out value="${mensaje}"/></div></c:if>
<c:if test="${not empty errorBD}"><div class="error"><c:out value="${errorBD}"/></div></c:if>
<c:if test="${not empty errores}">
    <div class="error"><ul><c:forEach var="er" items="${errores}"><li><c:out value="${er}"/></li></c:forEach></ul></div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/salida" novalidate>
    <label for="placa">Placa del vehículo</label>
    <input type="text" id="placa" name="placa" value="<c:out value='${placa}'/>" placeholder="Ejemplo: ABC123" autocomplete="off">

    <label for="fechaSalida">Fecha y hora de salida (opcional)</label>
    <input type="text" id="fechaSalida" name="fechaSalida" value="<c:out value='${fechaSalida}'/>"
           placeholder="aaaa-mm-dd hh:mm (vacío = ahora)" autocomplete="off">

    <p class="nota">Si deja la fecha vacía se usa la fecha y hora actuales. Al continuar se calcula el valor a pagar.</p>
    <button type="submit">Buscar vehículo y calcular cobro</button>
</form>

<h3>Vehículos dentro del parqueadero</h3>
<table>
    <tr><th>Placa</th><th>Tipo</th><th>Espacio</th><th>Fecha y hora de ingreso</th></tr>
    <c:forEach var="a" items="${activos}">
        <tr>
            <td><c:out value="${a.placa}"/></td>
            <td><c:out value="${a.tipo}"/></td>
            <td><c:out value="${a.codigoEspacio}"/></td>
            <td><c:out value="${a.fechaIngresoTexto}"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty activos}"><tr><td colspan="4">No hay vehículos dentro.</td></tr></c:if>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
