package com.parqueadero.servlet;

import com.parqueadero.dao.IngresoDAO;
import com.parqueadero.model.IngresoActivo;
import com.parqueadero.util.Fechas;
import com.parqueadero.util.Validador;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HU-02: Registrar la salida de un vehículo.
 * Busca el vehículo por placa, valida la fecha de salida y pasa a la pantalla de cobro (HU-03).
 */
@WebServlet("/salida")
public class SalidaServlet extends HttpServlet {

    private final IngresoDAO ingresoDAO = new IngresoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mostrar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String placa = Validador.limpiar(req.getParameter("placa"));
        String fechaTxt = Validador.limpiar(req.getParameter("fechaSalida"));

        List<String> errores = new ArrayList<>();
        LocalDateTime salida = LocalDateTime.now().withNano(0);
        IngresoActivo activo = null;

        String errPlaca = Validador.validarPlaca(placa);
        if (errPlaca != null) {
            errores.add(errPlaca);
        }

        boolean fechaValida = true;
        if (!fechaTxt.isEmpty()) {
            try {
                salida = LocalDateTime.parse(fechaTxt, Fechas.MINUTO);
            } catch (DateTimeParseException ex) {
                fechaValida = false;
                errores.add("Fecha y hora de salida inválida. Use el formato aaaa-mm-dd hh:mm (ejemplo: 2026-10-10 14:30)");
            }
        }

        if (errPlaca == null) {
            try {
                activo = ingresoDAO.buscarActivoPorPlaca(placa.toUpperCase());
                if (activo == null) {
                    errores.add("Vehículo no encontrado o sin ingreso activo");
                }
            } catch (SQLException ex) {
                errores.add("Error de base de datos: " + ex.getMessage());
            }
        }

        if (activo != null && fechaValida) {
            if (salida.isBefore(activo.getFechaIngreso())) {
                errores.add("La fecha de salida no puede ser anterior a la fecha de ingreso ("
                        + activo.getFechaIngresoTexto() + ")");
            } else if (salida.isAfter(LocalDateTime.now().plusMinutes(1))) {
                errores.add("La fecha de salida no puede ser posterior a la fecha y hora actual");
            }
        }

        if (errores.isEmpty()) {
            CobroServlet.cargarResumen(req, activo, salida);
            req.getRequestDispatcher("/cobro.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("errores", errores);
        req.setAttribute("placa", placa);
        req.setAttribute("fechaSalida", fechaTxt);
        mostrar(req, resp);
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("activos", ingresoDAO.listarActivos());
        } catch (SQLException ex) {
            req.setAttribute("errorBD", "Error de base de datos: " + ex.getMessage());
        }
        req.getRequestDispatcher("/salida.jsp").forward(req, resp);
    }
}
