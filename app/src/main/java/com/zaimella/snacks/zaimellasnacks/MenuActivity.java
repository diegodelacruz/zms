package com.zaimella.snacks.zaimellasnacks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zaimella.log.Logger;
import com.zaimella.snacks.service.ResultadoScanVO;
import com.zaimella.snacks.service.TiposRespuesta;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import cn.com.aratek.fp.Bione;
import cn.com.aratek.fp.FingerprintImage;
import cn.com.aratek.fp.FingerprintScanner;
import cn.com.aratek.util.Result;

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

        Intent intent = new Intent(this, ComprarActivity.class);
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

