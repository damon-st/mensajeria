package com.damon.messenger.call.videocall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.damon.messenger.call.newcall.AudioPlayer;
import com.damon.messenger.call.newcall.BaseActivity;
import com.damon.messenger.call.newcall.CallScreenActivity;
import com.damon.messenger.call.newcall.IncomingCallScreenActivity;
import com.damon.messenger.call.newcall.SinchService;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IncomingCallScreenVideoActivity extends BaseActivity {


    static final String TAG = IncomingCallScreenVideoActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    private String name,image;
    private ImageView imagenCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_screen_video);
        Button answer = (Button) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);
        Button decline = (Button) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);
        imagenCall = findViewById(R.id.image_call_video);

        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        try {
            Picasso.get().load(image).into(imagenCall);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new IncomingCallScreenVideoActivity.SinchCallListener());
            TextView remoteUser = (TextView) findViewById(R.id.remoteUser);
//            remoteUser.setText(call.getRemoteUserId());
            remoteUser.setText(name);
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            try {
                call.answer();
                Intent intent = new Intent(this, CallScreenActivityVideo.class);
                intent.putExtra(SinchService.CALL_ID, mCallId);
                intent.putExtra("name",name);
                startActivity(intent);
            } catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }
        } else {
            finish();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Ahora puedes responder la videollamada", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Esta aplicación necesita permiso para usar su micrófono para funcionar correctamente.", Toast
                    .LENGTH_LONG).show();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
    }
}