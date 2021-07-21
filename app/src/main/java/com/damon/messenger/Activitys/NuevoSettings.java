package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.damon.messenger.R;
import com.damon.messenger.SettingsPerfil.PerfilSettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NuevoSettings extends AppCompatActivity {


    CircleImageView profileImg;
    TextView nameUser,statusUser;
    LinearLayout layout_chat,layout_invite_friends,layout_help,layout_count;
    Dialog dialogPrivacity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_settings);

        dialogPrivacity = new Dialog(this);

        profileImg = findViewById(R.id.image_profile);
        nameUser = findViewById(R.id.tv_username);
        statusUser = findViewById(R.id.tv_bio);
        layout_chat = findViewById(R.id.ln_chats);
        layout_invite_friends = findViewById(R.id.ln_invite_friend);
        layout_help = findViewById(R.id.ln_help);
        layout_count = findViewById(R.id.ln_account);

        userData();

        layout_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NuevoSettings.this,ColoresChatActivity.class);
                startActivity(intent);
            }
        });

        layout_invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String shareappPackageName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola te recomiendo  " + getResources().getString(R.string.app_name) + " App at: https://play.google.com/store/apps/details?id=" + shareappPackageName);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        layout_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPrivacity.setContentView(R.layout.dialog_polity);
                TextView link = dialogPrivacity.findViewById(R.id.texto_info_privacy);
                String ulr = getString(R.string.polity_link);
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ulr));
                        startActivity(intent);
                    }
                });
                dialogPrivacity.show();
                Window window = dialogPrivacity.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

        layout_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NuevoSettings.this,CuentaActivity.class);
                startActivity(intent);
            }
        });

        profileImg.setOnClickListener(v -> {
           sentToPerfil();
        });

        nameUser.setOnClickListener(v -> {
            sentToPerfil();
        });
    }

    void sentToPerfil(){
        Intent intent = new Intent(NuevoSettings.this, PerfilSettingsActivity.class);
        startActivity(intent);
    }

    private void userData(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                reference.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String img = snapshot.child("image").getValue().toString();
                            String name = snapshot.child("name").getValue().toString();
                            String status  = snapshot.child("status").getValue().toString();

                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   try {
                                       Picasso.get().load(img).into(profileImg);
                                       nameUser.setText(name);
                                       statusUser.setText(status);
                                   }catch (Exception e){
                                       e.printStackTrace();
                                   }
                               }
                           });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }.start();
    }
}