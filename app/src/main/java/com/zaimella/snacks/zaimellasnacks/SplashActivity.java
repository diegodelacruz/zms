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
import com.zaimella.snacks.database.BaseHelper;
import com.zaimella.snacks.service.EmpleadoVO;
import com.zaimella.snacks.service.RespuestaVO;
import com.zaimella.snacks.service.ServicioBDD;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;


public class SplashActivity extends AppCompatActivity {

    Logger logger;
    public static final String TAG_SPLASH = "DLC SPLASH";
    Context context = this;
    int ultEmpleado;
    int contadorCarga = 0;
    boolean existeBase;

    ServicioBDD srvEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        new SincronizacionTask(this).execute();
        srvEmpleado = new ServicioBDD(this);
        BaseHelper base = new BaseHelper(context);
        ultEmpleado = base.ultimoEmpleado();
    }

    private class SincronizacionTask extends AsyncTask<Void, Void, Void> {

        public Context context;
        private ProgressDialog dialog;

        public SincronizacionTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            logger.addRecordToLog("HttpRquestTask.onPreExecute");

            dialog.setMessage("Procesando...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*Obtiene los nuevo usuarios de acuerdo al código*/
            try {
                logger.addRecordToLog("_HttpRequestTask.doInBackground");

                BaseHelper ultimoEmpleado = new BaseHelper(context);
                int codigoMaximo = ultimoEmpleado.ultimoEmpleado();

                RespuestaVO usuariosTipoA = this.obtenerUsuariosTipoA(codigoMaximo);
                checkDataBase();

                if (usuariosTipoA.getCodigo().equalsIgnoreCase("OK")) {
                    srvEmpleado.abrirBD();
                    for (EmpleadoVO empleado : usuariosTipoA.getEmpleados()) {
                        srvEmpleado.insertarEmpleado(empleado);
                        contadorCarga++;
                    }
                    srvEmpleado.cerrarBD();
                    Log.d(TAG_SPLASH, "Termina el FOR. Se cargaron " + contadorCarga + " nuevos empleados.");
                } else {
                    Log.d(TAG_SPLASH, "Ingresa al ELSE");
                }

            } catch (ClientProtocolException e) {

                logger.addRecordToLog("ClientProtocolException  : " + e.getMessage());

            } catch (IOException e) {

                logger.addRecordToLog("IOException : " + e.getMessage());

            } catch (Exception e) {

                e.printStackTrace();
                logger.addRecordToLog("Exception general doInBackground : " + e.getMessage());
            }

            /*Obtiene los usuario que se han desvinculado*/
            try {
                Log.d(TAG_SPLASH, "Ingresa al TRY de ACTUALIZAR");
                RespuestaVO desvinculados = this.obtenerUsuario("E");
                if (desvinculados.getCodigo().equalsIgnoreCase("OK")) {
                    Log.d(TAG_SPLASH, "Ingresa al IF de ACTUALIZAR");
                    srvEmpleado.abrirBD();
                    for (EmpleadoVO empleadoDesv : desvinculados.getEmpleados()) {
                        srvEmpleado.actualizarEstado(empleadoDesv);
                    }
                    srvEmpleado.cerrarBD();
                } else {
                    Log.d(TAG_SPLASH, "Ingresa al ELSE de ACTUALIZAR");
                }
            } catch (ClientProtocolException e) {

                Log.d(TAG_SPLASH, "ClientProtocolException  : " + e.getMessage());

            } catch (IOException e) {

                Log.d(TAG_SPLASH, "IOException : " + e.getMessage());

            } catch (Exception e) {

                Log.d(TAG_SPLASH, "Exception general doInBackground : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            logger.addRecordToLog("onPostExecute");

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ((SplashActivity) context).menuPrincipal();
        }

        private RespuestaVO obtenerUsuariosTipoA(int codigoMaximo) throws Exception {
            logger.addRecordToLog("obtenerUsuariosTipoA");

            Gson gson = new Gson();
            HttpClient httpclient = new DefaultHttpClient();
            String urlWS = "http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/listadoEmpleadosPorCodigo/" + codigoMaximo;
            Log.d(TAG_SPLASH, "codigoMaximo: " + codigoMaximo);

            HttpGet httpGet = new HttpGet(urlWS);
            logger.addRecordToLog("antes httpclient.execute(httpGet)");

            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            logger.addRecordToLog("despues responseString : " + responseString);

            RespuestaVO respuestaVO = gson.fromJson(responseString, RespuestaVO.class);
            return respuestaVO;
        }

        private RespuestaVO obtenerUsuario(String estado) throws Exception {
            Gson gson = new Gson();
            HttpClient httpclient = new DefaultHttpClient();
            String urlEstado = "http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/listadoEmpleados/" + estado;

            HttpGet httpGet = new HttpGet(urlEstado);
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            RespuestaVO respuestaVO = gson.fromJson(responseString, RespuestaVO.class);

            return respuestaVO;
        }

        public boolean checkDataBase() {
            boolean existe = true;
            File db = getApplicationContext().getDatabasePath("zai.db");

            if (!db.exists()) {
                Log.d(TAG_SPLASH, "No Existe la BD.");
                existe = false;
            } else {
                Log.d(TAG_SPLASH, "Existe la BD.");
                existe = true;
            }
            return existe;
        }
    }

    public void menuPrincipal() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}