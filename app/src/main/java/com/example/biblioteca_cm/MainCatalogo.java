package com.example.biblioteca_cm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.graphics.Color;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainCatalogo extends AppCompatActivity {

    private LinearLayout linearLayoutBooks;
    private FirebaseFirestore db;

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

        // Obtener una referencia al LinearLayout donde se agregarán las CardViews
        linearLayoutBooks = findViewById(R.id.linearLayoutBooks);

        // Consulta a Firestore para obtener los datos de libros
        db.collection("libro")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Libro libro = document.toObject(Libro.class);

                                // Decodificar la imagen de base64
                                byte[] decodedString = Base64.decode(libro.getPortada(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                // Crear una nueva CardView con márgenes y padding configurados
                                CardView cardView = new CardView(MainCatalogo.this);
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
                                LinearLayout layout = new LinearLayout(MainCatalogo.this);
                                layout.setOrientation(LinearLayout.HORIZONTAL);
                                cardView.addView(layout);

                                // Agregar la imagen a la izquierda
                                ImageView bookCover = new ImageView(MainCatalogo.this);
                                bookCover.setImageBitmap(decodedByte);
                                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        2
                                );
                                layout.addView(bookCover, imageParams);

                                // Crear un LinearLayout vertical para el título y el autor a la derecha
                                LinearLayout textLayout = new LinearLayout(MainCatalogo.this);
                                textLayout.setOrientation(LinearLayout.VERTICAL);
                                LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                        0,
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        7
                                );
                                layout.addView(textLayout, textParams);

                                // Obtener referencias a las vistas dentro del textLayout
                                TextView bookTitle = new TextView(MainCatalogo.this);
                                bookTitle.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                                bookTitle.setText(libro.getTitulo());
                                textLayout.addView(bookTitle);

                                TextView bookAuthor = new TextView(MainCatalogo.this);
                                bookAuthor.setLayoutParams(new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));
                                bookAuthor.setText(libro.getAutor());
                                textLayout.addView(bookAuthor);

                                // Agregar la CardView al LinearLayout
                                linearLayoutBooks.addView(cardView);
                            }
                        } else {
                            Toast.makeText(MainCatalogo.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
