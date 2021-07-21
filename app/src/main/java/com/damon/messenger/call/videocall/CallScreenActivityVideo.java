package com.damon.messenger.call.videocall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.damon.messenger.Activitys.ImageViewerActivity;
import com.damon.messenger.R;
import com.damon.messenger.call.newcall.AudioPlayer;
import com.damon.messenger.call.newcall.BaseActivity;
import com.damon.messenger.call.newcall.SinchService;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivityVideo extends BaseActivity {
    static final String TAG = CallScreenActivityVideo.class.getSimpleName();
    static final String ADDED_LISTENER = "addedListener";
    static final String VIEWS_TOGGLED = "viewsToggled";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private boolean mAddedListener = false;
    private boolean mLocalVideoViewAdded = false;
    private boolean mRemoteVideoViewAdded = false;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;
    boolean mToggleVideoViewPositions = false;
    private Button disableAudio,disableCamera;
    boolean audio, video = true;
//    private FrameListener mFrameListener;
    private String  name;


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivityVideo.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
        savedInstanceState.putBoolean(VIEWS_TOGGLED, mToggleVideoViewPositions);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
        mToggleVideoViewPositions = savedInstanceState.getBoolean(VIEWS_TOGGLED);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen_video);



        name = getIntent().getStringExtra("name");

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = findViewById(R.id.callDuration);
        mCallerName = findViewById(R.id.remoteUser);
        mCallState = findViewById(R.id.callState);
        Button endCallButton = findViewById(R.id.hangupButton);
        disableAudio = findViewById(R.id.disableAudio);
        disableCamera = findViewById(R.id.disableVideo);
//        mFrameListener = new FrameListener(this);

        endCallButton.setOnClickListener(v -> endCall());

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        disableAudio.setOnClickListener(v -> toggleAudio());

//        disableCamera.setOnClickListener(v -> takeScreenshot());

    }

    private void toggleAudio(){
        AudioController audioController = getSinchServiceInterface().getAudioController();
        if (audio){
            audio = false;
            disableAudio.setBackgroundResource(R.drawable.ic_terminargrabar);
            audioController.mute();
        }else {
            disableAudio.setBackgroundResource(R.drawable.ic_grabar);
            audio = true;
            audioController.unmute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (mFrameListener != null) {
//            if (getSinchServiceInterface() != null) {
//                getSinchServiceInterface().getVideoController().setRemoteVideoFrameListener(null);
//            }
//            mFrameListener = null;
//        }
    }

    private void takeScreenshot(){
        if (ActivityCompat.checkSelfPermission(CallScreenActivityVideo.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},40);
            }
        }
//        if (mFrameListener!=null){
//            mFrameListener.takeScreenshot();
//        }
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(new SinchCallListener());
                mAddedListener = true;
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }

        updateUI();
    }

    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
//            mCallerName.setText(call.getRemoteUserId());
            mCallerName.setText(name);
            mCallState.setText(call.getState().toString() == "INITIATING" ? "Llamando..." : call.getState().toString());
            if (call.getDetails().isVideoOffered()) {
                if (call.getState() == CallState.ESTABLISHED) {
                    setVideoViewsVisibility(true, true);
                } else {
                    setVideoViewsVisibility(true, false);
                }
            }
        } else {
            setVideoViewsVisibility(false, false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    private ViewGroup getVideoView(boolean localView) {
        if (mToggleVideoViewPositions) {
            localView = !localView;
        }
        return localView ? findViewById(R.id.localVideo) : findViewById(R.id.remoteVideo);
    }

    private void addLocalView() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                if (mLocalVideoViewAdded || getSinchServiceInterface() == null) {
                    return; //early
                }
                final VideoController vc = getSinchServiceInterface().getVideoController();
                if (vc != null) {
                    runOnUiThread(() -> {
                        ViewGroup localView = getVideoView(true);
                        localView.addView(vc.getLocalView());
                        localView.setOnClickListener(v -> vc.toggleCaptureDevicePosition());
                        mLocalVideoViewAdded = true;
                        vc.setLocalVideoZOrder(!mToggleVideoViewPositions);
                    });

                }
            }
        }.start();

    }
    private void addRemoteView() {
        if (mRemoteVideoViewAdded || getSinchServiceInterface() == null) {
            return; //early
        }
        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            runOnUiThread(() -> {
                ViewGroup remoteView = getVideoView(false);
                remoteView.addView(vc.getRemoteView());
                remoteView.setOnClickListener((View v) -> {
                    removeVideoViews();
                    mToggleVideoViewPositions = !mToggleVideoViewPositions;
                    addRemoteView();
                    addLocalView();
                });
//                if (mFrameListener != null) {
//                    if (getSinchServiceInterface() != null) {
//                        getSinchServiceInterface().getVideoController().setRemoteVideoFrameListener(mFrameListener);
//                    }
//                }

                mRemoteVideoViewAdded = true;
                vc.setLocalVideoZOrder(!mToggleVideoViewPositions);
            });
        }
    }


    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            runOnUiThread(() -> {
                ((ViewGroup)(vc.getRemoteView().getParent())).removeView(vc.getRemoteView());
                ((ViewGroup)(vc.getLocalView().getParent())).removeView(vc.getLocalView());
                mLocalVideoViewAdded = false;
                mRemoteVideoViewAdded = false;
            });
        }
    }

    private void setVideoViewsVisibility(final boolean localVideoVisibile, final boolean remoteVideoVisible) {
        if (getSinchServiceInterface() == null)
            return;
        if (mRemoteVideoViewAdded == false) {
            addRemoteView();
        }
        if (mLocalVideoViewAdded == false) {
            addLocalView();
        }
        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            runOnUiThread(() -> {
                vc.getLocalView().setVisibility(localVideoVisibile ? View.VISIBLE : View.GONE);
                vc.getRemoteView().setVisibility(remoteVideoVisible ? View.VISIBLE : View.GONE);
            });
        }
    }

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            System.out.println("onCallEnded"+ cause.toString());
            if (cause.toString().equals("NO_ANSWER"))
                Toast.makeText(CallScreenActivityVideo.this, "Lo sentimos tiempo agotado de respuesta no ay quien responda", Toast.LENGTH_LONG).show();
            else if (cause.toString().equals("TIMEOUT"))
                Toast.makeText(CallScreenActivityVideo.this, "Lo sentimos tiempo agotado de respuesta", Toast.LENGTH_LONG).show();
            else if (cause.toString().equals("HUNG_UP"))
                Toast.makeText(CallScreenActivityVideo.this, "LLamada Colgada", Toast.LENGTH_LONG).show();
            else if (cause.toString().equals("DENIED"))
                Toast.makeText(CallScreenActivityVideo.this, "Tu llamada fue rechazada", Toast.LENGTH_LONG).show();
            else if (cause.toString().equals("CANCELED"))
                Toast.makeText(CallScreenActivityVideo.this, "Cancelaste la videollamada", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(CallScreenActivityVideo.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString() == "ESTABLISHED" ? "Escuchando..." : call.getState().toString());
            System.out.println("onCallEstablished" + call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            if (call.getDetails().isVideoOffered()) {
                setVideoViewsVisibility(true, true);
            }
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {

        }

        @Override
        public void onVideoTrackPaused(Call call) {

        }

        @Override
        public void onVideoTrackResumed(Call call) {

        }
    }
}