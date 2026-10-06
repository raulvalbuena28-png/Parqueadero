package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Entrega conexiones JDBC a MySQL. Ajuste USUARIO y CLAVE según su servidor. */
public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/parqueadero_servlets"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota";
    private static final String USUARIO = "root";
    private static final String CLAVE = "R4ull2026*";

    public static Connection getConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
