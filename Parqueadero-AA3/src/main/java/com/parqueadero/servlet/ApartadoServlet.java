package com.parqueadero.servlet;

import com.parqueadero.dao.ApartadoDAO;
import com.parqueadero.dao.EspacioDAO;
import com.parqueadero.util.Fechas;
import com.parqueadero.util.ReglaNegocioException;
import com.parqueadero.util.Validador;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** HU-04: Apartar un espacio para una fecha y hora. */
@WebServlet("/apartado")
public class ApartadoServlet extends HttpServlet {

    private final EspacioDAO espacioDAO = new EspacioDAO();
    private final ApartadoDAO apartadoDAO = new ApartadoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mostrar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String nombre = Validador.limpiar(req.getParameter("nombre"));
        String placa = Validador.limpiar(req.getParameter("placa"));
        String sEspacio = Validador.limpiar(req.getParameter("idEspacio"));
        String fechaTxt = Validador.limpiar(req.getParameter("fecha"));
        String horaTxt = Validador.limpiar(req.getParameter("hora"));

        List<String> errores = new ArrayList<>();

        String err = Validador.validarNombre(nombre);
        if (err != null) {
            errores.add(err);
        }
        err = Validador.validarPlaca(placa);
        if (err != null) {
            errores.add(err);
        }

        int idEspacio = 0;
        if (sEspacio.isEmpty()) {
            errores.add("Seleccione un espacio");
        } else {
            try {
                idEspacio = Integer.parseInt(sEspacio);
            } catch (NumberFormatException ex) {
                errores.add("El espacio seleccionado no es válido");
            }
        }

        LocalDate fecha = null;
        LocalTime hora = null;
        if (fechaTxt.isEmpty()) {
            errores.add("La fecha del apartado es obligatoria");
        } else {
            try {
                fecha = LocalDate.parse(fechaTxt, Fechas.FECHA);
                if (fecha.isBefore(LocalDate.now())) {
                    errores.add("La fecha del apartado no puede ser anterior a hoy");
                    fecha = null;
                }
            } catch (DateTimeParseException ex) {
                errores.add("Fecha inválida. Use el formato aaaa-mm-dd (ejemplo: 2026-10-15)");
            }
        }
        if (horaTxt.isEmpty()) {
            errores.add("La hora del apartado es obligatoria");
        } else {
            try {
                hora = LocalTime.parse(horaTxt, Fechas.HORA);
            } catch (DateTimeParseException ex) {
                errores.add("Hora inválida. Use el formato hh:mm de 24 horas (ejemplo: 08:30)");
            }
        }
        if (fecha != null && hora != null && LocalDateTime.of(fecha, hora).isBefore(LocalDateTime.now())) {
            errores.add("La hora del apartado ya pasó");
        }

        if (errores.isEmpty()) {
            try {
                apartadoDAO.registrar(nombre, placa.toUpperCase(), idEspacio, fecha, hora);
                req.setAttribute("mensaje", "Espacio apartado correctamente para " + nombre
                        + " el " + fechaTxt + " a las " + horaTxt);
                nombre = "";
                placa = "";
                fechaTxt = "";
                horaTxt = "";
            } catch (ReglaNegocioException ex) {
                errores.add(ex.getMessage());
            } catch (SQLException ex) {
                errores.add("Error de base de datos: " + ex.getMessage());
            }
        }

        req.setAttribute("errores", errores);
        req.setAttribute("nombre", nombre);
        req.setAttribute("placa", placa);
        req.setAttribute("fecha", fechaTxt);
        req.setAttribute("hora", horaTxt);
        mostrar(req, resp);
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("espacios", espacioDAO.listarDisponibles());
            req.setAttribute("apartados", apartadoDAO.listarActivos());
        } catch (SQLException ex) {
            req.setAttribute("errorBD", "Error de base de datos: " + ex.getMessage());
        }
        req.getRequestDispatcher("/apartado.jsp").forward(req, resp);
    }
}
