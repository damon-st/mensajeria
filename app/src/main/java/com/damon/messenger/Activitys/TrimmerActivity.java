package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.damon.messenger.R;
import com.damon.trimmervideo.HgLVideoTrimmer;
import com.damon.trimmervideo.interfaces.OnHgLVideoListener;
import com.damon.trimmervideo.interfaces.OnTrimVideoListener;
import com.damon.trimmervideo.utils.FileUtils;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.Util;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnHgLVideoListener {

    private HgLVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;

    private String path;
    private int maxDuration = 10;
    private StorageReference storageReference;
    private  DatabaseReference userMessagerKeyRef;
    private DatabaseReference RootRef;
    private StorageReference filePath ;
    private String messagemSenderID,messageReciverID,saveCutrrentTime;
    private String messageReciverRef,messageSenderRef,messagePushID,saveCurrentData;

    private Date fechaEnviado;
    private long fechaTiempo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        fechaEnviado = new Date();
        fechaTiempo = fechaEnviado.getTime();

        path = getIntent().getStringExtra("video_path");
        maxDuration = getIntent().getIntExtra("duration",10);
        messagemSenderID = getIntent().getStringExtra("messagemSenderID");
        messageReciverID = getIntent().getStringExtra("messageReciverID");
        messageReciverRef = getIntent().getStringExtra("messageReciverRef");
        messageSenderRef = getIntent().getStringExtra("messageSenderRef");
        saveCutrrentTime = getIntent().getStringExtra("saveCutrrentTime");
        saveCurrentData = getIntent().getStringExtra("saveCurrentData");


        RootRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
        userMessagerKeyRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID).push();
        messagePushID = userMessagerKeyRef.getKey();
        filePath = storageReference.child(messagePushID + "." + "mp4");

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Preparando para enviar...");

        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {


            /**
             * get total duration of video file
             */
            Log.e("tg", "maxDuration = " + maxDuration);
            //mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                 Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTrimStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void getResult(Uri uri) {
//        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, getString(R.string.video_saved_at, contentUri.getPath()), Toast.LENGTH_SHORT).show();

            }
        });

        try {

            String path = uri.getPath();
            File file = new File(path);
            System.out.println(file.length());
            System.out.println(uri);
            Log.e("tg", " path1 = " + path + " uri1 = " + Uri.fromFile(file));
//            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
//            intent.setDataAndType(Uri.fromFile(file), "video/*");
//            startActivity(intent);
//            finish();
            int s = Math.round(file.length());
            int da = s / (1024 * 1024);
            if (da <= 10){
                sendVideo(Uri.fromFile(file));
            }else {
                mProgressDialog.cancel();
                mVideoTrimmer.destroy();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TrimmerActivity.this, "Lo Sentimos porfavor el Peso Maximo del Video 10MB ", Toast.LENGTH_LONG).show();
                    }
                });
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("tg", "resultCode = " + resultCode + " data " + data);
    }


    void  sendVideo(Uri fileUri) {
        new Thread(){
            @Override
            public void run() {
                super.run();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.show();
                    }
                });

                filePath.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri dowloadUrl = task.getResult();
                            Map messageTextBody = new HashMap();
                            messageTextBody.put("message", dowloadUrl.toString());
                            messageTextBody.put("name", fileUri.getLastPathSegment());
                            messageTextBody.put("type", "mp4");
                            messageTextBody.put("from", messagemSenderID);
                            messageTextBody.put("to", messageReciverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCutrrentTime);
                            messageTextBody.put("date", saveCurrentData);
                            messageTextBody.put("sender", messagemSenderID);
                            messageTextBody.put("receiver", messageReciverID);
                            messageTextBody.put("isseen", false);
                            messageTextBody.put("type_responder","");
                            messageTextBody.put("msg_responder_nombre_responder","");
                            messageTextBody.put("msg_sender_responder","");
                            messageTextBody.put("position",0);
                            messageTextBody.put("msgImage","");
                            messageTextBody.put("fecha",fechaTiempo);


                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.cancel();
                                }
                            });

                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TrimmerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        //  file.delete();
                    }
                });
            }
        }.start();
    }
}