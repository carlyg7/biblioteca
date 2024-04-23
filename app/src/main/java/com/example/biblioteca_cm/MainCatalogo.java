package com.example.biblioteca_cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.graphics.Color;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainCatalogo extends AppCompatActivity {

    private LinearLayout linearLayoutBooks;
    private FirebaseFirestore db;
    private static final String TAG = "MainCatalogo";
    private SharedPreferences sharedPreferences;

    private String dniUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogo);

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        //SharedPreferences
        sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        dniUsuario = getIntent().getStringExtra("dniUsuario");

        //Nombre usuario
        mostrarNombreUsu(dniUsuario);

        // Enlace perfil
        RelativeLayout link = findViewById(R.id.btn_perfil);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(MainCatalogo.this, MainDatosUsuarios.class);
                intent.putExtra("dniUsuario", dniUsuario);
                startActivity(intent);
            }
        });

        // Obtener una referencia al LinearLayout donde se agregarán las CardViews
        linearLayoutBooks = findViewById(R.id.linearLayoutBooks);
        //Mostrar los libros
        mostrarLibros();

    }

    private void agregarCardViewLibro(Libro libro, String libroId) {
        // Decodificar la imagen de base64
        byte[] decodedString = Base64.decode(libro.getPortada(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        // Crear una nueva CardView con márgenes y padding configurados
        CardView cardView = new CardView(this);
        CardView.LayoutParams cardLayoutParams = new CardView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Configurar márgenes
        int margin = 16; // Valor en dp
        int marginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());
        cardLayoutParams.setMargins(marginPx, marginPx, marginPx, marginPx);

        cardView.setLayoutParams(cardLayoutParams);

        // Configurar padding
        int padding = 20; // Valor en dp
        int paddingPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
        cardView.setContentPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        cardView.setCardBackgroundColor(Color.WHITE);
        cardView.setCardElevation(10);
        cardView.setRadius(25);

        // Configurar las vistas dentro de la CardView
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        cardView.addView(layout);

        // Agregar la imagen a la izquierda
        ImageView bookCover = new ImageView(this);
        bookCover.setImageBitmap(decodedByte);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                3
        );
        layout.addView(bookCover, imageParams);

        // Crear un LinearLayout vertical para el título y el autor a la derecha
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                7
        );
        layout.addView(textLayout, textParams);

        // Obtener referencias a las vistas dentro del textLayout
        TextView bookTitle = new TextView(this);
        bookTitle.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        // Establecer el título en negrita y aumentar el tamaño de la letra
        SpannableString titleSpan = new SpannableString(libro.getTitulo());
        titleSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleSpan.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        bookTitle.setText(titleSpan);
        bookTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Tamaño de letra de 18sp
        textLayout.addView(bookTitle);

        TextView bookAuthor = new TextView(this);
        bookAuthor.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        bookAuthor.setText(libro.getAutor());
        textLayout.addView(bookAuthor);

        // Agregar un OnClickListener para guardar la clave única del libro al hacer clic en la CardView
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Guardar la clave única del libro en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("libroId", libroId);
                editor.apply();

                // Redirigir a MainLibro
                Intent intent = new Intent(MainCatalogo.this, MainLibro.class);
                intent.putExtra("libroId", libroId);
                intent.putExtra("dniUsuario", dniUsuario);
                startActivity(intent);
            }
        });

        // Agregar la CardView al LinearLayout
        linearLayoutBooks.addView(cardView);
    }

    private void mostrarLibros() {
        // Consulta a Firestore para obtener los datos de libros
        db.collection("libro")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Libro libro = document.toObject(Libro.class);
                                String libroId = document.getId(); // Obtener la clave única del libro
                                agregarCardViewLibro(libro, libroId);
                            }
                        } else {
                            Toast.makeText(MainCatalogo.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
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
