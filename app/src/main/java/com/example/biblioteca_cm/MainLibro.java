package com.example.biblioteca_cm;

import static android.content.ContentValues.TAG;

import android.content.Intent;
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

    private String dniUsuario;

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
        dniUsuario = getIntent().getStringExtra("dniUsuario");
        mostrarNombreUsu(dniUsuario);

        // Obtener datos de libro
        String isbnLibro = getIntent().getStringExtra("libroId");
        obtenerDatosLibro(isbnLibro);


        TextView volverCatalogo = findViewById(R.id.volverLibro);
        volverCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View w){
                Intent intent = new Intent(MainLibro.this, MainCatalogo.class);
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



                        /*    ImageView imageViewPortada = findViewById(R.id.portadaLibro);
                            String portadaLibro = libro.getPortada();
                            int resID = getResources().getIdentifier(portadaLibro, "drawable", getPackageName());
                            Picasso.get().load(resID).into(imageViewPortada); */
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

    private void mostrarNombreUsu(String dni){
        // Obtener una referencia al documento del usuario en Firestore
        db.collection("user").document(dni)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);

                            // Mostrar los datos del usuario en los TextView
                            TextView textViewName = findViewById(R.id.nombreUsu);
                            textViewName.setText(usuario.getUsuario());

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
