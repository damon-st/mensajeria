package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.damon.messenger.R;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class VideoActivity extends AppCompatActivity {

    String url;
    VideoView videoView;
    ProgressBar progressBar;
    Toolbar toolbar;
    String name;
    boolean ready;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            ActivityCompat.requestPermissions(VideoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        toolbar = findViewById(R.id.tollbar);
        setSupportActionBar(toolbar);

        url = getIntent().getStringExtra("url");
//        Toast.makeText(this, ""+url, Toast.LENGTH_SHORT).show();
        videoView = findViewById(R.id.video_activity);
        progressBar = findViewById(R.id.proges_dialog_video);
        try {
            videoView.setVideoURI(Uri.parse(url));
        }catch (Exception e){
            e.printStackTrace();
        }

        MediaController mediaController  = new MediaController(this);


        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if ( what == mp.MEDIA_INFO_BUFFERING_START){
                    progressBar.setVisibility(View.VISIBLE);
                }else if (what == mp.MEDIA_INFO_BUFFERING_END){
                    progressBar.setVisibility(View.GONE);
                }
                return false;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                mp.start();
                ready = true;
            }
        });

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        name = UUID.randomUUID().toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.descargar_video,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.dowload_video){
            if (ready){
                downloadManager(url);

            }else {
                Toast.makeText(this, "Espere a que cargue el video Porfavor..", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void downloadManager(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("download");
        request.setTitle(""+name);
// in order for this if to run, you must use the android 3.2 to compile your app
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, ""+name+".mp4");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(this, "Descargando por favor espera...", Toast.LENGTH_SHORT).show();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults.length > 0){
            Toast.makeText(this, "Gracias ahora ya puedes descargar videos", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Por favor permite a la app el permiso de guardar videos", Toast.LENGTH_SHORT).show();
        }
    }
}