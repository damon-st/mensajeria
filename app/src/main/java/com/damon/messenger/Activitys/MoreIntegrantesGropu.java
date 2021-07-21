package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Adapters.UserListAdapter;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Model.UserObject;
import com.damon.messenger.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreIntegrantesGropu extends AppCompatActivity {

    String idChat;

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    private DatabaseReference Contactsref,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    Button mCreate;
    private TextView maximo_grupo_contador;
    private int contador = 0;
    ArrayList<UserObject> userList, contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_integrantes_gropu);
        idChat = getIntent().getStringExtra("id");


        contactList= new ArrayList<>();
        userList= new ArrayList<>();

        maximo_grupo_contador= findViewById(R.id.maximo_grupo_contador);


        mAuth = FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();

        Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mCreate = findViewById(R.id.creargrupo);


        initializeRecyclerView();
        getContactList();

    }

    private void getContactList(){

        FirebaseRecyclerOptions options = new
                FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)//currentuserid.child(currentUserID)
                .build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull final Contacts contacts) {

                final String usersId = getRef(position).getKey();
                final String id = getRef(position).getKey();
                final String[] email = new String[1];

                UsersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){



                            String nombre = dataSnapshot.child("name").getValue().toString();

                            UserObject mContacts = new UserObject(usersId, nombre, email[0]);
                            userList.add(mContacts);


                            holder.mName.setText(nombre);
//                            if (dataSnapshot.child("email").exists()) {
//                               email[0] = dataSnapshot.child("email").getValue().toString();
//                                holder.mPhone.setText(email[0]);
//                            }

                            if (dataSnapshot.child("id").exists()){
                                String  phone  = dataSnapshot.child("id").getValue().toString();
                                holder.mPhone.setText(phone);
                                holder.mPhone.setVisibility(View.GONE);
                            }
                            if (dataSnapshot.child("image").exists()){
                                String image = dataSnapshot.child("image").getValue().toString();
                                try {
                                    Picasso.get().load(image)
                                            .resize(50,50)
                                            .placeholder(R.drawable.profile_image)
                                            .into(holder.imagenPeril, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.imagenPeril);
                                                }
                                            });
                                }catch (Exception e){
                                    System.out.println("Errror----"+e);
                                }

                            }
                            holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        userList.get(holder.getAdapterPosition()).setSelected(isChecked);
                                        contador += 1;
                                        // Toast.makeText(CrearGrupoChatActivity.this, ""+ userList.get(holder.getAdapterPosition()).getUid(), Toast.LENGTH_SHORT).show();
                                        maximo_grupo_contador.setText("Integrantes: " + contador + " de 50");
                                    }else {
                                        userList.get(holder.getAdapterPosition()).setSelected(false);
                                        contador -= 1;
                                        maximo_grupo_contador.setText("Integrantes: " + contador + " de 50");
                                    }
                                    if (contador>=50){
                                        Toast.makeText(MoreIntegrantesGropu.this, "Hola recuerda son 50 integrantes nomas", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });


                            UserObject mContact = new UserObject(usersId, nombre, email[0]);
                            contactList.add(mContact);
                            getUserDetails(mContact);

                            mCreate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // createChat();

                                    final String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                                    final DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(idChat).child("info");
                                    final DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");

                                  if (contador <=50 && contador >=1){
                                        HashMap newChatMap = new HashMap();
                                        newChatMap.put("users/" + FirebaseAuth.getInstance().getUid(), true);

                                        Boolean validChat = false;
                                        for(UserObject mUser : userList){
                                            if(mUser.getSelected()){
                                                validChat = true;
                                                newChatMap.put("users/" + mUser.getUid(), true);
                                                userDb.child(mUser.getUid()).child("chat").child(key).setValue(true);
                                            }
                                        }

                                        if(validChat){

                                            chatInfoDb.updateChildren(newChatMap);
                                            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(idChat).setValue(true);
                                        }
                                        Toast.makeText(MoreIntegrantesGropu.this, "Añadido Integrantes  Correctamente", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        finish();
                                    }else {
                                        Toast.makeText(MoreIntegrantesGropu.this, "Maximo 50 integrantes porvfavor", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutView.setLayoutParams(lp);

                ContactsViewHolder rcv = new ContactsViewHolder(layoutView);
                return rcv;
            }
        };
        mUserList.setAdapter(adapter);
        adapter.startListening();



    }

      class  ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView mName, mPhone;
        LinearLayout mLayout;
        CheckBox mAdd;
        CircleImageView imagenPeril;
        ProgressBar progressBar;

        public ContactsViewHolder(@NonNull final View view) {
            super(view);

            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mAdd = view.findViewById(R.id.add);
            mLayout = view.findViewById(R.id.layout);
            imagenPeril = view.findViewById(R.id.imagen_para_creargrupo);
            progressBar = view.findViewById(R.id.proges_dialog);
        }
    }


    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mUserDB.orderByChild("email").equalTo(mContact.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("email").getValue()!=null)
                            phone = childSnapshot.child("email").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phone);
                        if (name.equals(phone))
                            for(UserObject mContactIterator : contactList){
                                if(mContactIterator.getPhone().equals(mUser.getPhone())){
                                    mUser.setName(mContactIterator.getName());
                                }
                            }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("WrongConstant")
    private void initializeRecyclerView() {
        mUserList= findViewById(R.id.recycler_creargrupos);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
}