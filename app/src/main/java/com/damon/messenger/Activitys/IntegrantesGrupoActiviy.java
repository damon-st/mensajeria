package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.damon.messenger.Adapters.VerIntegrandesGruopAdapter;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class IntegrantesGrupoActiviy extends AppCompatActivity {

    private RecyclerView recyclerView;
//    private UserAdapter userAdapter;
    private List<Contacts> userList;

    private VerIntegrandesGruopAdapter verIntegrandesGruopAdapter;
    private String id,title;
    private List<String> idList;
    private FloatingActionButton btn_more;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrantes_grupo_activiy);

        btn_more = findViewById(R.id.boton_anadir);
        title = "Integrantes del Grupo";
        id = getIntent().getStringExtra("id");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
       // userAdapter = new UserAdapter(this,userList,false);
        verIntegrandesGruopAdapter = new VerIntegrandesGruopAdapter(userList,IntegrantesGrupoActiviy.this,id,false, FirebaseAuth.getInstance().getUid());
        recyclerView.setAdapter(verIntegrandesGruopAdapter);

        idList = new ArrayList<>();

        getViews();

        btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntegrantesGrupoActiviy.this,MoreIntegrantesGropu.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }


    // @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions options = new
//                FirebaseRecyclerOptions.Builder<Contacts>()
//                .setQuery(UsersRef,Contacts.class)//currentuserid.child(currentUserID)
//                .build();
//
//        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
//                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts contacts) {
//
//                final String usersId = getRef(position).getKey();
//
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat")
//                        .child(id).child("info").child("users");
//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            if (dataSnapshot.hasChild(FirebaseAuth.getInstance().getUid())) {
//                                String nombre = dataSnapshot.child("name").getValue().toString();
//                                String usersImage =dataSnapshot.child("image").getValue().toString();
//                                String profilestatus = dataSnapshot.child("status").getValue().toString();
//
//                                holder.userName.setText(nombre);
//                                holder.userStatus.setText(profilestatus);
//                                Picasso.get().load(usersImage).placeholder(R.drawable.profile_image).into(holder.prolifeImage);
//                            } else {
//                            Toast.makeText(IntegrantesGrupoActiviy.this, "no existe", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//
//            }
//
//            @NonNull
//            @Override
//            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
//                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
//                return viewHolder;
//            }
//        };
//        myContactsList.setAdapter(adapter);
//        adapter.startListening();
//
//    }
//
//    //esta calse es para poder utilizar el recyvler view para poder ver contactos es necesarias
//    public static class  ContactsViewHolder extends RecyclerView.ViewHolder{
//
//        TextView userName , userStatus;
//        CircleImageView prolifeImage;
//        ImageView onlineIcon ;
//        public ContactsViewHolder(@NonNull final View itemView) {
//            super(itemView);
//
//            userName = itemView.findViewById(R.id.user_profile_name);
//            userStatus = itemView.findViewById(R.id.user_status);
//            prolifeImage = itemView.findViewById(R.id.users_profile_image);
//            onlineIcon = itemView.findViewById(R.id.user_online_status);
//        }
//    }

    private void getViews(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat")
                .child(id).child("info").child("users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void showUsers(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Contacts user = snapshot.getValue(Contacts.class);
                    for (String id : idList){
                        if (user.getId().equals(id)) {
                            userList.add(user);
                        }
                    }
                }
                verIntegrandesGruopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
