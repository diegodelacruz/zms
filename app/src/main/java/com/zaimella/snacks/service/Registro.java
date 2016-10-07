package com.zaimella.snacks.service;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class Registro {
    private int idcmp;
    private String cedula;
    private String idHuella;
    private String huellaAratek;

    public Registro() {
    }

    public Registro(int idcmp, String cedula, String idhuella, String huellaaratek) {
        this.cedula = cedula;
        idHuella = idhuella;
        huellaAratek = huellaaratek;
    }

    public int getIdcmp() {
        return idcmp;
    }

    public String getCedula() {
        return cedula;
    }

    public String getIdHuella() {
        return idHuella;
    }

    public String getHuellaAratek() {
        return huellaAratek;
    }

    public void setIdcmp(int idcmp) {
        this.idcmp = idcmp;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public void setIdHuella(String idHuella) {
        this.idHuella = idHuella;
    }

    public void setHuellaAratek(String huellaAratek) {
        this.huellaAratek = huellaAratek;
    }

    @Override
    public String toString() {
        return "Registro{" +
                "idcmp=" + idcmp +
                ", cedula='" + cedula + '\'' +
                ", idHuella='" + idHuella + '\'' +
                ", huellaAratek='" + huellaAratek + '\'' +
                '}';
    }
}
