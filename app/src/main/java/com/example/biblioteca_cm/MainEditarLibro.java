package com.example.biblioteca_cm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainEditarLibro extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "Editar Libro";
    private String imagenBase64;

    private String rolUsuario;
    private String dniUsuario;

    ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        try {
                            final InputStream imageStream = getContentResolver().openInputStream(result);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            imagenBase64 = encodeImage(selectedImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainEditarLibro.this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_libro);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Obtener datos de libro
        String isbn = getIntent().getStringExtra("isbnLibro");
        rolUsuario = getIntent().getStringExtra("rolUsuario");
        dniUsuario = getIntent().getStringExtra("dniUsuario");

        // Consultar la base de datos para obtener los detalles del libro
        db.collection("libro")
                .document(isbn)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Libro libro = documentSnapshot.toObject(Libro.class);
                            if (libro != null) {
                                // Mostrar los detalles del libro en los EditText
                                ((EditText) findViewById(R.id.reg_titulo)).setText(libro.getTitulo());
                                ((EditText) findViewById(R.id.reg_autor)).setText(libro.getAutor());
                                ((EditText) findViewById(R.id.reg_isbn)).setText(libro.getIsbn());
                                ((EditText) findViewById(R.id.reg_editorial)).setText(libro.getEditorial());
                                ((EditText) findViewById(R.id.reg_sinopsis)).setText(libro.getSinopsis());
                            }
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error getting document", e);
                    }
                });

        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        findViewById(R.id.btn_editar_libro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarLibro(isbn);
            }
        });

        ImageView volverReg = findViewById(R.id.volver_reg);
        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        onBackPressedDispatcher.addCallback(this, callback);
        volverReg.setOnClickListener(view -> onBackPressedDispatcher.onBackPressed());
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        galleryLauncher.launch("image/*");
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private void editarLibro(String isbn) {
        // Obtener referencias a los EditText y Obtener los valores de los EditText
        String titulo = ((EditText) findViewById(R.id.reg_titulo)).getText().toString().trim();
        String autor = ((EditText) findViewById(R.id.reg_autor)).getText().toString().trim();
        String editorial = ((EditText) findViewById(R.id.reg_editorial)).getText().toString().trim();
        String sinopsis = ((EditText) findViewById(R.id.reg_sinopsis)).getText().toString().trim();

        // Validar si los campos están vacíos
        if (titulo.isEmpty() || autor.isEmpty() || isbn.isEmpty() || editorial.isEmpty() || sinopsis.isEmpty()) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(MainEditarLibro.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución de la función si hay campos vacíos
        }

        Boolean disponible = true;

        // Comprobar si se ha seleccionado una nueva imagen
        if (imagenBase64 == null || imagenBase64.isEmpty()) {
            // Si no se ha seleccionado una nueva imagen, obtener la imagen actual del libro
            db.collection("libro")
                    .document(isbn)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Libro libro = documentSnapshot.toObject(Libro.class);
                                if (libro != null) {
                                    // Obtener la imagen actual del libro
                                    String portadaActual = libro.getPortada();
                                    // Actualizar el libro con la imagen actual
                                    Libro libroEditado = new Libro(autor, disponible, editorial, sinopsis, titulo, isbn, portadaActual);
                                    // Actualizar el libro en la base de datos
                                    actualizarLibro(isbn, libroEditado);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error getting document", e);
                        }
                    });
        } else {
            // Si se ha seleccionado una nueva imagen, crear el objeto Libro con la nueva imagen
            Libro libroEditado = new Libro(autor, disponible, editorial, sinopsis, titulo, isbn, imagenBase64);
            // Actualizar el libro en la base de datos
            actualizarLibro(isbn, libroEditado);
        }
    }

    // Método para actualizar el libro en la base de datos
    private void actualizarLibro(String isbn, Libro libroEditado) {
        db.collection("libro")
                .document(isbn) // Utilizar el ISBN como clave única
                .set(libroEditado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainEditarLibro.this, "Libro editado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainEditarLibro.this, MainMenu.class);
                        intent.putExtra("rolUsuario", rolUsuario);
                        intent.putExtra("dniUsuario", dniUsuario);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al editar libro", e);
                        Toast.makeText(MainEditarLibro.this, "Error al editar libro", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
