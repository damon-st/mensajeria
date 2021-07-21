package com.damon.messenger.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.messenger.Activitys.ChatGrupoActiviy;
import com.damon.messenger.Model.ChatObject;
import com.damon.messenger.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    ArrayList<ChatObject> chatList;
    private DatabaseReference usersRef;
    Context context;

    public ChatListAdapter(ArrayList<ChatObject> chatList){
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatListViewHolder rcv = new ChatListViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        final String  nombre =  chatList.get(position).getChatId();

        usersRef = FirebaseDatabase.getInstance().getReference().child("chat").child(nombre).child("info");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("nombre")){
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    holder.mTitle.setText(name);
                }
                if (dataSnapshot.hasChild("image")){
                    String image = dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(image)
                            .resize(75,75)
                            .into(holder.image_grupo, new Callback() {
                                @Override
                                public void onSuccess() {
                                    holder.progressBar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.image_grupo);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatGrupoActiviy.class);
                intent.putExtra("chatObject", chatList.get(holder.getAdapterPosition()));
                intent.putExtra("id",nombre);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }





    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public LinearLayout mLayout;
        public CircleImageView image_grupo;
        public ProgressBar progressBar;
        public ChatListViewHolder(View view){
            super(view);
            mTitle = view.findViewById(R.id.title);
            mLayout = view.findViewById(R.id.layout);
            image_grupo = view.findViewById(R.id.image_grupo);
            progressBar = view.findViewById(R.id.proges_dialog_user);
        }
    }
}
