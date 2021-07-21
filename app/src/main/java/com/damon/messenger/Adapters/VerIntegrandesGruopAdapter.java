package com.damon.messenger.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.messenger.Activitys.ChatGrupoActiviy;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
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

import java.util.List;

public class VerIntegrandesGruopAdapter extends RecyclerView.Adapter<VerIntegrandesGruopAdapter.IntegrantesHolder> {

    List<Contacts> contacts;
    Activity activity;
    String chatId;
    boolean isChat;
    DatabaseReference reference,refUser;
    String currentUser;

    public VerIntegrandesGruopAdapter(List<Contacts> contacts, Activity activity, String chatId, boolean isChat,String  currentUser) {
        this.contacts = contacts;
        this.activity = activity;
        this.chatId = chatId;
        this.isChat = isChat;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public IntegrantesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.users_integrate_group,parent,false);
        return new IntegrantesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final IntegrantesHolder holder, int position) {
        final Contacts contact = contacts.get(position);
        Picasso.get().load(contact.getImage()).into(holder.profile_image, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressGroup.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                holder.progressGroup.setVisibility(View.GONE);
                Picasso.get().load(R.mipmap.ic_launcher).into(holder.profile_image);
            }
        });
        holder.username.setText(contact.getName());
        reference = FirebaseDatabase.getInstance().getReference("chat")
                .child(chatId).child("info");
        refUser = FirebaseDatabase.getInstance().getReference("Users");
        esAdmin(holder,contact.getId(),currentUser,chatId);
       deleteUser(holder,contact.getId(),currentUser,chatId);
    }

    private void esAdmin(final IntegrantesHolder holder, final String id, final String currentUser, String chatId) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("creador").exists()){
                    String creador = dataSnapshot.child("creador").getValue().toString();
                    if (creador.equals(id)){
                        holder.admintext.setVisibility(View.VISIBLE);
                        holder.admintext.setText("ADMINISTRADOR");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteUser(final IntegrantesHolder holder, final String id, final String currentUser, final String chatId) {
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.child("creador").exists()){
                   final String creador = dataSnapshot.child("creador").getValue().toString();
                   if (creador.equals(currentUser)){
                       holder.delete.setVisibility(View.VISIBLE);
                       holder.delete.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               eliminarUsuario(id,creador,chatId);
                           }
                       });
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void eliminarUsuario(final String id, String creadorUid, String chatId) {
        if (!creadorUid.equals(id)){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(id)
                    .child("chat").child(chatId);

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat")
                    .child(chatId).child("info").child("users");

            reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        databaseReference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                activity.startActivity(new Intent(activity, MainActivity.class));
                                Toast.makeText(activity, "Has Expulsado Exitosamente", Toast.LENGTH_SHORT).show();
                                activity.finish();
                            }
                        });
                    }else {
                        Toast.makeText(activity, "Error..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
          //  Toast.makeText(activity, "NO es diferente"+id+"\n"+creadorUid, Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(activity, "Eres El administrador Puedes salir regresando al chat y precionando Salir del Grupo", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    class IntegrantesHolder extends RecyclerView.ViewHolder{

        public TextView username,admintext;
        public ImageView profile_image;
        public ImageView delete;
        public ProgressBar progressGroup;

        public IntegrantesHolder(View itemView) {
            super(itemView);

            progressGroup = itemView.findViewById(R.id.progress_group);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            delete = itemView.findViewById(R.id.remove_user);
            admintext = itemView.findViewById(R.id.admin_text);
        }
    }
}


