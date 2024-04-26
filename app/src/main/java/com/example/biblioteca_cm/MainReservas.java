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

    private LinearLayout linearLayoutBooks;
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


        return view;
    }
}
