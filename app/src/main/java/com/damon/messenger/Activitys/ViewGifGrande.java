package com.damon.messenger.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.damon.messenger.R;

public class ViewGifGrande extends AppCompatActivity {

    private String imageUlr;
    private ImageView gifView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gif_grande);
        gifView = findViewById(R.id.gif_image);
        imageUlr = getIntent().getStringExtra("gif");

        Glide.with(ViewGifGrande.this).load(imageUlr).into(gifView);
    }
}