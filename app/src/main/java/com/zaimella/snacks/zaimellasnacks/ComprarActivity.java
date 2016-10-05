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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.TiposRespuesta;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

public class ComprarActivity extends AppCompatActivity {

    Logger logger;

    private ProgressDialog mProgressDialog;
    private FingerprintScanner mScanner;
    private FingerprintTask mTask;
    private ImageView mFingerprintImage;
    private ImageView mHuella1;
    private ImageView mHuella2;
    private ImageView mHuella3;
    private TextView mMensaje;
    //private Integer numeroHuella;
    byte[][] huellas = new byte[3][];


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

                    Toast.makeText(ComprarActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_UPDATE_IMAGE: {
                    logger.addRecordToLog("MSG_UPDATE_IMAGE");

                    mFingerprintImage.setImageBitmap((Bitmap) msg.obj);
                    //fmMensaje.setText("Ingrese la huella No: ");
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar);

        //Atributos
        //numeroHuella = 0;
        mFingerprintImage = (ImageView)findViewById(R.id.imagenHuella);
        mHuella1 = (ImageView)findViewById(R.id.huella1);
        mHuella2 = (ImageView)findViewById(R.id.huella2);
        mHuella3 = (ImageView)findViewById(R.id.huella3);
        mMensaje = (TextView)findViewById(R.id.mensaje);

        logger.addRecordToLog("mMensaje  : " + mMensaje);

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

                if ((error = Bione.initialize(ComprarActivity.this, FP_DB_PATH)) == Bione.RESULT_OK) {
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

                int error;
                Log.i("MV","BEFORE mScanner.close()");
                if ((error = mScanner.close()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV","fingerprint_device_close_failed");
                    logger.addRecordToLog("fingerprint_device_close_failed");
                }else {
                    Log.i("MV", "fingerprint_device_close_success");
                    logger.addRecordToLog("fingerprint_device_close_success");
                }

                if ((error = mScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV","fingerprint_device_power_off_failed");
                    logger.addRecordToLog("fingerprint_device_power_off_failed");
                }else{
                    logger.addRecordToLog("power_off success");
                }

                if ((error = Bione.exit()) != Bione.RESULT_OK) {
                    //showErrorDialog(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                    Log.i("MV","algorithm_cleanup_failed");
                    logger.addRecordToLog("algorithm_cleanup_failed");
                }else{
                    logger.addRecordToLog("algorithm_cleanup_failed success");
                }


            }
        }.start();

    }

    public void btnCapturarCompra(View view){
        try {
            logger.addRecordToLog("btnCapturar -0-");

            mFingerprintImage.setImageResource(R.drawable.nuevahuella);
            mMensaje.setText("Ingrese la huella");

            //Lanza la tarea asíncrona para ingreso de huella
            (new FingerprintTask(this)).execute();
            logger.addRecordToLog("btnCapturar -3-");

        }catch(Exception e){

            /*Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();*/

            logger.addRecordToLog("Exception btnCapturar : " + e.getMessage());
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

            //mFingerprintImage.setImageResource( R.drawable.errornuevahuella );

            //e.printStackTrace();

        }

    }

    private class FingerprintTask extends AsyncTask<Integer, Integer, ResultadoScanVO> {

        public Context context;
        private boolean mIsDone = false;
        //FingerprintImage fiExterno = null;

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
                        return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Cancelado");
                    }

                }while(res.error != FingerprintScanner.RESULT_OK);

                /*if (res.error != FingerprintScanner.RESULT_OK) {
                    //Tomar la huella nuevamente
                    return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error al tomar la huella");
                }*/

                //Huella ok
                fingerprintImage = (FingerprintImage) res.data;
                res = Bione.extractFeature(fingerprintImage);
                if (res.error != Bione.RESULT_OK) {
                    //showErrorDialog(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(res.error));
                    //Tomar la huella nuevamente
                    logger.addRecordToLog("Error al extraer la huella");
                    return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error al extraer la huella");
                }

                //Utilizar este fingerprint
                //huellas[numeroHuella] = (byte[]) res.data;
                //huellas
                //Bione.identify()
                fingerPrintFeature = (byte[]) res.data;
                int id = Bione.identify( fingerPrintFeature );
                //verifyTime = System.currentTimeMillis() - startTime;
                logger.addRecordToLog("id : " + id);
                if (id < 0) {
                    //showErrorDialog(getString(R.string.identify_failed_because_of_error), getFingerprintErrorString(id));
                    //break;
                    logger.addRecordToLog("Error en la identificación de la huella");
                    return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error en la identificación de la huella");
                }

                showInfoToast("Id " + id + " encontrado");

                /* logger.addRecordToLog("Extract feature");
                startTime = System.currentTimeMillis();
                res = Bione.extractFeature(fi);
                extractTime = System.currentTimeMillis() - startTime;
                if (res.error != Bione.RESULT_OK) {
                    logger.addRecordToLog("enroll_failed_because_of_extract_feature");
                    //showErrorDialog(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(res.error));
                    break;
                }*/

                //Feature

                //mFpFeature = fpFeat;
                //logger.addRecordToLog("ENROLL SUCCESS!!! feature : " + fpFeat);

                //Bione.makeTemplate()
                //updateFingerprintImage(fi);

                mIsDone = true;
                //ResultadoScanVO(TiposRespuesta respuesta , Integer numeroHuella , FingerprintImage fingerprintImage , byte[] fingerPrintFeature, String mensaje){
                return new ResultadoScanVO(TiposRespuesta.EXITO, null , fingerprintImage, fingerPrintFeature, "");

            }catch(Exception e){

                Log.e("MV" , e.getMessage() );
                logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

                return new ResultadoScanVO(TiposRespuesta.ERROR , null , null , null , e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(ResultadoScanVO respuestaScan) {
            try {
                super.onPostExecute(respuestaScan);

                logger.addRecordToLog("FingerprintTask.onPostExecute");

                if (respuestaScan.getRespuesta().equals(TiposRespuesta.EXITO)) {
                    logger.addRecordToLog("FingerprintTask.onPostExecute - EXITO");

                    /*logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan.getNumeroHuella() : " + respuestaScan.getNumeroHuella());
                    logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan.getFingerPrintFeature() : " + respuestaScan.getFingerPrintFeature());
                    logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan.getFingerprintImage() : " + respuestaScan.getFingerprintImage());
                    logger.addRecordToLog("FingerprintTask.onPostExecute - huellas : " + huellas);*/

                    //Actualiza la imagen de la huella
                    updateFingerprintImage(respuestaScan.getFingerprintImage());

                    ((ComprarActivity)context).ingresarCedula();

                } else {
                    logger.addRecordToLog("FingerprintTask.onPostExecute - ERROR");

                    mFingerprintImage.setImageResource(R.drawable.errornuevahuella);

                }

                //logger.addRecordToLog("after update image");
            }catch(Exception e){

                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();

                logger.addRecordToLog("Exception onPostExecute : " + s);
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
            //finish();

        }catch(Exception e){

            Log.e("MV" , e.getMessage());
            logger.addRecordToLog("Exception : RegistrarACtivity.invocarPaginaMenu: " + e.getMessage());
        }

    }

    private void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }


    public void ingresarCedula(){
        logger.addRecordToLog("ComprarActivity.ingresarCedula");

        Intent intent = new Intent(this, ComprarCedulaActivity.class);
        startActivity(intent);
        //finish();

    }

}
