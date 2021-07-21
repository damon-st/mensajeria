package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.damon.messenger.call.Call2;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CallingActivity extends AppCompatActivity {

    private TextView nameContact;
    private ImageView profileImage;
    private ImageView cancelCallBtn,acceptCallBtn;
    private String receiverUserdId="",receiverUserImage="",receiverUsername="";
    private String senderUserdId="",senderUserImage="", senderUsername="",checker="";
    private String  callingID="",ringingID ="";

    private DatabaseReference userRef;
    private MediaPlayer mediaPlayer;
    private String senderID;



    private static final String APP_KEY = "92832192-d610-4007-b44b-825693aaada0";
    private static final String APP_SECRET = "4bQ70YlN5kKRPhSxwj/U0Q==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private Call call;
    private TextView callState;
    private SinchClient sinchClient;
    private Button button;
    private String callerId;
    private String recipientId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);


        senderID = getIntent().getExtras().get("senderID").toString();
        senderUserdId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverUserdId = getIntent().getExtras().get("id").toString();



        callState = findViewById(R.id.txt);
        callerId = getIntent().getStringExtra("senderID");
        recipientId = getIntent().getStringExtra("id");
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

        sinchClient.getCallClient().addCallClientListener(new CallingActivity.SinchCallClientListener());






        userRef = FirebaseDatabase.getInstance().getReference().child("Chat");

        nameContact = findViewById(R.id.name_calling);
        profileImage = findViewById(R.id.profile_image_calling);
        cancelCallBtn = findViewById(R.id.cancel_call);
        acceptCallBtn = findViewById(R.id.make_call);

        mediaPlayer = MediaPlayer.create(this,R.raw.ringing);
        mediaPlayer.setVolume(0.1f,0.1f);

        cancelCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying() && mediaPlayer!=null){
                    try {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }


                checker = "clicked";
                cancelCallingUser();
            }
        });


        acceptCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.setVolume(0.0f,0.0f);
                mediaPlayer.stop();
                mediaPlayer.release();

                final HashMap<String,Object> callingPickUpMap = new HashMap<>();
                callingPickUpMap.put("picked","picked");

                userRef.child(senderUserdId).child("Ringing")
                        .updateChildren(callingPickUpMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
//                                    Intent intent = new Intent(CallingActivity.this,VideoChatActivity.class);
//                                    intent.putExtra("id",receiverUserdId);
//                                    startActivity(intent);
//

                Intent intent = new Intent(getApplicationContext(), Call2.class);
                intent.putExtra("callerId", senderID);
                intent.putExtra("recipientId", receiverUserdId);
                startActivity(intent);
                finish();

//                                    if (call == null) {
//                                        call = sinchClient.getCallClient().callUser(recipientId);
//                                        call.addCallListener(new CallingActivity.SinchCallListener());
////                                        button.setText("Colgar");
//                                    } else {
//                                        call.hangup();
//                                    }
                                }
                            }
                        });
            }
        });



        getAndSetUserProfileInfo();//metodo para mostar los datos dle usuario

    }

    //metodo para cancelar la llmada
    private void cancelCallingUser() {
        //metodo para cancelar el que esta llamndo
//        userRef.child(senderUserdId).child("Calling")
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
//                                                userRef.child(senderUserdId)
//                                                        .child("Calling")
//                                                        .removeValue()
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                startActivity(new Intent(CallingActivity.this,MainActivity.class));
//                                                                finish();
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    });
//                        }else {
//                            startActivity(new Intent(CallingActivity.this,MainActivity.class));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//        //metodo para el que recive y cancela la lamda
//        userRef.child(senderUserdId)
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
//                                                userRef.child(senderUserdId)
//                                                        .child("Ringing")
//                                                        .removeValue()
//                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                startActivity(new Intent(CallingActivity.this,MainActivity.class));
//                                                                finish();
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    });
//                        }else {
//                            startActivity(new Intent(CallingActivity.this,MainActivity.class));
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });


        //toca borrar estas lineas si estan mal
        //Estas lineas le puse asi tocara ver si se borra
        userRef.child(senderUserdId)
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

                                                userRef.child(senderUserdId)
                                                        .child("Calling")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                startActivity(new Intent(CallingActivity.this,MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }else {
                            startActivity(new Intent(CallingActivity.this,MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        userRef.child(senderUserdId)
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

                                                userRef.child(senderUserdId)
                                                        .child("Ringing")
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                startActivity(new Intent(CallingActivity.this,MainActivity.class));
                                                                finish();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }else {
                            startActivity(new Intent(CallingActivity.this,MainActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void getAndSetUserProfileInfo() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(receiverUserdId).exists()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(receiverUserdId).exists()){
                                receiverUserImage = dataSnapshot.child(receiverUserdId).child("image").getValue().toString();
                                receiverUsername = dataSnapshot.child(receiverUserdId).child("name").getValue().toString();

                                nameContact.setText(receiverUsername);
                                Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile_image).into(profileImage);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(senderUserdId).exists()){
                            senderUserImage = dataSnapshot.child(senderUserdId).child("image").getValue().toString();
                            senderUsername = dataSnapshot.child(senderUserdId).child("name").getValue().toString();

//                    nameContact.setText(senderUsername);
//                    Picasso.get().load(senderUserImage).placeholder(R.drawable.profile_image).into(profileImage);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();


        mediaPlayer.start();

        userRef.child(receiverUserdId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!checker.equals("clicked")&&!dataSnapshot.hasChild("Calling")&&!dataSnapshot.hasChild("Ringing") ){


                            final HashMap<String ,Object> callingInfo = new HashMap<>();
//                            callingInfo.put("uid",senderUserdId);
//                            callingInfo.put("name",senderUsername);
//                            callingInfo.put("image",senderUserImage);
                            callingInfo.put("calling",receiverUserdId);

                            userRef.child(senderUserdId)
                                    .child("Calling")
                                    .updateChildren(callingInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                final HashMap<String ,Object> ringingInfo = new HashMap<>();
//                                                ringingInfo.put("uid",receiverUserdId);
//                                                ringingInfo.put("name",receiverUsername);
//                                                ringingInfo.put("image",receiverUserImage);
                                                ringingInfo.put("ringing",senderUserdId);

                                                userRef.child(receiverUserdId)
                                                        .child("Ringing")
                                                        .updateChildren(ringingInfo);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        //para ver el boton de aceptar llamda
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(senderUserdId).hasChild("Ringing")&&!dataSnapshot.child(senderUserdId).hasChild("Calling")){

                    acceptCallBtn.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child(receiverUserdId).child("Ringing").hasChild("picked")){

                    try {
                        mediaPlayer.setVolume(0.0f,0.0f);
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }catch (Exception e){
                        System.out.println("Error---"+e);
                    }

//                    Intent intent = new Intent(CallingActivity.this,VideoChatActivity.class);
//                    intent.putExtra("id",receiverUserdId);
//                    startActivity(intent);


                    Intent intent = new Intent(getApplicationContext(), Call2.class);
                    intent.putExtra("callerId", senderID);
                    intent.putExtra("recipientId", receiverUserdId);
                    startActivity(intent);
                    finish();

//                    if (call == null) {
//                        call = sinchClient.getCallClient().callUser(recipientId);
//                        call.addCallListener(new CallingActivity.SinchCallListener());
////                        button.setText("Colgar");
//                    } else {
//                        call.hangup();
//                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelCallingUser();
        finish();
    }

    @Override
    protected void onDestroy() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.setVolume(0.0f,0.0f);
                mediaPlayer.stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
//        cancelCallingUser();

    }


    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            SinchError a = endedCall.getDetails().getError();
//            button.setText("Call");
            callState.setText("");
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
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
            Toast.makeText(CallingActivity.this, "incoming call", Toast.LENGTH_SHORT).show();
            call.answer();
            call.addCallListener(new CallingActivity.SinchCallListener());
//            button.setText("Colgar");
        }


    }
}
