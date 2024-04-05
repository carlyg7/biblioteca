package com.example.biblioteca_cm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainLibro extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.libro);

        // Inicializar Firebase y Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Obtener datos de libro
        obtenerDatosLibro();


        TextView volverCatalogo = findViewById(R.id.volverLibro);
        volverCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                Intent intent = new Intent(MainLibro.this, MainCatalogo.class);


            }
        });


    }

    private void obtenerDatosLibro(){

    }




}
