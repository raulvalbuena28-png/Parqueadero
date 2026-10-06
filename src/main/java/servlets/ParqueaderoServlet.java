package servlets;

import dao.RegistroDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import modelo.Registro;

/**
 * Servlet principal del parqueadero.
 *  - doGet : consultas (listar / buscar por placa / ver comprobante)
 *  - doPost: operaciones que modifican datos (registrar ingreso / registrar salida)
 */
@WebServlet(name = "ParqueaderoServlet", urlPatterns = {"/parqueadero"})
public class ParqueaderoServlet extends HttpServlet {

    private static final double TARIFA_CARRO_HORA = 3000;
    private static final double TARIFA_MOTO_HORA = 1500;

    private final RegistroDAO dao = new RegistroDAO();

    // ---------------------------------------------------------------- GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        if (accion == null) {
            accion = "listar";
        }

        try {
            if ("comprobante".equals(accion)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Registro registro = dao.buscarPorId(id);
                if (registro == null) {
                    request.setAttribute("error", "No existe el registro solicitado.");
                    cargarListado(request, null);
                    request.getRequestDispatcher("listado.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("registro", registro);
                request.getRequestDispatcher("comprobante.jsp").forward(request, response);
            } else {
                // "listar": muestra todo o filtra por la placa recibida
                String placa = limpiarPlaca(request.getParameter("placa"));
                cargarListado(request, placa);
                request.getRequestDispatcher("listado.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El identificador del registro no es válido.");
            request.getRequestDispatcher("listado.jsp").forward(request, response);
        } catch (SQLException e) {
            errorBaseDatos(request, response, e);
        }
    }

    // --------------------------------------------------------------- POST
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        String placa = limpiarPlaca(request.getParameter("placa"));

        try {
            if (!placa.matches("[A-Z0-9]{5,7}")) {
                request.setAttribute("error", "La placa debe tener entre 5 y 7 letras o números.");
                cargarListado(request, null);
                request.getRequestDispatcher("listado.jsp").forward(request, response);
                return;
            }

            if ("ingreso".equals(accion)) {
                registrarIngreso(request, response, placa);
            } else if ("salida".equals(accion)) {
                registrarSalida(request, response, placa);
            } else {
                request.setAttribute("error", "Acción no reconocida.");
                cargarListado(request, null);
                request.getRequestDispatcher("listado.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            errorBaseDatos(request, response, e);
        }
    }

    // ------------------------------------------------------ lógica interna
    private void registrarIngreso(HttpServletRequest request, HttpServletResponse response, String placa)
            throws SQLException, ServletException, IOException {
        String tipo = request.getParameter("tipo");
        if (!"CARRO".equals(tipo) && !"MOTO".equals(tipo)) {
            request.setAttribute("error", "Seleccione si el vehículo es carro o moto.");
        } else if (dao.buscarActivoPorPlaca(placa) != null) {
            request.setAttribute("error", "El vehículo " + placa + " ya está dentro del parqueadero.");
        } else {
            dao.registrarIngreso(placa, tipo);
            request.setAttribute("mensaje", "Ingreso registrado para la placa " + placa + ".");
        }
        cargarListado(request, null);
        request.getRequestDispatcher("listado.jsp").forward(request, response);
    }

    private void registrarSalida(HttpServletRequest request, HttpServletResponse response, String placa)
            throws SQLException, ServletException, IOException {
        Registro activo = dao.buscarActivoPorPlaca(placa);
        if (activo == null) {
            request.setAttribute("error", "No hay un vehículo con placa " + placa + " dentro del parqueadero.");
            cargarListado(request, null);
            request.getRequestDispatcher("listado.jsp").forward(request, response);
            return;
        }

        Timestamp salida = new Timestamp(System.currentTimeMillis());
        double valor = calcularValor(activo, salida);
        dao.registrarSalida(activo.getId(), salida, valor);

        request.setAttribute("registro", dao.buscarPorId(activo.getId()));
        request.getRequestDispatcher("comprobante.jsp").forward(request, response);
    }

    /** Cobra por hora o fracción (mínimo una hora). */
    private double calcularValor(Registro r, Timestamp salida) {
        long minutos = (salida.getTime() - r.getFechaIngreso().getTime()) / 60000;
        long horas = Math.max(1, (long) Math.ceil(minutos / 60.0));
        double tarifa = "MOTO".equals(r.getTipoVehiculo()) ? TARIFA_MOTO_HORA : TARIFA_CARRO_HORA;
        return horas * tarifa;
    }

    private void cargarListado(HttpServletRequest request, String placa) throws SQLException {
        List<Registro> registros = dao.listar(placa);
        request.setAttribute("registros", registros);
        request.setAttribute("filtroPlaca", placa == null ? "" : placa);
    }

    private String limpiarPlaca(String placa) {
        return placa == null ? "" : placa.trim().toUpperCase();
    }

    private void errorBaseDatos(HttpServletRequest request, HttpServletResponse response, SQLException e)
            throws ServletException, IOException {
        log("Error de base de datos", e);
        request.setAttribute("error", "No se pudo conectar con la base de datos. Revise MySQL y util/Conexion.java.");
        request.getRequestDispatcher("listado.jsp").forward(request, response);
    }
}
