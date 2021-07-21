package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class VideoChatActivity extends AppCompatActivity
       // implements Session.SessionListener, PublisherKit.PublisherListener
{

//    //esta api y sessiion la optuvios de toxbox la api que ase las llamdas no es gratis ay que pagar :(
//    private static String API_Key = "46511962";
//    private static String SESSION_ID = "1_MX40NjUxMTk2Mn5-MTU4MTUzMTU0NDQ4NH5ERDViUGdGOHduWnlwMXQzRi8yMEd3YjV-fg";
//    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjUxMTk2MiZzaWc9OTIwZWY5NGY1OGQxMWUyOGFkMmQ0ZjE0MDM5MGM0OWM3ZjY1YzNiOTpzZXNzaW9uX2lkPTFfTVg0ME5qVXhNVGsyTW41LU1UVTRNVFV6TVRVME5EUTROSDVFUkRWaVVHZEdPSGR1V25sd01YUXpSaTh5TUVkM1lqVi1mZyZjcmVhdGVfdGltZT0xNTgxNTMxNjYyJm5vbmNlPTAuMzc3NzcxMzU2MDYwODE3OCZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTg0MTIwMDYwJmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
//    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
//    private static final int RC_VIDEO_APP_PERM = 124;
//
//    private ImageView closeVideoChatBtn;
//    private DatabaseReference usersRef;
//    private String userID ="";
//    private Subscriber mSubscriber;
//
//    private FrameLayout mpublisherViewController;
//    private FrameLayout mSubscriverViewController;
//    private Session mSession;//esta es la session ala cual se conecta
//    private Publisher mPublisher;//igual es la pra la sesion
//    private String receiverID ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

//        receiverID = getIntent().getStringExtra("id");
//
//        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        usersRef = FirebaseDatabase.getInstance().getReference().child("Chat");
//        closeVideoChatBtn = findViewById(R.id.close_video_chat_btn);
//
//
//        //para serrar la maladama
//
//        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                usersRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.child(userID).hasChild("Ringing")){
//                            usersRef.child(userID).child("Ringing").removeValue();
//                      //      usersRef.child(receiverID).child("Ringing").removeValue();
//
//
//                            if (mPublisher !=null){
//                                mPublisher.destroy();
//                            }
//                            if (mSubscriber !=null){
//                                mSubscriber.destroy();
//                            }
//
//
//                       //  startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//                            startActivity(new Intent(VideoChatActivity.this,MainActivity.class));
//
//                            finish();
//                        }
//
//                        if (dataSnapshot.child(userID).hasChild("Calling")){
//                            usersRef.child(userID).child("Calling").removeValue();
//                         //   usersRef.child(receiverID).child("Calling").removeValue();
//
//
//                            if (mPublisher !=null){
//                                mPublisher.destroy();
//                            }
//                            if (mSubscriber !=null){
//                                mSubscriber.destroy();
//                            }
//
//                           // startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//                          startActivity(new Intent(VideoChatActivity.this,MainActivity.class));
//
//                            finish();
//
//
//
//                        }else {
//                            if (mPublisher !=null){
//                                mPublisher.destroy();
//                            }
//                            if (mSubscriber !=null){
//                                mSubscriber.destroy();
//                            }
//
//                            //startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//                           startActivity(new Intent(VideoChatActivity.this,MainActivity.class));
//
//
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
////                usersRef.addValueEventListener(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        if (dataSnapshot.child(receiverID).hasChild("Ringing")){
////                            usersRef.child(userID).child("Ringing").removeValue();
////                            usersRef.child(receiverID).child("Ringing").removeValue();
////
////
////                            if (mPublisher !=null){
////                                mPublisher.destroy();
////                            }
////                            if (mSubscriber !=null){
////                                mSubscriber.destroy();
////                            }
////
////                            //startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
////                          startActivity(new Intent(VideoChatActivity.this,MainActivity.class));
////
////                            finish();
////                        }
////
////                        if (dataSnapshot.child(receiverID).hasChild("Calling")){
////                            usersRef.child(userID).child("Calling").removeValue();
////                            usersRef.child(receiverID).child("Calling").removeValue();
////
////
////                            if (mPublisher !=null){
////                                mPublisher.destroy();
////                            }
////                            if (mSubscriber !=null){
////                                mSubscriber.destroy();
////                            }
////
////                            //startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
////                            startActivity(new Intent(VideoChatActivity.this,MainActivity.class));
////                            finish();
////
////
////
////                        }else {
////                            if (mPublisher !=null){
////                                mPublisher.destroy();
////                            }
////                            if (mSubscriber !=null){
////                                mSubscriber.destroy();
////                            }
////
////                          //  startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
////                             startActivity(new Intent(VideoChatActivity.this,MainActivity.class));
////                            finish();
////                        }
////
//////                        if (dataSnapshot.child(userID).hasChild("Ringing")){
//////                            usersRef.child(userID).child("Ringing").removeValue();
//////
//////                            if (mPublisher !=null){
//////                                mPublisher.destroy();
//////                            }
//////                            if (mSubscriber !=null){
//////                                mSubscriber.destroy();
//////                            }
//////
//////                            startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//////                            finish();
//////                        }
//////                        if (dataSnapshot.child(userID).hasChild("Calling")){
//////                            usersRef.child(userID).child("Calling").removeValue();
//////                            if (mPublisher !=null){
//////                                mPublisher.destroy();
//////                            }
//////                            if (mSubscriber !=null){
//////                                mSubscriber.destroy();
//////                            }
//////                            startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//////                            finish();
//////                        }
//////                        else {
//////                            if (mPublisher !=null){
//////                                mPublisher.destroy();
//////                            }
//////                            if (mSubscriber !=null){
//////                                mSubscriber.destroy();
//////                            }
//////                            startActivity(new Intent(VideoChatActivity.this,ChatActivity.class).putExtra("userid",receiverID).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
//////                            finish();
//////                        }
////
////
////
////                    }
////
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////
////                    }
////                });
//
//            }
//        });
//        requestPermissions();
    }

    //esto son los permisos para el video chat primero asemos la comprobvacions
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,VideoChatActivity.this);
//    }
//
//    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
//    private void requestPermissions(){
//        String [] perms = {Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
//        if (EasyPermissions.hasPermissions(this,perms)){
//            //aqui inisialimoas si son correctos los permisios
//            mpublisherViewController = findViewById(R.id.publisher_container);
//            mSubscriverViewController = findViewById(R.id.subscriber_container);
//
//            //1.inicialiamos y conectamos con la session
//            mSession = new Session.Builder(this,API_Key,SESSION_ID).build();
//
//            mSession.setSessionListener(VideoChatActivity.this);
//            mSession.connect(TOKEN);
//
//        }else {
//            EasyPermissions.requestPermissions(this,"Esta app nesesita los permisos de camara , Porfavor aceptalos.",RC_VIDEO_APP_PERM,perms);
//        }
//    }
//
//    @Override
//    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
//
//    }
//
//    @Override
//    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
//
//    }
//
//    @Override
//    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
//
//    }
//
//    //2.Publihser y stream de la sesision
//    @Override
//    public void onConnected(Session session) {
//
//        Log.i(LOG_TAG,"Session Conectada");
//
//        mPublisher = new Publisher.Builder(this).build();
//        mPublisher.setPublisherListener(VideoChatActivity.this);
//
//        mpublisherViewController.addView(mPublisher.getView());//aqui pasamos la vista
//
//        if (mPublisher.getView() instanceof GLSurfaceView){
//            ((GLSurfaceView)mPublisher.getView()).setZOrderOnTop(true);
//        }
//        mSession.publish(mPublisher);
//    }
//
//    @Override
//    public void onDisconnected(Session session) {
//        Log.i(LOG_TAG,"Stream Disconnected");
//    }
//
//    //3.Suscribing to the stremas
//    @Override
//    public void onStreamReceived(Session session, Stream stream) {
//
//        Log.i(LOG_TAG,"Stream Received");
//        if (mSubscriber ==null){
//            mSubscriber = new Subscriber.Builder(this,stream).build();
//            mSession.subscribe(mSubscriber);
//            mSubscriverViewController.addView(mSubscriber.getView());
//        }
//    }
//
//    @Override
//    public void onStreamDropped(Session session, Stream stream) {
//
//        Log.i(LOG_TAG,"Stream Dropped");
//        if (mSubscriber !=null){
//            mSubscriber = null;
//            mSubscriverViewController.removeAllViews();
//        }
//    }
//
//    @Override
//    public void onError(Session session, OpentokError opentokError) {
//        Log.i(LOG_TAG,"Stream Error");
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
//
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        if (mPublisher !=null){
////            mPublisher.destroy();
////        }
////        if (mSubscriber !=null){
////            mSubscriber.destroy();
////        }
////
////    }

}



