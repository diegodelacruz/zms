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
import com.zaimella.snacks.service.Constantes;

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
        logger.addRecordToLog("idUsuarioAratek : " + intent.getIntExtra(Constantes.ID_USUARIO_ARATEK, 0));
        logger.addRecordToLog("valorCompra : " + intent.getFloatExtra(Constantes.VALOR_COMPRA, 0));
        logger.addRecordToLog("observaciones : " + intent.getStringExtra(Constantes.OBSERVACIONES));

        StringBuilder sbNombrePersona = new StringBuilder();
        sbNombrePersona.append(Constantes.CONFIRMAR_COMPRA_NOMBRE).append(Integer.toString(intent.getIntExtra(Constantes.ID_USUARIO_ARATEK, 0)));
        //mNombrePersona.setText(Integer.toString(intent.getIntExtra(Constantes.ID_USUARIO_ARATEK, 0)));
        mNombrePersona.setText(sbNombrePersona.toString());

        StringBuilder sbValorCompra = new StringBuilder();
        sbValorCompra.append(Constantes.CONFIRMAR_COMPRA_VALOR).append(Float.toString(intent.getFloatExtra(Constantes.VALOR_COMPRA, 0)));
        mValorCompra.setText(sbValorCompra.toString());

        StringBuilder sbObservacion = new StringBuilder();
        sbObservacion.append(Constantes.CONFIRMAR_COMPRA_OBSERVACION).append(intent.getStringExtra(Constantes.OBSERVACIONES));
        //mObservacionesCompra.setText(intent.getStringExtra(Constantes.OBSERVACIONES));
        mObservacionesCompra.setText(sbObservacion.toString());

    }

    public void btnConfirmarCompra(View view) {

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
