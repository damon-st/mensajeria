package com.damon.messenger.util;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

public class AudioService {
    private Context context;
    public String duration;
    private MediaPlayer tmpMediaPlayer;

    public AudioService(Context context) {
        this.context = context;

    }

    public String data (String url){
        if (tmpMediaPlayer!=null){
            tmpMediaPlayer.stop();
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            duration = getTime(mediaPlayer.getDuration()/1000);
            return duration;
        } catch (IOException e) {
            e.printStackTrace();
            return "00:00";
        }
    }

    public void playAudioFromUrl(String url, final OnPlayCallBack onPlayCallBack){
        if (tmpMediaPlayer!=null){
            tmpMediaPlayer.stop();
        }

        MediaPlayer mediaPlayer = new MediaPlayer();
        try {

            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();

            tmpMediaPlayer = mediaPlayer;
            tmpMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                duration = getTime(mp.getDuration()/1000);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onPlayCallBack.onFinished();
            }
        });
    }

    public interface OnPlayCallBack{
        void onFinished();
    }

    public String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }
}