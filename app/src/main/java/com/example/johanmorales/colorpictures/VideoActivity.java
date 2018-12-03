package com.example.johanmorales.colorpictures;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = findViewById(R.id.videoView);

        Intent cargaVideo = getIntent();

        Uri videoUri = cargaVideo.getData();

        videoView.setVideoURI(videoUri);

        videoView.start();
    }
}
