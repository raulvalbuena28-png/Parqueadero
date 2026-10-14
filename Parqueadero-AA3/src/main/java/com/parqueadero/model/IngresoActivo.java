package com.parqueadero.model;

import com.parqueadero.util.Fechas;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Vehículo que se encuentra actualmente dentro del parqueadero. */
public class IngresoActivo {
    private final int idIngreso;
    private final int idEspacio;
    private final String placa;
    private final String tipo;
    private final String codigoEspacio;
    private final LocalDateTime fechaIngreso;
    private final BigDecimal tarifaHora;

    public IngresoActivo(int idIngreso, int idEspacio, String placa, String tipo,
                         String codigoEspacio, LocalDateTime fechaIngreso, BigDecimal tarifaHora) {
        this.idIngreso = idIngreso;
        this.idEspacio = idEspacio;
        this.placa = placa;
        this.tipo = tipo;
        this.codigoEspacio = codigoEspacio;
        this.fechaIngreso = fechaIngreso;
        this.tarifaHora = tarifaHora;
    }

    public int getIdIngreso() { return idIngreso; }
    public int getIdEspacio() { return idEspacio; }
    public String getPlaca() { return placa; }
    public String getTipo() { return tipo; }
    public String getCodigoEspacio() { return codigoEspacio; }
    public LocalDateTime getFechaIngreso() { return fechaIngreso; }
    public BigDecimal getTarifaHora() { return tarifaHora; }
    public String getFechaIngresoTexto() { return Fechas.texto(fechaIngreso); }
    public String getTarifaHoraTexto() { return tarifaHora.toPlainString(); }
}
