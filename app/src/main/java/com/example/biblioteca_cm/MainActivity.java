package com.example.biblioteca_cm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "Registro de Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase y FireStore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Obtener referencia al TextView "Regístrate aquí"
        TextView signupLink = findViewById(R.id.signup_link);
        // Agregar OnClickListener al TextView "Regístrate aquí"
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });
    }
}
