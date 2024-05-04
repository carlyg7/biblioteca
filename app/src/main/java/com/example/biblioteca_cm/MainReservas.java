package com.example.biblioteca_cm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.biblioteca_cm.Libro;
import com.example.biblioteca_cm.MainDatosUsuarios;
import com.example.biblioteca_cm.MainLibro;
import com.example.biblioteca_cm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainReservas extends Fragment {

    private LinearLayout linearLayoutReservas;
    private FirebaseFirestore db;
    private static final String TAG = "MainCatalogo";
    private SharedPreferences sharedPreferences;

    private String dniUsuario;
    private String rolUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservas, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        // SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("mySharedPreferences", requireActivity().MODE_PRIVATE);
        dniUsuario = requireActivity().getIntent().getStringExtra("dniUsuario");
        rolUsuario = requireActivity().getIntent().getStringExtra("rolUsuario");

        // Obtener referencia al LinearLayout donde se mostrarán las reservas
        linearLayoutReservas = view.findViewById(R.id.linearLayoutReservas);

        // Cargar las reservas desde Firestore y mostrarlas
        cargarReservas();

        return view;
    }

    private void cargarReservas() {
        if ("admin".equals(rolUsuario)) {
            // Si el usuario es un administrador, obtener todas las reservas
            db.collection("reserva")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Convertir el documento en un objeto Reserva
                                    Reserva reserva = document.toObject(Reserva.class);
                                    // Crear una vista para mostrar la reserva
                                    crearVistaReserva(reserva);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        } else {
            // Si el usuario no es un administrador, obtener solo las reservas que coincidan con su DNI
            db.collection("reserva")
                    .whereEqualTo("dni", dniUsuario)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Convertir el documento en un objeto Reserva
                                    Reserva reserva = document.toObject(Reserva.class);
                                    // Crear una vista para mostrar la reserva
                                    crearVistaReserva(reserva);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


    private void crearVistaReserva(Reserva reserva) {
        // Crear un nuevo CardView para mostrar la reserva
        CardView cardView = new CardView(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, convertirDpToPx(8)); // Margen inferior entre reservas
        cardView.setLayoutParams(params);
        cardView.setRadius(convertirDpToPx(4)); // Radio de borde del CardView
        cardView.setCardElevation(convertirDpToPx(2)); // Elevación del CardView
        cardView.setUseCompatPadding(true); // Alinear el contenido al borde del CardView

        // Crear un LinearLayout vertical para el contenido de la reserva
        LinearLayout linearLayoutReserva = new LinearLayout(requireContext());
        linearLayoutReserva.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        linearLayoutReserva.setOrientation(LinearLayout.VERTICAL);
        linearLayoutReserva.setPadding(
                convertirDpToPx(16), // Padding izquierdo
                convertirDpToPx(16), // Padding superior
                convertirDpToPx(16), // Padding derecho
                convertirDpToPx(16)  // Padding inferior
        );

        // Crear un TextView para mostrar el nombre del libro
        TextView textViewLibro = new TextView(requireContext());
        textViewLibro.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textViewLibro.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Tamaño del texto en sp
        textViewLibro.setText(reserva.getTitulo_libro());

        // Crear un TextView para mostrar el nombre del usuario
        TextView textViewUsuario = new TextView(requireContext());
        textViewUsuario.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textViewUsuario.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Tamaño del texto en sp
        textViewUsuario.setText(reserva.getNombre_usuario());

        // Agregar los TextView al LinearLayout de la reserva
        linearLayoutReserva.addView(textViewLibro);
        linearLayoutReserva.addView(textViewUsuario);

        // Si el usuario es administrador, agregar el botón "Cancelar reserva"
        if ("admin".equals(rolUsuario)) {
            Button btnCancelarReserva = new Button(requireContext());
            btnCancelarReserva.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            btnCancelarReserva.setText("Cancelar reserva");
            btnCancelarReserva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí ejecuta la lógica para cancelar la reserva
                    cancelarReserva(reserva);
                }
            });
            linearLayoutReserva.addView(btnCancelarReserva);
        }

        // Agregar el LinearLayout de la reserva al CardView
        cardView.addView(linearLayoutReserva);

        // Agregar el CardView al LinearLayout principal
        linearLayoutReservas.addView(cardView);
    }


    private int convertirDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void cancelarReserva(Reserva reserva) {
        // Eliminar la reserva de la base de datos
        db.collection("reserva")
                .document(reserva.getDni()+"::"+reserva.getIsbn()) // Aquí asumo que hay un método getId() en la clase Reserva para obtener el ID del documento en Firestore
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Cancelación exitosa, actualiza la UI o muestra un mensaje
                        Toast.makeText(requireContext(), "Reserva cancelada", Toast.LENGTH_SHORT).show();

                        // Poner libro como disponible
                        String idLibro = reserva.getIsbn();
                        actualizarDisponibilidadLibro(idLibro);
                        // Actualizar la interfaz de usuario para reflejar los cambios
                        actualizarVistaDespuesCancelarReserva(reserva);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al cancelar la reserva, muestra un mensaje de error
                        Log.e(TAG, "Error al cancelar reserva", e);
                        Toast.makeText(requireContext(), "Error al cancelar reserva", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarVistaDespuesCancelarReserva(Reserva reserva) {
        // Eliminar todas las vistas de reservas del layout
        linearLayoutReservas.removeAllViews();

        // Volver a cargar las reservas desde Firestore
        cargarReservas();
    }

    private void actualizarDisponibilidadLibro(String idLibro) {
        // Actualizar el campo 'disponible' del libro a true en Firestore
        db.collection("libro")
                .document(idLibro)
                .update("disponible", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Campo disponible del libro actualizado correctamente a true");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error al actualizar el campo disponible del libro", e);
                    }
                });
    }

}
