package com.parqueadero.dao;

import com.parqueadero.model.IngresoActivo;
import com.parqueadero.util.Conexion;
import com.parqueadero.util.ReglaNegocioException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IngresoDAO {

    private static final String BASE =
            "SELECT i.id_ingreso, i.id_espacio, v.placa, t.nombre, e.codigo, i.fecha_ingreso, t.tarifa_hora "
            + "FROM ingresos i "
            + "JOIN vehiculos v ON v.id_vehiculo = i.id_vehiculo "
            + "JOIN tipos_vehiculo t ON t.id_tipo = v.id_tipo "
            + "JOIN espacios e ON e.id_espacio = i.id_espacio "
            + "WHERE i.fecha_salida IS NULL ";

    private IngresoActivo mapear(ResultSet rs) throws SQLException {
        return new IngresoActivo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                rs.getString(5), rs.getTimestamp(6).toLocalDateTime(), rs.getBigDecimal(7));
    }

    public List<IngresoActivo> listarActivos() throws SQLException {
        List<IngresoActivo> lista = new ArrayList<>();
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(BASE + "ORDER BY i.fecha_ingreso");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public IngresoActivo buscarActivoPorPlaca(String placa) throws SQLException {
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(BASE + "AND v.placa = ?")) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public IngresoActivo buscarActivoPorId(int idIngreso) throws SQLException {
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(BASE + "AND i.id_ingreso = ?")) {
            ps.setInt(1, idIngreso);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Registra el ingreso de un vehículo y marca el espacio como OCUPADO (en una sola transacción). */
    public void registrar(String placa, String tipoNombre, int idEspacio)
            throws SQLException, ReglaNegocioException {
        try (Connection c = Conexion.getConexion()) {
            c.setAutoCommit(false);
            try {
                int idTipo = obtenerIdTipo(c, tipoNombre);
                int idVehiculo = obtenerOCrearVehiculo(c, placa, idTipo);

                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT 1 FROM ingresos WHERE id_vehiculo = ? AND fecha_salida IS NULL")) {
                    ps.setInt(1, idVehiculo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new ReglaNegocioException("El vehículo ya tiene un ingreso activo");
                        }
                    }
                }

                int tipoEspacio;
                String estado;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT id_tipo, estado FROM espacios WHERE id_espacio = ? FOR UPDATE")) {
                    ps.setInt(1, idEspacio);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new ReglaNegocioException("El espacio seleccionado no existe");
                        }
                        tipoEspacio = rs.getInt(1);
                        estado = rs.getString(2);
                    }
                }
                if (!"DISPONIBLE".equals(estado)) {
                    throw new ReglaNegocioException("El espacio no está disponible (está ocupado o apartado)");
                }
                if (tipoEspacio != idTipo) {
                    throw new ReglaNegocioException("El espacio no corresponde al tipo de vehículo");
                }

                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO ingresos (id_vehiculo, id_espacio) VALUES (?, ?)")) {
                    ps.setInt(1, idVehiculo);
                    ps.setInt(2, idEspacio);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE espacios SET estado = 'OCUPADO' WHERE id_espacio = ?")) {
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

    private int obtenerIdTipo(Connection c, String nombre) throws SQLException, ReglaNegocioException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id_tipo FROM tipos_vehiculo WHERE nombre = ?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ReglaNegocioException("Tipo de vehículo no válido");
                }
                return rs.getInt(1);
            }
        }
    }

    private int obtenerOCrearVehiculo(Connection c, String placa, int idTipo) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT id_vehiculo FROM vehiculos WHERE placa = ?")) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    try (PreparedStatement up = c.prepareStatement(
                            "UPDATE vehiculos SET id_tipo = ? WHERE id_vehiculo = ?")) {
                        up.setInt(1, idTipo);
                        up.setInt(2, id);
                        up.executeUpdate();
                    }
                    return id;
                }
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO vehiculos (placa, id_tipo) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, placa);
            ps.setInt(2, idTipo);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
