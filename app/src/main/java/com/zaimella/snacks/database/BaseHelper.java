package com.zaimella.snacks.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.Compra;
import com.zaimella.snacks.service.Constantes;
import com.zaimella.snacks.service.TiposRespuesta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ddelacruz on 05/10/2016.
 */
public class BaseHelper extends SQLiteOpenHelper {

    Logger logger;
    public static final int DATABASE_VERSION = 1;
    public static final String TAG_SQL = "DLC BDD";

    String empleados =
            "CREATE TABLE empleados " +
                    "(idemp INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "codigo NUMERIC NOT NULL, " +
                    "cedula TEXT NOT NULL, " +
                    "nombres TEXT NOT NULL, " +
                    "estado TEXT NOT NULL)";

    String comprador =
            "CREATE TABLE comprador " +
                    "(idcmp INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cedula TEXT NOT NULL, " +
                    "idaratek TEXT NOT NULL)";

    String compras =
            "CREATE TABLE compras " +
                    "(idcpr INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "cedula TEXT NOT NULL, " +
                    "fechacompra DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "valorcompra TEXT NOT NULL, " +
                    "comentario TEXT, " +
                    "estado TEXT)";

    public BaseHelper(Context context) {
        super(context, Constantes.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(empleados);
        db.execSQL(comprador);
        db.execSQL(compras);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS empleados");
        db.execSQL("DROP TABLE IF EXISTS comprador");
        db.execSQL("DROP TABLE IF EXISTS compras");

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public int ultimoEmpleado() {
        int maximo = 0;
        String qry = "SELECT max(codigo) FROM empleados";
        Cursor cursor = getWritableDatabase().rawQuery(qry, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            maximo = cursor.getInt(0);
        }

        cursor.close();

        return maximo;
    }

    public String buscarEmpleado(String cedula) {

        logger.addRecordToLog("BaseHelper.buscarEmpleado: " + cedula);

        String nombreCompleto = null;
        String qry = "SELECT nombres FROM empleados WHERE cedula = " + cedula.trim();

        try {
            Cursor cursor = getWritableDatabase().rawQuery(qry, null);

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

    public Boolean existeComprador(String cedula) throws Exception {
        logger.addRecordToLog("BaseHelper.existeComprador : " + cedula);

        String qry = "SELECT count(*) FROM comprador WHERE cedula = ?";
        logger.addRecordToLog("qry : " + qry);

        Cursor cursor = getWritableDatabase().rawQuery(qry, new String[]{cedula});
        cursor.moveToFirst();
        int i = cursor.getInt(0);
        cursor.close();

        logger.addRecordToLog("count :  " + i);
        if (i > 0) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    //Método que retorna los datos de la tabla COMPRAS
    public List<Compra> obtenerCompras() {
        String qryCompras = "SELECT cedula, fechacompra, valorcompra, comentario, estado" +
                "FROM compras WHERE estado = " + TiposRespuesta.NO_SINCRONIZADO;
        Cursor cursor = getWritableDatabase().rawQuery(qryCompras, null);

        List<Compra> compras = new ArrayList<Compra>();

        Compra compra = null;
        if (cursor.moveToFirst()) {
            do {
                compra = new Compra();
                compra.setCedula(cursor.getString(0));
                //compra.setFecha(cursor.getString(1));
                compra.setValorCompra(cursor.getString(2));
                compra.setComentario(cursor.getString(3));
                compra.setEstado(TiposRespuesta.NO_SINCRONIZADO.toString());

                compras.add(compra);
                Log.d("DLC BDD Compras",compras.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();

        return compras;
    }
}
