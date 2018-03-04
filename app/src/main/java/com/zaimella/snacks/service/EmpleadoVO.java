package com.zaimella.snacks.service;

/**
 * Created by mvelasco on 29/09/2016.
 */
public class EmpleadoVO {

    private String codigoNomina;

    private String nombresCompletos;

    private String numeroDocumento;

    private String estado;

    public EmpleadoVO(String codigoNomina, String nombresCompletos, String numeroDocumento, String estado) {
        this.codigoNomina = codigoNomina;
        this.nombresCompletos = nombresCompletos;
        this.numeroDocumento = numeroDocumento;
        this.estado = estado;
    }

    public EmpleadoVO(String numeroDocumento, String nombresCompletos) {
        this.nombresCompletos = nombresCompletos;
        this.numeroDocumento = numeroDocumento;
    }

    public String getCodigoNomina() {
        return codigoNomina;
    }

    public void setCodigoNomina(String codigoNomina) {
        this.codigoNomina = codigoNomina;
    }

    public String getNombresCompletos() {
        return nombresCompletos;
    }

    public void setNombresCompletos(String nombresCompletos) {
        this.nombresCompletos = nombresCompletos;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
