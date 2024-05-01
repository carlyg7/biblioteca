package com.example.biblioteca_cm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ModifyUser extends AppCompatActivity {

    private String rolUsuario;
    private FirebaseFirestore db;
    private static final String TAG = "ModifyUser";
    private Usuario usuario; // Define la variable usuario aquí

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_user);

        // Inicializar Firebase y Firestore
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Obtener una referencia al documento del usuario en Firestore
        db.collection("user").document("04445678S")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento y asignarlos a la variable usuario
                            usuario = documentSnapshot.toObject(Usuario.class);

                            if (usuario != null) {
                                // Mostrar los datos del usuario en los EditText
                                ((EditText) findViewById(R.id.reg_nombre)).setText(usuario.getNombre());
                                ((EditText) findViewById(R.id.reg_apellidos)).setText(usuario.getApellidos());
                                ((EditText) findViewById(R.id.reg_usuario)).setText(usuario.getUsuario());
                                ((EditText) findViewById(R.id.reg_password)).setText(usuario.getPassword());
                                ((EditText) findViewById(R.id.reg_correo)).setText(usuario.getCorreo());
                                ((EditText) findViewById(R.id.reg_dni)).setText(usuario.getDni());
                                ((EditText) findViewById(R.id.reg_telefono)).setText(usuario.getTelefono());
                            }
                        } else {
                            Log.d(TAG, "No existe el documento");
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al obtener los detalles del usuario", e);
                    }
                });

    }

    private void editarUsuario(String dni) {

        // Obtener referencias a los EditText y Obtener los valores de los EditText
        String nombre = ((EditText) findViewById(R.id.reg_nombre)).getText().toString().trim();
        String apellidos = ((EditText) findViewById(R.id.reg_apellidos)).getText().toString().trim();
        String user = ((EditText) findViewById(R.id.reg_usuario)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.reg_password)).getText().toString().trim();
        String correo = ((EditText) findViewById(R.id.reg_correo)).getText().toString().trim();
        String telefono = ((EditText) findViewById(R.id.reg_telefono)).getText().toString().trim();

        // Validar si los campos están vacíos
        if (nombre.isEmpty() || apellidos.isEmpty() || user.isEmpty() || password.isEmpty() || correo.isEmpty() || dni.isEmpty() || telefono.isEmpty()) {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(ModifyUser.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return; // Detener la ejecución de la función si hay campos vacíos
        }

        String tipo_user = "cliente";

        // Utilizar la variable usuario definida anteriormente
        String nombreImagen = usuario.getImg(); // Suponiendo que getImg() devuelve el nombre de la imagen
        int resID = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
        ImageView imageViewPerfil = findViewById(R.id.imageViewPerfil);
        Picasso.get().load(resID).into(imageViewPerfil);

        Usuario editUser = new Usuario(nombre, apellidos, user, password, correo, dni, tipo_user, telefono, nombreImagen);
        // Actualizar el Usuario en la base de datos
        actualizarUsuario(dni, editUser);
    }

    private void actualizarUsuario(String dni, Usuario editUser) {
        db.collection("user")
                .document("04445678S") // Utilizar el dni como clave única
                .set(editUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ModifyUser.this, "Usuario editado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ModifyUser.this, MainMenu.class);
                        intent.putExtra("rolUsuario", rolUsuario);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@android.support.annotation.NonNull Exception e) {
                        Log.e(TAG, "Error al editar libro", e);
                        Toast.makeText(ModifyUser.this, "Error al editar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
