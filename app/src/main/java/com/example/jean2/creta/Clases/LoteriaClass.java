package com.example.jean2.creta.Clases;

public class LoteriaClass {
    public int id;
    public String descripcion;
    public String abreviatura;

    public LoteriaClass(int id, String descripcion, String abreviatura) {
        this.id = id;
        this.descripcion = descripcion;
        this.abreviatura = abreviatura;
    }

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

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }
}
