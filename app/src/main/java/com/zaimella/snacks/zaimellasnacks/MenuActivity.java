package com.zaimella.snacks.zaimellasnacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zaimella.log.Logger;
import com.zaimella.snacks.database.BaseHelper;
import com.zaimella.snacks.service.Compra;
import com.zaimella.snacks.service.ServicioBDD;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.addRecordToLog("MenuActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void btnComprar(View view){

        Log.d("MV","btnComprar");

        Intent intent = new Intent(this, ComprarActivityValor.class);
        startActivity(intent);
        //finish();

    }

    public void btnRegistrar(View view){

        Log.d("MV","btnRegistrar");

        Intent intent = new Intent(this, RegistraCedulaActivity.class);
        startActivity(intent);

    }

    public void btnSincronizar(View view){
        logger.addRecordToLog("MenuActivity.sincronizar");

        /*try {
            ServicioBDD servicioBDD = new ServicioBDD(this);
            servicioBDD.abrirBD();

            List<Compra> compras = servicioBDD.obtenerCompras();

            servicioBDD.cerrarBD();

        }catch(Exception e){

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            logger.addRecordToLog("Exception MenuActivity.sincronizar : " + s);

        }*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "No está permitido regresar. Presione Aceptar.", Toast.LENGTH_SHORT).show();
    }

}

