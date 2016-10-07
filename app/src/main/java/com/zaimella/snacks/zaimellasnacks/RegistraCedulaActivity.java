package com.zaimella.snacks.zaimellasnacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zaimella.snacks.service.Constantes;

public class RegistraCedulaActivity extends AppCompatActivity {

    private EditText mNumeroCedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_cedula);

        mNumeroCedula = (EditText)findViewById(R.id.idTxtNumeroCedula);

    }

    public void btnContinuarRegistroHuella(View view){

        Intent intent = new Intent(this, RegistrarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString( Constantes.NUMERO_CEDULA , mNumeroCedula.getText().toString() );
        bundle.putString( Constantes.NOMBRE_USUARIO , "XXXXXXXX YYYYYYYY");
        intent.putExtras( bundle );
        startActivity(intent);

    }

}
