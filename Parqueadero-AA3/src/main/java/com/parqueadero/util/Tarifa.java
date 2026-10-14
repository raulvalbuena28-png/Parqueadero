package com.parqueadero.util;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/** Cálculo del cobro: se cobra por hora o fracción, con un mínimo de 1 hora. */
public final class Tarifa {

    private Tarifa() {
    }

    public static int calcularHoras(LocalDateTime ingreso, LocalDateTime salida) {
        long minutos = Duration.between(ingreso, salida).toMinutes();
        long horas = (minutos + 59) / 60;
        return (int) Math.max(1, horas);
    }

    public static BigDecimal calcularValor(BigDecimal tarifaHora, int horas) {
        return tarifaHora.multiply(BigDecimal.valueOf(horas)).setScale(2);
    }

    public static String permanencia(LocalDateTime ingreso, LocalDateTime salida) {
        long minutos = Math.max(0, Duration.between(ingreso, salida).toMinutes());
        return (minutos / 60) + " h " + (minutos % 60) + " min";
    }
}
