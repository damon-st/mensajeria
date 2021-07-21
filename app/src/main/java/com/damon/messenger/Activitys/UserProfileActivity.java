package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.damon.messenger.Adapters.ArchivesUserAdapter;
import com.damon.messenger.Model.Messages;
import com.damon.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {


    private String  urlImageProfile,nombreUser,statusUser,tiempoUser;
    private TextView status,tiempo_conectado;
    private ImageView profile;
    Toolbar toolbar;
    private RecyclerView recyclerArchivos;
    List<Messages> listademsg = new ArrayList<>();
    ArchivesUserAdapter archivesUserAdapter;
    ArrayList<String> pocos_archivos = new ArrayList<>();

    DatabaseReference messageRef;
    Query messageQuery;
    DatabaseReference RootRef;
    String messageReciverID,messagemSenderID;

    GetMessages getMessages = new GetMessages();
    private TextView countTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        initToolbar();

        RootRef = FirebaseDatabase.getInstance().getReference();
        urlImageProfile = getIntent().getStringExtra("url");
        nombreUser = getIntent().getStringExtra("name");
        statusUser = getIntent().getStringExtra("status");
        tiempoUser = getIntent().getStringExtra("tiempo");
        messagemSenderID = getIntent().getStringExtra("messagemSenderID");
        messageReciverID = getIntent().getStringExtra("messageReciverID");
        countTV = findViewById(R.id.tv_count);

        getMessages.execute();
//        cargar();

        tiempo_conectado = findViewById(R.id.fecha_conecatdo);
        profile = findViewById(R.id.image_profile);
        status = findViewById(R.id.tv_desc);
        recyclerArchivos = findViewById(R.id.recycler_medios_user);
        recyclerArchivos.setHasFixedSize(true);
        recyclerArchivos.setLayoutManager(new LinearLayoutManager(UserProfileActivity.this,RecyclerView.HORIZONTAL,false));

        archivesUserAdapter = new ArchivesUserAdapter(listademsg,UserProfileActivity.this);
        recyclerArchivos.setAdapter(archivesUserAdapter);
        System.out.println("msg"+ listademsg);


        if (nombreUser !=null){
            Picasso.get().load(urlImageProfile).into(profile);
            toolbar.setTitle(nombreUser);
            status.setText(statusUser);
            tiempo_conectado.setText(tiempoUser);
            profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfileActivity.this, ImageViewerActivity.class);
                    intent.putExtra("url",urlImageProfile);
                    startActivity(intent);
                }
            });
        }

    }

    private void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private class GetMessages extends AsyncTask<Void,Integer,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            messageRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);

                    listademsg.add(messages);
                    archivesUserAdapter.notifyDataSetChanged();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            messageRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID);

            messageQuery = messageRef;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}