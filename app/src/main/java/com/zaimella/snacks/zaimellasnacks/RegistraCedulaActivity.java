package com.zaimella.snacks.zaimellasnacks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zaimella.snacks.database.BaseHelper;
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

        //Buscar por la cedula dada
        BaseHelper bddSnacks = new BaseHelper(this);
        String nombreEmpleado = bddSnacks.buscarEmpleado( mNumeroCedula.getText().toString() );

        if( nombreEmpleado==null || nombreEmpleado.length()==0 ){

            //No existe el empleado
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            builder.setMessage(R.string.mns_usuario_no_existe)
                    .setTitle(R.string.mns_titulo);
            return;
        }

        //Redireccionar hacia la siguiente vista
        Intent intent = new Intent(this, RegistrarActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString( Constantes.NUMERO_CEDULA , mNumeroCedula.getText().toString() );
        bundle.putString( Constantes.NOMBRE_USUARIO , "XXXXXXXX YYYYYYYY");
        intent.putExtras( bundle );
        startActivity(intent);
    }

    public void btnRegistrarMenu(View view){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();

    }

}
