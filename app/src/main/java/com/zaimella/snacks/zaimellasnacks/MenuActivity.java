package com.zaimella.snacks.zaimellasnacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.zaimella.log.Logger;

public class MenuActivity extends AppCompatActivity {

    Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.addRecordToLog("MenuActivity.onCreate");
        //Log.d("MV","MenuActivity.onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*mFingerprintImage = (ImageView)findViewById(R.id.imagenHuella);
        mHuella1 = (ImageView)findViewById(R.id.huella1);
        mHuella2 = (ImageView)findViewById(R.id.huella2);
        mHuella3 = (ImageView)findViewById(R.id.huella3);
        mMensaje = (TextView)findViewById(R.id.mensaje);

        //Instancia dispositivo
        mScanner = FingerprintScanner.getInstance();
        logger.addRecordToLog("mScanner : " + mScanner.hasFinger().error);*/
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        Log.i("MV","BEFORE openDevice()");
        openDevice();
        Log.i("MV","AFTER openDevice()");
    }*/

    public void btnComprar(View view){

        Log.d("MV","btnComprar");

        //Invoca a comprar
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ComprarFragment fragment = new ComprarFragment();
        fragmentTransaction.replace( R.id.fragments_container , fragment);
        fragmentTransaction.commit();*/

        Intent intent = new Intent(this, ComprarActivityValor.class);
        startActivity(intent);
        //finish();

    }

    public void btnRegistrar(View view){

        Log.d("MV","btnRegistrar");

        //Invoca a registrar
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //RegistrarFragment fragment = new RegistrarFragment();
        IngresoCedulaFragment ingresoCedulaFragment = new IngresoCedulaFragment();
        fragmentTransaction.replace( R.id.fragments_container , ingresoCedulaFragment);
        fragmentTransaction.commit();*/

        Intent intent = new Intent(this, RegistrarActivity.class);
        startActivity(intent);

    }

    public void btnRegistrarHuella(View view){

        Log.d("MV","btnRegistrarHuella");

        //Invoca a registrar
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //RegistrarFragment fragment = new RegistrarFragment();
        RegistrarFragment registrarFragment = new RegistrarFragment();
        fragmentTransaction.replace( R.id.fragments_container , registrarFragment);
        fragmentTransaction.commit();*/


    }


}

