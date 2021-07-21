package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.damon.messenger.Adapters.SaveImageHelper;
import com.damon.messenger.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageViewerActivity extends AppCompatActivity {
     private ImageView imageView;
     private String imageUrl;
     private PhotoViewAttacher photoViewAttacher;//esta libreria hace todo el trabajo del zoom
//     private Matrix  matrix = new Matrix();
//     Float scale = 1f;
//     ScaleGestureDetector SGD;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        toolbar = findViewById(R.id.tollbar);
        setSupportActionBar(toolbar);


        progressBar = findViewById(R.id.proges_dialog_image_viewer);
        imageView = findViewById(R.id.image_viewer);
        imageUrl = getIntent().getStringExtra("url");
        photoViewAttacher = new PhotoViewAttacher(imageView); //creamos una instancia y le pasamos el imageView

      //  SGD = new ScaleGestureDetector(this,new ScaleListener());

        if (imageUrl != null && imageUrl.length() >0){
            Picasso.get().load(imageUrl).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Picasso.get().load(R.mipmap.ic_launcher).into(imageView);
                }
            });
        }else {
            Toast.makeText(this, "Nose pudo cargar la imagen intente nuevamente", Toast.LENGTH_SHORT).show();
        }

    }

//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
//        @Override
//        public boolean onScale(ScaleGestureDetector detector) {
//
//            scale = scale*detector.getScaleFactor();
//            scale = Math.max(0.1f,Math.min(scale,5f));//comnprobacion para la pantalla sea grande o pequeña para escalar
//            matrix.setScale(scale,scale);
//            imageView.setImageMatrix(matrix);
//            return true;
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//
//        SGD.onTouchEvent(event);
//
//        return true;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 40:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    AlertDialog alertDialog = new SpotsDialog(ImageViewerActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Por favor espere...");

                    String  filename = UUID.randomUUID().toString() + ".jpg";
                    Picasso.get().load(imageUrl)
                            .into(new SaveImageHelper(getBaseContext(),
                                    alertDialog,
                                    getApplicationContext().getContentResolver(),
                                    filename,
                                    "Description"));
                }else {
                    Toast.makeText(this, "Permiso Necesario para Descargar Imagenes", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void DescargarImagen(){
        if (ActivityCompat.checkSelfPermission(ImageViewerActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},40);
            }
        }else {
            AlertDialog dialog = new SpotsDialog(ImageViewerActivity.this);
            dialog.show();
            dialog.setMessage("Por favor espera...");

            String filename = UUID.randomUUID().toString() + ".jpg";
            Picasso.get().load(imageUrl)
                    .into(new SaveImageHelper(getBaseContext(),
                            dialog,
                            getApplicationContext().getContentResolver(),
                            filename,
                            "Description"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.descarga,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);
         if (item.getItemId() == R.id.item_descargar){
             DescargarImagen();
         }
         return true;
    }
}
