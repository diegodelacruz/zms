package com.zaimella.snacks.zaimellasnacks;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.FloatRange;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.Constantes;

import java.math.BigDecimal;

public class ComprarActivityValor extends AppCompatActivity {

    Logger logger;
    private EditText mTxtValor;
    private EditText mTxtObservaciones;

    private ImageButton btnBorrarValor, btnBorrarObservacion, btnComprarHome, btnComprarContinuar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.addRecordToLog("ComprarActivityValor.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_valor);

        mTxtValor = (EditText) findViewById(R.id.idTxtValor);
        mTxtObservaciones = (EditText) findViewById(R.id.idTxtObservaciones);
        mTxtValor.requestFocus();

        btnComprarContinuar = (ImageButton) findViewById(R.id.imgComprarContinuar);

        btnComprarContinuar.setEnabled(false);

        verificarCampos();
        onClickBorrarValor();
        onClickBorrarObservacion();
        onClickComprarHome();
    }

    public void btnRegistrarHuella(View view) {
        Log.d("MV", "btnRegistrarHuella");

        //Invoca a comprar
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ComprarFragment fragment = new ComprarFragment();
        fragmentTransaction.replace( R.id.fragments_container , fragment);
        fragmentTransaction.commit();*/

        float valor = Float.parseFloat( mTxtValor.getText().toString() );
        if( valor>10 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.mns_ingrese_valor_menor_10)
                    .setTitle(R.string.mns_titulo)
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }


        Intent intent = new Intent(this, ComprarActivityHuella.class);

        Bundle bundle = new Bundle();
        bundle.putString(Constantes.VALOR_COMPRA, mTxtValor.getText().toString());
        bundle.putString(Constantes.OBSERVACIONES, mTxtObservaciones.getText().toString());
        intent.putExtras(bundle);

        startActivity(intent);
    }

    //Acción cuando presiona el boton "X" junto al Valor de la Compra.
    public void onClickBorrarValor() {
        btnBorrarValor = (ImageButton) findViewById(R.id.imgBorrarValor);
        btnBorrarValor.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTxtValor = (EditText) findViewById(R.id.idTxtValor);
                        mTxtValor.setText("");
                    }
                }
        );
    }

    //Acción cuando presiona el boton "X" junto a la Observación.
    public void onClickBorrarObservacion() {
        btnBorrarObservacion = (ImageButton) findViewById(R.id.imgBorrarObservacion);
        btnBorrarObservacion.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTxtObservaciones = (EditText) findViewById(R.id.idTxtObservaciones);
                        mTxtObservaciones.setText("");
                    }
                }
        );
    }

    //Acción cuando presiona el boton Home
    public void onClickComprarHome() {
        btnComprarHome = (ImageButton) findViewById(R.id.imgComprarHome);
        btnComprarHome.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ComprarActivityValor.this, MenuActivity.class));
                    }
                }
        );
    }

    //Verifico si el campo del Valor de la Compra no está vacio.
    public void verificarCampos() {
        mTxtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (s.toString().equals("") || Float.parseFloat(mTxtValor.getText().toString()) > 5) {
                if (s.toString().equals("")) {
                    btnComprarContinuar.setEnabled(false);
                } else {
                    btnComprarContinuar.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //Toast.makeText(getApplicationContext(), "No está permitido regresar. Presione Aceptar.", Toast.LENGTH_SHORT).show();
    }

}
