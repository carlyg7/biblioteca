package com.example.biblioteca_cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "Registro de Usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase y FireStore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Obtener referencia al TextView "Regístrate aquí"
        TextView signupLink = findViewById(R.id.signup_link);
        // Agregar OnClickListener al TextView "Regístrate aquí"
        signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(MainActivity.this, Registro.class);
                startActivity(intent);
            }
        });

        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Iniciar Sesion >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        // Obtener referencias a los campos de usuario y contraseña
        EditText input_user = findViewById(R.id.lg_user);
        EditText input_pass = findViewById(R.id.get_pass_login);
        // Configurar el campo de contraseña para que sea de tipo password
        input_pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Obtener referencia al botón de inicio de sesión
        Button bnt_login = findViewById(R.id.btn_login);
        bnt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario

                String user = input_user.getText().toString();
                String pass = input_pass.getText().toString();

                // Validar que los campos no estén vacíos
                if (!user.isEmpty() && !pass.isEmpty()) {
                    // Llamar a la función de inicio de sesión
                    loginUsuario(user, pass);
                } else {
                    // Mostrar un mensaje de error si algún campo está vacío
                    Toast.makeText(MainActivity.this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginUsuario(String user, String pass) {
        db.collection("user")
                .whereEqualTo("usuario", user)
                .whereEqualTo("password", pass)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Las credenciales son correctas, permitir el acceso
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                            // Después de que el usuario inicie sesión correctamente obtener dni y direccionar a Main datos usuarios
                            obtenerDniUsuario(user, pass);

                            //Intent intent = new Intent(MainActivity.this, MainDatosUsuarios.class);
                            //startActivity(intent);
                        } else {
                            // Las credenciales son incorrectas, mostrar mensaje de error
                            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al iniciar sesión: " + e.getMessage());
                        Toast.makeText(MainActivity.this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void obtenerDniUsuario(String user, String pass) {
        db.collection("user")
                .whereEqualTo("usuario", user)
                .whereEqualTo("password", pass)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Se encontró un usuario con las credenciales proporcionadas
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Obtener dni del usuario (clave primaria)
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String dniUsuario = documentSnapshot.getId();

                            // SharedPreferences -> Aqui es donde vamos a guardar el DNI (id del usuario) para usarlo en otras vistas
                            dniSharedPreferences(dniUsuario);

                            // Después de obtener el DNI del usuario, iniciar la actividad MainDatosUsuarios
                            Intent intent = new Intent(MainActivity.this, MainCatalogo.class);
                            intent.putExtra("dniUsuario", dniUsuario);
                            startActivity(intent);
                        } else {
                            // No se encontró un usuario con las credenciales proporcionadas
                            Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener el DNI del usuario", e);
                    }
                });
    }

    private void dniSharedPreferences(String dniUsuario) {
        // Obtener una referencia al objeto SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        // Editar el objeto SharedPreferences para escribir el DNI del usuario
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("dniUsuario", dniUsuario);

        // Aplicar los cambios
        editor.apply();
    }
}
