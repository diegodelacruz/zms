package com.zaimella.snacks.zaimellasnacks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.Compra;
import com.zaimella.snacks.service.Constantes;
import com.zaimella.snacks.service.EmpleadoVO;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.ServicioBDD;
import com.zaimella.snacks.service.TiposRespuesta;
import com.zaimella.snacks.util.SnacksUtil;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

import static com.zaimella.snacks.zaimellasnacks.R.id.mensaje;


public class UnicaCompraActivity extends AppCompatActivity {


    Logger logger;
    //private String valorCompra;
    private String observaciones;
    //private Boolean openDeviceEnEjecucion;

    private EditText mCedulaIdentidad;
    private TextView mNombrePersona;
    private EditText mValorCompra;

    private FingerprintTask mTask;

    private ImageView mImgHuella;
    private FingerprintScanner mHuellaScanner;
    private ProgressDialog mProgressDialog;

    private ImageButton mImgBebida;
    private ImageButton mImgHelado;
    private ImageButton mImgSnacks;
    private ImageButton mImgVarios;

    private Boolean bebidaSeleccionado;
    private Boolean heladoSeleccionado;
    private Boolean snacksSeleccionado;
    private Boolean variosSeleccionado;

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
                    //logger.addRecordToLog("MSG_SHOW_ERROR");
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    //logger.addRecordToLog("MSG_SHOW_INFO");
                    Toast.makeText(UnicaCompraActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                }
                case MSG_UPDATE_IMAGE: {
                    //loger.addRecordToLog("MSG_UPDATE_IMAGE");
                    mImgHuella.setImageBitmap((Bitmap) msg.obj);
                    break;
                }
                case MSG_UPDATE_TEXT: {
                    //loger.addRecordToLog("MSG_UPDATE_TEXT");
                    String[] texts = (String[]) msg.obj;
                    break;
                }
                case MSG_UPDATE_BUTTON: {
                    //loger.addRecordToLog("MSG_UPDATE_BUTTON");
                    Boolean enable = (Boolean) msg.obj;
                    break;
                }
                case MSG_UPDATE_SN: {
                    //loger.addRecordToLog("MSG_UPDATE_SN");
                    break;
                }
                case MSG_UPDATE_FW_VERSION: {
                    //loger.addRecordToLog("MSG_UPDATE_FW_VERSION");
                    break;
                }
                case MSG_SHOW_PROGRESS_DIALOG: {
                    //loger.addRecordToLog("MSG_SHOW_PROGRESS_DIALOG");
                    String[] info = (String[]) msg.obj;
                    mProgressDialog.setTitle(info[0]);
                    mProgressDialog.setMessage(info[1]);
                    mProgressDialog.show();
                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
                    //loger.addRecordToLog("MSG_SHOW_PROGRESS_DIALOG");
                    mProgressDialog.dismiss();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unica_compra);

        //Instancia dispositivo
        this.mHuellaScanner = FingerprintScanner.getInstance();

        this.mCedulaIdentidad = (EditText)findViewById(R.id.idTxtCedula);
        this.mNombrePersona = (TextView)findViewById(R.id.idLblNombrePersona);
        this.mValorCompra = (EditText) findViewById(R.id.idTxtValor);
        this.mImgHuella = (ImageView) findViewById(R.id.imgUCHuellaVerificar);

        this.mImgBebida = (ImageButton) findViewById(R.id.idBebida);
        this.mImgHelado = (ImageButton) findViewById(R.id.idHelado);
        this.mImgSnacks = (ImageButton) findViewById(R.id.idSnacks);
        this.mImgVarios = (ImageButton) findViewById(R.id.idVarios);

        this.bebidaSeleccionado = false;
        this.heladoSeleccionado = false;
        this.snacksSeleccionado = false;
        this.variosSeleccionado = false;

        this.inicializaCompraUnica();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        //loger.addRecordToLog("AFTER FingerprintScanner.getInstance : " + mHuellaScanner.hasFinger().error);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.i("MV", "BEFORE openDevice()");
        openDevice();

        Log.i("MV", "AFTER openDevice()");
    }

    public void openDevice() {
        //Log.i("MV", "_openDevice_");
        //loger.addRecordToLog("_openDevice_ inicio");

        new Thread() {

            @Override
            public void run() {
                //loger.addRecordToLog("_openDevice_ inicio run");

                //openDeviceEnEjecucion = Boolean.TRUE;

                showProgressDialog(getString(R.string.mns_titulo), getString(R.string.preparing_device));

                int error;
                if ((error = mHuellaScanner.powerOn()) != FingerprintScanner.RESULT_OK) {
                    //showErrorDialog(getString(R.string.fingerprint_device_power_on_failed), getFingerprintErrorString(error));
                    //loger.addRecordToLog("fingerprint_device_power_on_failed : " + error);
                } else {
                    //loger.addRecordToLog("mScanner.powerOn() success : " + error);
                }

                Log.i("MV", "before mScanner.open()");
                //error = mScanner.open();
                //Log.i("MV", "after mScanner.open() : " + error);
                //logger.addRecordToLog("after mScanner.open() : " + error);
                if ((error = mHuellaScanner.open()) != FingerprintScanner.RESULT_OK) {
                    Log.i("MV", "mScanner.open() error");
                    Log.i("MV", "MSG_UPDATE_SN");
                    Log.i("MV", "MSG_UPDATE_FW_VERSION");

                    //loger.addRecordToLog("mScanner.open() error");
                    //loger.addRecordToLog("MSG_UPDATE_SN");
                    //loger.addRecordToLog("MSG_UPDATE_FW_VERSION");

                } else {

                    Result res = mHuellaScanner.getSN();
                    res = mHuellaScanner.getFirmwareVersion();
                    //loger.addRecordToLog("MSG_UPDATE_FW_VERSION : " + (String) res.data);

                    /*mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, getString(R.string.fps_sn, (String) res.data)));
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_FW_VERSION, getString(R.string.fps_fw, (String) res.data)));
                    showInfoToast(getString(R.string.fingerprint_device_open_success));
                    enableControl(true);*/
                }

                if ((error = Bione.initialize(UnicaCompraActivity.this, Constantes.FP_DB_PATH)) == Bione.RESULT_OK) {
                    //loger.addRecordToLog("algorithm_initialization_success");
                } else {
                    //loger.addRecordToLog("algorithm_initialization_failed");
                }

                dismissProgressDialog();

                //openDeviceEnEjecucion = Boolean.FALSE;
                //loger.addRecordToLog("_openDevice_ fin run");
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

        //loger.addRecordToLog("UnicaCompraActivity.onPause");

        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(false);
            mTask.waitForDone();
        }

        this.closeDevice();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //loger.addRecordToLog("UnicaCompraActivity.onDestroy");

        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(false);
            mTask.waitForDone();
        }

        super.onDestroy();

        Log.i("MV", "before closeDevice");
        this.closeDevice();
        Log.i("MV", "after closeDevice");

        this.mHuellaScanner = null;
    }

    private void closeDevice() {
        //loger.addRecordToLog("_closeDevice_");

        new Thread() {

            @Override
            public void run() {

                //loger.addRecordToLog("_closeDevice_ inicio run");

                try {

                    /*while(openDeviceEnEjecucion){
                        logger.addRecordToLog("openDeviceEnEjecucion");
                    }*/

                    int error;
                    //Log.i("MV", "BEFORE mScanner.close()");
                    //loger.addRecordToLog("_closeDevice_.UnicaCompraActivity.closeDevice.mHuellaScanner : " + mHuellaScanner);

                    if( mHuellaScanner==null ){
                        return;
                    }

                    if ((error = mHuellaScanner.close()) != FingerprintScanner.RESULT_OK) {
                        //Log.i("MV", "fingerprint_device_close_failed");
                        //loger.addRecordToLog("fingerprint_device_close_failed");
                    } else {
                        //Log.i("MV", "fingerprint_device_close_success");
                        //loger.addRecordToLog("fingerprint_device_close_success");
                    }

                    if ((error = mHuellaScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                        //Log.i("MV", "fingerprint_device_power_off_failed");
                        //loger.addRecordToLog("fingerprint_device_power_off_failed");
                    } else {
                        //loger.addRecordToLog("power_off success");
                    }

                    if ((error = Bione.exit()) != Bione.RESULT_OK) {
                        //showErrorDialog(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                        //Log.i("MV", "algorithm_cleanup_failed");
                        //loger.addRecordToLog("algorithm_cleanup_failed");
                    } else {
                        //loger.addRecordToLog("algorithm_cleanup_failed success");
                    }

                    //loger.addRecordToLog("_closeDevice_ fin");
                }catch(Exception e){

                    //loger.addRecordToLog("UnicaCompraActivity.closeDevice : " + SnacksUtil.obtenerStackErrores(e));

                }

            }
        }.start();

    }

    public void btnCapturarCompra(View view) {
        try {
            //loger.addRecordToLog("UnicaActivityHuella.btnCapturarCompra");

            mImgHuella.setImageResource(R.drawable.nuevahuella);

            //Lanza la tarea asíncrona para ingreso de huella
            this.mTask = new FingerprintTask(this);
            this.mTask.execute();

            //loger.addRecordToLog("btnCapturar -3-");

        } catch (Exception e) {

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            //loger.addRecordToLog("Exception btnCapturar : " + s);
        }
    }

    private void updateFingerprintImage(FingerprintImage fi) {
        //loger.addRecordToLog("updateFingerprintImage : " + fi);

        try {
            byte[] fpBmp = null;
            Bitmap bitmap;
            if (fi == null || (fpBmp = fi.convert2Bmp()) == null || (bitmap = BitmapFactory.decodeByteArray(fpBmp, 0, fpBmp.length)) == null) {
                //loger.addRecordToLog("updateFingerprintImage sin huella ");
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sinhuella);
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_IMAGE, bitmap));

        } catch (Exception e) {

            //loger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

            //mFingerprintImage.setImageResource( R.drawable.errornuevahuella );

            //e.printStackTrace();

        }

    }

    private class FingerprintTask extends AsyncTask<Integer, Integer, ResultadoScanVO> {

        public Context context;
        private boolean mIsDone = false;
        private ProgressDialog dialog = new ProgressDialog(UnicaCompraActivity.this);

        public FingerprintTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            //enableControl(false);
            //loger.addRecordToLog("FingerprintTask.onPreExecute");
        }

        @Override
        protected ResultadoScanVO doInBackground(Integer... params) {
            //loger.addRecordToLog("UnicaCompraActivity.FingerprintTask.doInBackground");

            FingerprintImage fingerprintImage = null;
            byte[] fingerPrintFeature = null;
            Result resultado = null;
            Boolean ejecucionExitosa = Boolean.TRUE;
            Integer idUsuarioAratek = null;
            try {

                do {
                    //Prepara el scanner
                    int resultadoPrepare = mHuellaScanner.prepare();
                    ////loger.addRecordToLog("UnicaCompraActivity.FingerprintTask.luego prepare : " + resultadoPrepare);
                    do {
                        resultado = mHuellaScanner.capture();
                    } while (resultado.error == FingerprintScanner.NO_FINGER && !isCancelled());
                    mHuellaScanner.finish();
                    if (isCancelled()) {
                        //loger.addRecordToLog("UnicaCompraActivity.FingerprintTask.doInBackground isCancelled()");
                        //return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Cancelado", null);
                        ejecucionExitosa = false;
                        break;
                    }

                    if (resultado.error != FingerprintScanner.RESULT_OK) {
                        //showErrorDialog(getString(R.string.capture_image_failed), getFingerprintErrorString(res.error));
                        //loger.addRecordToLog("UnicaCompraActivity.FingerprintTask.doInBackground capture_image_failed)");
                        ejecucionExitosa = false;
                        break;
                    }
                    //} while (resultado.error != FingerprintScanner.RESULT_OK);

                    //Huella ok|
                        fingerprintImage = (FingerprintImage) resultado.data;
                        //loger.addRecordToLog("Fingerprint image quality is " + Bione.getFingerprintQuality(fingerprintImage));

                        resultado = Bione.extractFeature(fingerprintImage);
                        if (resultado.error != Bione.RESULT_OK) {
                            //showErrorDialog(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(res.error));
                            //Tomar la huella nuevamente
                            //loger.addRecordToLog("Error al extraer FEATURE");
                            //return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error al extraer la huella", null);
                            ejecucionExitosa = false;
                            break;
                        } else {
                            //loger.addRecordToLog("OK extraer FEATURE");
                        }

                        //Utilizar este fingerprint
                        fingerPrintFeature = (byte[]) resultado.data;
                        idUsuarioAratek = Bione.identify(fingerPrintFeature);
                        //verifyTime = System.currentTimeMillis() - startTime;
                        //loger.addRecordToLog("idUsuarioAratek : " + idUsuarioAratek);

                        if (idUsuarioAratek < 0) {
                            //showErrorDialog(getString(R.string.identify_failed_because_of_error), getFingerprintErrorString(id));
                            //break;
                            //loger.addRecordToLog("Error en la identificación de la huella");
                            //return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error en la identificación de la huella", null);
                            ejecucionExitosa = false;
                            break;
                        }

                        showInfoToast("El ID encontrado es: " + idUsuarioAratek);
                        //Toast.makeText(UnicaCompraActivity.this, "El ID encontrado es: " + idUsuarioAratek, Toast.LENGTH_LONG).show();

                }while (false);

                mIsDone = true;

                if( ejecucionExitosa ){

                    return new ResultadoScanVO(TiposRespuesta.EXITO, null, fingerprintImage, fingerPrintFeature, "", idUsuarioAratek);

                }

                return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error en la identificación de la huella", null);

            } catch (Exception e) {

                //loger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

                mIsDone = true;
                return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, e.getMessage(), null);
            }

        }

        @Override
        protected void onPostExecute(ResultadoScanVO respuestaScan) {
            try {
                super.onPostExecute(respuestaScan);

                //loger.addRecordToLog("FingerprintTask.onPostExecute - respuestaScan : " + respuestaScan);

                if (respuestaScan.getRespuesta().equals(TiposRespuesta.EXITO)) {
                    //loger.addRecordToLog("FingerprintTask.onPostExecute - EXITO");

                    //Actualiza la imagen de la huella
                    updateFingerprintImage(respuestaScan.getFingerprintImage());

                    ((UnicaCompraActivity) context).actualizarDatosComprador(respuestaScan.getIdUsuarioAratek());

                } else {
                    //loger.addRecordToLog("FingerprintTask.onPostExecute - ERROR");

                    mImgHuella.setImageResource(R.drawable.errornuevahuella);

                }

                //logger.addRecordToLog("after update image");
            } catch (Exception e) {

                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                e.printStackTrace(printWriter);
                String s = writer.toString();

                //loger.addRecordToLog("Exception onPostExecute : " + s);
                mImgHuella.setImageResource(R.drawable.errornuevahuella);
            }
        }

        public void waitForDone() {
            while (!mIsDone) {
                //loger.addRecordToLog("not done");
            }
        }

    }

    private void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "No está permitido regresar. Presione Aceptar.", Toast.LENGTH_SHORT).show();
    }

    public void actualizarDatosComprador(Integer idUsuarioAratek) {
        //loger.addRecordToLog("UnicaCompraActivity.actualizarDatosComprador : " + idUsuarioAratek);
        //logger.addRecordToLog("idUsuarioAratek : " + idUsuarioAratek);
        //logger.addRecordToLog("valorCompra : " + this.mValorCompra.getText().toString());

        ServicioBDD servicioBDD = new ServicioBDD(this);
        servicioBDD.abrirBD();
        EmpleadoVO empleadoVO = servicioBDD.obtenerNombreUsuario( idUsuarioAratek.toString() );
        servicioBDD.cerrarBD();

        if( empleadoVO==null ){

            StringBuilder mensaje = new StringBuilder();
            mensaje.append("No se encuentra registrado el usuario con id aratek: ")
                    .append( idUsuarioAratek );

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage( mensaje.toString() );
            builder.setTitle(R.string.mns_titulo)
                    .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //Intent intent = new Intent(context, MenuActivity.class);
                            //startActivity(intent);
                            //finish();
                            return;

                        }
                    })
                    .setCancelable(false)
                    .show();

        }else{

            mCedulaIdentidad.setText( empleadoVO.getNumeroDocumento() );
            mNombrePersona.setText( empleadoVO.getNombresCompletos() );

        }

        //logger.addRecordToLog("observaciones : " + observaciones);

        /*Intent intent = new Intent(this, ConfirmarCompraActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.NUMERO_CEDULA , empleadoVO.getNumeroDocumento());
        bundle.putString(Constantes.VALOR_COMPRA, this.valorCompra);
        bundle.putString(Constantes.OBSERVACIONES, observaciones);
        bundle.putInt(Constantes.ID_USUARIO_ARATEK, idUsuarioAratek);
        bundle.putString(Constantes.NOMBRE_USUARIO, empleadoVO.getNombresCompletos());

        intent.putExtras(bundle);

        startActivity(intent);
        //finish();*/
        //aqui


        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("XXXYYYYY confirma la compra por xxxx !!!?");
        builder.setTitle(R.string.mns_titulo)
                .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Intent intent = new Intent(context, MenuActivity.class);
                        //startActivity(intent);
                        //finish();


                        confirmarCompra();

                        Toast.makeText(UnicaCompraActivity.this, "Compra confirmada" , Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.mns_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //Intent intent = new Intent(context, MenuActivity.class);
                        //startActivity(intent);
                        //finish();


                        cancelarCompra();

                        Toast.makeText(UnicaCompraActivity.this, "Compra confirmada" , Toast.LENGTH_LONG).show();
                    }
                })
                .setCancelable(false)
                .show();
        */
    }

    public void btnConfirmarCompraUnica(View view){

        Boolean exito = Boolean.TRUE;
        try {
            //loger.addRecordToLog("UnicaCompraActivity.btnConfirmarCompraUnica");

            if( this.mCedulaIdentidad.getText()==null || this.mCedulaIdentidad.getText().length()<=0 || this.mCedulaIdentidad.getText().toString().contains("CI") ||
                this.mNombrePersona.getText()==null || this.mNombrePersona.getText().length()<=0 || this.mNombrePersona.getText().toString().contains("NOMBRE") ){

                //Ingrese la cedula
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Debe seleccionar un usuario para proceder");
                builder.setTitle(R.string.mns_titulo)
                        .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setCancelable(false)
                        .show();

                return;
            }

            if( this.mValorCompra.getText()==null || this.mValorCompra.getText().length()<=0 ){

                //Ingrese el valor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Ingrese el valor de la compra");
                builder.setTitle(R.string.mns_titulo)
                        .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setCancelable(false)
                        .show();

                return;
            }


            BigDecimal valorCompra = new BigDecimal( this.mValorCompra.getText().toString() );
            if( valorCompra.compareTo( new BigDecimal(0) )<=0 || valorCompra.compareTo( new BigDecimal(10) )>0){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Ingrese un valor mayor a 0 y menor a 10");
                builder.setTitle(R.string.mns_titulo)
                        .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setCancelable(false)
                        .show();

                return;
            }

            //Generar las observaciones
            StringBuilder observaciones = new StringBuilder();

            if( this.bebidaSeleccionado ){
                observaciones.append("bebidas");
            }

            if( this.snacksSeleccionado ){
                if( observaciones.length()>0 ){
                    observaciones.append(",");
                }
                observaciones.append("snacks");
            }

            if( this.heladoSeleccionado ){
                if( observaciones.length()>0 ){
                    observaciones.append(",");
                }
                observaciones.append("helados");
            }

            if( this.variosSeleccionado ){
                if( observaciones.length()>0 ){
                    observaciones.append(",");
                }
                observaciones.append("servicio_comedor");
            }

            if( observaciones.length()==0 ){
                observaciones.append("NA");
            }

            //Insertar en la bdd la conpra realizada
            ServicioBDD servicioBDD = new ServicioBDD(this);
            servicioBDD.abrirBD();
            Compra compra = new Compra();
            compra.setValorCompra( valorCompra.toString() );
            compra.setComentario( observaciones.toString() );
            compra.setCedula( this.mCedulaIdentidad.getText().toString() );

            servicioBDD.insertarCompra(compra);
            servicioBDD.cerrarBD();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Compra realizada exitosamente");
            builder.setTitle(R.string.mns_titulo)
                    .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            inicializaCompraUnica();

                        }
                    })
                    .setCancelable(false)
                    .show();

        }catch(Exception e){

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            exito = Boolean.FALSE;
            //loger.addRecordToLog("Exception ConfirmarCompraActivity.btnConfirmarCompra: "+ s);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No es posible realizar la compra, consulte con el administrador");
            builder.setTitle(R.string.mns_titulo)
                    .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setCancelable(false)
                    .show();

        }

        /*final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if( exito ){
            builder.setMessage("Compra realizada exitosamente!!!");
        }else{
            builder.setMessage("No es posible completar la compra!!!");
        }

        builder.setTitle(R.string.mns_titulo)
                .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(context, MenuActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .setCancelable(false)
                .show();
        */

    }

    public void btnCancelarCompraUnica(View view){

        Toast.makeText(UnicaCompraActivity.this, "CANCELAR COMPRA", Toast.LENGTH_LONG).show();
        this.inicializaCompraUnica();

    }

    public void inicializaCompraUnica(){

        this.mCedulaIdentidad.requestFocus();
        this.mCedulaIdentidad.setText("");
        this.mNombrePersona.setText("");
        this.mValorCompra.setText("");
        this.mImgHuella.setImageResource(R.drawable.sinhuella);
        this.mImgBebida.setImageResource(R.drawable.cb_bebida_ss);
        this.mImgHelado.setImageResource(R.drawable.cb_helado_ss);
        this.mImgSnacks.setImageResource(R.drawable.cb_snacks_ss);
        this.mImgVarios.setImageResource(R.drawable.cb_varios_ss);

    }

    public void btnMenuCompraUnica(View view){
        //loger.addRecordToLog("UnicaCompraActivity.btnMenuCompraUnica");

        try {
            //loger.addRecordToLog("antes close device");
            //this.closeDevice();

            //loger.addRecordToLog("antes intent");
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        }catch(Exception e){

            //loger.addRecordToLog("UnicaCOmpraActivity,btnMenuCompraUnica exception : " + SnacksUtil.obtenerStackErrores(e));

        }
    }

    public void btnOnClickBebidas(View view){

        if( this.bebidaSeleccionado ){
            this.mImgBebida.setImageResource(R.drawable.cb_bebida_ss);
            this.bebidaSeleccionado = false;
        }else{
            this.mImgBebida.setImageResource(R.drawable.cb_bebida_cs);
            this.bebidaSeleccionado = true;
        }

    }


    public void btnOnClickHelados(View view){

        if( this.heladoSeleccionado ){
            this.mImgHelado.setImageResource(R.drawable.cb_helado_ss);
            this.heladoSeleccionado = false;
        }else{
            this.mImgHelado.setImageResource(R.drawable.cb_helado_cs);
            this.heladoSeleccionado = true;
        }

    }


    public void btnOnClickSnacks(View view){

        if( this.snacksSeleccionado ) {
            this.mImgSnacks.setImageResource(R.drawable.cb_snacks_ss);
            this.snacksSeleccionado = false;
        }else{
            this.mImgSnacks.setImageResource(R.drawable.cb_snacks_cs);
            this.snacksSeleccionado = true;
        }

    }


    public void btnOnClickVarios(View view){

        if( this.variosSeleccionado ) {
            this.mImgVarios.setImageResource(R.drawable.cb_varios_ss);
            this.variosSeleccionado = false;
        }else{
            this.mImgVarios.setImageResource(R.drawable.cb_varios_cs);
            this.variosSeleccionado = true;
        }

    }

    public void btnOnClickCedulaIdentidad(View view){

        String numeroCedula = this.mCedulaIdentidad.getText().toString();

        if( numeroCedula==null || numeroCedula.length()==0 ){
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ingrese el número de cédula");
            builder.setTitle(R.string.mns_titulo)
                    .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setCancelable(false)
                    .show();

            return;*/
            return;
        }


        ServicioBDD srvEmpleado = new ServicioBDD(this);
        srvEmpleado.abrirBD();
        String empleado = srvEmpleado.buscarEmpleado( numeroCedula );

        if( empleado==null || empleado.length()==0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ingrese el número de cédula");
            builder.setTitle(R.string.mns_titulo)
                    .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    })
                    .setCancelable(false)
                    .show();

            return;
        }

        //Asigna el nombre de la persona
        this.mNombrePersona.setText( empleado );
        this.mValorCompra.requestFocus();

    }


}
