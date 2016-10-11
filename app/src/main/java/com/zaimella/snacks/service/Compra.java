package com.zaimella.snacks.service;

import java.util.Date;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class Compra {
    private int idcpr;
    private String cedula;
    private Float valorCompra;
    private String comentario;
    private Date fecha;
    private TiposRespuesta estado;

    public Compra(){
        estado = TiposRespuesta.NO_SINCRONIZADO;
    }

    public Compra(int idcpr, String cedula, Float valorcompra, String comentario){
        this.cedula = cedula;
        this.valorCompra = valorcompra;
        this.comentario = comentario;
        estado = TiposRespuesta.NO_SINCRONIZADO;
    }

    public int getIdcpr() {
        return idcpr;
    }

    public void setIdcpr(int idcpr) {
        this.idcpr = idcpr;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Float getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(Float valorCompra) {
        this.valorCompra = valorCompra;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public TiposRespuesta getEstado() {
        return estado;
    }

    public void setEstado(TiposRespuesta estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idcpr=" + idcpr +
                ", cedula='" + cedula + '\'' +
                ", valorCompra=" + valorCompra +
                ", comentario='" + comentario + '\'' +
                ", estado=" + estado +
                '}';
    }
}
