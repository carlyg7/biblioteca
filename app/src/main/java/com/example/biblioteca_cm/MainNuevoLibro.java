package com.example.biblioteca_cm;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class MainNuevoLibro extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "Añadir Libro";
    private String imagenBase64;

    private static final int REQUEST_MEDIA_PERMISSION = 101;

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
                        Toast.makeText(MainNuevoLibro.this, "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Llamar a los diferentes xml (vistas)
        setContentView(R.layout.nuevo_libro);

        // Inicializar Firebase y FireStore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Botón para seleccionar imagen
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        // Click boton AÑADIR LIBRO
        findViewById(R.id.btn_reg_libro).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarLibro();
            }
        });

        //Boton de volver atras
        ImageView volverReg = findViewById(R.id.volver_reg);
        OnBackPressedDispatcher onBackPressedDispatcher = this.getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Aquí puedes poner el código para manejar el evento de retroceso
                finish(); // Esto cierra la actividad
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

    private void registrarLibro() {
        // Obtener referencias a los EditText y Obtener los valores de los EditText
        String titulo = ((EditText) findViewById(R.id.reg_titulo)).getText().toString().trim();
        String autor = ((EditText) findViewById(R.id.reg_autor)).getText().toString().trim();
        String isbn = ((EditText) findViewById(R.id.reg_isbn)).getText().toString().trim();
        String editorial = ((EditText) findViewById(R.id.reg_editorial)).getText().toString().trim();
        String sinopsis = ((EditText) findViewById(R.id.reg_sinopsis)).getText().toString().trim();

        // Validar si los campos están vacíos
        if (titulo.isEmpty() || autor.isEmpty() || isbn.isEmpty() || editorial.isEmpty() || sinopsis.isEmpty()) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(MainNuevoLibro.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución de la función si hay campos vacíos
        }

        // Disponibilidad Libro
        Boolean disponible = true;


        db.collection("libro")
                .document(isbn)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(MainNuevoLibro.this, "El ISBN ya está en uso. El libro ya existe en la base de datos.", Toast.LENGTH_SHORT).show();
                        } else {
                            Libro nuevoLibro = new Libro(autor, disponible, editorial, sinopsis, titulo, isbn, imagenBase64);

                            db.collection("libro")
                                    .document(isbn) // Utilizar el DNI como clave única
                                    .set(nuevoLibro)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MainNuevoLibro.this, "Libro añadido correctamente", Toast.LENGTH_SHORT).show();
                                            // Limpiar los campos del formulario después del registro exitoso
                                            ((EditText) findViewById(R.id.reg_titulo)).setText("");
                                            ((EditText) findViewById(R.id.reg_autor)).setText("");
                                            ((EditText) findViewById(R.id.reg_isbn)).setText("");
                                            ((EditText) findViewById(R.id.reg_editorial)).setText("");
                                            ((EditText) findViewById(R.id.reg_sinopsis)).setText("");
                                            Intent intent = new Intent(MainNuevoLibro.this, MainMenu.class);
                                            startActivity(intent);
                                            finish(); // Cierra la actividad actual para que el usuario no pueda regresar con el botón de retroceso


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error al crear documento", e);
                                            Toast.makeText(MainNuevoLibro.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainNuevoLibro.this, "Error al verificar el ISBN. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
