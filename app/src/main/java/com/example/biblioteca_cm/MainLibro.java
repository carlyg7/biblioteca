package com.example.biblioteca_cm;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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

    private String rolUsuario;
    private String dniUsuario;
    private SharedPreferences sharedPreferences;


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

        //Nombre usuario
        rolUsuario = getIntent().getStringExtra("rolUsuario");
        dniUsuario = getIntent().getStringExtra("dniUsuario");

        // Obtener datos de libro
        String isbnLibro = getIntent().getStringExtra("libroId");
        obtenerDatosLibro(isbnLibro);


        TextView volverCatalogo = findViewById(R.id.volverLibro);
        volverCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                // Cierra la actividad actual y vuelve a la actividad anterior
                finish();
            }
        });

        //Botones solo para admin
        TextView borrarlibro = findViewById(R.id.borrarlibro);
        ImageView borrarlibro2 = findViewById(R.id.borrarlibro2);
        TextView modlibro = findViewById(R.id.modlibro);
        ImageView modlibro2 = findViewById(R.id.modlibro2);

        if ("admin".equals(rolUsuario)) {
            // Si el usuario es administrador
            borrarlibro.setVisibility(View.VISIBLE);
            borrarlibro2.setVisibility(View.VISIBLE);
            modlibro.setVisibility(View.VISIBLE);
            modlibro2.setVisibility(View.VISIBLE);
        } else {
            // Si el usuario no es administrador
            borrarlibro.setVisibility(View.GONE);
            borrarlibro2.setVisibility(View.GONE);
            modlibro.setVisibility(View.GONE);
            modlibro2.setVisibility(View.GONE);
        }

        modlibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a MainLibro
                Intent intent = new Intent(MainLibro.this, MainEditarLibro.class);
                intent.putExtra("isbnLibro", isbnLibro);
                intent.putExtra("rolUsuario", rolUsuario);
                intent.putExtra("dniUsuario", dniUsuario);
                startActivity(intent);
            }
        });

        borrarlibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a MainLibro
                Intent intent = new Intent(MainLibro.this, MainBorrarLibro.class);
                intent.putExtra("isbnLibro", isbnLibro);
                intent.putExtra("rolUsuario", rolUsuario);
                intent.putExtra("dniUsuario", dniUsuario);
                startActivity(intent);
            }
        });
    }

    private void obtenerDatosLibro(String isbn){

        db.collection("libro").document(isbn)
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
                                textViewDisp.setText("Disponible");
                            }else{
                                textViewDisp.setText("No disponible");
                            }

                            TextView textViewAutor = findViewById(R.id.autorLibro);
                            textViewAutor.setText(libro.getAutor());

                            TextView textViewEditorial = findViewById(R.id.editorialLibro);
                            textViewEditorial.setText(libro.getEditorial());

                            TextView textViewSinopsis = findViewById(R.id.sinopsisLibro);
                            textViewSinopsis.setText(libro.getSinopsis());

                            // Cargar imagen decodificada en Base64
                            ImageView imageViewPortada = findViewById(R.id.portadaLibro);
                            String imagenBase64 = libro.getPortada();
                            byte[] decodedString = Base64.decode(imagenBase64, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageViewPortada.setImageBitmap(decodedByte);

                        } else {
                            Log.d(TAG, "No existe el documento");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener los datos del libro", e);
                    }
                });

    }

}
