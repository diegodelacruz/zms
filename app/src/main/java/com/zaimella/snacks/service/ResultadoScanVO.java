package com.zaimella.snacks.service;

import java.util.Arrays;

import cn.com.aratek.fp.FingerprintImage;

/**
 * Created by mvelasco on 30/09/2016.
 */
public class ResultadoScanVO {

    private TiposRespuesta respuesta;

    private Integer numeroHuella;

    private String mensaje;

    private FingerprintImage fingerprintImage;

    private byte[] fingerPrintFeature;

    private Integer idUsuarioAratek;

    private String numeroCedula;

    public ResultadoScanVO() {
    }

    public ResultadoScanVO(TiposRespuesta respuesta, Integer numeroHuella, FingerprintImage fingerprintImage, byte[] fingerPrintFeature, String mensaje, Integer idUsuarioAratek) {
        this.respuesta = respuesta;
        this.numeroHuella = numeroHuella;
        this.fingerprintImage = fingerprintImage;
        this.fingerPrintFeature = fingerPrintFeature;
        this.mensaje = mensaje;
        this.idUsuarioAratek = idUsuarioAratek;
    }

    public TiposRespuesta getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(TiposRespuesta respuesta) {
        this.respuesta = respuesta;
    }

    public Integer getNumeroHuella() {
        return numeroHuella;
    }

    public void setNumeroHuella(Integer numeroHuella) {
        this.numeroHuella = numeroHuella;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public FingerprintImage getFingerprintImage() {
        return fingerprintImage;
    }

    public void setFingerprintImage(FingerprintImage fingerprintImage) {
        this.fingerprintImage = fingerprintImage;
    }

    public byte[] getFingerPrintFeature() {
        return fingerPrintFeature;
    }

    public void setFingerPrintFeature(byte[] fingerPrintFeature) {
        this.fingerPrintFeature = fingerPrintFeature;
    }

    public Integer getIdUsuarioAratek() {
        return idUsuarioAratek;
    }

    public void setIdUsuarioAratek(Integer idUsuarioAratek) {
        this.idUsuarioAratek = idUsuarioAratek;
    }

    public String getNumeroCedula() {
        return numeroCedula;
    }

    public void setNumeroCedula(String numeroCedula) {
        this.numeroCedula = numeroCedula;
    }

    @Override
    public String toString() {
        return "ResultadoScanVO{" +
                "respuesta=" + respuesta +
                ", numeroHuella=" + numeroHuella +
                ", mensaje='" + mensaje + '\'' +
                ", fingerprintImage=" + fingerprintImage +
                ", fingerPrintFeature=" + Arrays.toString(fingerPrintFeature) +
                ", idUsuarioAratek=" + idUsuarioAratek +
                '}';
    }
}
