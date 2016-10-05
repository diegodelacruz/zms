package com.zaimella.snacks.zaimellasnacks;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

    }

    public void btnComprar(View view){

        Log.d("MV","btnComprar");

        //Invoca a comprar
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ComprarFragment fragment = new ComprarFragment();
        fragmentTransaction.replace( R.id.fragments_container , fragment);
        fragmentTransaction.commit();

    }

    public void btnRegistrar(View view){

        Log.d("MV","btnRegistrar");

        //Invoca a registrar
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RegistrarFragment fragment = new RegistrarFragment();
        fragmentTransaction.replace( R.id.fragments_container , fragment);
        fragmentTransaction.commit();

    }
}
