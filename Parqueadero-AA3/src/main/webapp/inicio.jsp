<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h2>Estado del parqueadero</h2>

<c:if test="${not empty errorBD}"><div class="error"><c:out value="${errorBD}"/></div></c:if>

<div class="resumen">
    <div class="tarjeta disponible"><span><c:out value="${disponibles}"/></span>Disponibles</div>
    <div class="tarjeta ocupado"><span><c:out value="${ocupados}"/></span>Ocupados</div>
    <div class="tarjeta apartado"><span><c:out value="${apartados}"/></span>Apartados</div>
</div>

<h3>Espacios</h3>
<table>
    <tr><th>Espacio</th><th>Tipo</th><th>Estado</th></tr>
    <c:forEach var="e" items="${espacios}">
        <tr>
            <td><c:out value="${e.codigo}"/></td>
            <td><c:out value="${e.tipo}"/></td>
            <td><span class="etiqueta ${e.estado}"><c:out value="${e.estado}"/></span></td>
        </tr>
    </c:forEach>
</table>

<h3>Vehículos dentro del parqueadero</h3>
<table>
    <tr><th>Placa</th><th>Tipo</th><th>Espacio</th><th>Ingreso</th></tr>
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

<h3>Espacios apartados</h3>
<table>
    <tr><th>Cliente</th><th>Placa</th><th>Espacio</th><th>Fecha</th><th>Hora</th></tr>
    <c:forEach var="p" items="${listaApartados}">
        <tr>
            <td><c:out value="${p.nombre}"/></td>
            <td><c:out value="${p.placa}"/></td>
            <td><c:out value="${p.espacio}"/></td>
            <td><c:out value="${p.fecha}"/></td>
            <td><c:out value="${p.hora}"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty listaApartados}"><tr><td colspan="5">No hay apartados activos.</td></tr></c:if>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
