<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h2>Apartar un espacio</h2>

<c:if test="${not empty mensaje}"><div class="ok"><c:out value="${mensaje}"/></div></c:if>
<c:if test="${not empty errorBD}"><div class="error"><c:out value="${errorBD}"/></div></c:if>
<c:if test="${not empty errores}">
    <div class="error"><ul><c:forEach var="er" items="${errores}"><li><c:out value="${er}"/></li></c:forEach></ul></div>
</c:if>

<form method="post" action="${pageContext.request.contextPath}/apartado" novalidate>
    <label for="nombre">Nombre del cliente</label>
    <input type="text" id="nombre" name="nombre" value="<c:out value='${nombre}'/>" placeholder="Ejemplo: Juan Pérez" autocomplete="off">

    <label for="placa">Placa</label>
    <input type="text" id="placa" name="placa" value="<c:out value='${placa}'/>" placeholder="Ejemplo: DEF456" autocomplete="off">

    <label for="idEspacio">Espacio</label>
    <select id="idEspacio" name="idEspacio">
        <option value="">-- Seleccione --</option>
        <c:forEach var="e" items="${espacios}">
            <option value="${e.id}"><c:out value="${e.codigo}"/> (<c:out value="${e.tipo}"/>)</option>
        </c:forEach>
    </select>

    <label for="fecha">Fecha del apartado</label>
    <input type="text" id="fecha" name="fecha" value="<c:out value='${fecha}'/>" placeholder="aaaa-mm-dd (ejemplo: 2026-10-15)" autocomplete="off">

    <label for="hora">Hora del apartado</label>
    <input type="text" id="hora" name="hora" value="<c:out value='${hora}'/>" placeholder="hh:mm en 24 horas (ejemplo: 08:30)" autocomplete="off">

    <button type="submit">Apartar espacio</button>
</form>

<h3>Apartados activos</h3>
<table>
    <tr><th>Cliente</th><th>Placa</th><th>Espacio</th><th>Fecha</th><th>Hora</th></tr>
    <c:forEach var="p" items="${apartados}">
        <tr>
            <td><c:out value="${p.nombre}"/></td>
            <td><c:out value="${p.placa}"/></td>
            <td><c:out value="${p.espacio}"/></td>
            <td><c:out value="${p.fecha}"/></td>
            <td><c:out value="${p.hora}"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty apartados}"><tr><td colspan="5">No hay apartados activos.</td></tr></c:if>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
