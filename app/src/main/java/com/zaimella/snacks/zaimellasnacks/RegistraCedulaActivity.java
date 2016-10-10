package com.zaimella.snacks.zaimellasnacks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.zaimella.log.Logger;
import com.zaimella.snacks.database.BaseHelper;
import com.zaimella.snacks.service.Constantes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class RegistraCedulaActivity extends AppCompatActivity {

    Logger logger;
    private EditText mNumeroCedula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_cedula);

        mNumeroCedula = (EditText)findViewById(R.id.idTxtNumeroCedula);

    }

    public void btnContinuarRegistroHuella(View view){

        try {
            logger.addRecordToLog("RegistraCedulaActivity.btnContinuarRegistroHuella");

            //Buscar por la cedula dada
            BaseHelper bddSnacks = new BaseHelper(this);
            String numeroCedula = mNumeroCedula.getText().toString();

            logger.addRecordToLog("RegistraCedulaActivity.numeroCedula : " + numeroCedula);

            if (numeroCedula == null || numeroCedula.length() == 0) {
                //Ingrese el número de cédula
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.mns_ingrese_cedula)
                        .setTitle(R.string.mns_titulo)
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            //Validar si la cédula ingresada ya existe
            Boolean existeComprador = bddSnacks.existeComprador(numeroCedula);
            logger.addRecordToLog("RegistraCedulaActivity.existeComprador : " + existeComprador);

            if (existeComprador) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.mns_usuario_existe)
                        .setTitle(R.string.mns_titulo)
                        .setPositiveButton(R.string.mns_ok, null)
                        .show();
                return;

            }


            String nombreEmpleado = bddSnacks.buscarEmpleado(numeroCedula);
            logger.addRecordToLog("RegistraCedulaActivity.nombreEmpleado : " + nombreEmpleado);
            if (nombreEmpleado == null || nombreEmpleado.length() == 0) {
                //No existe el empleado
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.mns_cedula_no_encontrada)
                        .setTitle(R.string.mns_titulo)
                        .setPositiveButton(R.string.mns_ok, null)
                        .show();
                return;
            }


            //Redireccionar hacia la siguiente vista
            Intent intent = new Intent(this, RegistrarActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(Constantes.NUMERO_CEDULA, mNumeroCedula.getText().toString());
            bundle.putString(Constantes.NOMBRE_USUARIO, nombreEmpleado);
            intent.putExtras(bundle);
            startActivity(intent);

        }catch (Exception e){

            Log.d("MV", e.getMessage());

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            logger.addRecordToLog("RegistraCedulaActivity.btnContinuarRegistroHuella : " + s);
        }

    }

    public void btnRegistrarMenu(View view){

        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();

    }

}
