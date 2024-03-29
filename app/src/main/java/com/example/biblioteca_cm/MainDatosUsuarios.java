package com.example.biblioteca_cm;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainDatosUsuarios extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final String TAG = "MainDatosUsuarios";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datos_usuarios);

        // Inicializar Firebase y Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Obtener datos del usuario desde Firestore y mostrarlos en los TextView
        obtenerDatosUsuario();
    }

    // Método para obtener los datos del usuario desde Firestore
    private void obtenerDatosUsuario() {
        // Obtener una referencia al documento del usuario en Firestore
        db.collection("usuarios").document("04445678S")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);

                            // Mostrar los datos del usuario en los TextView
                            TextView textViewNombre = findViewById(R.id.textViewNombre);
                            textViewNombre.setText("Nombre: " + usuario.getNombre());

                            TextView textViewApellidos = findViewById(R.id.textViewApellidos);
                            textViewApellidos.setText("Apellidos: " + usuario.getApellidos());

                            TextView textViewUsuario = findViewById(R.id.textViewUsuario);
                            textViewUsuario.setText("Usuario: " + usuario.getUsuario());

                            TextView textViewCorreo = findViewById(R.id.textViewCorreo);
                            textViewCorreo.setText("Correo: " + usuario.getCorreo());

                            TextView textViewDNI = findViewById(R.id.textViewDNI);
                            textViewDNI.setText("DNI: " + usuario.getDni());

                            TextView textViewTelefono = findViewById(R.id.textViewTelefono);
                            textViewTelefono.setText("Teléfono: " + usuario.getTelefono());

                            TextView textViewTipoUsuario = findViewById(R.id.textViewTipoUsuario);
                            textViewTipoUsuario.setText("Tipo de Usuario: " + usuario.getTipo_user());

                            TextView textViewPassword = findViewById(R.id.textViewPassword);
                            textViewPassword.setText("Password: " + usuario.getPassword());
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
