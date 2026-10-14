package com.parqueadero.util;

/** Error de una regla de negocio (por ejemplo: espacio ocupado). El mensaje se muestra al usuario. */
public class ReglaNegocioException extends Exception {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
