package com.zaimella.snacks.service;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class Compra {
    private int idcpr;
    private String cedula;
    private Double valorCompra;
    private String comentario;
    private TiposRespuesta estado;

    public Compra(){
        estado = TiposRespuesta.NO_SINCRONIZADO;
    }

    public Compra(int idcpr, String cedula, Double valorcompra, String comentario){
        this.cedula = cedula;
        this.valorCompra = valorcompra;
        this.comentario = comentario;
        estado = TiposRespuesta.NO_SINCRONIZADO;
    }

    public void setIdcpr(int idcpr) {
        this.idcpr = idcpr;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setValorCompra(Double valorCompra) {
        this.valorCompra = valorCompra;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setEstado(TiposRespuesta estado) {
        this.estado = estado;
    }

    public int getIdcpr() {
        return idcpr;
    }

    public String getCedula() {
        return cedula;
    }

    public Double getValorCompra() {
        return valorCompra;
    }

    public String getComentario() {
        return comentario;
    }

    public TiposRespuesta getEstado() {
        return estado;
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
