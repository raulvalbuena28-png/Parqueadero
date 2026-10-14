package com.parqueadero.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Conexión a la base de datos MySQL del parqueadero.
 * Ajuste USUARIO y CLAVE según su instalación de MySQL.
 */
public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/parqueadero"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";   // <-- cambie por su contraseña de MySQL

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private Conexion() {
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
