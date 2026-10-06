package modelo;

import java.sql.Timestamp;

/** Un ingreso de vehículo al parqueadero (y su salida, cuando ocurre). */
public class Registro {

    private int id;
    private String placa;
    private String tipoVehiculo;   // CARRO o MOTO
    private Timestamp fechaIngreso;
    private Timestamp fechaSalida; // null mientras el vehículo siga dentro
    private double valorPagado;

    public Registro() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Timestamp getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Timestamp fechaSalida) { this.fechaSalida = fechaSalida; }

    public double getValorPagado() { return valorPagado; }
    public void setValorPagado(double valorPagado) { this.valorPagado = valorPagado; }

    public boolean isActivo() { return fechaSalida == null; }
}
