package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import modelo.Registro;
import util.Conexion;

/** Acceso a datos de la tabla registro_parqueo. */
public class RegistroDAO {

    /** Registra el ingreso de un vehículo y devuelve el id generado. */
    public int registrarIngreso(String placa, String tipo) throws SQLException {
        String sql = "INSERT INTO registro_parqueo (placa, tipo_vehiculo, fecha_ingreso) VALUES (?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, placa);
            ps.setString(2, tipo);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Busca el registro sin salida (vehículo dentro) de una placa. */
    public Registro buscarActivoPorPlaca(String placa) throws SQLException {
        String sql = "SELECT * FROM registro_parqueo WHERE placa = ? AND fecha_salida IS NULL "
                   + "ORDER BY id DESC LIMIT 1";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, placa);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Cierra el registro con la hora de salida y el valor a pagar. */
    public boolean registrarSalida(int id, Timestamp salida, double valor) throws SQLException {
        String sql = "UPDATE registro_parqueo SET fecha_salida = ?, valor_pagado = ? WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, salida);
            ps.setDouble(2, valor);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Registro buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM registro_parqueo WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Lista todos los registros; si placa trae texto, filtra por esa placa. */
    public List<Registro> listar(String placa) throws SQLException {
        boolean filtrar = placa != null && !placa.isEmpty();
        String sql = "SELECT * FROM registro_parqueo "
                   + (filtrar ? "WHERE placa LIKE ? " : "")
                   + "ORDER BY id DESC";
        List<Registro> lista = new ArrayList<>();
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (filtrar) {
                ps.setString(1, "%" + placa + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    private Registro mapear(ResultSet rs) throws SQLException {
        Registro r = new Registro();
        r.setId(rs.getInt("id"));
        r.setPlaca(rs.getString("placa"));
        r.setTipoVehiculo(rs.getString("tipo_vehiculo"));
        r.setFechaIngreso(rs.getTimestamp("fecha_ingreso"));
        r.setFechaSalida(rs.getTimestamp("fecha_salida"));
        r.setValorPagado(rs.getDouble("valor_pagado"));
        return r;
    }
}
