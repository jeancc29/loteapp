package com.example.jean2.creta.Clases;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigInteger;
import java.util.List;

public class VentasClass implements Parcelable {
    public BigInteger id;
    public double total;
    public int idUsuario;
    public String usuario;
    public int idBanca;
    public String codigo;
    public int descuentoPorcentaje;
    public double descuentoMonto;
    public int hayDescuento;
    public double subTotal;
    public BigInteger idTicket;
    public BigInteger ticket;
    public String codigoBarra;
    public String codigoQr;
    public int status;
//    public String created_at;
    public int pagado;
    public double montoPagado;
    public double premio;
    public double montoAPagar;
    public String razon;
    //public JsonObject usuarioCancelacion;
//    public String fechaCancelacion;
    public List<LoteriaClass> loterias;
    public List<JugadaClass> jugadas;
    public String fecha;
    public BancaClass banca;
    //public JsonObject usuarioObject;
    String img;


    public VentasClass(BigInteger id, double total, int idUsuario, String usuario, int idBanca, String codigo, int descuentoPorcentaje, double descuentoMonto, int hayDescuento, double subTotal, BigInteger idTicket, BigInteger ticket, String codigoBarra, String codigoQr, int status, int pagado, double montoPagado, double premio, double montoAPagar, String razon, List<LoteriaClass> loterias, List<JugadaClass> jugadas, String fecha, BancaClass banca, String img) {
        this.id = id;
        this.total = total;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.idBanca = idBanca;
        this.codigo = codigo;
        this.descuentoPorcentaje = descuentoPorcentaje;
        this.descuentoMonto = descuentoMonto;
        this.hayDescuento = hayDescuento;
        this.subTotal = subTotal;
        this.idTicket = idTicket;
        this.ticket = ticket;
        this.codigoBarra = codigoBarra;
        this.codigoQr = codigoQr;
        this.status = status;
//        this.created_at = created_at;
        this.pagado = pagado;
        this.montoPagado = montoPagado;
        this.premio = premio;
        this.montoAPagar = montoAPagar;
        this.razon = razon;
//        this.fechaCancelacion = fechaCancelacion;
        this.loterias = loterias;
        this.jugadas = jugadas;
        this.fecha = fecha;
        this.banca = banca;
        this.img = img;
    }

    protected VentasClass(Parcel in) {
        total = in.readDouble();
        idUsuario = in.readInt();
        usuario = in.readString();
        idBanca = in.readInt();
        codigo = in.readString();
        descuentoPorcentaje = in.readInt();
        descuentoMonto = in.readDouble();
        hayDescuento = in.readInt();
        subTotal = in.readDouble();
        codigoBarra = in.readString();
        codigoQr = in.readString();
        status = in.readInt();
        pagado = in.readInt();
        montoPagado = in.readDouble();
        premio = in.readDouble();
        montoAPagar = in.readDouble();
        razon = in.readString();
//        fechaCancelacion = in.readString();
        fecha = in.readString();
    }

    public static final Creator<VentasClass> CREATOR = new Creator<VentasClass>() {
        @Override
        public VentasClass createFromParcel(Parcel in) {
            return new VentasClass(in);
        }

        @Override
        public VentasClass[] newArray(int size) {
            return new VentasClass[size];
        }
    };

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public int getIdBanca() {
        return idBanca;
    }

    public void setIdBanca(int idBanca) {
        this.idBanca = idBanca;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(int descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public double getDescuentoMonto() {
        return descuentoMonto;
    }

    public void setDescuentoMonto(double descuentoMonto) {
        this.descuentoMonto = descuentoMonto;
    }

    public int isHayDescuento() {
        return hayDescuento;
    }

    public void setHayDescuento(int hayDescuento) {
        this.hayDescuento = hayDescuento;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public BigInteger getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(BigInteger idTicket) {
        this.idTicket = idTicket;
    }

    public BigInteger getTicket() {
        return ticket;
    }

    public void setTicket(BigInteger ticket) {
        this.ticket = ticket;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

//    public String getCreated_at() {
//        return created_at;
//    }
//
//    public void setCreated_at(String created_at) {
//        this.created_at = created_at;
//    }

    public int isPagado() {
        return pagado;
    }

    public void setPagado(int pagado) {
        this.pagado = pagado;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public double getPremio() {
        return premio;
    }

    public void setPremio(double premio) {
        this.premio = premio;
    }

    public double getMontoAPagar() {
        return montoAPagar;
    }

    public void setMontoAPagar(double montoAPagar) {
        this.montoAPagar = montoAPagar;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }





    public List<LoteriaClass> getLoterias() {
        return loterias;
    }

    public void setLoterias(List<LoteriaClass> loterias) {
        this.loterias = loterias;
    }

    public List<JugadaClass> getJugadas() {
        return jugadas;
    }

    public void setJugadas(List<JugadaClass> jugadas) {
        this.jugadas = jugadas;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BancaClass getBanca() {
        return banca;
    }

    public void setBanca(BancaClass banca) {
        this.banca = banca;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(total);
        parcel.writeInt(idUsuario);
        parcel.writeString(usuario);
        parcel.writeInt(idBanca);
        parcel.writeString(codigo);
        parcel.writeInt(descuentoPorcentaje);
        parcel.writeDouble(descuentoMonto);
        parcel.writeInt(hayDescuento);
        parcel.writeDouble(subTotal);
        parcel.writeString(codigoBarra);
        parcel.writeString(codigoQr);
        parcel.writeInt(status);
        parcel.writeInt(pagado);
        parcel.writeDouble(montoPagado);
        parcel.writeDouble(premio);
        parcel.writeDouble(montoAPagar);
        parcel.writeString(razon);
        //parcel.writeString(fechaCancelacion);
        parcel.writeString(fecha);
        parcel.writeString(img);
    }
}
