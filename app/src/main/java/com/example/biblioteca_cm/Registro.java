package com.example.biblioteca_cm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Registro extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "Registro de Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Llamar a los diferentes xml (vistas)
        setContentView(R.layout.registro_user);

        // Inicializar Firebase y FireStore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Click boton registrar
        findViewById(R.id.btn_reg_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        // Obtener referencia al TextView "Regístrate aquí"
        TextView signupLink = findViewById(R.id.enlace_login);
        // Agregar OnClickListener al TextView "Regístrate aquí"
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent nos redirige a otra vista
                Intent intent = new Intent(Registro.this, MainActivity.class);
                startActivity(intent);
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

    private void registrarUsuario() {
        // Obtener referencias a los EditText y Obtener los valores de los EditText
        String nombre = ((EditText) findViewById(R.id.reg_nombre)).getText().toString().trim();
        String apellidos = ((EditText) findViewById(R.id.reg_apellidos)).getText().toString().trim();
        String dni = ((EditText) findViewById(R.id.reg_dni)).getText().toString().trim();
        String usuario = ((EditText) findViewById(R.id.reg_usuario)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.reg_password)).getText().toString().trim();
        String correo = ((EditText) findViewById(R.id.reg_correo)).getText().toString().trim();
        String telefono = ((EditText) findViewById(R.id.reg_telefono)).getText().toString().trim();

        // Validar si los campos están vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || usuario.isEmpty() || password.isEmpty() || correo.isEmpty() || telefono.isEmpty()) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(Registro.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución de la función si hay campos vacíos
        }

        // Tipo de usuario -> Cliente por defecto
        String tipo_user = "cliente";

        // Imagen de perfil aleatorio
        Random random = new Random();
        int n_perfil = random.nextInt(10) + 1;
        String img = "perfil"+n_perfil;

        // Verificar si el DNI ya está registrado
        db.collection("user")
                .document(dni)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // El DNI ya está registrado
                            Toast.makeText(Registro.this, "El DNI ya está en uso. Por favor, ingrese un DNI diferente.", Toast.LENGTH_SHORT).show();
                        } else {
                            // El DNI no está registrado, procede a registrar el usuario
                            // Crear un nuevo objeto Usuario con los datos
                            Usuario nuevoUsuario = new Usuario(nombre, apellidos, dni, usuario, password, correo, telefono, tipo_user, img);

                            // Guardar el nuevo usuario en Firebase Firestore
                            db.collection("user")
                                    .document(dni) // Utilizar el DNI como clave única
                                    .set(nuevoUsuario)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Registro.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                            // Limpiar los campos del formulario después del registro exitoso
                                            ((EditText) findViewById(R.id.reg_nombre)).setText("");
                                            ((EditText) findViewById(R.id.reg_apellidos)).setText("");
                                            ((EditText) findViewById(R.id.reg_dni)).setText("");
                                            ((EditText) findViewById(R.id.reg_usuario)).setText("");
                                            ((EditText) findViewById(R.id.reg_password)).setText("");
                                            ((EditText) findViewById(R.id.reg_correo)).setText("");
                                            ((EditText) findViewById(R.id.reg_telefono)).setText("");
                                            Intent intent = new Intent(Registro.this, MainActivity.class);
                                            startActivity(intent);
                                            finish(); // Cierra la actividad actual para que el usuario no pueda regresar con el botón de retroceso


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error al crear documento", e);
                                            Toast.makeText(Registro.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Registro.this, "Error al verificar el DNI. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
