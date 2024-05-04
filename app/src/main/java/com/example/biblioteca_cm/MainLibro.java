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
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;

public class MainLibro extends AppCompatActivity {

    private FirebaseFirestore db;

    private String rolUsuario;
    private String dniUsuario;
    private SharedPreferences sharedPreferences;

    private Boolean disponible;
    private String dniReserva;
    private String nombreReserva;
    private String isbnReserva;
    private String tituloReserva;

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
        Button reserva = findViewById(R.id.btnReservarLibro);

        if ("admin".equals(rolUsuario)) {
            // Si el usuario es administrador
            borrarlibro.setVisibility(View.VISIBLE);
            borrarlibro2.setVisibility(View.VISIBLE);
            modlibro.setVisibility(View.VISIBLE);
            modlibro2.setVisibility(View.VISIBLE);
            reserva.setVisibility(View.GONE);
        } else {
            // Si el usuario no es administrador
            borrarlibro.setVisibility(View.GONE);
            borrarlibro2.setVisibility(View.GONE);
            modlibro.setVisibility(View.GONE);
            modlibro2.setVisibility(View.GONE);
            libroDisponible(isbnLibro, new OnLibroDisponibleListener() {
                @Override
                public void onLibroDisponible(boolean disponible) {
                    // Aquí puedes manejar la visibilidad de reserva
                    if (disponible) {
                        reserva.setVisibility(View.VISIBLE);
                    } else {
                        reserva.setVisibility(View.GONE);
                    }
                }
            });

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

        Button btnReservarLibro = findViewById(R.id.btnReservarLibro);
        btnReservarLibro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarReserva(dniUsuario, isbnLibro);
            }
        });
    }

    private void realizarReserva(String dni, String isbn) {
        db.collection("user").document(dni)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                            dniReserva = usuario.getDni();
                            nombreReserva = usuario.getNombre() + " " + usuario.getApellidos();

                            // Ahora que tenemos los datos del usuario, podemos obtener los datos del libro
                            obtenerDatosLibroParaReserva(isbn);
                        } else {
                            Log.d(TAG, "No existe el documento de usuario");
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

    private void obtenerDatosLibroParaReserva(String isbn) {
        db.collection("libro").document(isbn)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Libro libro = documentSnapshot.toObject(Libro.class);
                            isbnReserva = libro.getIsbn();
                            tituloReserva = libro.getTitulo();

                            // Ahora que tenemos todos los datos necesarios, podemos crear y guardar la reserva
                            guardarReserva();
                        } else {
                            Log.d(TAG, "No existe el documento de libro");
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

    private void guardarReserva() {
        Reserva nuevaReserva = new Reserva(dniReserva, isbnReserva, tituloReserva, nombreReserva);
        db.collection("reserva")
                .document(dniReserva + isbnReserva) // Utilizar el DNI y el ISBN como clave única
                .set(nuevaReserva)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Marcar el libro como no disponible
                        marcarLibroComoNoDisponible(isbnReserva);

                        Toast.makeText(MainLibro.this, "Libro reservado correctamente", Toast.LENGTH_SHORT).show();
                        // Redirigir a MainLibro
                        Intent intent = new Intent(MainLibro.this, MainLibro.class);
                        intent.putExtra("rolUsuario", rolUsuario);
                        intent.putExtra("dniUsuario", dniUsuario);
                        startActivity(intent);
                        finish(); // Cierra la actividad actual para que el usuario no pueda regresar con el botón de retroceso
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al crear documento de reserva", e);
                        Toast.makeText(MainLibro.this, "Error al reservar el libro", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void marcarLibroComoNoDisponible(String isbn) {
        db.collection("libro")
                .document(isbn)
                .update("disponible", false)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Libro marcado como no disponible");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al actualizar el campo disponible del libro", e);
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

    private void libroDisponible(String isbn, OnLibroDisponibleListener listener) {
        db.collection("libro").document(isbn)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        boolean disponible = false; // Valor predeterminado si no se encuentra el documento
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Libro libro = documentSnapshot.toObject(Libro.class);
                            disponible = libro.getDisponible();
                            Log.d(TAG, "Campo disponible: " + libro.getDisponible());
                            Log.d(TAG, "Disponible: " + disponible);
                        } else {
                            Log.d(TAG, "No existe el documento");
                        }
                        listener.onLibroDisponible(disponible);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener los datos del libro", e);
                        listener.onLibroDisponible(false); // Manejo del error: suponemos que el libro no está disponible
                    }
                });
    }

    public interface OnLibroDisponibleListener {
        void onLibroDisponible(boolean disponible);
    }

}
