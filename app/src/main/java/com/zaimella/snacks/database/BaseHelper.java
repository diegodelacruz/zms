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


}
