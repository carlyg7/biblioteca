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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.Objects;

public class MainCatalogo extends Fragment {

    private GridLayout gridLayoutBooks;
    private FirebaseFirestore db;
    private static final String TAG = "MainCatalogo";
    private SharedPreferences sharedPreferences;
    private String dniUsuario;
    private String rolUsuario;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalogo, container, false);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        // SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("mySharedPreferences", requireActivity().MODE_PRIVATE);
        dniUsuario = requireActivity().getIntent().getStringExtra("dniUsuario");
        rolUsuario = requireActivity().getIntent().getStringExtra("rolUsuario");

        // Obtener una referencia al GridLayout donde se agregarán las CardViews
        gridLayoutBooks = view.findViewById(R.id.gridLayoutBooks);
        // Mostrar los libros
        mostrarLibros();

        // Botón de agregar libros solo para administrador
        TextView anadirlibro = view.findViewById(R.id.anadirlibro);
        ImageView anadirlibro2 = view.findViewById(R.id.anadirlibro2);
        if("admin".equals(rolUsuario)){
            anadirlibro.setVisibility(View.VISIBLE);
            anadirlibro2.setVisibility(View.VISIBLE);
        }
        else{
            anadirlibro.setVisibility(View.GONE);
            anadirlibro2.setVisibility(View.GONE);
        }

        RelativeLayout boton_anadir_libro = view.findViewById(R.id.btn_anadirLibro);
        boton_anadir_libro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(requireActivity(), MainNuevoLibro.class);
                intent.putExtra("rolUsuario", rolUsuario);
                intent.putExtra("dniUsuario", dniUsuario);
                startActivity(intent);
            }
        });

        return view;
    }

    private void agregarCardViewLibro(Libro libro, String libroId) {
        // Decodificar la imagen de base64
        byte[] decodedString = Base64.decode(libro.getPortada(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        // Crear una nueva CardView
        CardView cardView = new CardView(requireContext());

        // Configurar la vista de la tarjeta
        GridLayout.LayoutParams cardLayoutParams = new GridLayout.LayoutParams();
        cardLayoutParams.width = getResources().getDimensionPixelSize(R.dimen.card_width); // Establecer un tamaño fijo para la tarjeta
        cardLayoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        cardLayoutParams.setMargins(8, 16, 8, 16); // Ajustar los márgenes izquierdo y derecho para centrar la tarjeta
        cardLayoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // Alinear la tarjeta en el centro de la columna

        // Crear un LinearLayout vertical para el contenido de la tarjeta
        LinearLayout cardContentLayout = new LinearLayout(requireContext());
        cardContentLayout.setOrientation(LinearLayout.VERTICAL);

        // Agregar la imagen del libro a la tarjeta
        ImageView bookCover = new ImageView(requireContext());
        bookCover.setImageBitmap(decodedByte);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.image_width), // Establecer un tamaño fijo para la imagen
                getResources().getDimensionPixelSize(R.dimen.image_height) // Establecer un tamaño fijo para la imagen
        );
        imageParams.gravity = Gravity.CENTER;
        imageParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.image_margin), 0, getResources().getDimensionPixelSize(R.dimen.image_margin)); // Establecer el margen superior
        cardContentLayout.addView(bookCover, imageParams);

        // Agregar un OnClickListener para abrir los detalles del libro
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Guardar la clave única del libro en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("libroId", libroId);
                editor.apply();

                // Abrir los detalles del libro
                Intent intent = new Intent(requireActivity(), MainLibro.class);
                intent.putExtra("libroId", libroId);
                intent.putExtra("rolUsuario", rolUsuario);
                intent.putExtra("dniUsuario", dniUsuario);
                startActivity(intent);
            }
        });

        // Agregar la vista de contenido a la tarjeta
        cardView.addView(cardContentLayout);

        // Agregar la CardView al GridLayout
        GridLayout gridLayout = requireView().findViewById(R.id.gridLayoutBooks);
        gridLayout.addView(cardView, cardLayoutParams);
        Log.d(TAG, "CardView agregada para el libro: " + libro.getTitulo());
    }

    private void mostrarLibros() {
        // Consulta a Firestore para obtener los datos de libros
        db.collection("libro")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Libro libro = document.toObject(Libro.class);
                                String libroId = document.getId(); // Obtener la clave única del libro
                                agregarCardViewLibro(libro, libroId);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
