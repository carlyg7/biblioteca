package com.example.biblioteca_cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.Context;

public class MainDatosUsuarios extends Fragment {
    private FirebaseFirestore db;
    private static final String TAG = "MainDatosUsuarios";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        // Inicializar Firebase y Firestore
        FirebaseApp.initializeApp(requireContext());
        db = FirebaseFirestore.getInstance();

        // Obtener el dni del usuario del que queremos mostrar los datos, guardado en SharedPreferences
        String dniUsuario = requireActivity().getIntent().getStringExtra("dniUsuario");
        obtenerDatosUsuario(dniUsuario);

        // Obtener referencia de icono cerrar sesion
        TextView cerrarSesion = view.findViewById(R.id.btn_logout);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
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
                            TextView textViewName = requireView().findViewById(R.id.textViewNombre);
                            textViewName.setText(usuario.getNombre() + " " + usuario.getApellidos());

                            TextView textViewUser = requireView().findViewById(R.id.textViewUsuario);
                            textViewUser.setText(usuario.getUsuario());

                            TextView textViewMail = requireView().findViewById(R.id.textViewCorreo);
                            textViewMail.setText(usuario.getCorreo());

                            TextView textViewDni = requireView().findViewById(R.id.textViewDNI);
                            textViewDni.setText(usuario.getDni());

                            TextView textViewPhoneNumber = requireView().findViewById(R.id.textViewTelefono);
                            textViewPhoneNumber.setText(usuario.getTelefono());

                            TextView textViewTypeUser = requireView().findViewById(R.id.textViewTipoUsuario);
                            textViewTypeUser.setText(usuario.getTipo_user());

                            ImageView imageViewPerfil = requireView().findViewById(R.id.imageViewPerfil);
                            String nombreImagen = usuario.getImg();
                            int resID = getResources().getIdentifier(nombreImagen, "drawable", requireActivity().getPackageName());
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

    private void logout() {
        // Limpiar cualquier dato de sesión almacenado localmente
        limpiarDatosSesion();

        // Redirigir al usuario a la pantalla de inicio de sesión o la pantalla principal
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish(); // Cierra la actividad actual para evitar que el usuario regrese presionando el botón "Atrás"
    }

    private void limpiarDatosSesion() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
