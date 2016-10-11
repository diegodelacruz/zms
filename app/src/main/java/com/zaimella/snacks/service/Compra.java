package com.zaimella.snacks.service;

import java.util.Date;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class Compra {
    private int idcpr;
    private String cedula;
    private String valorCompra;
    private String comentario;
    private Date fecha;
    private String estado;

    public Compra(){
        estado = TiposRespuesta.NO_SINCRONIZADO.toString();
    }

    public Compra(int idcpr, String cedula, String valorcompra, String comentario){
        this.cedula = cedula;
        this.valorCompra = valorcompra;
        this.comentario = comentario;
        estado = TiposRespuesta.NO_SINCRONIZADO.toString();
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

    public String getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(String valorCompra) {
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
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
