package com.example.biblioteca_cm;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

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

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Obtener el dni del usuario del que queremos mostrar los datos, guardado en SharedPreferences
        String dniUsuario = getIntent().getStringExtra("dniUsuario");
        obtenerDatosUsuario(dniUsuario);
    }

    private void obtenerDatosUsuario(String dni) {
        // Obtener una referencia al documento del usuario en Firestore
        db.collection("user").document(dni)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Obtener los datos del documento
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);

                            // Mostrar los datos del usuario en los TextView
                            TextView textViewName = findViewById(R.id.textViewNombre);
                            textViewName.setText(usuario.getNombre() +" "+ usuario.getApellidos());

                            TextView textViewUser = findViewById(R.id.textViewUsuario);
                            textViewUser.setText(usuario.getUsuario());

                            TextView textViewMail = findViewById(R.id.textViewCorreo);
                            textViewMail.setText(usuario.getCorreo());

                            TextView textViewDni = findViewById(R.id.textViewDNI);
                            textViewDni.setText(usuario.getDni());

                            TextView textViewPhoneNumber = findViewById(R.id.textViewTelefono);
                            textViewPhoneNumber.setText(usuario.getTelefono());

                            TextView textViewTypeUser = findViewById(R.id.textViewTipoUsuario);
                            textViewTypeUser.setText(usuario.getTipo_user());

                            ImageView imageViewPerfil = findViewById(R.id.imageViewPerfil);
                            String nombreImagen = usuario.getImg();
                            int resID = getResources().getIdentifier(nombreImagen, "drawable", getPackageName());
                            Picasso.get().load(resID).into(imageViewPerfil);
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
