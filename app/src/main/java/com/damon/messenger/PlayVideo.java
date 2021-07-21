package com.damon.messenger;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayVideo implements iConstants {


    public  static Dialog dialog;
    public static TextView contador_final,contador_inicial;
    public static DatabaseReference reference;
    public static ProgressBar progressBar;
    public Handler seekBarHandler;
    public Runnable updateSeekbar;
    public TextView descargar_audio;
     MediaPlayer vid;
     SeekBar seekBar;
     public ImageView closeAudio;
     private boolean cerrarAudio = false;



    public   void play(final Context context , final String url , final String  title , String ext) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.video_dialog);
        dialog.setTitle("Title...");

        final Button play = (Button)dialog.findViewById(R.id.play_boton);
        seekBar = (SeekBar)dialog.findViewById(R.id.custom_seekbar);
        final Button stop = (Button)dialog.findViewById(R.id.stop_boton);
        contador_final = (TextView)dialog.findViewById(R.id.final_contador);
        final CircleImageView  profileImage =(CircleImageView)dialog.findViewById(R.id.video_foto);
        reference = FirebaseDatabase.getInstance().getReference("Users");
        progressBar = dialog.findViewById(R.id.proges_dialog_audio);
        contador_inicial = dialog.findViewById(R.id.inicio_contador);
        descargar_audio = dialog.findViewById(R.id.descargar_audio);
        closeAudio = dialog.findViewById(R.id.close_audio);



        final int[] contador = new int[1];

        vid =  new MediaPlayer();

            new Thread(){
                @Override
                public void run() {
                    super.run();

                    try {
                        vid.setDataSource(url);
                        vid.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();




        vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                cerrarAudio = true;
                descargar_audio.setVisibility(View.VISIBLE);
                descargar_audio.setOnClickListener(v -> DowloadAuido(url,context));
                seekBar.setMax(vid.getDuration());
                seekBarHandler = new Handler();

                mp.start();
                play.setVisibility(View.INVISIBLE);
                stop.setVisibility(View.VISIBLE);

                updateRunnable();
                seekBarHandler.postDelayed(updateSeekbar,0);

                try {
//                    contador_final.setText(Integer.valueOf(s).toString());
                    contador_final.setText(PlayVideo.this.getTime(mp.getDuration()/1000));
                    System.out.println("currenPosition"+ mp.getCurrentPosition());
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                        reference.child(title).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    if (dataSnapshot.child("image").exists()) {
                                        String image = dataSnapshot.child("image").getValue().toString();
                                        try {

                                            Picasso.get().load(image).into(profileImage, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    progressBar.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Picasso.get().load(R.mipmap.ic_launcher).into(profileImage);
                                                }
                                            });
                                        } catch (Exception e) {

                                        }
                                    } else {
                                        Picasso.get().load(R.mipmap.ic_launcher).into(profileImage, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                progressBar.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Picasso.get().load(R.mipmap.ic_launcher).into(profileImage);
                                            }
                                        });
                                    }


                                } else {
                                    Toast.makeText(context, "no existe", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Picasso.get().load(R.mipmap.ic_launcher).into(profileImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Picasso.get().load(R.mipmap.ic_launcher).into(profileImage);
                            }
                        });
                    }

                }catch (Exception e){

                }



                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        play.setVisibility(View.INVISIBLE);
                        stop.setVisibility(View.VISIBLE);
                        if (vid.isPlaying()){
                            mp.stop();
                        }else {
                            try {
                                mp.start();
                            }catch (Exception e){
                                System.out.println("Error---"+e);
                            }
                        }

                        updateRunnable();
                        seekBarHandler.postDelayed(updateSeekbar,0);
//                            try {
//                                vid.start();
//                            }catch (Exception e){
//                                System.out.println("Error---"+e);
//                            }


//                       if (vid != null){
//                           new CountDownTimer(vid.getDuration(),100){
//
//                               @Override
//                               public void onTick(long millisUntilFinished) {
//
//                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                       try {
//                                           contador_inicial.setText(PlayVideo.this.getTime(vid.getCurrentPosition()/1000));
//                                       }catch (Exception e){
//                                           e.printStackTrace();
//                                       }
//
//                                   }
//                                   play.setVisibility(View.INVISIBLE);
//                                   stop.setVisibility(View.VISIBLE);
//                               }
//
//                               @Override
//                               public void onFinish() {
////                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
////                                    dialog.dismiss();
////                                }
//                                   play.setVisibility(View.VISIBLE);
//                                   stop.setVisibility(View.GONE);
//                               }
//                           }.start();
//                       }
                    }
                });

                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stop. setVisibility(View.INVISIBLE);
                        play.setVisibility(View.VISIBLE);
                      try {
                          mp.pause();
//                          vid.setDataSource(String.valueOf(Uri.parse(url)));
                      }catch (Exception e){
                          System.out.println("Error---"+e);
                      }
                        seekBarHandler.removeCallbacks(updateSeekbar);
                    }
                });

                try {
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                mp.pause();
                                stop. setVisibility(View.INVISIBLE);
                                play.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                stop. setVisibility(View.VISIBLE);
                                play.setVisibility(View.INVISIBLE);
                                int progress = seekBar.getProgress();
                                mp.seekTo(progress);
                                mp.start();
                            }
                        });

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

//        vid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                if (vid !=null){
//                    vid.stop();
//                    vid.release();
//                    seekBarHandler.removeCallbacks(updateSeekbar);
//                    dialog.dismiss();
//                }
//            }
//        });

        closeAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cerrarAudio){
                    cerrarAudio =false;
                    seekBarHandler.removeCallbacks(updateSeekbar);
                    vid.stop();
                    vid.release();
                    dialog.cancel();
                }

            }
        });

        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    public String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(vid.getCurrentPosition());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        contador_inicial.setText(PlayVideo.this.getTime(vid.getCurrentPosition()/1000));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                seekBarHandler.postDelayed(this,100);

            }
        };
    }

    private void DowloadAuido(String url, Context context){
        String name = UUID.randomUUID().toString();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("download");
        request.setTitle(""+name);
// in order for this if to run, you must use the android 3.2 to compile your app
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, ""+name+".mp3");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(context, "Descargando el audio por favor espera...", Toast.LENGTH_SHORT).show();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
    }
}
