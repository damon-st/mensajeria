package com.damon.messenger.viewholders;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.messenger.Activitys.VideoActivity;
import com.damon.messenger.R;
import com.damon.messenger.interfaces.VideoPlaying;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder{


    public TextView senderMessageText , reciverMessageText,duration_audio,duration_audio_receiver,nombreUsuarioRespuesta,
            texto_aresponder_sender,texto_respuesta_sender,nombreUsuarioRespuestaReceiver,texto_aresponder_receiver,texto_respuesta_receiver,
            msg_img_sender,msg_img_receiver;
    public CircleImageView reciverProfileImage;
    public ImageView messageSenderPicture,messageReceiverPicture,img_responder_sender,img_responder_receiver;
    public TextView txt_seen1, txt_seen,make_msg_sender,time_video_sender,time_video_receiver;
    public ImageView visto;
    public RelativeLayout lyt_parent;
    public LinearLayout layout_voice,layout_voice_receiver;
    public ImageButton btn_play_auido;
    public CardView card_sender,card_receiver;
    public ImageView play_btn_sender,play_btn_receiver,voice_video_sender,voice_video_receiver,voice_off;
    public VideoView videoViewReceiver;

    public FrameLayout video_layout_sender;


    public PlayerView playerView;
    public SimpleExoPlayer exoPlayer;

    private PlaybackStateListener playbackStateListener;
    public TextView time_duration;
    public ProgressBar progressVideo;
    public VideoPlaying videoPlaying;


    public MessageViewHolder(@NonNull View itemView,VideoPlaying videoPlaying) {
        super(itemView);

        senderMessageText = itemView.findViewById(R.id.sender_message_text);
        reciverMessageText = itemView.findViewById(R.id.reciver_message_text);
        reciverProfileImage = itemView.findViewById(R.id.message_profile_image);
        messageSenderPicture = itemView.findViewById(R.id.message_sender_imageView);
        messageReceiverPicture = itemView.findViewById(R.id.message_receiver_imageView);
        txt_seen = itemView.findViewById(R.id.txt_seen);
        txt_seen1 = itemView.findViewById(R.id.txt_seen1);
        visto = itemView.findViewById(R.id.visto);
        make_msg_sender = itemView.findViewById(R.id.make_msg);
        lyt_parent = itemView.findViewById(R.id.layout_msg);
        layout_voice = itemView.findViewById(R.id.layout_voice);
        btn_play_auido = itemView.findViewById(R.id.btn_play_chat);
        duration_audio = itemView.findViewById(R.id.duration);
        layout_voice_receiver = itemView.findViewById(R.id.layout_voice_receiver);
        duration_audio_receiver = itemView.findViewById(R.id.duration_receiver);
        card_sender = itemView.findViewById(R.id.carview_msg_sender);
        nombreUsuarioRespuesta = itemView.findViewById(R.id.nombre_usuario_respuesta);
        texto_aresponder_sender = itemView.findViewById(R.id.texto_msg_antiguo);
        texto_respuesta_sender = itemView.findViewById(R.id.texto_msg);
        img_responder_sender = itemView.findViewById(R.id.img_msg_antiguo);
        card_receiver = itemView.findViewById(R.id.carview_msg_receiver);
        nombreUsuarioRespuestaReceiver = itemView.findViewById(R.id.nombre_res_receiver);
        img_responder_receiver = itemView.findViewById(R.id.img_msg_receiver);
        texto_aresponder_receiver = itemView.findViewById(R.id.texto_msg_receiver);
        texto_respuesta_receiver = itemView.findViewById(R.id.texto_msg_new_receiver);
        play_btn_sender = itemView.findViewById(R.id.play_btn_video_sender);
        play_btn_receiver = itemView.findViewById(R.id.play_btn_video_receiver);

        video_layout_sender = itemView.findViewById(R.id.video_layout);

        voice_video_sender = itemView.findViewById(R.id.voice_sender_video);
        videoViewReceiver = itemView.findViewById(R.id.videoReceiver);
        voice_video_receiver = itemView.findViewById(R.id.voice_receiver_video);
        time_video_sender= itemView.findViewById(R.id.sender_time_video);
        time_video_receiver = itemView.findViewById(R.id.receiver_time_video);
        msg_img_sender = itemView.findViewById(R.id.msg_sender_img);
        msg_img_receiver = itemView.findViewById(R.id.msg_receiver_img);


        voice_off = itemView.findViewById(R.id.ic_voice);
        time_duration = itemView.findViewById(R.id.time_video);

        progressVideo = itemView.findViewById(R.id.progress_video);

        playerView = itemView.findViewById(R.id.ep_video_view);

        this.videoPlaying = videoPlaying;
        playbackStateListener = new PlaybackStateListener();
    }


    private class  PlaybackStateListener implements  Player.EventListener{
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case ExoPlayer.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    progressVideo.setVisibility(View.VISIBLE);
                    videoPlaying.isPlaying(exoPlayer,false);
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    progressVideo.setVisibility(View.VISIBLE);
                    videoPlaying.isPlaying(exoPlayer,false);
                    break;
                case ExoPlayer.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    progressVideo.setVisibility(View.GONE);
                    voice_off.setVisibility(View.VISIBLE);
                    videoPlaying.isPlaying(exoPlayer,true);
                    break;
                case ExoPlayer.STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    progressVideo.setVisibility(View.GONE);
                    videoPlaying.isPlaying(exoPlayer,false);

                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
        }
    }
    public void releaseExo(){
        if (exoPlayer != null){
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void setVideo(final Application ctx, final String url) {

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(ctx).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(ctx);
            Uri video = Uri.parse(url);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            if (url.contains("https")){
                exoPlayer.prepare(mediaSource);
            }else {
                ExtractorMediaSource audioSource = new ExtractorMediaSource(
                        Uri.parse(url),
                        new DefaultDataSourceFactory(ctx.getApplicationContext(),"MyExoplayer"),
                        new DefaultExtractorsFactory(),
                        null,
                        null
                );
                exoPlayer.prepare(audioSource);
            }

            exoPlayer.addListener(playbackStateListener);
            exoPlayer.setPlayWhenReady(true);

            exoPlayer.setVolume(0);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);


            final boolean[] volumen = {true};

            voice_off.setOnClickListener(v -> {
                if (volumen[0]){
                    volumen[0] = false;
                    voice_off.setImageResource(R.drawable.ic_voice_on);
                    exoPlayer.setVolume(1);
                }else {
                    volumen[0] = true;
                    voice_off.setImageResource(R.drawable.ic_voice_of);
                    exoPlayer.setVolume(0);
                }
            });


        } catch (Exception e) {
            Log.e("ViewHolder2", "exoplayer error" + e.toString());
        }


    }

    public void pararVideo(){
        if (exoPlayer != null){
            if (exoPlayer.isPlaying()){
                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            }
        }

    }


    public void continuarVideo(){
        if (exoPlayer != null){
            if (!exoPlayer.isPlaying()){
                exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
            }
        }

    }

    public void VideoActivity(Activity activity , String  url){
        if (exoPlayer != null){
            boolean playWhenReady = true;
            int currentWindow = 0;
            long playbackPosition = 0;

            Intent intent = new Intent(activity, VideoActivity.class);
            playWhenReady = exoPlayer.getPlayWhenReady();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            intent.putExtra("url",url);
            intent.putExtra("playWhenReady",playWhenReady);
            intent.putExtra("playbackPosition",playbackPosition);
            intent.putExtra("currentWindow",currentWindow);
            pararVideo();
            activity.startActivity(intent);
        }

    }

}