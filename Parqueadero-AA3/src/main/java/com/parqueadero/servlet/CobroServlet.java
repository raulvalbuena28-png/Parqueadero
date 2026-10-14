package com.parqueadero.servlet;

import com.parqueadero.dao.CobroDAO;
import com.parqueadero.dao.IngresoDAO;
import com.parqueadero.model.IngresoActivo;
import com.parqueadero.util.Fechas;
import com.parqueadero.util.ReglaNegocioException;
import com.parqueadero.util.Tarifa;
import com.parqueadero.util.Validador;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HU-03: Calcular y registrar el cobro.
 * POST registra el pago (y la salida); GET con ?id= muestra el comprobante.
 */
@WebServlet("/cobro")
public class CobroServlet extends HttpServlet {

    private final IngresoDAO ingresoDAO = new IngresoDAO();
    private final CobroDAO cobroDAO = new CobroDAO();

    /** Calcula el resumen del cobro y lo deja en la request para cobro.jsp. */
    static void cargarResumen(HttpServletRequest req, IngresoActivo ingreso, LocalDateTime salida) {
        int horas = Tarifa.calcularHoras(ingreso.getFechaIngreso(), salida);
        BigDecimal total = Tarifa.calcularValor(ingreso.getTarifaHora(), horas);
        req.setAttribute("ingreso", ingreso);
        req.setAttribute("fechaSalidaTxt", Fechas.texto(salida));
        req.setAttribute("permanencia", Tarifa.permanencia(ingreso.getFechaIngreso(), salida));
        req.setAttribute("horas", horas);
        req.setAttribute("total", total.toPlainString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(Validador.limpiar(req.getParameter("id")));
            Map<String, String> comprobante = cobroDAO.obtenerComprobante(id);
            if (comprobante != null) {
                req.setAttribute("c", comprobante);
                req.getRequestDispatcher("/comprobante.jsp").forward(req, resp);
                return;
            }
        } catch (NumberFormatException | SQLException ex) {
            // se redirige al inicio
        }
        resp.sendRedirect(req.getContextPath() + "/inicio");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String sId = Validador.limpiar(req.getParameter("idIngreso"));
        String fechaTxt = Validador.limpiar(req.getParameter("fechaSalida"));
        String recibidoTxt = Validador.limpiar(req.getParameter("valorRecibido"));
        String metodo = Validador.limpiar(req.getParameter("metodo"));

        IngresoActivo ingreso;
        LocalDateTime salida;
        try {
            ingreso = ingresoDAO.buscarActivoPorId(Integer.parseInt(sId));
            salida = LocalDateTime.parse(fechaTxt, Fechas.SEGUNDO);
        } catch (NumberFormatException | DateTimeParseException ex) {
            resp.sendRedirect(req.getContextPath() + "/salida");
            return;
        } catch (SQLException ex) {
            req.setAttribute("errorBD", "Error de base de datos: " + ex.getMessage());
            req.getRequestDispatcher("/salida.jsp").forward(req, resp);
            return;
        }
        if (ingreso == null) {
            List<String> errores = new ArrayList<>();
            errores.add("El ingreso no existe o ya fue cobrado");
            req.setAttribute("errores", errores);
            req.getRequestDispatcher("/salida.jsp").forward(req, resp);
            return;
        }

        cargarResumen(req, ingreso, salida);
        int horas = Tarifa.calcularHoras(ingreso.getFechaIngreso(), salida);
        BigDecimal total = Tarifa.calcularValor(ingreso.getTarifaHora(), horas);

        List<String> errores = new ArrayList<>();
        BigDecimal recibido = null;

        String errMonto = Validador.validarMonto(recibidoTxt);
        if (errMonto != null) {
            errores.add(errMonto);
        } else {
            recibido = new BigDecimal(recibidoTxt);
            if (recibido.signum() <= 0) {
                errores.add("El valor recibido debe ser mayor a cero");
                recibido = null;
            } else if (recibido.compareTo(total) < 0) {
                errores.add("Valor insuficiente: el valor recibido es menor al valor a pagar ($" + total.toPlainString() + ")");
                recibido = null;
            }
        }
        if (!metodo.equals("EFECTIVO") && !metodo.equals("TARJETA") && !metodo.equals("TRANSFERENCIA")) {
            errores.add("Seleccione el método de pago");
        }

        if (errores.isEmpty()) {
            try {
                int idCobro = cobroDAO.registrar(ingreso.getIdIngreso(), salida, horas, total, recibido, metodo);
                resp.sendRedirect(req.getContextPath() + "/cobro?id=" + idCobro);
                return;
            } catch (ReglaNegocioException ex) {
                errores.add(ex.getMessage());
            } catch (SQLException ex) {
                errores.add("Error de base de datos: " + ex.getMessage());
            }
        }

        req.setAttribute("errores", errores);
        req.setAttribute("valorRecibido", recibidoTxt);
        req.setAttribute("metodo", metodo);
        req.getRequestDispatcher("/cobro.jsp").forward(req, resp);
    }
}
