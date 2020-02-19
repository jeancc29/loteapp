package com.example.jean2.creta.Clases;

import java.util.List;

public class LoteriaClass {
    public int id;
    public String descripcion;
    public String abreviatura;
    public String primera;
    public String segunda;
    public String tercera;
    public String pick3;
    public String pick4;
    public List<SorteosClass> sorteos;
    public String horaCierre;

    public LoteriaClass(){}
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

    public String getPrimera() {
        return primera;
    }

    public void setPrimera(String primera) {
        this.primera = primera;
    }

    public String getSegunda() {
        return segunda;
    }

    public void setSegunda(String segunda) {
        this.segunda = segunda;
    }

    public String getTercera() {
        return tercera;
    }

    public void setTercera(String tercera) {
        this.tercera = tercera;
    }

    public String getPick3() {
        return pick3;
    }

    public void setPick3(String pick3) {
        this.pick3 = pick3;
    }

    public String getPick4() {
        return pick4;
    }

    public void setPick4(String pick4) {
        this.pick4 = pick4;
    }

    public List<SorteosClass> getSorteos() {
        return sorteos;
    }

    public void setSorteos(List<SorteosClass> sorteos) {
        this.sorteos = sorteos;
    }

    public String getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(String horaCierre) {
        this.horaCierre = horaCierre;
    }
}
