package com.damon.messenger.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.messenger.Activitys.ImageViewerActivity;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.MessageObject;
import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapterGrupo extends RecyclerView.Adapter<MessageAdapterGrupo.MessageViewHolder> {

    Activity activity;
    public MessageAdapterGrupo(ArrayList<MessageObject> messageList,Activity activity) {
        this.messageList = messageList;
        this.activity = activity;
    }

    private DatabaseReference usersRef;

    ArrayList<MessageObject> messageList;
    FirebaseStorage storage;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        MessageViewHolder rcv = new MessageViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

        storage = FirebaseStorage.getInstance();
        String fromUserID = messageList.get(position).getSenderId();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("name")){
                    String name = dataSnapshot.child("name").getValue().toString();
                    holder.mSender.setText(name);
                }
                if (dataSnapshot.hasChild("image")){
                    String image = dataSnapshot.child("image").getValue().toString();

                    try {
                        Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imagen_sender_grupo);
                    }catch (Exception e){
                        System.out.println("Errror"+e);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.mMessage.setText(messageList.get(position).getMessage());
        if (messageList.get(position).getCreator().equals(FirebaseAuth.getInstance().getUid())){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext(),R.style.AlertDialog).create();
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Eliminar Mensjae", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletemessageText(holder,messageList.get(position).getMessageId(),messageList.get(position).getChat());
                        }
                    });
                    alertDialog.show();
                    return false;
                }
            });
        }


        if (messageList.get(position).getImageUrl().isEmpty()){
            holder.imagenesGroupo.setVisibility(View.GONE);
        }else {
            try {
                final String image = messageList.get(position).getImageUrl();
                Picasso.get().load(image).into(holder.imagenesGroupo);
                holder.imagenesGroupo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent  = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                        intent.putExtra("url",image);
                        holder.itemView.getContext().startActivity(intent);
                    }
                });

                if (messageList.get(position).getCreator().equals(FirebaseAuth.getInstance().getUid())){
                    holder.imagenesGroupo.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext(),R.style.AlertDialog).create();
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Eliminar Mensaje", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   deleteMessage(messageList.get(position).getImageUrl(),messageList.get(position).getMessageId(),messageList.get(position).getChat(),holder);
                                   // Toast.makeText(holder.itemView.getContext(), ""+messageList.get(position).getMessageId(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog.show();
                            return false;
                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }


        if(messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()){
            holder.mViewMedia.setVisibility(View.GONE);
        }


       // Picasso.get().load(messageList.get(holder.getAdapterPosition()).getMediaUrlList().get(position)).into(holder.imagenesGroupo);

        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
//                        .setStartPosition(0)
//                        .show();
                Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                intent.putExtra("url",messageList.get(holder.getAdapterPosition()).getMediaUrlList().get(0));
                v.getContext().startActivity(intent);
              //  Toast.makeText(v.getContext(), ""+messageList.get(holder.getAdapterPosition()).getMediaUrlList().get(0), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deletemessageText(final MessageViewHolder holder, String messageId, String chat) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chat").child(chat).child("messages");
        reference.child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(holder.itemView.getContext(), "Exito al eliminar", Toast.LENGTH_SHORT).show();
//                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                        activity.finish();
                    }else {
                        Toast.makeText(holder.itemView.getContext(), "Error al Eliminar el Mensaje", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void deleteMessage(String imageUrl, String messageId, String child, final MessageViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("chat").child(child).child("messages");
        final StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
        reference.child(messageId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                                        holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                        activity.finish();
                                    }else {
                                        Toast.makeText(holder.itemView.getContext(), "Error Al eliminar la imagen", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        });
                    }else {
                        Toast.makeText(holder.itemView.getContext(), "Error al Eliminar", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView mMessage,
                mSender;
        Button mViewMedia;
        LinearLayout mLayout;
        CircleImageView imagen_sender_grupo;
        ImageView imagenesGroupo;
        MessageViewHolder(View view){
            super(view);
            mLayout = view.findViewById(R.id.layout);

            mMessage = view.findViewById(R.id.message);
            mSender = view.findViewById(R.id.sender);

            mViewMedia = view.findViewById(R.id.viewMedia);
            imagen_sender_grupo = view.findViewById(R.id.imagen_sender_grupo);
            imagenesGroupo = view.findViewById(R.id.imagenes_group);
        }
    }

}
