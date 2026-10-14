<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h2>Registrar ingreso de un vehículo</h2>

<c:if test="${not empty mensaje}"><div class="ok"><c:out value="${mensaje}"/></div></c:if>
<c:if test="${not empty errorBD}"><div class="error"><c:out value="${errorBD}"/></div></c:if>
<c:if test="${not empty errores}">
    <div class="error"><ul><c:forEach var="er" items="${errores}"><li><c:out value="${er}"/></li></c:forEach></ul></div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/ingreso" novalidate>
    <label for="placa">Placa</label>
    <input type="text" id="placa" name="placa" value="<c:out value='${placa}'/>" placeholder="Ejemplo: ABC123" autocomplete="off">

    <label for="tipo">Tipo de vehículo</label>
    <select id="tipo" name="tipo">
        <option value="">-- Seleccione --</option>
        <option value="CARRO" ${tipo == 'CARRO' ? 'selected' : ''}>Carro</option>
        <option value="MOTO" ${tipo == 'MOTO' ? 'selected' : ''}>Moto</option>
    </select>

    <label for="idEspacio">Espacio</label>
    <select id="idEspacio" name="idEspacio">
        <option value="">-- Seleccione --</option>
        <c:forEach var="e" items="${espacios}">
            <option value="${e.id}"><c:out value="${e.codigo}"/> (<c:out value="${e.tipo}"/>)</option>
        </c:forEach>
    </select>

    <p class="nota">La fecha y hora de ingreso se registran automáticamente.</p>
    <button type="submit">Registrar ingreso</button>
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
