package com.damon.messenger.call.newcall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {


    static final String TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;

    private String nameReceiver;

    private ImageView imgSender;
    private String receiverID;
    private String image;
    private Button enableAudio;
    private boolean audio;


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);

        nameReceiver = getIntent().getStringExtra("name");
        receiverID = getIntent().getStringExtra("receiverID");
        image = getIntent().getStringExtra("image");

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.callDuration);
        mCallerName = (TextView) findViewById(R.id.remoteUser);
        mCallState = (TextView) findViewById(R.id.callState);
        imgSender = findViewById(R.id.imagen_sender);
        Button endCallButton = (Button) findViewById(R.id.hangupButton);
        enableAudio = findViewById(R.id.enableAudio);

        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        enableAudio.setOnClickListener(v -> toggleAudio());

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        try {
            Picasso.get().load(image).into(imgSender);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void toggleAudio(){
        AudioController audioController = getSinchServiceInterface().getAudioController();
     if (audio){
         audio = false;
         enableAudio.setBackgroundResource(R.drawable.ic_terminargrabar);
         audioController.mute();
     }else {
         audio = true;
         enableAudio.setBackgroundResource(R.drawable.ic_grabar);
         audioController.unmute();
     }
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
//            mCallerName.setText(call.getRemoteUserId());
            mCallerName.setText(nameReceiver);
            mCallState.setText(call.getState().toString() == "INITIATING" ? "Llamando..." : "INITIATING");
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
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

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            System.out.println("onCallEnded"+cause.toString());
            if (cause.toString().equals("NO_ANSWER"))
                Toast.makeText(CallScreenActivity.this, "Llamada sin respuesta no ay quien conteste...", Toast.LENGTH_SHORT).show();
            else if (cause.toString().equals("TIMEOUT"))
                Toast.makeText(CallScreenActivity.this, "Se termino el Tiempo", Toast.LENGTH_SHORT).show();
            else if (cause.toString().equals("DENIED"))
                Toast.makeText(CallScreenActivity.this, "Tu llamada fue rechazada", Toast.LENGTH_LONG).show();
            else if (cause.toString().equals("CANCELED"))
                Toast.makeText(CallScreenActivity.this,"Tu cancelaste la llamada",Toast.LENGTH_SHORT).show();
            else if (cause.toString().equals("HUNG_UP"))
                Toast.makeText(CallScreenActivity.this, "Se a finalizado tu llamada :D", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            System.out.println(" onCallEstablished"+ call.getState().toString());
            mCallState.setText(call.getState().toString() == "ESTABLISHED" ? "Escuchando" : call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.disableSpeaker();
            audioController.enableAutomaticAudioRouting(true, AudioController.UseSpeakerphone.SPEAKERPHONE_AUTO);
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

    }
}