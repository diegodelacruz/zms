package com.zaimella.snacks.zaimellasnacks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.gson.Gson;
import com.zaimella.log.Logger;
import com.zaimella.snacks.service.EmpleadoVO;
import com.zaimella.snacks.service.RespuestaVO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;

import cn.com.aratek.dev.Terminal;


public class MainActivity extends AppCompatActivity {

    Logger logger;
    //private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Log.i("BMAPI", "SDK version: v" + Terminal.getSdkVersion() + ", device SN: " + Terminal.getSN());
        //logger.addRecordToLog("SDK version: v" + Terminal.getSdkVersion() + ", device SN: " + Terminal.getSN());

        new SincronizacionTask( this ).execute();

    }

    private class SincronizacionTask extends AsyncTask<Void, Void, Void> {

        public Context context;
        private ProgressDialog dialog; // = new ProgressDialog(MainActivity.this);
        //String data ="";

        public SincronizacionTask(Context context){
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        protected void onPreExecute(){
            // NOTE: You can call UI Element here.
            logger.addRecordToLog("HttpRquestTask.onPreExecute");

            //Dialogo
            dialog.setMessage("Procesando...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                logger.addRecordToLog("_HttpRequestTask.doInBackground");

                //0. Validar si existe BD y si no existe crearla
                //this.validarBDD();

                //1. Obtener el código máximo del usuario
                Integer codigoMaximo = 0; //existe db, obtener el maximo?

                //2. Invocar el servicio para obtener el listado de empleados de tipo A
                RespuestaVO usuariosTipoA = this.obtenerUsuariosTipoA( codigoMaximo );

                //3. Insertar en la bdd
                if( usuariosTipoA.getCodigo().equalsIgnoreCase("OK") ) {

                    //this.insertarUsuarios( usuariosTipoA.getEmpleados() );

                }else{

                    //Mostrar mensaje de error

                }

                //logger.addRecordToLog("respuestaVO : " + respuestaVO);

            }catch (ClientProtocolException e) {

                logger.addRecordToLog("ClientProtocolException  : " + e.getMessage());

            }catch (IOException e) {

                logger.addRecordToLog("IOException : " + e.getMessage());

            }catch (Exception e) {
                //Log.e("RegistrarActivity.HttpRequestTask ", e.getMessage());

                e.printStackTrace();

                logger.addRecordToLog("Exception general doInBackground : " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            logger.addRecordToLog("onPostExecute");

            if( dialog!=null && dialog.isShowing()) {
                dialog.dismiss();
            }

            ((MainActivity)context).presentarMenu();

        }

        private RespuestaVO obtenerUsuariosTipoA(Integer codigoMaximo) throws Exception{
            logger.addRecordToLog("obtenerUsuariosTipoA");

            Gson gson = new Gson();
            HttpClient httpclient = new DefaultHttpClient();
            //HttpPost httppost = new HttpPost("http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/listadoEmpleados/A");
            HttpGet httpGet = new HttpGet("http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/listadoEmpleadosPorCodigo/"+codigoMaximo);
            logger.addRecordToLog("antes httpclient.execute(httpGet)");

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            logger.addRecordToLog("despues responseString : " + responseString);

            RespuestaVO respuestaVO = gson.fromJson(responseString , RespuestaVO.class);
            return respuestaVO;

            /*for( EmpleadoVO empleado : respuestaVO.getEmpleados() ) {

                //empleado.get

            }*/
        }

    }

    public void presentarMenu(){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();

    }

}
