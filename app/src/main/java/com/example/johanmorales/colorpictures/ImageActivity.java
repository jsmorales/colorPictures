package com.example.johanmorales.colorpictures;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        ImageView fotoImageView = findViewById(R.id.fotoImageView);

        //se obtiene los datos enviados desde el intent de MainActivity
        Intent cargarFoto = getIntent();

        //para mostrar la imagen con la libreria picasso
        //https://github.com/square/picasso

        Picasso.get().load(cargarFoto.getData()).into(fotoImageView);
    }
}
