package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Model.ChatObject;
import com.damon.messenger.Adapters.MediaAdapter;
import com.damon.messenger.Adapters.MessageAdapterGrupo;
import com.damon.messenger.MessageObject;
import com.damon.messenger.Model.Messages;
import com.damon.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ChatGrupoActiviy extends AppCompatActivity {

    private RecyclerView mChat, mMedia;
    private RecyclerView.Adapter mChatAdapter, mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager, mMediaLayoutManager;

    ArrayList<MessageObject> messageList;

    ChatObject mChatObject;
    private String checker="";
    private Uri imageUri;
    private String myUrl ="";

    DatabaseReference mChatMessagesDb,reference;

    private StorageReference storageProlifePictureRef;

    private Toolbar ChatToolbar;
    private String id;
    private ImageView iconodeLlamda;

    private TextView userName, userLassSenn;
    private CircleImageView userImage;

    private StorageTask uploadTask;

    private Uri uri;
    private Bitmap bitmap;

    List<String> keyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_grupo_activiy);

        keyList = new ArrayList<String>();
        mChatObject = (ChatObject) getIntent().getSerializableExtra("chatObject");

        storageProlifePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Grupos");

        mChatMessagesDb = FirebaseDatabase.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child("messages");

        reference = FirebaseDatabase.getInstance().getReference().child("chat");

        id = getIntent().getStringExtra("id");

        ImageButton mSend = findViewById(R.id.send);
        ImageButton mAddMedia = findViewById(R.id.addMedia);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        initializeMessage();
        initializeMedia();
        inicializar();
        getChatMessages();

        new ChatThread().run();

        iconodeLlamda.setVisibility(View.GONE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_grupo_salir,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menu_salir_grupo){
            salirGrupo();
        }else if (item.getItemId()==R.id.menu_cambiarnombre_grupo){
            cambairnombregrupo();
        }else if (item.getItemId() == R.id.menu_foto_grupo){
            metodoImagen();
        }


        return true;
    }

    private void metodoImagen(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatGrupoActiviy.this,R.style.AlertDialog);
        builder.setTitle("Hola Seleciona tu imagen");//AQUI SERA EL DIALOGO QUE MOSTRARA
        builder.setNegativeButton("Selecionar Imagen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checker = "clicked";
                //Comience a recortar la actividad para la imagen adquirida previamente guardada en el dispositivo
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(ChatGrupoActiviy.this);

            }
        });
        builder.show();
    }

    private void cambarImagenGrupo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat")
                .child(id).child("info");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Actualizando perfil");
        progressDialog.setMessage("Espera porfavor,actualizando");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null){
            final  StorageReference  fileRef= storageProlifePictureRef
                    .child(id+".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat")
                                .child(id).child("info");

                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("image",myUrl);

                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(ChatGrupoActiviy.this, "Has cambiado la foto correctamente", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    private void cambairnombregrupo() {

        //AQUI ESTAMOS CREANDO UNA PANTALLA FLOTANTE DE DIALOGO QUE SERA QUIEN ALMACENE EL GRUPO A CREAR
        //ESTA ALERTA DIALOG ES PARA AMOSTRAR EL RECUDRO DONDE ESCRIBIREMOS EL NOMBRE DEL GRUPO
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatGrupoActiviy.this,R.style.AlertDialog);
        builder.setTitle("Ingresa El nuevo nombre del Grupo:");//AQUI SERA EL DIALOGO QUE MOSTRARA
        final EditText  groupNameField=new EditText(ChatGrupoActiviy.this);//DONDE LO MOSTRARA
        groupNameField.setHint("Hola ingresa aqui el nombre del grupo");// LO QUE IRA DENTRO DONDE SE ESCRIBIRA
        builder.setView(groupNameField);//AQUI ES PARA PODER VER EN LA PANTALLA LO QUE CREAMOS

        //ESTE POSITIVEBUTTON ES PARA CONFIRMAR LA CREACION
        builder.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String grooupName = groupNameField.getText().toString();//asenmos un casting
                if (TextUtils.isEmpty(grooupName)){//este condicion es para ver si escribio o no algo
                    Toast.makeText(ChatGrupoActiviy.this, "Porfavor escribe el nombre del grupo", Toast.LENGTH_SHORT).show();
                }else {
                    //aqui le estamos pasando un parametro que es el nombre del grupo
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chat")
                            .child(id).child("info");

                    HashMap <String ,Object> hashMap = new HashMap<>();
                    hashMap.put("nombre",grooupName);

                    reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ChatGrupoActiviy.this, "Nombre Cambiado Correctamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }
                        }
                    });
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

    private void salirGrupo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid())
                .child("chat").child(id);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("chat")
                .child(id).child("info").child("users");

        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    databaseReference.child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            Toast.makeText(ChatGrupoActiviy.this, "Has salido del grupo", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }else {
                    Toast.makeText(ChatGrupoActiviy.this, "Error..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getChatMessages() {
        mChatMessagesDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String  text = "",
                            creatorID = "",
                            imageUrl = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    keyList.add(dataSnapshot.getKey());

                    if(dataSnapshot.child("imageUrl").getValue() != null)
                        imageUrl = dataSnapshot.child("imageUrl").getValue().toString();

                    if(dataSnapshot.child("text").getValue() != null)
                        text = dataSnapshot.child("text").getValue().toString();
                    if(dataSnapshot.child("creator").getValue() != null)
                        creatorID = dataSnapshot.child("creator").getValue().toString();

                    if(dataSnapshot.child("media").getChildrenCount() > 0)
                        for (DataSnapshot mediaSnapshot : dataSnapshot.child("media").getChildren())
                            mediaUrlList.add(mediaSnapshot.getValue().toString());

                    MessageObject mMessage = new MessageObject(dataSnapshot.getKey(), creatorID, text, mediaUrlList,imageUrl,FirebaseAuth.getInstance().getUid(),mChatObject.getChatId());
                    messageList.add(mMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size()-1);
                    mChatAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageObject messageObject = dataSnapshot.getValue(MessageObject.class);
                String key = dataSnapshot.getKey();
                int index = keyList.indexOf(key);
                try {
                    messageList.set(index,messageObject);
                    mChatAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = keyList.indexOf(dataSnapshot.getKey());
                messageList.remove(index);
                keyList.remove(index);
                mChatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    EditText mMessage;
    private void sendMessage(){


        String messageId = mChatMessagesDb.push().getKey();
        final DatabaseReference newMessageDb = mChatMessagesDb.child(messageId);

        final Map newMessageMap = new HashMap<>();

        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

        if(!mMessage.getText().toString().isEmpty())
            newMessageMap.put("text", mMessage.getText().toString());


        if(!mediaUriList.isEmpty()){
            for (String mediaUri : mediaUriList){
                String mediaId = newMessageDb.child("media").push().getKey();
                mediaIdList.add(mediaId);
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);

                UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());

                                totalMediaUploaded++;
                                if(totalMediaUploaded == mediaUriList.size())
                                    updateDatabaseWithNewMessage(newMessageDb, newMessageMap);

                            }
                        });
                    }
                });
            }
        }else{
            if(!mMessage.getText().toString().isEmpty())
                updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
        }


    }


    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap){
        newMessageDb.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIdList.clear();
        totalMediaUploaded=0;
        mMediaAdapter.notifyDataSetChanged();

        String message;

        if(newMessageMap.get("text") != null)
            message = newMessageMap.get("text").toString();
        else
            message = "Sent Media";

//        for(UserObject mUser : mChatObject.getUserObjectArrayList()){
//            if(!mUser.getUid().equals(FirebaseAuth.getInstance().getUid())){
//                new SendNotification(message, "New Message", mUser.getNotificationKey());
//            }
//        }
    }

    @SuppressLint("WrongConstant")
    private void initializeMessage() {
        messageList = new ArrayList<>();
        mChat= findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapterGrupo(messageList,ChatGrupoActiviy.this);
        mChat.setAdapter(mChatAdapter);
    }



    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaUriList = new ArrayList<>();

    @SuppressLint("WrongConstant")
    private void initializeMedia() {
        mediaUriList = new ArrayList<>();
        mMedia= findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaUriList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        intent.setAction(intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);

        CropImage.activity(uri)
                .start(ChatGrupoActiviy.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            if(requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
//                if(data.getClipData() == null){
//                    mediaUriList.add(data.getData().toString());
//                }else{
//                    for(int i = 0; i < data.getClipData().getItemCount(); i++){
//                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
//                    }
//                }

                CropImage.ActivityResult results = CropImage.getActivityResult(data);
                imageUri = results.getUri();//aqui recueramos
                userImage.setImageURI(imageUri);//aqui asignamos

                if (imageUri!=null){
                    if (checker.equals("clicked")) {
                        cambarImagenGrupo();
                    }else {
                        final ProgressDialog pd = new ProgressDialog(this);
                        pd.setMessage("Enviando Imagen");
                        pd.show();
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        uri = result.getUri();//aqui recueramos

                        String messageId = mChatMessagesDb.push().getKey();
                        final DatabaseReference newMessageDb = mChatMessagesDb.child(messageId);
                        String mediaId = newMessageDb.child("media").push().getKey();
                        mediaIdList.add(mediaId);
                        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(mChatObject.getChatId()).child(messageId).child(mediaId);

                        final Map newMessageMap = new HashMap<>();
                        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
                        newMessageMap.put("text", mMessage.getText().toString());

                        final File file = new File(SiliCompressor.with(this)
                                .compress(FileUtils.getPath(this,uri),
                                        new File(this.getCacheDir(),"temp")));

                        Uri uri = Uri.fromFile(file);

                        filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()){
                                    pd.dismiss();
                                    Uri dowloadUri = task.getResult();
                                    newMessageMap.put("imageUrl",dowloadUri.toString());

                                    updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
                                }else {
                                    Toast.makeText(ChatGrupoActiviy.this, "Error", Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            }
                        });
                    }
                }



              //  mMediaAdapter.notifyDataSetChanged();
            }

        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ChatGrupoActiviy.this,MainActivity.class));
            finish();
        }

    }

    private void  inicializar(){

        ChatToolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        ChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionbarView);

        userImage = findViewById(R.id.custom_profile_IMAGE);
        userName = findViewById(R.id.custom_profile_name);
        userLassSenn = findViewById(R.id.custom_user_last_seen);
        iconodeLlamda = findViewById(R.id.llamada);
        mMessage = findViewById(R.id.messageInput);
    }

    private class ChatThread extends Thread{
        @Override
        public void run() {
            super.run();
            reference.child(id).child("info").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("nombre").exists()){
                        String nombre = dataSnapshot.child("nombre").getValue().toString();

                        userName.setText(nombre);
                        userImage.setImageResource(R.mipmap.ic_launcher);
                        userLassSenn.setText("Ver Miembros del Grupo");


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                userLassSenn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(),IntegrantesGrupoActiviy.class);
                                        intent.putExtra("id",id);
                                        startActivity(intent);
                                    }
                                });
                            }
                        });

                        if (dataSnapshot.hasChild("image")){
                            final String  image = dataSnapshot.child("image").getValue().toString();

                            Picasso.get().load(image).into(userImage);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    userImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(),ImageViewerActivity.class);
                                            intent.putExtra("url",image);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });


                        }
                    }else {
                        userName.setText("grupo");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
