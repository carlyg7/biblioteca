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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainBorrarLibro extends AppCompatActivity {
    private FirebaseFirestore db;

    private String rolUsuario;
    private String isbnLibro;
    private String dniUsuario;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrar_libro);

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
        isbnLibro = getIntent().getStringExtra("isbnLibro");
        obtenerDatosLibro(isbnLibro);

        Button btnConfirmarBorrar = findViewById(R.id.btn_confirmar_borrar);
        Button btnCancelarBorrar = findViewById(R.id.btn_cancelar_borrar);

        btnConfirmarBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Borrar el libro
                borrarLibro(isbnLibro);
            }
        });

        btnCancelarBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Simplemente cierra esta actividad si se cancela el borrado
                finish();
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

    private void borrarLibro(String isbn) {
        db.collection("libro").document(isbn)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainBorrarLibro.this, "Libro borrado correctamente.", Toast.LENGTH_SHORT).show();

                        // Una vez borrado, iniciar la actividad MainMenu
                        Intent intent = new Intent(MainBorrarLibro.this, MainMenu.class);
                        intent.putExtra("rolUsuario", rolUsuario);
                        intent.putExtra("dniUsuario", dniUsuario);
                        startActivity(intent);
                        finish(); // Finaliza esta actividad para evitar volver atrás con el botón de retroceso
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainBorrarLibro.this, "Error al borrar el libro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
