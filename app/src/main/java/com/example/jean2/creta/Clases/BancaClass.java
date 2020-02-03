package com.example.jean2.creta.Clases;

public class BancaClass {
    public int id;
    public String descripcion;
    public String dueno;
    public String usuario;
    public String piepagina1;
    public String piepagina2;
    public String piepagina3;
    public String piepagina4;
    public int imprimirCodigoQr;
    public double descontar;
    public double deCada;
    public double ventas;
    public double comisiones;
    public double descuentos;
    public double neto;
    public double balance;
    public double balanceActual;
    public double premios;
    public double prestamo;
    public int ticketsPendientes;

    public BancaClass(){}

    public BancaClass(int id, String descripcion, String piepagina1, String piepagina2, String piepagina3, String piepagina4, int imprimirCodigoQr, double descontar, double deCada) {
        this.id = id;
        this.descripcion = descripcion;
        this.piepagina1 = piepagina1;
        this.piepagina2 = piepagina2;
        this.piepagina3 = piepagina3;
        this.piepagina4 = piepagina4;
        this.imprimirCodigoQr = imprimirCodigoQr;
        this.descontar = descontar;
        this.deCada = deCada;
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

    public String getPiepagina1() {
        return piepagina1;
    }

    public void setPiepagina1(String piepagina1) {
        this.piepagina1 = piepagina1;
    }

    public String getPiepagina2() {
        return piepagina2;
    }

    public void setPiepagina2(String piepagina2) {
        this.piepagina2 = piepagina2;
    }

    public String getPiepagina3() {
        return piepagina3;
    }

    public void setPiepagina3(String piepagina3) {
        this.piepagina3 = piepagina3;
    }

    public String getPiepagina4() {
        return piepagina4;
    }

    public void setPiepagina4(String piepagina4) {
        this.piepagina4 = piepagina4;
    }

    public int getImprimirCodigoQr() {
        return imprimirCodigoQr;
    }

    public void setImprimirCodigoQr(int imprimirCodigoQr) {
        this.imprimirCodigoQr = imprimirCodigoQr;
    }

    public double getDescontar() {
        return descontar;
    }

    public void setDescontar(double descontar) {
        this.descontar = descontar;
    }

    public double getDeCada() {
        return deCada;
    }

    public void setDeCada(double deCada) {
        this.deCada = deCada;
    }

    public double getVentas() {
        return ventas;
    }

    public void setVentas(double ventas) {
        this.ventas = ventas;
    }

    public double getComisiones() {
        return comisiones;
    }

    public void setComisiones(double comisiones) {
        this.comisiones = comisiones;
    }

    public double getDescuentos() {
        return descuentos;
    }

    public void setDescuentos(double descuentos) {
        this.descuentos = descuentos;
    }

    public double getNeto() {
        return neto;
    }

    public void setNeto(double neto) {
        this.neto = neto;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalanceActual() {
        return balanceActual;
    }

    public void setBalanceActual(double balanceActual) {
        this.balanceActual = balanceActual;
    }

    public double getPremios() {
        return premios;
    }

    public void setPremios(double premios) {
        this.premios = premios;
    }

    public int getTicketsPendientes() {
        return ticketsPendientes;
    }

    public void setTicketsPendientes(int ticketsPendientes) {
        this.ticketsPendientes = ticketsPendientes;
    }

    public String getDueno() {
        return dueno;
    }

    public void setDueno(String dueno) {
        this.dueno = dueno;
    }

    public double getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(double prestamo) {
        this.prestamo = prestamo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
