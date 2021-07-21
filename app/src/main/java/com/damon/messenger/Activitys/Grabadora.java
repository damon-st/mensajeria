package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class Grabadora extends AppCompatActivity {

    private MediaRecorder grabacion;
    private String archivoSalidad =null;
    private Button btn_recorder,btn_reproducir;
    private ProgressDialog mProgress;
    private StorageReference reference;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabadora);

        mProgress = new ProgressDialog(this);

        btn_recorder = findViewById(R.id.btn_rec);
        btn_reproducir = findViewById(R.id.btn_play);

        auth = FirebaseAuth.getInstance();


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Grabadora.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Grabadora.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1000);
        }



        btn_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grabacion == null) {
                    archivoSalidad = Environment.getExternalStorageDirectory().getAbsolutePath() ;
                    archivoSalidad += "/Grabacion.mp3";
                    grabacion = new MediaRecorder();
                    grabacion.reset();
                    grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
                    grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                    grabacion.setOutputFile(archivoSalidad);

                    try {
                        grabacion.prepare();
                        grabacion.start();
                    } catch (IOException e) {

                    }
                    btn_recorder.setBackgroundResource(R.drawable.rec);
                    Toast.makeText(Grabadora.this, "Grabando...", Toast.LENGTH_SHORT).show();
                }else if (grabacion !=null){
                    try{
                        grabacion.stop();
                        uploadAudio();
                    }catch(RuntimeException stopException){
                        //handle cleanup here
                    }
                    grabacion.release();
                    grabacion = null;

                    btn_recorder.setBackgroundResource(R.drawable.stop_rec);
                    Toast.makeText(Grabadora.this, "Grabacion Finalizada", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(archivoSalidad);
                    mediaPlayer.prepare();
                }catch (IOException e){
                    System.out.println("ERRORRR----"+e);
                }
                mediaPlayer.start();
                Toast.makeText(Grabadora.this, "Reproducuendo audio", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadAudio(){
        mProgress.setMessage("Subiendo");
        mProgress.show();
        reference = FirebaseStorage.getInstance().getReference().child("audio");
        final StorageReference filePath = reference.child(auth.getUid()+".mp3");

        Uri uri = Uri.fromFile(new File(archivoSalidad));
        filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(Grabadora.this, "Exito", Toast.LENGTH_SHORT).show();
                }else {
                    mProgress.dismiss();
                    Toast.makeText(Grabadora.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
