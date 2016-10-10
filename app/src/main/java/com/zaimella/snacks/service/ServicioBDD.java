package com.zaimella.snacks.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zaimella.log.Logger;
import com.zaimella.snacks.database.BaseHelper;

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

    public void insertarCompra(Compra compra) {
        ContentValues values = new ContentValues();

        values.put("valorcompra", compra.getValorCompra());
        values.put("comentario", compra.getComentario());
        values.put("cedula", compra.getCedula());
        values.put("estado", compra.getEstado().toString());

        sqLiteDatabase.insert("compras", null, values);
    }

    public void insertarRegistro(Registro registro) {
        logger.addRecordToLog("ServicioBDD.insertarRegistro");

        try {
            logger.addRecordToLog("ServicioBDD.insertarRegistro -1-");

            ContentValues values = new ContentValues();
            values.put("cedula", registro.getCedula());
            values.put("idaratek", registro.getIdaratek());

            logger.addRecordToLog("ServicioBDD.insertarRegistro -2- "+ registro.getCedula() +" " + registro.getIdaratek());
            long resultado = sqLiteDatabase.insertOrThrow("comprador", null, values);
            logger.addRecordToLog("ServicioBDD.insertarRegistro -3- : " + resultado);

        }catch (Exception e){
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

}