package com.damon.messenger.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Activitys.CallingActivity;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Activitys.CrearGrupoChatActivity;
import com.damon.messenger.Activitys.ProlifeActivity;
import com.damon.messenger.R;
import com.damon.messenger.Model.Story;
import com.damon.messenger.Adapters.StoryAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */

public class ContactsFragment extends Fragment {


    private View contactsView;
    private RecyclerView myContactsList;
    private DatabaseReference Contactsref,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    private StoryAdapter storyAdapter;

    private  String usersId;

    private List<String> followingList;

    private RecyclerView recyclerView_story;
    private List<Story> storyList;

    private String  calledBy="";

    public ContactsFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView =  inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactsList = contactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));


        recyclerView_story = contactsView.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);


        mAuth = FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();

        Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        Button button =  contactsView.findViewById(R.id.add);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CrearGrupoChatActivity.class));
            }
        });



        try {
            checkFollowing();
        }catch (Exception e){
            e.printStackTrace();
        }


        return contactsView;
    }

    @Override
    public void onStart() {
        super.onStart();

      //  checkForRecevingCall();//para recicivr las llamadas

        FirebaseRecyclerOptions options = new
                FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)//currentuserid.child(currentUserID)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, final int position, @NonNull Contacts model) {

                final String usersId = getRef(position).getKey();
                final String id = getRef(position).getKey();



//                DatabaseReference getTypeRef = getRef(position).child("request_type");//

                     UsersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                         @Override
                         public void onDataChange(DataSnapshot dataSnapshot) {
                         if (dataSnapshot.exists()){
                             //condicion para crear la notificacionj
                             if(dataSnapshot.child("userSate").hasChild("state")){

                                 String state = dataSnapshot.child("userSate").child("state").getValue().toString();
                                 String date = dataSnapshot.child("userSate").child("date").getValue().toString();
                                 String time = dataSnapshot.child("userSate").child("time").getValue().toString();
                                 if (state.equals("online")){

                                     holder.onlineIcon.setVisibility(View.VISIBLE);

                                 }
                                 else if (state.equals("offline")){

                                     holder.onlineIcon.setVisibility(View.INVISIBLE);
                                 }
                             }else {
                                 holder.onlineIcon.setVisibility(View.INVISIBLE);
                             }

                             if (dataSnapshot.hasChild("image")){
                                 String usersImage =dataSnapshot.child("image").getValue().toString();
                                 String profilename = dataSnapshot.child("name").getValue().toString();
                                 String profilestatus = dataSnapshot.child("status").getValue().toString();

                                 holder.userName.setText(profilename);
                                 holder.userStatus.setText(profilestatus);

                                 //primero se manda lo que recuperamos con el datasnapshot
                                 //segundo es el nombre de al variable que esta en el circlview
                                 //el placeholder es obcional para poner una foto por defecto encaso que no aya tenido foto el usuario
                                 Picasso.get()
                                         .load(usersImage)
                                         .resize(90,90)
                                         .into(holder.prolifeImage, new Callback() {
                                             @Override
                                             public void onSuccess() {
                                                 holder.progressBar.setVisibility(View.GONE);
                                             }

                                             @Override
                                             public void onError(Exception e) {
                                                 Picasso.get().load(R.mipmap.ic_launcher).into(holder.prolifeImage);
                                             }
                                         });

                             }
//                             else {
//                                 String profilename = dataSnapshot.child("name").getValue().toString();
//                                 String profilestatus = dataSnapshot.child("status").getValue().toString();
//
//                                 holder.userName.setText(profilename);
//                                 holder.userStatus.setText(profilestatus);
//                             }
                         }

                         }

                         @Override
                         public void onCancelled(DatabaseError databaseError) {

                         }
                     });

                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        CharSequence options[] = new CharSequence[]{
                                "Ver Perfil"
                        };
                        AlertDialog.Builder builder =new  AlertDialog.Builder(getContext());
                        builder.setTitle("Ver el Perfil de Contacto?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {

                                if (which ==0){

                                    Intent intent = new Intent(getContext(), ProlifeActivity.class);
                                    intent.putExtra("visit_user_id",usersId);
                                    startActivity(intent);


//                                    Contactsref.child(currentUserID).child(usersId)
//                                            .removeValue()
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()){
//                                                        Contactsref.child(usersId).child(currentUserID)
//                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<Void> task) {
//
//                                                                Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
//                                                                DeleteSentMessages(position,holder);
//                                                            }
//                                                        });
//                                                    }
//
//                                                }
//                                            });

                                }
                            }
                        });
                        builder.show();


                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;

            }
        };
        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }

    private void checkForRecevingCall() {
//cdodigo para las llamdas

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat");
        reference.child(currentUserID)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ringing")) {

                            calledBy = dataSnapshot.child("ringing").getValue().toString();

                            Intent intent = new Intent(Objects.requireNonNull(getContext()).getApplicationContext(), CallingActivity.class);
                            intent.putExtra("id",calledBy);
                            intent.putExtra("senderID",currentUserID);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    //esta calse es para poder utilizar el recyvler view para poder ver contactos es necesarias 
    public static class  ContactsViewHolder extends RecyclerView.ViewHolder{

        TextView  userName , userStatus;
        CircleImageView prolifeImage;
        ImageView onlineIcon ;
        ProgressBar progressBar;
        public ContactsViewHolder(@NonNull final View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            prolifeImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status);
            progressBar = itemView.findViewById(R.id.proges_dialog_chat);



        }
    }

    public int getItemCount() {
        return myContactsList.getItemDecorationCount();
    }

    private void DeleteSentMessages(final int position ,final ContactsViewHolder holder){
        //metodo para eliminar los mensajes
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Contacts").child(currentUserID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contacts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
               ;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
               // readPosts();
                readStory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,
                        0,"",FirebaseAuth.getInstance().getCurrentUser().getUid(),""));
                for (String  id:followingList){
                    int countStory =0;
                    Story story = null;
                    for (DataSnapshot snapshot: dataSnapshot.child(id).getChildren()){
                        story = snapshot.getValue(Story.class);
                        if (timecurrent>story.getTimestart() && timecurrent<story.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory>0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
