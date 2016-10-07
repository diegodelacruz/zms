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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void btnComprar(View view){

        Log.d("MV","btnComprar");

        Intent intent = new Intent(this, ComprarActivityValor.class);
        startActivity(intent);
        //finish();

    }

    public void btnRegistrar(View view){

        Log.d("MV","btnRegistrar");

        //Intent intent = new Intent(this, RegistrarActivity.class);
        Intent intent = new Intent(this, RegistraCedulaActivity.class);
        startActivity(intent);

    }

}

