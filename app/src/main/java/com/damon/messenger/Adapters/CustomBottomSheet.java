package com.damon.messenger.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Notifications.APIService;
import com.damon.messenger.Notifications.Client;
import com.damon.messenger.Notifications.Data;
import com.damon.messenger.Notifications.MyResponse;
import com.damon.messenger.Notifications.Sender;
import com.damon.messenger.Notifications.Token;
import com.damon.messenger.R;
import com.damon.messenger.util.AES;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    private TextView txt_name_user, txtAndroid, txtWorld;
    private FloatingActionButton btnSubmit;
    private EditText mensaje_enviar;

    private Context mContext;
    private String imgUrl,nameUser,idUserPublisher;
    private ImageView imgStory;
    private boolean notify;
    private String messagemSenderID,nombre_responder;
    private DatabaseReference RootRef;
    private FirebaseUser fuser;
    private String saveCutrrentTime, saveCurrentData;
    private APIService apiService;


    public CustomBottomSheet(String urlImage,String nameUser,String idUserPublisher) {
        this.imgUrl = urlImage;
        this.nameUser = nameUser;
        this.idUserPublisher = idUserPublisher;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate your custom layout in place of bottom_dialog
        View view=inflater.inflate(R.layout.bottom_sheet_story,container,false);

        CalcularFecha();

        imgStory = view.findViewById(R.id.imagen_responder);
        txt_name_user = view.findViewById(R.id.nombreUsuario);
        mensaje_enviar = view.findViewById(R.id.input_message);
        btnSubmit = view.findViewById(R.id.enviar_message_button);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        RootRef = FirebaseDatabase.getInstance().getReference();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        messagemSenderID = fuser.getUid();

        Picasso.get().load(imgUrl).into(imgStory);
        txt_name_user.setText(nameUser);


        btnSubmit.setOnClickListener(this);

        return view;
    }

    private void CalcularFecha(){
        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        saveCurrentData = dateFormat.format(date.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }


    @Override
    public void onAttach(Context context) {
        mContext = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.enviar_message_button:
                System.out.println("mensaje"+ mensaje_enviar.getText().toString() + "\n" + "img" + imgUrl + "\n" + "id" + idUserPublisher);
                SendMessageResputa();
                break;
        }
    }


    private void SendMessageResputa() {
        notify = true;
        String messageText = mensaje_enviar.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(getContext(), "Debes escribir el mesnaje", Toast.LENGTH_SHORT).show();
        } else {

            String messageSenderRef = "Messages/" + messagemSenderID + "/" + idUserPublisher;
            String messageReciverRef = "Messages/" + idUserPublisher + "/" + messagemSenderID;

            DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                    .child(messagemSenderID).child(idUserPublisher).push();

            String messagePushID = userMessagerKeyRef.getKey();

            String msgEncryp = EncrypMessage(messageText);
//            String encypMsg = EncrypMessage(msgResponder);

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", msgEncryp);
            messageTextBody.put("type", "respuesta");
            messageTextBody.put("from", messagemSenderID);
            messageTextBody.put("to", idUserPublisher);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCutrrentTime);
            messageTextBody.put("date", saveCurrentData);
            messageTextBody.put("sender", messagemSenderID);
            messageTextBody.put("receiver", idUserPublisher);
            messageTextBody.put("isseen",false);
            messageTextBody.put("type_responder","image");
            messageTextBody.put("msg_responder_nombre_responder",nameUser);
            messageTextBody.put("msg_sender_responder",imgUrl);
            messageTextBody.put("position",0);
            messageTextBody.put("msgImage","");



            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Respuesta Enviada", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final String msg = "A contestado tu estado";

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contacts contacts = dataSnapshot.getValue(Contacts.class);
                    if (notify) {
                        sendNotifiaction(idUserPublisher, contacts.getName(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }



    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "Nuevo Mensaje",
                                    idUserPublisher);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(getContext(), "Fallo al enviar notificaion!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                        }
                    } catch (Exception e) {
                        System.out.println("Error" + e);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    AES aes = new AES("lv39eptlvuhaqqsr");
    private String  EncrypMessage(String msg){

        try {
            String cadenaEncryp = aes.encrypt(msg);
            return cadenaEncryp;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}