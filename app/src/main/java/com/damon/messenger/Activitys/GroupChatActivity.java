package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef,GropuNameRef,GroupMessageKeyRef;

    private String currentGroupName,currentUserID,currentUserName,currentTime,currentData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //aqui resivimos datos de la clase gropusfragmens que es el nombre del grupo resibvimos
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GropuNameRef  = FirebaseDatabase.getInstance().getReference().child("Gropus").child(currentGroupName);



        InitializeFields();

        GetUserInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveMessageInfoToDataBase();

                userMessageInput.setText("");

                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });





    }


    @Override
    protected void onStart() {
        super.onStart();

        GropuNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void GetUserInfo() {

        //aqui recuperamos los datos

        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SaveMessageInfoToDataBase() {
  //aqui es para poner la ora al moeemnto de enviar un mensaje
        String  message = userMessageInput.getText().toString();
        String messageKey = GropuNameRef.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "porfavor escribe el mensaje", Toast.LENGTH_SHORT).show();
        }else {

            //este es el calendario que utiliza firebase
            // creamos su instanci y lo transformamos
            Calendar ccalForData = Calendar.getInstance();
            SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentData  = currentDataFormat.format(ccalForData.getTime());


            Calendar ccalForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime   = currentTimeFormat.format(ccalForTime.getTime());

            //este arreglo es para crear un chat dentro del grupo utilizando sus keys
            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GropuNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GropuNameRef.child(messageKey);//aqui actualizamos lo que recuperamos arriba

            //aqui enviamos los datos al servidor
            HashMap<String , Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentData);
            messageInfoMap.put("time",currentTime);
            //aqui se envia las llaves de refrerencia de los mensajes con todos esos datos
            GroupMessageKeyRef.updateChildren(messageInfoMap);


        }

    }



    private void InitializeFields() {

        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);//aqui le pasamos el parametro

        SendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        displayTextMessages =findViewById(R.id.group_chat_text_display);
        mScrollView = findViewById(R.id.my_scroll_view);

    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {

        //AQui sera el metodo apra mostrar los mensajes
        Iterator iterator = dataSnapshot.getChildren().iterator();
        //este es un bucle que recorera linea por linea viendo si ay mensajes
        //y si los ay los dibujara utilizando el textView que llamamos displaytextmessages
        while (iterator.hasNext()){

            String chatDate =(String)  ((DataSnapshot)iterator.next()).getValue();
            String chatMessage =(String)  ((DataSnapshot)iterator.next()).getValue();
            String chatName =(String)  ((DataSnapshot)iterator.next()).getValue();
            String chatTime =(String)  ((DataSnapshot)iterator.next()).getValue();

            displayTextMessages.append(chatName+" :\n"+chatMessage+"\n"+chatTime+"     "+chatDate+"\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);



        }
    }


}
