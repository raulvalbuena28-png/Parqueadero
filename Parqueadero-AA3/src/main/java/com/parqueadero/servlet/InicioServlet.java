package com.parqueadero.servlet;

import com.parqueadero.dao.ApartadoDAO;
import com.parqueadero.dao.EspacioDAO;
import com.parqueadero.dao.IngresoDAO;
import com.parqueadero.model.Espacio;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Pantalla principal: estado de los espacios, vehículos dentro y apartados activos. */
@WebServlet("/inicio")
public class InicioServlet extends HttpServlet {

    private final EspacioDAO espacioDAO = new EspacioDAO();
    private final IngresoDAO ingresoDAO = new IngresoDAO();
    private final ApartadoDAO apartadoDAO = new ApartadoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Espacio> espacios = espacioDAO.listar();
            int disponibles = 0, ocupados = 0, apartados = 0;
            for (Espacio e : espacios) {
                switch (e.getEstado()) {
                    case "DISPONIBLE": disponibles++; break;
                    case "OCUPADO": ocupados++; break;
                    default: apartados++; break;
                }
            }
            req.setAttribute("espacios", espacios);
            req.setAttribute("disponibles", disponibles);
            req.setAttribute("ocupados", ocupados);
            req.setAttribute("apartados", apartados);
            req.setAttribute("activos", ingresoDAO.listarActivos());
            req.setAttribute("listaApartados", apartadoDAO.listarActivos());
        } catch (SQLException ex) {
            req.setAttribute("errorBD", "Error de base de datos: " + ex.getMessage());
        }
        req.getRequestDispatcher("/inicio.jsp").forward(req, resp);
    }
}
