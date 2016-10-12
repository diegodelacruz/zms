package com.zaimella.snacks.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zaimella.log.Logger;
import com.zaimella.snacks.database.BaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class ServicioBDD<SQLiteDataBase> {

    Logger logger;
    private BaseHelper baseHelper;
    private SQLiteDatabase sqLiteDatabase;
    public static final String TAG_SERVICIO_BDD = "DLC BDD";

    public ServicioBDD(Context context) {
        baseHelper = new BaseHelper(context);
    }

    public void abrirBD() {

        sqLiteDatabase = baseHelper.getWritableDatabase();
    }

    public void cerrarBD() {
        baseHelper.close();
    }

    public void insertarCompra(Compra compra) throws Exception {
        ContentValues values = new ContentValues();

        values.put("valorcompra", compra.getValorCompra());
        values.put("comentario", compra.getComentario());
        values.put("cedula", compra.getCedula());
        values.put("estado", compra.getEstado().toString());

        long resultado = sqLiteDatabase.insertOrThrow("compras", null, values);
    }

    public void insertarRegistro(Registro registro) {
        logger.addRecordToLog("ServicioBDD.insertarRegistro");

        try {
            logger.addRecordToLog("ServicioBDD.insertarRegistro -1-");

            ContentValues values = new ContentValues();
            values.put("cedula", registro.getCedula());
            values.put("idaratek", registro.getIdaratek());

            logger.addRecordToLog("ServicioBDD.insertarRegistro -2- " + registro.getCedula() + " " + registro.getIdaratek());
            long resultado = sqLiteDatabase.insertOrThrow("comprador", null, values);
            logger.addRecordToLog("ServicioBDD.insertarRegistro -3- : " + resultado);

        } catch (Exception e) {
            logger.addRecordToLog("ServicioBDD.insertarRegistro -4-");
            logger.addRecordToLog("Exception insertarRegistro: " + e.getMessage());
            Log.d("MV", "Error: " + e.getMessage());
        }
    }

    public void insertarEmpleado(EmpleadoVO empleado) {
        ContentValues values = new ContentValues();

        values.put("codigo", empleado.getCodigoNomina());
        values.put("cedula", empleado.getNumeroDocumento());
        values.put("nombres", empleado.getNombresCompletos());
        values.put("estado", empleado.getEstado());

        try {
            sqLiteDatabase.insert("empleados", null, values);
        } catch (Exception e) {
            Log.d(TAG_SERVICIO_BDD, "Error: " + e.getMessage());
            //TODO: Validar la existencia de los usuarios con error
        }
    }

    public void actualizarEstado(EmpleadoVO empleado) {
        ContentValues values = new ContentValues();

        values.put("estado", empleado.getEstado());

        sqLiteDatabase.update("empleados", values, "codigo = " + empleado.getCodigoNomina(), null);
    }

    public EmpleadoVO obtenerNombreUsuario(String idUsuarioAratek) {

        EmpleadoVO empleado = null;
        //String qry = "SELECT nombres FROM empleados WHERE cedula = " + cedula.trim();
        StringBuilder consulta = new StringBuilder();
        consulta.append("SELECT cedula , nombres ")
                .append("  FROM empleados ")
                .append(" WHERE cedula = ( SELECT cedula FROM comprador WHERE idaratek=").append(idUsuarioAratek).append(")");

        logger.addRecordToLog("consulta : " + consulta.toString());

        try {
            Cursor cursor = sqLiteDatabase.rawQuery(consulta.toString(), null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                empleado = new EmpleadoVO(cursor.getString(0), cursor.getString(1));

                cursor.close();

            }
        } catch (Exception e) {

            logger.addRecordToLog("BaseHelper.exception : " + e.getMessage());

        }
        return empleado;
    }

    //Método que retorna los datos de la tabla COMPRAS
    public List<Compra> obtenerCompras() throws Exception {
        logger.addRecordToLog("ServiciosBDD.obtenerCompras");

        String qryCompras = "SELECT cedula, fechacompra, valorcompra, comentario, estado" +
                "  FROM compras WHERE estado = '" + TiposRespuesta.NO_SINCRONIZADO.toString() + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(qryCompras, null);

        List<Compra> compras = new ArrayList<Compra>();

        Compra compra = null;
        if (cursor.moveToFirst()) {
            do {
                compra = new Compra();
                compra.setCedula(cursor.getString(0));
                compra.setFechaNumero(cursor.getLong(1));
                compra.setValorCompra(cursor.getString(2));
                compra.setComentario(cursor.getString(3));
                compra.setEstado(TiposRespuesta.NO_SINCRONIZADO.toString());

                Log.d("DLC BDD Compras", compra.toString());

                compras.add(compra);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return compras;
    }

    public Boolean existeComprador(String cedula) throws Exception {
        logger.addRecordToLog("BaseHelper.existeComprador : " + cedula);

        String qry = "SELECT count(*) FROM comprador WHERE cedula = '" + cedula + "'";
        logger.addRecordToLog("qry : " + qry);

        Cursor cursor = sqLiteDatabase.rawQuery(qry, null);
        //Cursor cursor = getWritableDatabase().rawQuery(qry, new String[]{cedula});
        cursor.moveToFirst();
        int i = cursor.getInt(0);
        cursor.close();

        logger.addRecordToLog("count :  " + i);
        if (i > 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public String buscarEmpleado(String cedula) {

        logger.addRecordToLog("BaseHelper.buscarEmpleado: " + cedula);

        String nombreCompleto = null;
        String qry = "SELECT nombres FROM empleados WHERE cedula = '" + cedula + "'";

        try {
            Cursor cursor = sqLiteDatabase.rawQuery(qry, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                nombreCompleto = cursor.getString(0);

                cursor.close();
                nombreCompleto = nombreCompleto;
            }
        } catch (Exception e) {

            logger.addRecordToLog("BaseHelper.exception : " + e.getMessage());

        }
        return nombreCompleto;
    }

    public void actualizarCompra(int idCompra, String estadoSincronizacion) {

        String qry = "UPDATE compras SET estado = '" + estadoSincronizacion + "'" + "WHERE idcpr = " + idCompra;
        try {
            sqLiteDatabase.rawQuery(qry, null);
        } catch (SQLException s) {
            logger.addRecordToLog("BaseHelper.SQLException: " + s.getMessage());
        }
    }
}