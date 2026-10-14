package com.parqueadero.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Formatos de fecha/hora estrictos (rechazan fechas inexistentes como 30/02). */
public final class Fechas {

    public static final DateTimeFormatter MINUTO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter SEGUNDO =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    public static final DateTimeFormatter HORA =
            DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private Fechas() {
    }

    public static String texto(LocalDateTime f) {
        return f.format(SEGUNDO);
    }
}
