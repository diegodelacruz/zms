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

    private TextView mCedulaIdentidad;
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
                    logger.addRecordToLog("MSG_SHOW_ERROR");
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    logger.addRecordToLog("MSG_SHOW_INFO");
                    Toast.makeText(UnicaCompraActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
                }
                case MSG_UPDATE_IMAGE: {
                    logger.addRecordToLog("MSG_UPDATE_IMAGE");
                    mImgHuella.setImageBitmap((Bitmap) msg.obj);
                    break;
                }
                case MSG_UPDATE_TEXT: {
                    logger.addRecordToLog("MSG_UPDATE_TEXT");
                    String[] texts = (String[]) msg.obj;
                    break;
                }
                case MSG_UPDATE_BUTTON: {
                    logger.addRecordToLog("MSG_UPDATE_BUTTON");
                    Boolean enable = (Boolean) msg.obj;
                    break;
                }
                case MSG_UPDATE_SN: {
                    logger.addRecordToLog("MSG_UPDATE_SN");
                    break;
                }
                case MSG_UPDATE_FW_VERSION: {
                    logger.addRecordToLog("MSG_UPDATE_FW_VERSION");
                    break;
                }
                case MSG_SHOW_PROGRESS_DIALOG: {
                    logger.addRecordToLog("MSG_SHOW_PROGRESS_DIALOG");
                    String[] info = (String[]) msg.obj;
                    mProgressDialog.setTitle(info[0]);
                    mProgressDialog.setMessage(info[1]);
                    mProgressDialog.show();
                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
                    logger.addRecordToLog("MSG_SHOW_PROGRESS_DIALOG");
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

        this.mCedulaIdentidad = (TextView)findViewById(R.id.idLblNumeroCedula);
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
        //Log.i("MV", "_openDevice_");
        logger.addRecordToLog("_openDevice_ inicio");

        new Thread() {

            @Override
            public void run() {
                logger.addRecordToLog("_openDevice_ inicio run");

                //openDeviceEnEjecucion = Boolean.TRUE;

                showProgressDialog(getString(R.string.mns_titulo), getString(R.string.preparing_device));

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

                if ((error = Bione.initialize(UnicaCompraActivity.this, Constantes.FP_DB_PATH)) == Bione.RESULT_OK) {
                    logger.addRecordToLog("algorithm_initialization_success");
                } else {
                    logger.addRecordToLog("algorithm_initialization_failed");
                }

                dismissProgressDialog();

                //openDeviceEnEjecucion = Boolean.FALSE;
                logger.addRecordToLog("_openDevice_ fin run");
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

        logger.addRecordToLog("UnicaCompraActivity.onPause");

        if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
            mTask.cancel(false);
            mTask.waitForDone();
        }

        this.closeDevice();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        logger.addRecordToLog("UnicaCompraActivity.onDestroy");

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
        logger.addRecordToLog("_closeDevice_");

        new Thread() {

            @Override
            public void run() {

                logger.addRecordToLog("_closeDevice_ inicio run");

                try {

                    /*while(openDeviceEnEjecucion){
                        logger.addRecordToLog("openDeviceEnEjecucion");
                    }*/

                    int error;
                    //Log.i("MV", "BEFORE mScanner.close()");
                    logger.addRecordToLog("_closeDevice_.UnicaCompraActivity.closeDevice.mHuellaScanner : " + mHuellaScanner);

                    if( mHuellaScanner==null ){
                        return;
                    }

                    if ((error = mHuellaScanner.close()) != FingerprintScanner.RESULT_OK) {
                        //Log.i("MV", "fingerprint_device_close_failed");
                        logger.addRecordToLog("fingerprint_device_close_failed");
                    } else {
                        //Log.i("MV", "fingerprint_device_close_success");
                        logger.addRecordToLog("fingerprint_device_close_success");
                    }

                    if ((error = mHuellaScanner.powerOff()) != FingerprintScanner.RESULT_OK) {
                        //Log.i("MV", "fingerprint_device_power_off_failed");
                        logger.addRecordToLog("fingerprint_device_power_off_failed");
                    } else {
                        logger.addRecordToLog("power_off success");
                    }

                    if ((error = Bione.exit()) != Bione.RESULT_OK) {
                        //showErrorDialog(getString(R.string.algorithm_cleanup_failed), getFingerprintErrorString(error));
                        //Log.i("MV", "algorithm_cleanup_failed");
                        logger.addRecordToLog("algorithm_cleanup_failed");
                    } else {
                        logger.addRecordToLog("algorithm_cleanup_failed success");
                    }

                    logger.addRecordToLog("_closeDevice_ fin");
                }catch(Exception e){

                    logger.addRecordToLog("UnicaCompraActivity.closeDevice : " + SnacksUtil.obtenerStackErrores(e));

                }

            }
        }.start();

    }

    public void btnCapturarCompra(View view) {
        try {
            logger.addRecordToLog("UnicaActivityHuella.btnCapturarCompra");

            mImgHuella.setImageResource(R.drawable.nuevahuella);

            //Lanza la tarea asíncrona para ingreso de huella
            this.mTask = new FingerprintTask(this);
            this.mTask.execute();

            logger.addRecordToLog("btnCapturar -3-");

        } catch (Exception e) {

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

        } catch (Exception e) {

            logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

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
            logger.addRecordToLog("FingerprintTask.onPreExecute");
        }

        @Override
        protected ResultadoScanVO doInBackground(Integer... params) {
            logger.addRecordToLog("UnicaCompraActivity.FingerprintTask.doInBackground");

            FingerprintImage fingerprintImage = null;
            byte[] fingerPrintFeature = null;
            Result resultado = null;
            Boolean ejecucionExitosa = Boolean.TRUE;
            Integer idUsuarioAratek = null;
            try {

                do {
                    //Prepara el scanner
                    int resultadoPrepare = mHuellaScanner.prepare();
                    //logger.addRecordToLog("UnicaCompraActivity.FingerprintTask.luego prepare : " + resultadoPrepare);
                    do {
                        resultado = mHuellaScanner.capture();
                    } while (resultado.error == FingerprintScanner.NO_FINGER && !isCancelled());
                    mHuellaScanner.finish();
                    if (isCancelled()) {
                        logger.addRecordToLog("UnicaCompraActivity.FingerprintTask.doInBackground isCancelled()");
                        //return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Cancelado", null);
                        ejecucionExitosa = false;
                        break;
                    }

                    if (resultado.error != FingerprintScanner.RESULT_OK) {
                        //showErrorDialog(getString(R.string.capture_image_failed), getFingerprintErrorString(res.error));
                        logger.addRecordToLog("UnicaCompraActivity.FingerprintTask.doInBackground capture_image_failed)");
                        ejecucionExitosa = false;
                        break;
                    }
                    //} while (resultado.error != FingerprintScanner.RESULT_OK);

                    //Huella ok|
                        fingerprintImage = (FingerprintImage) resultado.data;
                        logger.addRecordToLog("Fingerprint image quality is " + Bione.getFingerprintQuality(fingerprintImage));

                        resultado = Bione.extractFeature(fingerprintImage);
                        if (resultado.error != Bione.RESULT_OK) {
                            //showErrorDialog(getString(R.string.enroll_failed_because_of_extract_feature), getFingerprintErrorString(res.error));
                            //Tomar la huella nuevamente
                            logger.addRecordToLog("Error al extraer FEATURE");
                            //return new ResultadoScanVO(TiposRespuesta.ERROR, null, null, null, "Error al extraer la huella", null);
                            ejecucionExitosa = false;
                            break;
                        } else {
                            logger.addRecordToLog("OK extraer FEATURE");
                        }

                        //Utilizar este fingerprint
                        fingerPrintFeature = (byte[]) resultado.data;
                        idUsuarioAratek = Bione.identify(fingerPrintFeature);
                        //verifyTime = System.currentTimeMillis() - startTime;
                        logger.addRecordToLog("idUsuarioAratek : " + idUsuarioAratek);

                        if (idUsuarioAratek < 0) {
                            //showErrorDialog(getString(R.string.identify_failed_because_of_error), getFingerprintErrorString(id));
                            //break;
                            logger.addRecordToLog("Error en la identificación de la huella");
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

                logger.addRecordToLog("Exception updateFingerprintImage : " + e.getMessage());

                mIsDone = true;
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

                    ((UnicaCompraActivity) context).actualizarDatosComprador(respuestaScan.getIdUsuarioAratek());

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
                logger.addRecordToLog("not done");
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
        logger.addRecordToLog("UnicaCompraActivity.actualizarDatosComprador : " + idUsuarioAratek);
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
            logger.addRecordToLog("UnicaCompraActivity.btnConfirmarCompraUnica");

            if( this.mCedulaIdentidad.getText()==null || this.mCedulaIdentidad.getText().toString().contains("CI") ||
                 this.mCedulaIdentidad.getText().toString().contains("NOMBRE") ||
                    this.mCedulaIdentidad.getText().length()<=0 ){

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
            if( valorCompra.compareTo( new BigDecimal(0) )<=0 ){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Ingrese un valor mayor a 0.00");
                builder.setTitle(R.string.mns_titulo)
                        .setPositiveButton(R.string.mns_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setCancelable(false)
                        .show();

                return;
            }


            //Insertar en la bdd la conpra realizada
            ServicioBDD servicioBDD = new ServicioBDD(this);
            servicioBDD.abrirBD();
            //Registro registro = new Registro( mNumeroCedula.getText().toString() , idUsuarioAratek.toString() );
            Compra compra = new Compra();
            compra.setValorCompra( valorCompra.toString() );
            compra.setComentario("NA");
            compra.setCedula( this.mCedulaIdentidad.getText().toString() );
            //compra.setEstado( TiposRespuesta.EXITO.toString() );

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
            logger.addRecordToLog("Exception ConfirmarCompraActivity.btnConfirmarCompra: "+ s);

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

        this.mCedulaIdentidad.setText("CI:");
        this.mNombrePersona.setText("NOMBRE:");
        this.mValorCompra.setText("");
        this.mImgHuella.setImageResource(R.drawable.sinhuella);

    }

    public void btnMenuCompraUnica(View view){
        logger.addRecordToLog("UnicaCompraActivity.btnMenuCompraUnica");

        try {
            logger.addRecordToLog("antes close device");
            //this.closeDevice();

            logger.addRecordToLog("antes intent");
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
            finish();
        }catch(Exception e){

            logger.addRecordToLog("UnicaCOmpraActivity,btnMenuCompraUnica exception : " + SnacksUtil.obtenerStackErrores(e));

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


}
