package com.damon.messenger.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.damon.messenger.Activitys.AddStory;
import com.damon.messenger.Activitys.AddStoryActivity;
import com.damon.messenger.Activitys.StoryActivity;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
import com.damon.messenger.Model.Story;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {


    private Context mcontext;
    private List<Story> mStory;

    public StoryAdapter(Context mcontext, List<Story> mStory) {
        this.mcontext = mcontext;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i ==0){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.add_story,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        final Story story  = mStory.get(i);

        userInfo(holder,story.getUserid(),i);

        if (holder.getAdapterPosition() !=0){
            seenStory(holder,story.getUserid());
        }
        if (holder.getAdapterPosition() ==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() ==0){
                    myStory(holder.addstory_text,holder.story_plus,true);
                }else {
                    Intent intent = new Intent(mcontext, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    mcontext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        public ImageView story_photo,story_plus,story_photo_seen;
        public TextView story_username,addstory_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return 0;
        }
        return 1;
    }
    private void userInfo(final ViewHolder viewHolder , final String userid, final int pos){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contacts user = dataSnapshot.getValue(Contacts.class);

//                Glide.with(mcontext).load(user.getImage()).into(viewHolder.story_photo);
                Picasso.get().load(user.getImage()).into(viewHolder.story_photo);
                if (pos !=0){
                    Picasso.get().load(user.getImage()).into(viewHolder.story_photo_seen);
                   // Glide.with(mcontext).load(user.getImage()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myStory(final TextView textView, final ImageView imageView, final boolean click){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    if (timecurrent>story.getTimestart() &&timecurrent < story.getTimeend()){
                        count++;
                    }
                }


                if (click){

                    if (count >0){
                        AlertDialog alertDialog = new AlertDialog.Builder(mcontext,R.style.AlertDialog).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Ver Historia",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mcontext, StoryActivity.class);
                                        intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mcontext.startActivity(intent);
                                        dialog.dismiss();

                                    }
                                });

                        if (count < 6)
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Nueva Historia",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Intent intent = new Intent(mcontext, AddStory.class);
                                        mcontext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.show();

                    }else {

                        Intent intent = new Intent(mcontext, AddStory.class);
                        mcontext.startActivity(intent);
                    }

                }else {
                    if (count>0){
                        textView.setText("MiHistoria");
                        imageView.setVisibility(View.GONE);
                    }else{
                        textView.setText("Nueva Historia");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void seenStory(final ViewHolder viewHolder , String  userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (!snapshot.child("views")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .exists() && System.currentTimeMillis()<snapshot.getValue(Story.class).getTimeend()){
                        i++;
                    }
                }
                if (i>0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
