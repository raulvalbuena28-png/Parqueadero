package com.parqueadero.servlet;

import com.parqueadero.dao.EspacioDAO;
import com.parqueadero.dao.IngresoDAO;
import com.parqueadero.util.ReglaNegocioException;
import com.parqueadero.util.Validador;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** HU-01: Registrar el ingreso de un vehículo. GET muestra el formulario, POST lo procesa. */
@WebServlet("/ingreso")
public class IngresoServlet extends HttpServlet {

    private final EspacioDAO espacioDAO = new EspacioDAO();
    private final IngresoDAO ingresoDAO = new IngresoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mostrar(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String placa = Validador.limpiar(req.getParameter("placa"));
        String tipo = Validador.limpiar(req.getParameter("tipo"));
        String sEspacio = Validador.limpiar(req.getParameter("idEspacio"));

        List<String> errores = new ArrayList<>();

        String errPlaca = Validador.validarPlaca(placa);
        if (errPlaca != null) {
            errores.add(errPlaca);
        }
        if (!tipo.equals("CARRO") && !tipo.equals("MOTO")) {
            errores.add("Seleccione el tipo de vehículo (carro o moto)");
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

        if (errores.isEmpty()) {
            try {
                ingresoDAO.registrar(placa.toUpperCase(), tipo, idEspacio);
                req.setAttribute("mensaje", "Ingreso registrado correctamente. Placa: " + placa.toUpperCase());
                placa = "";
                tipo = "";
            } catch (ReglaNegocioException ex) {
                errores.add(ex.getMessage());
            } catch (SQLException ex) {
                errores.add("Error de base de datos: " + ex.getMessage());
            }
        }

        req.setAttribute("errores", errores);
        req.setAttribute("placa", placa);
        req.setAttribute("tipo", tipo);
        mostrar(req, resp);
    }

    private void mostrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("espacios", espacioDAO.listarDisponibles());
            req.setAttribute("activos", ingresoDAO.listarActivos());
        } catch (SQLException ex) {
            req.setAttribute("errorBD", "Error de base de datos: " + ex.getMessage());
        }
        req.getRequestDispatcher("/ingreso.jsp").forward(req, resp);
    }
}
