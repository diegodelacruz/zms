package com.zaimella.snacks.service;

import java.util.List;

/**
 * Created by mvelasco on 27/09/2016.
 */
public class RespuestaVO {

    private String codigo;

    private List<EmpleadoVO> empleados;

    private String mensaje;

    public RespuestaVO(String codigo, List<EmpleadoVO> empleados, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.empleados = empleados;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<EmpleadoVO> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<EmpleadoVO> empleados) {
        this.empleados = empleados;
    }

    @Override
    public String toString() {
        return "RespuestaVO{" +
                "codigo='" + codigo + '\'' +
                ", empleados=" + empleados +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }

}
