package com.example.biblioteca_cm;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "Registro de Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        FirebaseApp.initializeApp(this);
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        Button buttonRegistrar = findViewById(R.id.btn_reg_user);
        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
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
            Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución de la función si hay campos vacíos
        }

        // Tipo de usuario -> Cliente por defecto
        String tipo_user = "cliente";

        // Verificar si el DNI ya está registrado
        db.collection("user")
                .document(dni)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // El DNI ya está registrado
                            Toast.makeText(MainActivity.this, "El DNI ya está en uso. Por favor, ingrese un DNI diferente.", Toast.LENGTH_SHORT).show();
                        } else {
                            // El DNI no está registrado, procede a registrar el usuario
                            // Crear un nuevo objeto Usuario con los datos
                            Usuario nuevoUsuario = new Usuario(nombre, apellidos, dni, usuario, password, correo, telefono, tipo_user);

                            // Guardar el nuevo usuario en Firebase Firestore
                            db.collection("user")
                                    .document(dni) // Utilizar el DNI como clave única
                                    .set(nuevoUsuario)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Documento creado con ID: " + dni);
                                            Toast.makeText(MainActivity.this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                                            // Limpiar los campos del formulario después del registro exitoso
                                            ((EditText) findViewById(R.id.reg_nombre)).setText("");
                                            ((EditText) findViewById(R.id.reg_apellidos)).setText("");
                                            ((EditText) findViewById(R.id.reg_dni)).setText("");
                                            ((EditText) findViewById(R.id.reg_usuario)).setText("");
                                            ((EditText) findViewById(R.id.reg_password)).setText("");
                                            ((EditText) findViewById(R.id.reg_correo)).setText("");
                                            ((EditText) findViewById(R.id.reg_telefono)).setText("");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error al crear documento", e);
                                            Toast.makeText(MainActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al verificar el DNI en Firestore", e);
                        Toast.makeText(MainActivity.this, "Error al verificar el DNI. Por favor, inténtelo de nuevo.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
