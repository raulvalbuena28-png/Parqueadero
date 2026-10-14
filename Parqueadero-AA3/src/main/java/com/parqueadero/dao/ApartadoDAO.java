package com.parqueadero.dao;

import com.parqueadero.util.Conexion;
import com.parqueadero.util.ReglaNegocioException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApartadoDAO {

    /** Registra el apartado y marca el espacio como APARTADO (en una sola transacción). */
    public void registrar(String nombre, String placa, int idEspacio, LocalDate fecha, LocalTime hora)
            throws SQLException, ReglaNegocioException {
        try (Connection c = Conexion.getConexion()) {
            c.setAutoCommit(false);
            try {
                String estado;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT estado FROM espacios WHERE id_espacio = ? FOR UPDATE")) {
                    ps.setInt(1, idEspacio);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new ReglaNegocioException("El espacio seleccionado no existe");
                        }
                        estado = rs.getString(1);
                    }
                }
                if (!"DISPONIBLE".equals(estado)) {
                    throw new ReglaNegocioException("El espacio no está disponible (está ocupado o ya apartado)");
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO apartados (nombre_cliente, placa, id_espacio, fecha_apartado, hora_apartado) "
                        + "VALUES (?, ?, ?, ?, ?)")) {
                    ps.setString(1, nombre);
                    ps.setString(2, placa);
                    ps.setInt(3, idEspacio);
                    ps.setDate(4, Date.valueOf(fecha));
                    ps.setTime(5, Time.valueOf(hora));
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE espacios SET estado = 'APARTADO' WHERE id_espacio = ?")) {
                    ps.setInt(1, idEspacio);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | ReglaNegocioException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public List<Map<String, String>> listarActivos() throws SQLException {
        String sql = "SELECT a.nombre_cliente, a.placa, e.codigo, a.fecha_apartado, a.hora_apartado "
                + "FROM apartados a JOIN espacios e ON e.id_espacio = a.id_espacio "
                + "WHERE a.estado = 'ACTIVO' ORDER BY a.fecha_apartado, a.hora_apartado";
        List<Map<String, String>> lista = new ArrayList<>();
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("nombre", rs.getString(1));
                m.put("placa", rs.getString(2));
                m.put("espacio", rs.getString(3));
                m.put("fecha", rs.getDate(4).toLocalDate().toString());
                m.put("hora", rs.getTime(5).toLocalTime().toString());
                lista.add(m);
            }
        }
        return lista;
    }
}
