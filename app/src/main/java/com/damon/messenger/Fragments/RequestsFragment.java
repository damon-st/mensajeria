package com.damon.messenger.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */

//esta clase sera la encargada del chat
public class RequestsFragment extends Fragment {


    private View RequestsFragmentView;
    private RecyclerView myRecyclerList;

    private DatabaseReference ChatRequestsRef,usersRef,contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RequestsFragmentView =  inflater.inflate(R.layout.fragment_requests, container, false);

        myRecyclerList = RequestsFragmentView.findViewById(R.id.chat_request_list);
        myRecyclerList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();



        ChatRequestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");



        return RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options  =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestsRef.child(currentUserID),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestViewHolder>  adapter =
                new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contacts model) {
                        holder.itemView.findViewById(R.id.request_accept_button).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.VISIBLE);

                        final String list_user_id = getRef(position).getKey();


                        DatabaseReference getTypeRef = getRef(position).child("request_type");

                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override

                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String type = dataSnapshot.getValue().toString();
                                    if (type.equals("received")){

                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final  String requestProlifeImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProlifeImage)
                                                            .resize(90,90)
                                                            .placeholder(R.drawable.profile_image)
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

                                                final  String requestUsername = dataSnapshot.child("name").getValue().toString();
                                                final  String requestUserstatus = dataSnapshot.child("status").getValue().toString();
                                                holder.userName.setText(requestUsername);
                                                holder.userStatus.setText("Quiere ser tu amigo");

                                                holder.AccepButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        contactsRef.child(currentUserID).child(list_user_id).child("Contact").setValue("Saved")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            contactsRef.child(list_user_id).child(currentUserID).child("Contact").setValue("Saved")
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()){

                                                                                                ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                                                        .removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()){

                                                                                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                                            .removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                                                                    Toast.makeText(getContext(), "Nuevo Contacto Guardado", Toast.LENGTH_SHORT).show();


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
                                                });

                                                holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                                            Toast.makeText(getContext(), "Contacto Eliminado", Toast.LENGTH_SHORT).show();


                                                                                        }
                                                                                    });
                                                                        }


                                                                    }
                                                                });
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }else if (type.equals("sent")){

                                        Button request_sent_button = holder.itemView.findViewById(R.id.request_accept_button);
                                        request_sent_button.setText("Solicitud Enviada");
                                        holder.itemView.findViewById(R.id.request_cancel_button).setVisibility(View.INVISIBLE);
                                        usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.hasChild("image")){

                                                    final  String requestProlifeImage = dataSnapshot.child("image").getValue().toString();
                                                    Picasso.get().load(requestProlifeImage)
                                                            .resize(90,90)
                                                            .placeholder(R.drawable.profile_image)
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

                                                final  String requestUsername = dataSnapshot.child("name").getValue().toString();
                                                final  String requestUserstatus = dataSnapshot.child("status").getValue().toString();

                                                holder.userName.setText(requestUsername);
                                                holder.userStatus.setText("Tu has enviado una solicitud a "+requestUsername);

                                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CharSequence options[] = new CharSequence[]{
                                                                "Cancelar Solicitud"
                                                        };
                                                        AlertDialog.Builder builder =new  AlertDialog.Builder(getContext());
                                                        builder.setTitle("Seguro quieres cancelar?");

                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int i) {

                                                                if (i ==0){
                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()){

                                                                                        ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                                                        Toast.makeText(getContext(), "Tu as cancelado la solicitud", Toast.LENGTH_SHORT).show();


                                                                                                    }
                                                                                                });
                                                                                    }


                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });

                                                        builder.show();



                                                    }
                                                });

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {

                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        RequestViewHolder holder = new RequestViewHolder(view);
                        return holder;
                    }
                };

        myRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView userName , userStatus;
        CircleImageView prolifeImage;
        Button AccepButton , CancelButton;
        ProgressBar progressBar ;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            prolifeImage = itemView.findViewById(R.id.users_profile_image);
            AccepButton = itemView.findViewById(R.id.request_accept_button);
            CancelButton = itemView.findViewById(R.id.request_cancel_button);

            progressBar = itemView.findViewById(R.id.proges_dialog_chat);

        }
    }
}
