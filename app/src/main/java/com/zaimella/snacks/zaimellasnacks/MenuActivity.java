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

import com.google.gson.Gson;
import com.zaimella.log.Logger;
import com.zaimella.snacks.database.BaseHelper;
import com.zaimella.snacks.service.Compra;
import com.zaimella.snacks.service.Constantes;
import com.zaimella.snacks.service.RespuestaVO;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.ServicioBDD;
import com.zaimella.snacks.service.TiposRespuesta;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;
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

    public void btnComprar(View view) {

        Log.d("MV", "btnComprar");

        Intent intent = new Intent(this, UnicaCompraActivity.class);
        startActivity(intent);
        finish();

    }

    public void btnRegistrar(View view) {

        Log.d("MV", "btnRegistrar");

        Intent intent = new Intent(this, RegistraCedulaActivity.class);
        startActivity(intent);
        finish();

    }

    public void btnSincronizar(View view) {
        //logger.addRecordToLog("MenuActivity.sincronizar");

        ServicioBDD servicioBDD = new ServicioBDD(this);
        new SincronizarApexTask(this, servicioBDD).execute();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "No está permitido regresar. Presione Aceptar.", Toast.LENGTH_SHORT).show();
    }

    private class SincronizarApexTask extends AsyncTask<Void, Void, Void> {

        public Context context;
        private ServicioBDD servicioBDD;
        private ProgressDialog dialog;

        public SincronizarApexTask(Context context, ServicioBDD servicioBDD) {
            //logger.addRecordToLog("SincronizarApexTask");
            this.servicioBDD = servicioBDD;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            //logger.addRecordToLog("SincronizarApexTask.onPreExecute");

            dialog.setMessage("Sincronizando...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            /* OJO Borra la tabla de compras
            this.servicioBDD.abrirBD();
            this.servicioBDD.borrarTablaCompras( 1 );
            servicioBDD.cerrarBD();
            */

            try {
                //logger.addRecordToLog("SincronizarApexTask.doInBackground");
                RespuestaVO respuestaVO = null;
                this.servicioBDD.abrirBD();

                List<Compra> compras = servicioBDD.obtenerCompras();
                //logger.addRecordToLog("SincronizarApexTask.doInBackground compras : " + compras);

                for (Compra compra : compras) {
                    //System.out.println("compra: " + compra);
                    //logger.addRecordToLog("compra: " + compra);

                    if (this.invocarServicioCarga(compra)) {

                        //Sincronización OK
                        //logger.addRecordToLog("SINCRONIZACION OK : " + compra);
                        this.servicioBDD.actualizarCompra(compra.getIdcpr(), TiposRespuesta.SINCRONIZADO.toString());
                        //compra.setEstado( TiposRespuesta.SINCRONIZADO.toString() );
                        //this.servicioBDD.actualizarCompra( compra );

                    } else {

                        ////Sincronización ERROR
                        //logger.addRecordToLog("SINCRONIZACION ERROR : " + compra);
                        //compra.setEstado( TiposRespuesta.SINCRONIZADO_ERROR.toString() );
                        this.servicioBDD.actualizarCompra(compra.getIdcpr(), TiposRespuesta.SINCRONIZADO_ERROR.toString());

                    }

                    //this.servicioBDD.actualizarCompra( compra );

                }

                //Borrar los registros con estado SINCRONIZADO_OK
                this.servicioBDD.borrarTablaCompras(2);

                servicioBDD.cerrarBD();

            } catch (Exception e) {

                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();

                //logger.addRecordToLog("Exception MenuActivity.sincronizar : " + s);

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            //logger.addRecordToLog("Exception MenuActivity.onPostExecute");

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        private Boolean invocarServicioCarga(Compra compra) {
            try {
                //logger.addRecordToLog("MenuActivity.invocarServicioCarga");

                Gson gson = new Gson();
                HttpClient httpclient = new DefaultHttpClient();

                //http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/registrarCompra/1711441418/1476222478896/0.2/NA
                StringBuilder url = new StringBuilder();
                url.append(Constantes.URL_SERVICIO_CARGA)
                        .append(compra.getCedula())
                        .append("/").append(compra.getFechaNumero())
                        .append("/").append(compra.getValorCompra());

                if (compra.getComentario() != null && compra.getComentario().length() > 0) {
                    url.append("/").append(URLEncoder.encode(compra.getComentario(), "UTF-8"));
                } else {
                    url.append("/").append("NA");
                }

                HttpPost httpPost = new HttpPost(url.toString());

                HttpResponse response = httpclient.execute(httpPost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                //logger.addRecordToLog("despues responseString : " + responseString);

                RespuestaVO respuestaVO = gson.fromJson(responseString, RespuestaVO.class);

                if (!respuestaVO.getCodigo().equalsIgnoreCase("OK")) {
                    //error al insertar el registro
                    return Boolean.FALSE;
                }

                return Boolean.TRUE;
            } catch (Exception e) {

                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();

                //logger.addRecordToLog("Exception MenuActivity.invocarServicioCarga : " + s);

                return Boolean.FALSE;
            }
        }

    }

}

