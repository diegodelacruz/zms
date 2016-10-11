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
import com.zaimella.snacks.service.Constantes;
import com.zaimella.snacks.service.EmpleadoVO;
import com.zaimella.snacks.service.Registro;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.ServicioBDD;
import com.zaimella.snacks.service.TiposRespuesta;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

public class ComprarActivityHuella extends AppCompatActivity {

    Logger logger;

    private String valorCompra;
    private String observaciones;
    //private ServicioBDD servicioBDD;

    private FingerprintTask mTask;
    private ImageView mImgHuella;
    private FingerprintScanner mHuellaScanner;

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

                    Toast.makeText(ComprarActivityHuella.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                }
                case MSG_UPDATE_IMAGE: {
                    logger.addRecordToLog("MSG_UPDATE_IMAGE");

                    mImgHuella.setImageBitmap((Bitmap) msg.obj);
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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_huella);

        mImgHuella = (ImageView) findViewById(R.id.imgHuellaVerificar);

        Intent intent = getIntent();

        this.valorCompra = intent.getStringExtra( Constantes.VALOR_COMPRA );
        Toast.makeText(ComprarActivityHuella.this, "valorCompra : " + this.valorCompra, Toast.LENGTH_LONG).show();

        observaciones = intent.getStringExtra(Constantes.OBSERVACIONES);
        Toast.makeText(ComprarActivityHuella.this, "observaciones: " + observaciones, Toast.LENGTH_LONG).show();

        //Instancia dispositivo
        mHuellaScanner = FingerprintScanner.getInstance();
        logger.addRecordToLog("AFTER FingerprintScanner.getInstance : " + mHuellaScanner.hasFinger().error);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("MV", "BEFORE openDevice()");
        openDevice();
        Log.i("MV", "AFTER openDevice()");
    }

    public void openDevice() {
        Log.i("MV", "_openDevice_");

        new Thread() {

            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));

                int error;
                if ((error = mHuellaScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
                    //showErrorDialog(getString(R.string.fingerprint_device_power_on_failed), getFingerprintErrorString(error));
                    logger.addRecordToLog("fingerprint_device_power_on_failed : " + error);
                } else {
                    logger.addRecordToLog("mScanner.powerOn() success : " + error);
                }

                Log.i("MV", "before mScanner.open()");
                //error = mScanner.open();
                //Log.i("MV", "after mScanner.open() : " + error);
                //logger.addRecordToLog("after mScanner.open() : " + error);
                if ((error = mHuellaScanner.open()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV", "mScanner.open() error");
                    Log.i("MV", "MSG_UPDATE_SN");
                    Log.i("MV", "MSG_UPDATE_FW_VERSION");

                    logger.addRecordToLog("mScanner.open() error");
                    logger.addRecordToLog("MSG_UPDATE_SN");
                    logger.addRecordToLog("MSG_UPDATE_FW_VERSION");

                } else {

                    Result res = mHuellaScanner.getSN();
                    res = mHuellaScanner.getFirmwareVersion();
                    logger.addRecordToLog("MSG_UPDATE_FW_VERSION : " + (String) res.data);

                    /*mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, (String) res.data)));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, (String) res.data)));
                    showInfoToast(getString(R.string.fingerprint_device_open_success));
                    enableControl(true);*/
                }

                if ((error = Bione.initialize(ComprarActivityHuella.this, Constantes.FP_DB_PATH)) == Bione.RESULT_OK) {
                    logger.addRecordToLog("algorithm_initialization_success");
                } else {
                    logger.addRecordToLog("algorithm_initialization_failed");
                }

                dismissProgressDialog();
            }
        }.start();

    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[]{title, message}));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    @Override
    protected void onPause() {

        logger.addRecordToLog("ComprarActivityHuella.onPause");

        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(false);
            mTask.waitForDone();
        }

        this.closeDevice();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        logger.addRecordToLog("ComprarActivityHuella.onDestroy");

        super.onDestroy();

        Log.i("MV", "before closeDevice");
        this.closeDevice();
        Log.i("MV", "after closeDevice");

        this.mHuellaScanner = null;
    }

    private void closeDevice() {
        Log.i("MV", "_closeDevice_");

        new Thread() {

            @Override
            public void run() {

                int error;
                Log.i("MV", "BEFORE mScanner.close()");
                if ((error = mHuellaScanner.close()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV", "fingerprint_device_close_failed");
                    logger.addRecordToLog("fingerprint_device_close_failed");
                } else {
                    Log.i("MV", "fingerprint_device_close_success");
                    logger.addRecordToLog("fingerprint_device_close_success");
                }

                if ((error = mHuellaScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV", "fingerprint_device_power_off_failed");
                    logger.addRecordToLog("fingerprint_device_power_off_failed");
                } else {
                    logger.addRecordToLog("power_off success");
                }

                if ((error = Bione.exit()) != Bione.RESULT_OK) {
                    //showErrorDialog(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                    Log.i("MV", "algorithm_cleanup_failed");
                    logger.addRecordToLog("algorithm_cleanup_failed");
                } else {
                    logger.addRecordToLog("algorithm_cleanup_failed success");
                }


            }
        }.start();

    }

    public void btnCapturarCompra(View view) {
        try {
            logger.addRecordToLog("ComprarActivityHuella.btnCapturarCompra");

            mImgHuella.setImageResource(R.drawable.nuevahuella);

            //Lanza la tarea asíncrona para ingreso de huella
            (new FingerprintTask(this)).execute();
            logger.addRecordToLog("btnCapturar -3-");

        } catch (Exception e) {

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            logger.addRecordToLog("Exception btnCapturar : " + s);
        }
    }

    public void btnCancelarCompra(View view) {
        logger.addRecordToLog("ComprarActivityHuella.btnCancelarCompra");

        Intent menuIntent = new Intent(this, MenuActivity.class);
        startActivity(menuIntent);

    }

    public void btnEditarCompra(View view){
        logger.addRecordToLog("ComprarActivityHuella.btnEditarCompra");

        Intent menuIntent = new Intent(this, ComprarActivityValor.class);
        startActivity(menuIntent);
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

        } catch (Exception e) {

            logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

            //mFingerprintImage.setImageResource( R.drawable.errornuevahuella );

            //e.printStackTrace();

        }

    }

    private class FingerprintTask extends AsyncTask<Integer, Integer, ResultadoScanVO> {

        public Context context;
        private boolean mIsDone = false;
        private ProgressDialog dialog = new ProgressDialog(ComprarActivityHuella.this);

        public FingerprintTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            //enableControl(false);
            logger.addRecordToLog("FingerprintTask.onPreExecute");
        }

        @Override
        protected ResultadoScanVO doInBackground(Integer... params) {
            logger.addRecordToLog("ComprarActivityHuella.FingerprintTask.doInBackground");

            FingerprintImage fingerprintImage = null;
            byte[] fingerPrintFeature = null;
            Result resultado = null;

            try {

                do {
                    //Prepara el scanner
                    int resultadoPrepare = mHuellaScanner.prepare();
                    //logger.addRecordToLog("ComprarActivityHuella.FingerprintTask.luego prepare : " + resultadoPrepare);

                    do {
                        resultado = mHuellaScanner.capture();
                    } while (resultado.error == FingerprintScanner.NO_FINGER && !isCancelled());
                    mHuellaScanner.finish();
                    if (isCancelled()) {
                        logger.addRecordToLog("ComprarActivityHuella.FingerprintTask.doInBackground isCancelled()");
                        return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Cancelado", null);
                    }

                } while (resultado.error != FingerprintScanner.RESULT_OK);

                //Huella ok
                fingerprintImage = (FingerprintImage) resultado.data;
                resultado = Bione.extractFeature(fingerprintImage);
                if (resultado.error != Bione.RESULT_OK) {
                    //showErrorDialog(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(res.error));
                    //Tomar la huella nuevamente
                    logger.addRecordToLog("Error al extraer FEATURE");
                    return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error al extraer la huella", null);
                } else {
                    logger.addRecordToLog("OK extraer FEATURE");
                }

                //Utilizar este fingerprint
                fingerPrintFeature = (byte[]) resultado.data;
                int idUsuarioAratek = Bione.identify(fingerPrintFeature);
                //verifyTime = System.currentTimeMillis() - startTime;
                logger.addRecordToLog("idUsuarioAratek : " + idUsuarioAratek);
                if (idUsuarioAratek < 0) {
                    //showErrorDialog(getString(R.string.identify_failed_because_of_error), getFingerprintErrorString(id));
                    //break;
                    logger.addRecordToLog("Error en la identificación de la huella");
                    return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error en la identificación de la huella", null);
                }

                showInfoToast("El ID encontrado es: " + idUsuarioAratek);
                //Toast.makeText(ComprarActivityHuella.this, "El ID encontrado es: " + idUsuarioAratek, Toast.LENGTH_LONG).show();

                mIsDone = true;
                return new ResultadoScanVO(TiposRespuesta.EXITO, null, fingerprintImage, fingerPrintFeature, "", idUsuarioAratek);

            } catch (Exception e) {

                logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

                return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, e.getMessage(), null);
            }
        }

        @Override
        protected void onPostExecute(ResultadoScanVO respuestaScan) {
            try {
                super.onPostExecute(respuestaScan);

                logger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan : " + respuestaScan);

                if (respuestaScan.getRespuesta().equals(TiposRespuesta.EXITO)) {
                    logger.addRecordToLog("FingerprintTask.onPostExecute - EXITO");

                    //Actualiza la imagen de la huella
                    updateFingerprintImage(respuestaScan.getFingerprintImage());

                    ((ComprarActivityHuella) context).confirmarCompra(respuestaScan.getIdUsuarioAratek());

                } else {
                    logger.addRecordToLog("FingerprintTask.onPostExecute - ERROR");

                    mImgHuella.setImageResource(R.drawable.errornuevahuella);

                }

                //logger.addRecordToLog("after update image");
            } catch (Exception e) {

                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();

                logger.addRecordToLog("Exception onPostExecute : " + s);
                mImgHuella.setImageResource(R.drawable.errornuevahuella);
            }
        }

        public void waitForDone() {
            while (!mIsDone) {
                logger.addRecordToLog("mIsDone");
            }
        }

    }

    public void invocarPaginaMenu() {

        logger.addRecordToLog("RegistrarACtivity.invocarPaginaMenu");

        try {

            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            //finish();

        } catch (Exception e) {

            Log.e("MV", e.getMessage());
            logger.addRecordToLog("Exception : RegistrarACtivity.invocarPaginaMenu: " + e.getMessage());
        }

    }

    private void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }

    public void confirmarCompra(Integer idUsuarioAratek) {
        logger.addRecordToLog("ComprarActivityHuella.confirmarCompra");
        logger.addRecordToLog("idUsuarioAratek : " + idUsuarioAratek);
        logger.addRecordToLog("valorCompra : " + this.valorCompra);
        logger.addRecordToLog("observaciones : " + observaciones);

        ServicioBDD servicioBDD = new ServicioBDD(this);

        //Inserta el registro en la BDD
        servicioBDD.abrirBD();
        EmpleadoVO empleadoVO = servicioBDD.obtenerNombreUsuario( idUsuarioAratek.toString() );
        //logger.addRecordToLog("ConfirmarCompra - nombreUsuario : " + nombreUsuario);
        servicioBDD.cerrarBD();

        Intent intent = new Intent(this, ConfirmarCompraActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.NUMERO_CEDULA , empleadoVO.getNumeroDocumento());
        bundle.putString(Constantes.VALOR_COMPRA, this.valorCompra);
        bundle.putString(Constantes.OBSERVACIONES, observaciones);
        bundle.putInt(Constantes.ID_USUARIO_ARATEK, idUsuarioAratek);
        bundle.putString(Constantes.NOMBRE_USUARIO, empleadoVO.getNombresCompletos());

        intent.putExtras(bundle);

        startActivity(intent);
        //finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "No está permitido regresar. Presione Aceptar.", Toast.LENGTH_SHORT).show();
    }

}
