package com.example.biblioteca_cm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.biblioteca_cm.Controlador.PagerController;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainMenu extends AppCompatActivity {

    //CONFIGURAR MENU
    private TabLayout tablayout;
    private ViewPager viewpager;

    //CATALOGO
    private LinearLayout linearLayoutBooks;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private String dniUsuario;
    private String rolUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();
        //SharedPreferences
        sharedPreferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        dniUsuario = getIntent().getStringExtra("dniUsuario");
        rolUsuario = getIntent().getStringExtra("rolUsuario");

        //Configuracion del menu y las diferentes vistas
        menu_viewpager();

    }

    private void menu_viewpager() {
        // Inicialización de los componentes
        tablayout = findViewById(R.id.menu_cliente);
        viewpager = findViewById(R.id.viewPager);

        // Configuración del adaptador para el ViewPager
        PagerController pagerAdapter = new PagerController(getSupportFragmentManager(), tablayout.getTabCount());
        viewpager.setAdapter(pagerAdapter);

        // Configuración de la selección de pestañas
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewpager.setCurrentItem(tab.getPosition());
                Log.d("MainMenu", "Tab selected: " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Sincronización de los cambios de pestaña con el ViewPager
        viewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tablayout));
    }


}
