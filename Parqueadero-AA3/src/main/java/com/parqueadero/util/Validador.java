package com.parqueadero.util;

import java.util.regex.Pattern;

/** Validaciones de los datos de entrada. Cada método devuelve el mensaje de error o null si es válido. */
public final class Validador {

    private static final Pattern NOMBRE = Pattern.compile("^[A-Za-zÁÉÍÓÚÜáéíóúüÑñ ]+$");
    private static final Pattern MONTO = Pattern.compile("^\\d{1,8}(\\.\\d{1,2})?$");

    private Validador() {
    }

    public static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }

    /** Placa: obligatoria, solo letras y números, de 5 a 6 caracteres. */
    public static String validarPlaca(String placa) {
        if (placa.isEmpty()) {
            return "La placa es obligatoria";
        }
        if (!placa.matches("[A-Za-z0-9]+")) {
            return "La placa solo admite letras y números (sin espacios ni caracteres especiales)";
        }
        if (placa.length() < 5) {
            return "La placa debe tener mínimo 5 caracteres";
        }
        if (placa.length() > 6) {
            return "La placa debe tener máximo 6 caracteres";
        }
        return null;
    }

    /** Nombre: obligatorio, solo letras y espacios, de 3 a 50 caracteres. */
    public static String validarNombre(String nombre) {
        if (nombre.isEmpty()) {
            return "El nombre del cliente es obligatorio";
        }
        if (!NOMBRE.matcher(nombre).matches()) {
            return "El nombre solo admite letras y espacios (sin números ni caracteres especiales)";
        }
        if (nombre.length() < 3) {
            return "El nombre debe tener mínimo 3 caracteres";
        }
        if (nombre.length() > 50) {
            return "El nombre debe tener máximo 50 caracteres";
        }
        return null;
    }

    /** Monto: obligatorio, numérico, positivo, hasta 8 enteros y 2 decimales. */
    public static String validarMonto(String s) {
        if (s.isEmpty()) {
            return "Ingrese el valor recibido";
        }
        if (s.startsWith("-")) {
            return "No se admiten valores negativos";
        }
        if (!s.matches("[0-9.]+")) {
            return "El valor solo admite números (sin letras ni caracteres especiales)";
        }
        if (!MONTO.matcher(s).matches()) {
            String[] p = s.split("\\.", -1);
            if (p.length > 2 || p[0].isEmpty() || (p.length == 2 && p[1].isEmpty())) {
                return "Formato numérico inválido";
            }
            if (p[0].length() > 8) {
                return "El valor supera la longitud máxima (8 dígitos enteros)";
            }
            return "El valor admite máximo 2 decimales";
        }
        return null;
    }
}
