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
import com.damon.messenger.R;
import com.damon.messenger.Model.UserObject;
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

public class CrearGrupoChatActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_crear_grupo_chat);


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

    private void createChat(){
        String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

        DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap newChatMap = new HashMap();
        newChatMap.put("id", key);
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
            userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
        }

    }

    private void getContactList(){

        FirebaseRecyclerOptions options = new
                FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)//currentuserid.child(currentUserID)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder>adapter
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
                                        Toast.makeText(CrearGrupoChatActivity.this, "Hola recuerda son 50 integrantes nomas", Toast.LENGTH_SHORT).show();
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

                                    final DatabaseReference chatInfoDb = FirebaseDatabase.getInstance().getReference().child("chat").child(key).child("info");
                                    final DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");



                                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(CrearGrupoChatActivity.this,R.style.AlertDialog);
                                    builder.setTitle("Ingresa El nombre del Grupo:");//AQUI SERA EL DIALOGO QUE MOSTRARA
                                    final EditText groupNameField=new EditText(CrearGrupoChatActivity.this);//DONDE LO MOSTRARA
                                    groupNameField.setHint("Hola ingresa aqui el nombre del grupo");// LO QUE IRA DENTRO DONDE SE ESCRIBIRA
                                    builder.setView(groupNameField);//AQUI ES PARA PODER VER EN LA PANTALLA LO QUE CREAMOS

                                    //ESTE POSITIVEBUTTON ES PARA CONFIRMAR LA CREACION
                                    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            String grooupName = groupNameField.getText().toString();//asenmos un casting
                                            if (TextUtils.isEmpty(grooupName)){//este condicion es para ver si escribio o no algo
                                                Toast.makeText(CrearGrupoChatActivity.this, "Porfavor escribe el nombre del grupo", Toast.LENGTH_SHORT).show();
                                            }else if (contador <=50 && contador >=1){
                                                HashMap newChatMap = new HashMap();
                                                newChatMap.put("id", key);
                                                newChatMap.put("nombre",grooupName);
                                                newChatMap.put("creador",currentUserID);
                                                newChatMap.put("image","https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/grupo.jpg?alt=media&token=b349eb42-c14d-4df5-9d35-a58821be11d4");
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
                                                    userDb.child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(true);
                                                }
                                                Toast.makeText(CrearGrupoChatActivity.this, "Grupo Creado Correctamente", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                finish();
                                            }else {
                                                Toast.makeText(CrearGrupoChatActivity.this, "Maximo 50 integrantes porvfavor", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    //ESTE NEGATIVEBUTTON ES PARA CANCELAR LA CREACION
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {

                                            dialogInterface.cancel();// ESTE ES DIALOGO DE CANCELAR LA OPERACION PARA NO CRAR EL GRUPO

                                        }
                                    });

                                    builder.show();//esto es para mostrar

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

    public static class  ContactsViewHolder extends RecyclerView.ViewHolder{
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



//    private String getCountryISO(){
//        String iso = null;
//
//        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
//        if(telephonyManager.getNetworkCountryIso()!=null)
//            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
//                iso = telephonyManager.getNetworkCountryIso().toString();
//
//        return CountryToPhonePrefix.getPhone(iso);
//    }

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
