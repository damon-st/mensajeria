package com.damon.messenger.call;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Activitys.CallingActivity;
import com.damon.messenger.Activitys.FingerprintActivity;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.Activitys.SplashActivity;
import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;

public class Call2 extends AppCompatActivity {

    private static final String APP_KEY = "92832192-d610-4007-b44b-825693aaada0";
    private static final String APP_SECRET = "4bQ70YlN5kKRPhSxwj/U0Q==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private TextView callState;
    private SinchClient sinchClient;
    private Button button;
    private String callerId;
    private String recipientId;
    ImageView imagenCall;

    private DatabaseReference userRef;
    private String  callingID="",ringingID ="";
    private final int DURACION_SPLASH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call2);


        imagenCall = findViewById(R.id.make_call);
        userRef = FirebaseDatabase.getInstance().getReference().child("Chat");

        Intent intent = getIntent();
        callerId = intent.getStringExtra("callerId");
        recipientId = intent.getStringExtra("recipientId");

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(callerId)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        button = (Button) findViewById(R.id.button);
        callState = (TextView) findViewById(R.id.callState);


        imagenCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call == null) {
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    button.setText("Colgar");
                    imagenCall.setImageResource(R.drawable.cancel_call);
                } else {
                    call.hangup();
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callUser(recipientId);
                    call.addCallListener(new SinchCallListener());
                    button.setText("Colgar");
                } else {
                    call.hangup();
                }
            }
        });
    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
            button.setText("Call");
            imagenCall.setImageResource(R.drawable.make_call);
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            cancelCallingUser();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private class SinchCallClientListener implements CallClientListener {
        @Override
        public void onIncomingCall(CallClient callClient, Call incomingCall) {
            call = incomingCall;
            Toast.makeText(Call2.this, "incoming call", Toast.LENGTH_SHORT).show();
            call.answer();
            call.addCallListener(new SinchCallListener());
            button.setText("Colgar");
            imagenCall.setImageResource(R.drawable.cancel_call);
        }
    }

    //metodo para cancelar la llmada
    private void cancelCallingUser() {
        //metodo para cancelar el que esta llamndo
//        userRef.child(callerId).child("Calling")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()&& dataSnapshot.hasChild("calling")){
//                            callingID = dataSnapshot.child("calling").getValue().toString();
//
//                            userRef.child(callingID)
//                                    .child("Ringing")
//                                    .removeValue()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()){
//
//                                                userRef.child(callerId)
//                                                        .child("Calling")
//                                                        .removeValue()
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
////
//                                                                if (task.isSuccessful()){
//                                                                    userRef.child(callerId)
//                                                                            .child("Ringing")
//                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                                @Override
//                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                                                                    if (dataSnapshot.exists()&& dataSnapshot.hasChild("ringing")){
//
//                                                                                        ringingID = dataSnapshot.child("ringing").getValue().toString();
//
//                                                                                        userRef.child(ringingID)
//                                                                                                .child("Calling")
//                                                                                                .removeValue()
//                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                    @Override
//                                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                                        if (task.isSuccessful()){
//
//                                                                                                            userRef.child(callerId)
//                                                                                                                    .child("Ringing")
//                                                                                                                    .removeValue()
//                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                                                        @Override
//                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                                                                                                            startActivity(new Intent(Call2.this,MainActivity.class));
//                                                                                                                            finish();
//                                                                                                                        }
//                                                                                                                    });
//                                                                                                        }
//                                                                                                    }
//                                                                                                });
//                                                                                    }else {
//                                                                                        startActivity(new Intent(Call2.this,MainActivity.class));
//                                                                                        finish();
//                                                                                    }
//                                                                                }
//
//                                                                                @Override
//                                                                                public void onCancelled(DatabaseError databaseError) {
//
//                                                                                }
//                                                                            });
//                                                                }
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    });
//                        }else {
//                            startActivity(new Intent(Call2.this,MainActivity.class));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

        //metodo para el que recive y cancela la lamda
//        userRef.child(callerId)
//                .child("Ringing")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()&& dataSnapshot.hasChild("ringing")){
//
//                            ringingID = dataSnapshot.child("ringing").getValue().toString();
//
//                            userRef.child(ringingID)
//                                    .child("Calling")
//                                    .removeValue()
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()){
//
//                                                userRef.child(callerId)
//                                                        .child("Ringing")
//                                                        .removeValue()
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                startActivity(new Intent(Call2.this,MainActivity.class));
//                                                                finish();
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    });
//                        }else {
//                            startActivity(new Intent(Call2.this,MainActivity.class));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

//
//        toca borrar estas lineas si estan mal
//        Estas lineas le puse asi tocara ver si se borra
        userRef.child(callerId)
                .child("Calling")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()&& dataSnapshot.hasChild("calling")){
                            callingID = dataSnapshot.child("calling").getValue().toString();

                            userRef.child(callingID)
                                    .child("Ringing")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                userRef.child(callerId)
                                                        .child("Calling")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                startActivity(new Intent(Call2.this,MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }else {
                            startActivity(new Intent(Call2.this,MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        userRef.child(callerId)
                .child("Ringing")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()&& dataSnapshot.hasChild("ringing")){

                            ringingID = dataSnapshot.child("ringing").getValue().toString();

                            userRef.child(ringingID)
                                    .child("Calling")
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                userRef.child(callerId)
                                                        .child("Ringing")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                startActivity(new Intent(Call2.this,MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }else {
                            startActivity(new Intent(Call2.this,MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {

        try {
            cancelCallingUser();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}