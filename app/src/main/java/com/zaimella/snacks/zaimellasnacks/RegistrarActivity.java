package com.zaimella.snacks.zaimellasnacks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.Constantes;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.TiposRespuesta;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLEncoder;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

public class RegistrarActivity extends AppCompatActivity  {

    Logger logger;

    private TextView mNumeroCedula;
    private TextView mNombreUsuario;
    private ProgressDialog mProgressDialog;
    private FingerprintScanner mScanner;
    private FingerprintTask mTask;
    private ImageView mFingerprintImage;
    private ImageView mHuella1;
    private ImageView mHuella2;
    private ImageView mHuella3;
    //private TextView mMensaje;
    private Integer numeroHuella;
    byte[][] huellas = new byte[3][];
    private String numeroCedula;
    private String nombrePersona;

    private static final String FP_DB_PATH = "/sdcard/zaimella.db";
    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    private static final int MSG_UPDATE_IMAGE = 2;
    private static final int MSG_UPDATE_TEXT = 3;
    private static final int MSG_UPDATE_BUTTON = 4;
    private static final int MSG_UPDATE_SN = 5;
    private static final int MSG_UPDATE_FW_VERSION = 6;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 7;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 8;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_ERROR: {
                    logger.addRecordToLog("MSG_SHOW_ERROR");
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    logger.addRecordToLog("MSG_SHOW_INFO");

                    Toast.makeText(RegistrarActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                }
                case MSG_UPDATE_IMAGE: {
                    logger.addRecordToLog("MSG_UPDATE_IMAGE");

                    mFingerprintImage.setImageBitmap((Bitmap) msg.obj);
                    //mMensaje.setText("Ingrese la huella No: " + numeroHuella);
                    break;
                }
                case MSG_UPDATE_TEXT: {
                    logger.addRecordToLog("MSG_UPDATE_TEXT");

                    String[] texts = (String[]) msg.obj;
                    /*mCaptureTime.setText(texts[0]);
                    mExtractTime.setText(texts[1]);
                    mGeneralizeTime.setText(texts[2]);
                    mVerifyTime.setText(texts[3]);*/

                    break;
                }
                case MSG_UPDATE_BUTTON: {
                    logger.addRecordToLog("MSG_UPDATE_BUTTON");

                    Boolean enable = (Boolean) msg.obj;
                    /*mBtnEnroll.setEnabled(enable);
                    mBtnVerify.setEnabled(enable);
                    mBtnIdentify.setEnabled(enable);
                    mBtnClear.setEnabled(enable);
                    mBtnShow.setEnabled(enable);*/
                    break;
                }
                case MSG_UPDATE_SN: {
                    logger.addRecordToLog("MSG_UPDATE_SN");

                    //mSN.setText((String) msg.obj);
                    break;
                }
                case MSG_UPDATE_FW_VERSION: {
                    logger.addRecordToLog("MSG_UPDATE_FW_VERSION");

                    //mFwVersion.setText((String) msg.obj);
                    break;
                }
                case MSG_SHOW_PROGRESS_DIALOG: {
                    logger.addRecordToLog("MSG_SHOW_PROGRESS_DIALOG");

                    /*String[] info = (String[]) msg.obj;
                    mProgressDialog.setTitle(info[0]);
                    mProgressDialog.setMessage(info[1]);
                    mProgressDialog.show();*/

                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
                    logger.addRecordToLog("MSG_SHOW_PROGRESS_DIALOG");
                    //mProgressDialog.dismiss();
                    break;
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Instancia actividad
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        //Atributos
        numeroHuella = 0;
        mNumeroCedula =(TextView)findViewById(R.id.idNumeroCedula);
        mNombreUsuario = (TextView)findViewById(R.id.idNombreUsuario);
        mFingerprintImage = (ImageView)findViewById(R.id.imagenHuella);
        mHuella1 = (ImageView)findViewById(R.id.huella1);
        mHuella2 = (ImageView)findViewById(R.id.huella2);
        mHuella3 = (ImageView)findViewById(R.id.huella3);
        //mMensaje = (TextView)findViewById(R.id.mensaje);

        Intent intent = getIntent();
        mNumeroCedula.setText( intent.getStringExtra(Constantes.NUMERO_CEDULA) );
        mNombreUsuario.setText( intent.getStringExtra(Constantes.NOMBRE_USUARIO) );

        //Instancia dispositivo
        mScanner = FingerprintScanner.getInstance();
        logger.addRecordToLog("AFTER FingerprintScanner.getInstance : " + mScanner.hasFinger().error);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("MV","BEFORE openDevice()");
        openDevice();
        Log.i("MV","AFTER openDevice()");
    }

    public void openDevice() {
        Log.i("MV","_openDevice_");

        new Thread() {

            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));

                int error;
                if ((error = mScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
                    //showErrorDialog(getString(R.string.fingerprint_device_power_on_failed), getFingerprintErrorString(error));
                    logger.addRecordToLog("fingerprint_device_power_on_failed : " + error);
                }else{
                    logger.addRecordToLog("mScanner.powerOn() success : " + error);
                }

                Log.i("MV", "before mScanner.open()");
                //error = mScanner.open();
                //Log.i("MV", "after mScanner.open() : " + error);
                //logger.addRecordToLog("after mScanner.open() : " + error);
                if ((error = mScanner.open()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV","mScanner.open() error");
                    Log.i("MV","MSG_UPDATE_SN");
                    Log.i("MV","MSG_UPDATE_FW_VERSION");

                    logger.addRecordToLog("mScanner.open() error");
                    logger.addRecordToLog("MSG_UPDATE_SN");
                    logger.addRecordToLog("MSG_UPDATE_FW_VERSION");

                }else{

                    Result res = mScanner.getSN();
                    res = mScanner.getFirmwareVersion();
                    logger.addRecordToLog("MSG_UPDATE_FW_VERSION : " + (String) res.data);

                    /*mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, (String) res.data)));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, (String) res.data)));
                    showInfoToast(getString(R.string.fingerprint_device_open_success));
                    enableControl(true);*/
                }

                if ((error = Bione.initialize(RegistrarActivity.this, FP_DB_PATH)) == Bione.RESULT_OK) {
                    logger.addRecordToLog("algorithm_initialization_success");
                }else{
                    logger.addRecordToLog("algorithm_initialization_failed");
                }

                dismissProgressDialog();
            }
        }.start();

    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[] { title, message }));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    @Override
    protected void onPause() {
        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(false);
            mTask.waitForDone();
        }

        this.closeDevice();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.i("MV","before closeDevice");
        this.closeDevice();
        Log.i("MV","after closeDevice");

        this.mScanner = null;
    }

    private void closeDevice() {
        Log.i("MV","_closeDevice_");

        new Thread() {

            @Override
            public void run() {

                try {

                    int error;
                    Log.i("MV", "BEFORE mScanner.close()");
                    if ((error = mScanner.close()) != FingerprintScanner.RESULT_OK) {
                        Log.i("MV", "fingerprint_device_close_failed");
                        logger.addRecordToLog("fingerprint_device_close_failed");
                    } else {
                        Log.i("MV", "fingerprint_device_close_success");
                        logger.addRecordToLog("fingerprint_device_close_success");
                    }

                    if ((error = mScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                        Log.i("MV", "fingerprint_device_power_off_failed");
                        logger.addRecordToLog("fingerprint_device_power_off_failed");
                    } else {
                        logger.addRecordToLog("power_off success");
                    }

                    if ((error = Bione.exit()) != Bione.RESULT_OK) {
                        //showErrorDialog(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                        Log.i("MV", "algorithm_cleanup_failed");
                        logger.addRecordToLog("algorithm_cleanup_failed");}
                    else {
                        logger.addRecordToLog("algorithm_cleanup_failed success");
                    }

                }catch(Exception e){

                    logger.addRecordToLog("Exception close device : " + e.getMessage());
                }

            }
        }.start();

    }

    public void btnCapturar(View view){
        try {
            logger.addRecordToLog("btnCapturar -0-");

            mFingerprintImage.setImageResource(R.drawable.nuevahuella);
            //mMensaje.setText("Ingrese la huella No: " + numeroHuella);

            //Lanza la tarea asíncrona para ingreso de huella
            (new FingerprintTask(this)).execute(numeroHuella);
            logger.addRecordToLog("btnCapturar -3-");

        }catch(Exception e){

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            logger.addRecordToLog("Exception btnCapturar : " + s);
        }
    }

    private void updateFingerprintImage(FingerprintImage fi) {
        logger.addRecordToLog("updateFingerprintImage : " + fi);

        try {

            byte[] fpBmp = null;
            Bitmap bitmap;
            if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
                logger.addRecordToLog("updateFingerprintImage sin huella ");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sinhuella);
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, bitmap));
            //mFingerprintImage.setImageBitmap(bitmap);

        }catch(Exception e){

            logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

        }

    }

    private class FingerprintTask extends AsyncTask<Integer, Integer, ResultadoScanVO> {

        public Context context;
        private boolean mIsDone = false;

        public FingerprintTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute(){
            //enableControl(false);
            logger.addRecordToLog("FingerprintTask.onPreExecute");
        }

        @Override
        protected ResultadoScanVO doInBackground(Integer... params) {
            long startTime, captureTime = -1, extractTime = -1, generalizeTime = -1, verifyTime = -1;
            FingerprintImage fingerprintImage = null;
            byte[] fingerPrintFeature = null;
            Result res = null;

            try {
                logger.addRecordToLog("FingerprintTask.doInBackground");

                do {
                    //Prepara el scanner
                    mScanner.prepare();
                    do {
                        startTime = System.currentTimeMillis();
                        res = mScanner.capture();
                        captureTime = System.currentTimeMillis() - startTime;
                    } while (res.error == FingerprintScanner.NO_FINGER && !isCancelled());
                    mScanner.finish();
                    if (isCancelled()) {
                        logger.addRecordToLog("FingerprintTask.doInBackground isCancelled()");
                        return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Cancelado",null);
                    }

                }while(res.error != FingerprintScanner.RESULT_OK);

                //Huella ok
                fingerprintImage = (FingerprintImage) res.data;
                res = Bione.extractFeature(fingerprintImage);
                if (res.error != Bione.RESULT_OK) {
                    //Tomar la huella nuevamente
                    return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error al extraer la huella",null);
                }

                fingerPrintFeature = (byte[]) res.data;

                mIsDone = true;
                return new ResultadoScanVO(TiposRespuesta.EXITO, params[0], fingerprintImage, fingerPrintFeature, "Error al extraer la huella",null);

            }catch(Exception e){

                Log.e("MV" , e.getMessage() );
                logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

                return new ResultadoScanVO(TiposRespuesta.ERROR , null , null , null , e.getMessage(),null);
            }
        }

        @Override
        protected void onPostExecute(ResultadoScanVO respuestaScan) {
            try {
                super.onPostExecute(respuestaScan);

                logger.addRecordToLog("FingerprintTask.onPostExecute");

                if (respuestaScan.getRespuesta().equals(TiposRespuesta.EXITO)) {
                    logger.addRecordToLog("FingerprintTask.onPostExecute - EXITO");
                    logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan.getNumeroHuella() : " + respuestaScan.getNumeroHuella());
                    logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan.getFingerPrintFeature() : " + respuestaScan.getFingerPrintFeature());
                    logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan.getFingerprintImage() : " + respuestaScan.getFingerprintImage());
                    logger.addRecordToLog("FingerprintTask.onPostExecute - huellas : " + huellas);

                    //Asigna en el arreglo las huellas
                    huellas[respuestaScan.getNumeroHuella()] = respuestaScan.getFingerPrintFeature();

                    //Actualiza la imagen de la huella
                    updateFingerprintImage(respuestaScan.getFingerprintImage());

                    switch ( respuestaScan.getNumeroHuella() ){
                        case 0: mHuella1.setImageResource(R.drawable.fpon);
                            break;
                        case 1: mHuella2.setImageResource(R.drawable.fpon);
                                break;
                        case 2: mHuella3.setImageResource(R.drawable.fpon);
                            break;
                        default: break;
                    }

                    if( numeroHuella>= 2 ){

                        logger.addRecordToLog("onPostExecute - Número de huellas existentes : " + numeroHuella+1);

                        Result res = Bione.makeTemplate( huellas[0] , huellas[1] , huellas[2] );
                        if (res.error != Bione.RESULT_OK) {

                            showInfoToast(getString( R.string.enroll_failed_because_of_make_template) );
                            logger.addRecordToLog("onPostExecute - enroll_failed_because_of_make_template");

                            ((RegistrarActivity)context).invocarPaginaMenu();
                            return;
                        }

                        byte[] fpTemp = (byte[]) res.data;

                        int id = Bione.getFreeID();
                        if (id < 0) {

                            showInfoToast(getString( R.string.enroll_failed_because_of_get_id) );
                            logger.addRecordToLog("onPostExecute - enroll_failed_because_of_get_id");

                            ((RegistrarActivity)context).invocarPaginaMenu();
                            return;
                        }

                        int ret = Bione.enroll(id, fpTemp);
                        if (ret != Bione.RESULT_OK) {

                            showInfoToast(getString( R.string.enroll_failed_because_of_get_id) );

                            logger.addRecordToLog("onPostExecute - enroll_failed_because_of_error");
                            ((RegistrarActivity)context).invocarPaginaMenu();
                            return;
                        }

                        logger.addRecordToLog("onPostExecute - id generado : "+ id);

                        showInfoToast(getString(R.string.enroll_success) + id);

                        ((RegistrarActivity)context).invocarPaginaMenu();

                    }else{

                        ++numeroHuella;

                        //Actualizar para el ingreso de una nueva huella
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {}
                        }, 1000);

                        mFingerprintImage.setImageResource(R.drawable.sinhuella);

                    }

                } else {
                    logger.addRecordToLog("FingerprintTask.onPostExecute - ERROR");

                    mFingerprintImage.setImageResource(R.drawable.errornuevahuella);

                }

                //logger.addRecordToLog("after update image");
            }catch(Exception e){

                /*Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();*/

                logger.addRecordToLog("Exception onPostExecute : " + e.getMessage());
            }
        }

        public void waitForDone() {
            while (!mIsDone) {
                logger.addRecordToLog("mIsDone");
            }
        }


    }

    public void invocarPaginaMenu(){

        logger.addRecordToLog("RegistrarACtivity.invocarPaginaMenu");

        try{

            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();

        }catch(Exception e){

            Log.e("MV" , e.getMessage());
            logger.addRecordToLog("Exception : RegistrarACtivity.invocarPaginaMenu: " + e.getMessage());
        }

    }


    /*
    private class HttpRequestTask extends AsyncTask<Object, Void, Void>{

        private ProgressDialog dialog = new ProgressDialog(RegistrarActivity.this);
        String data ="";

        protected void onPreExecute() {

            logger.addRecordToLog("HttpRquestTask.onPreExecute");

            dialog.setMessage("Procesando...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Object... params) {

            BufferedReader reader=null;

            try {
                logger.addRecordToLog("_HttpRequestTask.doInBackground");

                logger.addRecordToLog("antes httpclient");

                // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                //HttpPost httppost = new HttpPost("http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/test");

                String ci = (String)params[0];
                byte[] huella = (byte[])params[1];
                String huellaString = URLEncoder.encode( Base64.encodeToString( huella , Base64.DEFAULT) ,  "UTF-8");

                logger.addRecordToLog("ci : " + ci);
                logger.addRecordToLog("huellaString : " + huellaString);

                HttpPost httppost = new HttpPost("http://192.168.5.32:8888/ComedorSnack-war/webresources/servicios/registrar/" + ci + "/" + huellaString);

                try {
                    logger.addRecordToLog("antes httpclient.execute(httppost)");

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity, "UTF-8");

                    logger.addRecordToLog("despues responseString : " + responseString);

                } catch (ClientProtocolException e) {

                    logger.addRecordToLog("ClientProtocolException  : " + e.getMessage());

                } catch (IOException e) {
                    logger.addRecordToLog("IOException : " + e.getMessage());
                }

                logger.addRecordToLog("despues");
                return null;
            }catch (Exception e) {
                //Log.e("RegistrarActivity.HttpRequestTask ", e.getMessage());
                logger.addRecordToLog("doInBackground : " + e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void greeting) {

            logger.addRecordToLog("onPostExecute : " + greeting);

            dialog.dismiss();
        }

    }

    public void btnServicio(View view){

        //Log.i("MV","btnServicio");
        logger.addRecordToLog("btnServicio");

        //new HttpRequestTask().execute("171123432" , mFpFeature);

    }*/

    private void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }

    /*@Override
    public void onBackPressed() {
    }*/

}

