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
import com.zaimella.snacks.service.Constantes;
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

    }

    private class SincronizacionTask extends AsyncTask<Void, Void, Void> {

        public Context context;
        private ProgressDialog dialog;

        public SincronizacionTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            dialog.setMessage("Procesando...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*Obtiene los nuevo usuarios de acuerdo al código*/
            try {
                //logger.addRecordToLog("_HttpRequestTask.doInBackground");

                //Crea la BDD y obtiene el último empleado registrado
                srvEmpleado = new ServicioBDD(context);
                BaseHelper bddSnacks = new BaseHelper(context);

                //Validar la existencia de la BDD y crearla
                //checkDataBase();

                //Obtiene el último empleado
                int codigoMaximo = bddSnacks.ultimoEmpleado();

                //Obtener el listado de usuarios (con estado A) desde el último empleado
                RespuestaVO usuariosTipoA = this.obtenerUsuariosTipoA(codigoMaximo);

                if (usuariosTipoA.getCodigo().equalsIgnoreCase(Constantes.RESPUESTA_OK)) {

                    srvEmpleado.abrirBD();

                    for (EmpleadoVO empleado : usuariosTipoA.getEmpleados()) {
                        srvEmpleado.insertarEmpleado(empleado);
                        contadorCarga++;
                    }

                    //srvEmpleado.cerrarBD();
                    Log.d(TAG_SPLASH, "Termina el FOR. Se cargaron " + contadorCarga + " nuevos empleados.");
                } else {
                    Log.d(TAG_SPLASH, "Ingresa al ELSE");
                    //TODO:Mostar un mensaje de error "No es posible sincronizar los usuarios, consulte con el administrador"
                }


                Log.d(TAG_SPLASH, "Actualización de usuarios");
                RespuestaVO desvinculados = this.obtenerUsuario(Constantes.ESTADO_EMPLEADO_E);
                if (desvinculados.getCodigo().equalsIgnoreCase(Constantes.RESPUESTA_OK)) {
                    Log.d(TAG_SPLASH, "Ingresa al IF de ACTUALIZAR");
                    //srvEmpleado.abrirBD();
                    for (EmpleadoVO empleadoDesv : desvinculados.getEmpleados()) {
                        srvEmpleado.actualizarEstado(empleadoDesv);
                    }
                    srvEmpleado.cerrarBD();
                } else {
                    Log.d(TAG_SPLASH, "Ingresa al ELSE de ACTUALIZAR");
                }

                Log.d(TAG_SPLASH, "Lee de la tabla de compras");


            } catch (ClientProtocolException e) {

                logger.addRecordToLog("ClientProtocolException  : " + e.getMessage());

            } catch (IOException e) {

                logger.addRecordToLog("IOException : " + e.getMessage());

            } catch (Exception e) {

                e.printStackTrace();
                logger.addRecordToLog("Exception general doInBackground : " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            ((SplashActivity) context).menuPrincipal();

        }

        private RespuestaVO obtenerUsuariosTipoA(int codigoMaximo) throws Exception {

            Gson gson = new Gson();
            HttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG_SPLASH, "codigoMaximo: " + codigoMaximo);

            StringBuilder url = new StringBuilder();
            url.append( Constantes.URL_SERVICIO_EMPLEADOS_POR_CODIGO ).append( codigoMaximo );

            HttpGet httpGet = new HttpGet( url.toString() );
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

            StringBuilder url = new StringBuilder();
            url.append(Constantes.URL_SERVICIO_EMPLEADOS_POR_ESTADO).append(estado);

            HttpGet httpGet = new HttpGet(url.toString());

            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            RespuestaVO respuestaVO = gson.fromJson(responseString, RespuestaVO.class);

            return respuestaVO;
        }

    }

    public void menuPrincipal() {
        //Intent intent = new Intent(this, MenuActivity.class);
        Intent intent = new Intent(this, UnicaCompraActivity.class);
        startActivity(intent);
        finish();
    }

}