package com.example.jean2.creta.Clases;

import java.util.List;

public class SorteosClass {
    public int id;
    public String descripcion;
    public List<JugadaClass> jugadas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<JugadaClass> getJugadas() {
        return jugadas;
    }

    public void setJugadas(List<JugadaClass> jugadas) {
        this.jugadas = jugadas;
    }
}
