package com.example.biblioteca_cm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainLogo extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.inicio_logo);

        // Ocultar ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Animation a1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation a2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);

        TextView biblioTextView = findViewById(R.id.biblioTextView);
        TextView byTextView = findViewById(R.id.byTextView);
        ImageView logo = findViewById(R.id.logoImageView);

        biblioTextView.setAnimation(a2);
        byTextView.setAnimation(a2);
        logo.setAnimation(a1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainLogo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }
}
