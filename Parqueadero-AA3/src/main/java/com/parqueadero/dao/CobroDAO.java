package com.parqueadero.dao;

import com.parqueadero.util.Conexion;
import com.parqueadero.util.Fechas;
import com.parqueadero.util.ReglaNegocioException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class CobroDAO {

    /**
     * Registra la salida y el cobro en una sola transacción:
     * pone la fecha de salida, guarda el pago y libera el espacio.
     * @return id del cobro creado
     */
    public int registrar(int idIngreso, LocalDateTime salida, int horas, BigDecimal total,
                         BigDecimal recibido, String metodo) throws SQLException, ReglaNegocioException {
        try (Connection c = Conexion.getConexion()) {
            c.setAutoCommit(false);
            try {
                int idEspacio;
                LocalDateTime ingreso;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT id_espacio, fecha_ingreso FROM ingresos "
                        + "WHERE id_ingreso = ? AND fecha_salida IS NULL FOR UPDATE")) {
                    ps.setInt(1, idIngreso);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new ReglaNegocioException("El ingreso no existe o ya fue cobrado");
                        }
                        idEspacio = rs.getInt(1);
                        ingreso = rs.getTimestamp(2).toLocalDateTime();
                    }
                }
                if (salida.isBefore(ingreso)) {
                    throw new ReglaNegocioException("La fecha de salida no puede ser anterior a la de ingreso");
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE ingresos SET fecha_salida = ? WHERE id_ingreso = ?")) {
                    ps.setTimestamp(1, Timestamp.valueOf(salida));
                    ps.setInt(2, idIngreso);
                    ps.executeUpdate();
                }
                int idCobro;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO cobros (id_ingreso, horas_cobradas, valor_total, valor_recibido, cambio, metodo_pago) "
                        + "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idIngreso);
                    ps.setInt(2, horas);
                    ps.setBigDecimal(3, total);
                    ps.setBigDecimal(4, recibido);
                    ps.setBigDecimal(5, recibido.subtract(total));
                    ps.setString(6, metodo);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        idCobro = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE espacios SET estado = 'DISPONIBLE' WHERE id_espacio = ?")) {
                    ps.setInt(1, idEspacio);
                    ps.executeUpdate();
                }
                c.commit();
                return idCobro;
            } catch (SQLException | ReglaNegocioException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Datos del comprobante de un cobro (o null si no existe). */
    public Map<String, String> obtenerComprobante(int idCobro) throws SQLException {
        String sql = "SELECT c.id_cobro, v.placa, t.nombre, e.codigo, i.fecha_ingreso, i.fecha_salida, "
                + "c.horas_cobradas, c.valor_total, c.valor_recibido, c.cambio, c.metodo_pago, c.fecha_pago "
                + "FROM cobros c "
                + "JOIN ingresos i ON i.id_ingreso = c.id_ingreso "
                + "JOIN vehiculos v ON v.id_vehiculo = i.id_vehiculo "
                + "JOIN tipos_vehiculo t ON t.id_tipo = v.id_tipo "
                + "JOIN espacios e ON e.id_espacio = i.id_espacio "
                + "WHERE c.id_cobro = ?";
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCobro);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Map<String, String> m = new LinkedHashMap<>();
                m.put("id", String.valueOf(rs.getInt(1)));
                m.put("placa", rs.getString(2));
                m.put("tipo", rs.getString(3));
                m.put("espacio", rs.getString(4));
                m.put("ingreso", Fechas.texto(rs.getTimestamp(5).toLocalDateTime()));
                m.put("salida", Fechas.texto(rs.getTimestamp(6).toLocalDateTime()));
                m.put("horas", String.valueOf(rs.getInt(7)));
                m.put("total", rs.getBigDecimal(8).toPlainString());
                m.put("recibido", rs.getBigDecimal(9).toPlainString());
                m.put("cambio", rs.getBigDecimal(10).toPlainString());
                m.put("metodo", rs.getString(11));
                m.put("pago", Fechas.texto(rs.getTimestamp(12).toLocalDateTime()));
                return m;
            }
        }
    }
}
