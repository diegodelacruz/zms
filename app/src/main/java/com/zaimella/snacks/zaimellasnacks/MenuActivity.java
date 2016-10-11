package com.zaimella.snacks.zaimellasnacks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zaimella.log.Logger;
import com.zaimella.snacks.database.BaseHelper;
import com.zaimella.snacks.service.Compra;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.ServicioBDD;
import com.zaimella.snacks.service.TiposRespuesta;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

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

        ServicioBDD servicioBDD = new ServicioBDD(this);
        new SincronizarApexTask( this , servicioBDD ).execute();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "No está permitido regresar. Presione Aceptar.", Toast.LENGTH_SHORT).show();
    }

    private class SincronizarApexTask extends AsyncTask<Void,Void,Void>{

        public Context context;
        private ServicioBDD servicioBDD;
        private ProgressDialog dialog;

        public SincronizarApexTask(Context context,ServicioBDD servicioBDD) {
            logger.addRecordToLog("SincronizarApexTask");
            this.servicioBDD = servicioBDD;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            logger.addRecordToLog("SincronizarApexTask.onPreExecute");

            dialog.setMessage("Procesando...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try{
                logger.addRecordToLog("SincronizarApexTask.doInBackground");

                this.servicioBDD.abrirBD();

                List<Compra> compras = servicioBDD.obtenerCompras();
                logger.addRecordToLog("SincronizarApexTask.doInBackground compras : " + compras);

                for( Compra compra  : compras ){
                    //System.out.println("compra: " + compra);
                    logger.addRecordToLog("compra: " + compra);
                }

                servicioBDD.cerrarBD();

            }catch(Exception e){

                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();

                logger.addRecordToLog("Exception MenuActivity.sincronizar : " + s);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            logger.addRecordToLog("Exception MenuActivity.onPostExecute");

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

    }
}

