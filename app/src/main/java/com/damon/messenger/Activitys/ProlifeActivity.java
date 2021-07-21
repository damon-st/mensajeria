package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Dialog.DialogFragment;
import com.damon.messenger.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProlifeActivity extends AppCompatActivity {

    private String reciverUsersID,current_state;
    private CircleImageView userProlifeImage;
    private TextView usersProlifeName,userprolifeStatus;
    private Button SendMessageRequestButton, declineRequestButton;

    FirebaseUser sendUsersID ;

    private Dialog dialog;
    private Button si,no;

    private ProgressBar progressBar;

    private DatabaseReference usersRef,chatRequestRef,contactsRef, NotificationRef;
    private FirebaseAuth mAuth;

    private static final String APP_ID ="ca-app-pub-1691614301371531~7301440527";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prolife);


        dialog = new Dialog(this);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");//qui buscamos por la key "Users"
        mAuth =FirebaseAuth.getInstance();//creamos la instancia con la base de datos que tiene nuestras keys
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");//qui buscamos por la key Chat Requests
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");//qui creamos una nuea key para contactos
        NotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");



        reciverUsersID = getIntent().getExtras().get("visit_user_id").toString();//aqui estamos resiviendo la llave
       // Toast.makeText(this, "Users ID: "+reciverUsersID, Toast.LENGTH_SHORT).show();
        sendUsersID = mAuth.getCurrentUser();//recuperamso nuestra key y la guardamos


        userProlifeImage = findViewById(R.id.visit_prolife_image);
        usersProlifeName = findViewById(R.id.visit_user_name);
        userprolifeStatus = findViewById(R.id.visit_prolife_status);
        SendMessageRequestButton = findViewById(R.id.send_message_request_button);
        declineRequestButton = findViewById(R.id.decline_message_request_button) ;
        progressBar = findViewById(R.id.proges_dialog_profile);


        current_state = "new";

        MobileAds.initialize(this,APP_ID);
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        RetriveUserInfo();





    }

    private void RetriveUserInfo() {



        usersRef.child(reciverUsersID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //la primera condision dice que si existen los datos
                //i la segunda condicion dise qeu si esxiste en la base de datos la key "image"
                if (dataSnapshot.exists()&&(dataSnapshot.hasChild("image"))){

                    //recueramos los datos dentro de la base de datos
                    final String userImage= dataSnapshot.child("image").getValue().toString();
                    String userName= dataSnapshot.child("name").getValue().toString();
                    String userStatus= dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage)
                            .placeholder(R.drawable.profile_image)
                            .into(userProlifeImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(R.mipmap.ic_launcher).into(userProlifeImage);
                                }
                            });

                    //aqui lo asignamos alas variables de vista que tenemos
                    usersProlifeName.setText("Nombre de Usuario \n \n"+userName);
                    userprolifeStatus.setText("Estado de Usuario \n \n"+userStatus);

                    ManageChatRequest();

                    userProlifeImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(),ImageViewerActivity.class);
                            intent.putExtra("url",userImage);
                            startActivity(intent);
                        }
                    });


                }else {
                    String userName= dataSnapshot.child("name").getValue().toString();
                    String userStatus= dataSnapshot.child("status").getValue().toString();

                    usersProlifeName.setText(userName);
                    userprolifeStatus.setText(userStatus);

                    ManageChatRequest();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ManageChatRequest() {
       //para el boton cancelar se mantega
        //estas lineas son para que sevea que se puede cancelar
        chatRequestRef.child(sendUsersID.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(reciverUsersID)){
                           String requestType = dataSnapshot.child(reciverUsersID).child("request_type").getValue().toString();
                           if (requestType.equals("sent")){
                               current_state ="request_sent";
                               SendMessageRequestButton.setText("Cancelar el mensaje");
                           }else if (requestType.equals("received")){
                               current_state = "request_received";
                               SendMessageRequestButton.setText("Acceptar nuevo chat ");

                               declineRequestButton.setVisibility(View.VISIBLE);
                               declineRequestButton.setEnabled(true);
                               declineRequestButton.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       CancelChatRequest();
                                   }
                               });
                           }
                        }
                        else {
                            contactsRef.child(sendUsersID.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(reciverUsersID)){
                                                current_state = "friends";
                                                SendMessageRequestButton.setText("Eliminar Contacto");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        if (!sendUsersID.getUid().equals(reciverUsersID)){
         SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SendMessageRequestButton.setEnabled(false);
                 if (current_state.equals("new")){
                      SendChatRequest();
                 }
                 if (current_state.equals("request_sent")){
                     CancelChatRequest();
                 }
                 if (current_state.equals("request_received")){
                     AcceptChatRequest();
                 }
                 if (current_state.equals("friends")){
                     RemoveEspecifiqueContacts();
                 }
             }
         });
        }else {
            //aqui estamos asiendo una condicional que si quiere enviarese asi mismo un mensaje
            //nosera visible el boton de enviar
            SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    private void RemoveEspecifiqueContacts() {
 //metodo para eliminar los contactos que ayamos aceptados
        //fijaete utiliza el contatref que creamos con la base de datos

         contactsRef.child(sendUsersID.getUid()).child(reciverUsersID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                   contactsRef.child(sendUsersID.getUid()).child(sendUsersID.getUid())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //esto es echo aorita
                                contactsRef.child(reciverUsersID).child(sendUsersID.getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            DialogFragment dialogFragment = new DialogFragment();
                                            dialogFragment.show(getSupportFragmentManager(),"DialogoEliminar");
                                            SendMessageRequestButton.setEnabled(true);
                                            current_state= "new";
                                            SendMessageRequestButton.setText("Enviar mensaje");

                                            //aqui se desabvilitara el boton cancelar de la vista de ala app
                                            declineRequestButton.setVisibility(View.INVISIBLE);
                                            declineRequestButton.setEnabled(false);



                                        }
                                    }
                                });

                            }

                        }
                    });
                }

            }
        });
    }

    private void AcceptChatRequest() {

        contactsRef.child(sendUsersID.getUid()).child(reciverUsersID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            contactsRef.child(reciverUsersID).child(sendUsersID.getUid())
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                chatRequestRef.child(sendUsersID.getUid()).child(reciverUsersID)
                                                        .removeValue()
                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                         if (task.isSuccessful()){
                                                             chatRequestRef.child(reciverUsersID).child(sendUsersID.getUid())
                                                                     .removeValue()
                                                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                         @Override
                                                                         public void onComplete(@NonNull Task<Void> task) {

                                                                             SendMessageRequestButton.setEnabled(true);
                                                                             current_state = "friends";
                                                                             SendMessageRequestButton.setText("Eliminar Contacto");

                                                                             declineRequestButton.setVisibility(View.INVISIBLE);
                                                                             declineRequestButton.setEnabled(false);


                                                                         }
                                                                     });
                                                         }
                                                    }
                                                });

                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void CancelChatRequest() {
        //ojo que se esta comaparando la llaves creadas de tipo string para asi poder cancelar
        chatRequestRef.child(sendUsersID.getUid()).child(reciverUsersID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    chatRequestRef.child(sendUsersID.getUid()).child(sendUsersID.getUid())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()){
                                   SendMessageRequestButton.setEnabled(true);
                                   current_state= "new";
                                   SendMessageRequestButton.setText("Enviar mensaje");

                                   //aqui se desabvilitara el boton cancelar de la vista de ala app
                                   declineRequestButton.setVisibility(View.INVISIBLE);
                                   declineRequestButton.setEnabled(false);
                               }

                        }
                    });
                }

            }
        });
    }


    private void SendChatRequest() {
        //metodo para enviar el mensaje del boton que dice enviar
      chatRequestRef.child(sendUsersID.getUid()).child(reciverUsersID)
              .child("request_type").setValue("sent")
              .addOnCompleteListener(new OnCompleteListener<Void>() {
                  @Override
                  public void onComplete(@NonNull Task<Void> task) {
                      if (task.isSuccessful()){
                          chatRequestRef.child(reciverUsersID).child(sendUsersID.getUid())
                                  .child("request_type").setValue("received")
                                  .addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {

                                          if (task.isSuccessful()){


                                              HashMap<String,String> chatnotificacionMap = new HashMap<>();
                                              chatnotificacionMap.put("from",sendUsersID.getUid());
                                              chatnotificacionMap.put("type","request");

                                              NotificationRef.child(reciverUsersID).push()
                                                      .setValue(chatnotificacionMap)
                                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {

                                                              if (task.isSuccessful()){

                                                                  SendMessageRequestButton.setEnabled(true);
                                                                  current_state = "request_sent";
                                                                  SendMessageRequestButton.setText("Cancelar Mensaje");
                                                              }

                                                          }
                                                      });



                                          }
                                      }
                                  });
                      }
                  }
              });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProlifeActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
