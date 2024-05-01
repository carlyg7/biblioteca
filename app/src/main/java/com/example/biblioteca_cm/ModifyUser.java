package com.example.biblioteca_cm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ModifyUser extends AppCompatActivity {

    private String rolUsuario;
    private String dniUsuario;
    private FirebaseFirestore db;
    private static final String TAG = "ModifyUser";
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mod_user);

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

        mostrarDatosUsuario(dniUsuario);

        findViewById(R.id.btn_mod_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarUsuario(dniUsuario);
            }
        });

    }

    private void mostrarDatosUsuario(String dniUsuario) {
        // Obtener una referencia al documento del usuario en Firestore
        db.collection("user").document(dniUsuario)
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

        // Modificar los campos del usuario obtenido de Firestore
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setUsuario(user);
        usuario.setPassword(password);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);

        // Actualizar el Usuario en la base de datos
        actualizarUsuario(dni, usuario);
    }

    private void actualizarUsuario(String dni, Usuario editUser) {
        db.collection("user")
                .document(dni) // Utilizar el dni como clave única
                .set(editUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ModifyUser.this, "Usuario editado correctamente", Toast.LENGTH_SHORT).show();
                        // Envía un mensaje de vuelta a MainDatosUsuarios indicando que la edición se realizó correctamente
                        Intent intent = new Intent();
                        intent.putExtra("usuario_editado", true);
                        intent.putExtra("dniUsuario", dniUsuario);
                        intent.putExtra("rolUsuario", rolUsuario);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Log.e(TAG, "Error al editar libro", e);
                        Toast.makeText(ModifyUser.this, "Error al editar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
