package com.parqueadero.dao;

import com.parqueadero.model.Espacio;
import com.parqueadero.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspacioDAO {

    private static final String BASE =
            "SELECT e.id_espacio, e.codigo, t.nombre, e.estado "
            + "FROM espacios e JOIN tipos_vehiculo t ON t.id_tipo = e.id_tipo ";

    public List<Espacio> listar() throws SQLException {
        return consultar(BASE + "ORDER BY e.codigo");
    }

    public List<Espacio> listarDisponibles() throws SQLException {
        return consultar(BASE + "WHERE e.estado = 'DISPONIBLE' ORDER BY e.codigo");
    }

    private List<Espacio> consultar(String sql) throws SQLException {
        List<Espacio> lista = new ArrayList<>();
        try (Connection c = Conexion.getConexion();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Espacio(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        }
        return lista;
    }
}
