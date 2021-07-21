package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.damon.messenger.Adapters.CustomBottomSheet;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
import com.damon.messenger.Model.Story;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter =0;

    long pressTime = 0L;

    long limint = 500L;

    StoriesProgressView storiesProgressView;
    ImageView image ,story_photo;
    TextView story_username,text_story;

    LinearLayout r_seen;
    TextView seen_number;
    ImageView story_delete;

    ConstraintLayout story_constrain;

    List<String> images;
    List<String > storyids;
    List<String> textListStory = new ArrayList<>();
    String userid;
    String  urlImage,nameUser;
    StorageReference storageReference;
    FirebaseStorage storage;
    private ProgressBar progressBar;
    private LinearLayout linar_contestar;



    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){

                case  MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return  limint < now - pressTime;

            }

            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);



        text_story = findViewById(R.id.txv_story);
        linar_contestar = findViewById(R.id.linear_contestar);
        progressBar = findViewById(R.id.proges_dialog_image_story);
        storage = FirebaseStorage.getInstance();

        r_seen = findViewById(R.id.r_seen);
        seen_number = findViewById(R.id.seen_number);

        story_delete = findViewById(R.id.story_delete);


        r_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);


        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image);
        story_photo = findViewById(R.id.story_photo);
        story_username  = findViewById(R.id.story_username);

        userid = getIntent().getStringExtra("userid");

        if (userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
            linar_contestar.setVisibility(View.GONE);
        }

        getStories(userid);
        userInfo(userid);

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);

        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this, QuienViomiHistoriaActivity.class);
                intent.putExtra("id",userid);
                intent.putExtra("storyid",storyids.get(counter));
                intent.putExtra("title","views");
                startActivity(intent);
            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                        .child(userid).child(storyids.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            storageReference = storage.getReferenceFromUrl(images.get(counter));
                            storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(StoryActivity.this, "Eliminado", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }else {
                                        Toast.makeText(StoryActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });

        linar_contestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.pause();
                System.out.println("img"+urlImage);
                CustomBottomSheet filterBottomFragment = new CustomBottomSheet(images.get(counter),nameUser,userid);
                //set filterBottomFragment.setCancelable(true) if you want to cancel on touch out side;
                filterBottomFragment.setCancelable(true);
                //filterBottomFragment.setFilterTagClickListener(this);
                filterBottomFragment.setCancelable(true);
                filterBottomFragment.show(getSupportFragmentManager(), filterBottomFragment.getTag());;
            }
        });

    }

    @Override
    public void onNext() {

    //    Glide.with(getApplicationContext()).load(images.get(++counter)).into(image);
//        urlImage = images.get(++counter);
        Picasso.get().load(images.get(++counter)).into(image, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(R.mipmap.ic_launcher).into(image);
            }
        });
        if (textListStory.size()>0){
            text_story.setText(textListStory.get(counter));
        }

        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));

    }

    @Override
    public void onPrev() {

        if( (counter-1)<0)return;

      //  Glide.with(getApplicationContext()).load(images.get(--counter)).into(image);
//        urlImage = images.get(--counter);
        Picasso.get().load(images.get(--counter)).into(image, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(R.mipmap.ic_launcher).into(image);
            }
        });
        if (textListStory.size()>0){
            text_story.setText(textListStory.get(counter));
        }

        seenNumber(storyids.get(counter));

    }

    @Override
    public void onComplete() {

        finish();

    }

    @Override
    protected void onDestroy() {

        storiesProgressView.destroy();

        super.onDestroy();

    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        storiesProgressView.resume();
        super.onRestart();
    }

    private void getStories(String userid){
        images = new ArrayList<>();
        storyids = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                images.clear();
                storyids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Story story = snapshot.getValue(Story.class);
                    long timecurrent =System.currentTimeMillis();
                    if (timecurrent > story.getTimestart() && timecurrent <story.getTimeend()){
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                        if (story.getMsgImage()!=null){
                            textListStory.add(story.getMsgImage());
                        }
                    }

                }

                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);

//                urlImage = images.get(counter);

                Picasso.get().load(images.get(counter)).into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(image);
                    }
                });

                if (textListStory.size()>0){
                    text_story.setText(textListStory.get(counter));
                }

//                Glide.with(getApplicationContext()).load(images.get(counter))
//                        .into(image);

                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void userInfo(String userid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contacts user = dataSnapshot.getValue(Contacts.class);
                Glide.with(getApplicationContext()).load(user.getImage()).into(story_photo);
                story_username.setText(user.getName());
                nameUser = user.getName();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addView(String  storyid){
        FirebaseDatabase.getInstance().getReference("Story").child(userid)
                .child(storyid).child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(true);

    }
    private void  seenNumber(String  storyid){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(userid).child(storyid).child("views");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                seen_number.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
