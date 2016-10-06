package com.zaimella.snacks.zaimellasnacks;

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
        logger.addRecordToLog("idUsuarioAratek : " + intent.getIntExtra( Constantes.ID_USUARIO_ARATEK , 0 ));
        logger.addRecordToLog("valorCompra : " + intent.getFloatExtra( Constantes.VALOR_COMPRA , 0  ) );
        logger.addRecordToLog("observaciones : " + intent.getStringExtra( Constantes.OBSERVACIONES ));

        mNombrePersona.setText( Integer.toString( intent.getIntExtra( Constantes.ID_USUARIO_ARATEK , 0 ) ) );
        mValorCompra.setText( Float.toString( intent.getFloatExtra( Constantes.VALOR_COMPRA , 0  ) ) );
        mObservacionesCompra.setText( intent.getStringExtra( Constantes.OBSERVACIONES ) );

    }

    //public void btnRegistrarHuella(View view){
    public void btnConfirmarCompra(View view){

    }

    public void btnCancelarCompra(View view){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();

    }

}
