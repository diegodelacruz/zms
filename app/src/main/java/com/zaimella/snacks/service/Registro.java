package com.zaimella.snacks.service;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class Registro {
    private int idcmp;
    private String cedula;
    private String idaratek;

    public Registro() {
    }

    public Registro(String cedula, String idaratek) {
        this.cedula = cedula;
        this.idaratek = idaratek;
    }

    public String getIdaratek() {
        return idaratek;
    }

    public void setIdaratek(String idaratek) {
        this.idaratek = idaratek;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public int getIdcmp() {
        return idcmp;
    }

    public void setIdcmp(int idcmp) {
        this.idcmp = idcmp;
    }

    @Override
    public String toString() {
        return "Registro{" +
                "idcmp=" + idcmp +
                ", cedula='" + cedula + '\'' +
                ", idaratek='" + idaratek + '\'' +
                '}';
    }

}
