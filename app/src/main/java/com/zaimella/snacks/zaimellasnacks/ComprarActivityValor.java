package com.zaimella.snacks.zaimellasnacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ComprarActivityValor extends AppCompatActivity {

    private EditText mTxtValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_valor);

        mTxtValor=(EditText) findViewById(R.id.idTxtValor);
        mTxtValor.requestFocus();

    }

    public void btnRegistrarHuella(View view){
        Log.d("MV","btnRegistrarHuella");

        //Invoca a comprar
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ComprarFragment fragment = new ComprarFragment();
        fragmentTransaction.replace( R.id.fragments_container , fragment);
        fragmentTransaction.commit();*/

        Intent intent = new Intent(this, ComprarActivityHuella.class);
        startActivity(intent);
    }

}
