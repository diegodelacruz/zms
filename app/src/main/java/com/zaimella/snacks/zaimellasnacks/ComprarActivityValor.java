package com.zaimella.snacks.zaimellasnacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private ImageButton btnBorrarValor, btnBorrarObservacion, btnComprarHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.addRecordToLog("ComprarActivityValor.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_valor);

        mTxtValor = (EditText) findViewById(R.id.idTxtValor);
        mTxtObservaciones = (EditText) findViewById(R.id.idTxtObservaciones);
        mTxtValor.requestFocus();

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

        Intent intent = new Intent(this, ComprarActivityHuella.class);

        Bundle bundle = new Bundle();
        bundle.putFloat(Constantes.VALOR_COMPRA, Float.parseFloat(mTxtValor.getText().toString()));
        bundle.putString(Constantes.OBSERVACIONES, mTxtObservaciones.getText().toString());
        intent.putExtras(bundle);

        startActivity(intent);
    }

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
}
