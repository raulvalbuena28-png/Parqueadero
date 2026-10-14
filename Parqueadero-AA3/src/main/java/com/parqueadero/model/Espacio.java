package com.parqueadero.model;

public class Espacio {
    private final int id;
    private final String codigo;
    private final String tipo;
    private final String estado;

    public Espacio(int id, String codigo, String tipo, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getTipo() { return tipo; }
    public String getEstado() { return estado; }
}
