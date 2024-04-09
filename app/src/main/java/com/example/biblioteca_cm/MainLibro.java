package com.example.biblioteca_cm;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

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

        db.collection("libro").document("978-84-939750-7-4")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Libro libro = documentSnapshot.toObject(Libro.class);

                            // Mostrar los datos del usuario en los TextView
                            TextView textViewTitle = findViewById(R.id.tituloLibro);
                            textViewTitle.setText(libro.getTitulo());

                            TextView textViewDisp = findViewById(R.id.disponibilidadLibro);
                            if(libro.getDisponible()){
                                textViewDisp.setText("Está disponible");
                            }else{
                                textViewDisp.setText("No está disponible");
                            }

                            TextView textViewAutor = findViewById(R.id.autorLibro);
                            textViewAutor.setText(libro.getAutor());

                            TextView textViewEditorial = findViewById(R.id.editorialLibro);
                            textViewEditorial.setText(libro.getEditorial());

                            TextView textViewSinopsis = findViewById(R.id.sinopsisLibro);
                            textViewSinopsis.setText(libro.getSinopsis());

                            ImageView imageViewPortada = findViewById(R.id.portadaLibro);
                            String portadaLibro = libro.getPortada();
                            int resID = getResources().getIdentifier(portadaLibro, "drawable", getPackageName());
                            Picasso.get().load(resID).into(imageViewPortada);
                        } else {
                            Log.d(TAG, "No existe el documento");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener los datos del usuario", e);
                    }
                });

    }




}
