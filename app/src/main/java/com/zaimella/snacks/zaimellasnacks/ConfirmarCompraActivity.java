package com.zaimella.snacks.zaimellasnacks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.Compra;
import com.zaimella.snacks.service.Constantes;
import com.zaimella.snacks.service.Registro;
import com.zaimella.snacks.service.ServicioBDD;
import com.zaimella.snacks.service.TiposRespuesta;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class ConfirmarCompraActivity extends AppCompatActivity {

    Logger logger;

    private TextView mNumeroCedula;
    private TextView mNombrePersona;
    private TextView mValorCompra;
    private TextView mObservacionesCompra;
    private String valorCompra;
    private String cedula;
    private String observaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_compra);

        mNumeroCedula = (TextView) findViewById(R.id.idNumeroCedula);
        mNombrePersona = (TextView) findViewById(R.id.idNombrePersona);
        mValorCompra = (TextView) findViewById(R.id.idValorCompra);
        mObservacionesCompra = (TextView) findViewById(R.id.idObservacionesCompra);

        Intent intent = getIntent();

        logger.addRecordToLog("NUMERO_CEDULA : " + intent.getStringExtra(Constantes.NUMERO_CEDULA));
        logger.addRecordToLog("ID_USUARIO_ARATEK : " + intent.getIntExtra(Constantes.ID_USUARIO_ARATEK, 0));
        logger.addRecordToLog("VALOR_COMPRA   : " + intent.getFloatExtra(Constantes.VALOR_COMPRA, 0));
        logger.addRecordToLog("OBSERVACIONES  : " + intent.getStringExtra(Constantes.OBSERVACIONES));
        logger.addRecordToLog("NOMBRE_USUARIO : " + intent.getStringExtra(Constantes.NOMBRE_USUARIO));

        this.cedula = intent.getStringExtra(Constantes.NUMERO_CEDULA);
        mNumeroCedula.setText( intent.getStringExtra(Constantes.NUMERO_CEDULA) );
        mNombrePersona.setText( intent.getStringExtra(Constantes.NOMBRE_USUARIO) );

        this.valorCompra = intent.getStringExtra(Constantes.VALOR_COMPRA);
        mValorCompra.setText(  String.valueOf(this.valorCompra) );

        this.observaciones = intent.getStringExtra(Constantes.OBSERVACIONES);
        mObservacionesCompra.setText( intent.getStringExtra(Constantes.OBSERVACIONES) );
    }

    public void btnConfirmarCompra(View view) {

        Boolean exito = Boolean.TRUE;
        try {
            logger.addRecordToLog("ConfirmarCompraActivity.btnConfirmarCompra - mValorCompra.toString() : " + this.valorCompra);

            //Insertar en la bdd la conpra realizada
            ServicioBDD servicioBDD = new ServicioBDD(this);
            servicioBDD.abrirBD();
            //Registro registro = new Registro( mNumeroCedula.getText().toString() , idUsuarioAratek.toString() );
            Compra compra = new Compra();
            compra.setValorCompra( this.valorCompra );
            compra.setComentario( this.observaciones );
            compra.setCedula( this.cedula );
            //compra.setEstado( TiposRespuesta.EXITO.toString() );

            servicioBDD.insertarCompra(compra);
            servicioBDD.cerrarBD();

        }catch(Exception e){

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            exito = Boolean.FALSE;
            logger.addRecordToLog("Exception ConfirmarCompraActivity.btnConfirmarCompra: "+ s);

        }

        final Context context = this;
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
                .show();

    }

    public void btnCancelarCompra(View view) {

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
