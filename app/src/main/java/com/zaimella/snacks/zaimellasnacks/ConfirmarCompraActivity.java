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

public class ConfirmarCompraActivity extends AppCompatActivity {

    Logger logger;

    private TextView mNombrePersona;
    private TextView mValorCompra;
    private TextView mObservacionesCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_compra);

        mNombrePersona = (TextView) findViewById(R.id.idNombrePersona);
        mValorCompra = (TextView) findViewById(R.id.idValorCompra);
        mObservacionesCompra = (TextView) findViewById(R.id.idObservacionesCompra);

        Intent intent = getIntent();

        logger.addRecordToLog("NUMERO_CEDULA : " + intent.getStringExtra(Constantes.NUMERO_CEDULA));
        logger.addRecordToLog("ID_USUARIO_ARATEK : " + intent.getIntExtra(Constantes.ID_USUARIO_ARATEK, 0));
        logger.addRecordToLog("VALOR_COMPRA   : " + intent.getFloatExtra(Constantes.VALOR_COMPRA, 0));
        logger.addRecordToLog("OBSERVACIONES  : " + intent.getStringExtra(Constantes.OBSERVACIONES));
        logger.addRecordToLog("NOMBRE_USUARIO : " + intent.getStringExtra(Constantes.NOMBRE_USUARIO));

        StringBuilder sbNombrePersona = new StringBuilder();
        sbNombrePersona.append(Constantes.CONFIRMAR_COMPRA_NOMBRE)
                       .append(" ")
                       .append( intent.getStringExtra(Constantes.NOMBRE_USUARIO) );
        mNombrePersona.setText( sbNombrePersona.toString() );

        StringBuilder sbValorCompra = new StringBuilder();
        sbValorCompra.append(Constantes.CONFIRMAR_COMPRA_VALOR).append(Float.toString(intent.getFloatExtra(Constantes.VALOR_COMPRA, 0)));
        mValorCompra.setText(sbValorCompra.toString());

        StringBuilder sbObservacion = new StringBuilder();
        sbObservacion.append(Constantes.CONFIRMAR_COMPRA_OBSERVACION).append(intent.getStringExtra(Constantes.OBSERVACIONES));
        //mObservacionesCompra.setText(intent.getStringExtra(Constantes.OBSERVACIONES));
        mObservacionesCompra.setText(sbObservacion.toString());

    }

    public void btnConfirmarCompra(View view) {

        //Insertar en la bdd la conpra realizada
        ServicioBDD servicioBDD = new ServicioBDD(this);
        servicioBDD.abrirBD();
        //Registro registro = new Registro( mNumeroCedula.getText().toString() , idUsuarioAratek.toString() );
        Compra compra = new Compra();
        servicioBDD.insertarCompra( compra );
        servicioBDD.cerrarBD();




        final Context context = this;
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage("Compra realizada exitosamente!!!");
        builder.setTitle(R.string.mns_titulo)
                .setPositiveButton(R.string.mns_ok , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent( context , MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();


        /*Intent intent = new Intent( this , MenuActivity.class);
        startActivity(intent);
        finish();*/
    }

    public void btnCancelarCompra(View view) {

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
