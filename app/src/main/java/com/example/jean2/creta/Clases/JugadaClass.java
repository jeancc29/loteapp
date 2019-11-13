package com.example.jean2.creta.Clases;

import com.example.jean2.creta.Utilidades;

import java.math.BigInteger;

public class JugadaClass {
    public BigInteger id;
    public BigInteger idVenta;
    public String jugada;
    public int idBanca;
    public int idLoteria;
    public int idSorteo;
    public double monto;
    public double premio;
    public int pagado;
    public int status;
    public String sorteo;
//    public String fechaPagado;
    public String pagadoPor;
    public String descripcion;



    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(BigInteger idVenta) {
        this.idVenta = idVenta;
    }

    public String getJugada() {
        return jugada;
    }

    public void setJugada(String jugada) {
        this.jugada = jugada;
    }

    public int getIdLoteria() {
        return idLoteria;
    }

    public void setIdLoteria(int idLoteria) {
        this.idLoteria = idLoteria;
    }

    public int getIdSorteo() {
        return idSorteo;
    }

    public void setIdSorteo(int idSorteo) {
        this.idSorteo = idSorteo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getPremio() {
        return premio;
    }

    public void setPremio(double premio) {
        this.premio = premio;
    }

    public int isPagado() {
        return pagado;
    }

    public void setPagado(int pagado) {
        this.pagado = pagado;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSorteo() {
        return sorteo;
    }

    public void setSorteo(String sorteo) {
        this.sorteo = sorteo;
    }

//    public String getFechaPagado() {
//        return fechaPagado;
//    }

//    public void setFechaPagado(String fechaPagado) {
//        this.fechaPagado = fechaPagado;
//    }

    public String getPagadoPor() {
        return pagadoPor;
    }

    public void setPagadoPor(String pagadoPor) {
        this.pagadoPor = pagadoPor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdBanca() {
        return idBanca;
    }

    public void setIdBanca(int idBanca) {
        this.idBanca = idBanca;
    }
}
