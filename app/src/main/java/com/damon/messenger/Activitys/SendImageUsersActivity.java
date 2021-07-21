package com.damon.messenger.Activitys;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.messenger.Adapters.UserListAdapter;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Model.UserObject;
import com.damon.messenger.Notifications.APIService;
import com.damon.messenger.Notifications.Client;
import com.damon.messenger.Notifications.Data;
import com.damon.messenger.Notifications.MyResponse;
import com.damon.messenger.Notifications.Sender;
import com.damon.messenger.Notifications.Token;
import com.damon.messenger.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.iceteck.silicompressorr.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class SendImageUsersActivity extends AppCompatActivity {

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    private DatabaseReference Contactsref,UsersRef,RootRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    Button mCreate;
    private TextView maximo_grupo_contador;
    private int contador = 0;

    Uri imageUri;

    ArrayList<UserObject> userList, contactList;

    private StorageReference storageReference;

    private String messagemSenderID,messageReciverID,saveCurrentData,saveCutrrentTime;
    private ProgressDialog progressBar;
    private String  prueba;

    private boolean multi;

    private List<Uri> paths;
    private APIService apiService;
    private  boolean notify;
    private  String  isImage;
    private String  typeFile,messageNotify;
    private TextView enviandoUsuario;
    int size;
    InputStream stream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image_users);

        paths = new ArrayList<>();

        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Enviando imagen");
        progressBar.setMessage("Porfavor espera....");
        progressBar.setCancelable(false);

        contactList= new ArrayList<>();
        userList= new ArrayList<>();

        apiService  = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        maximo_grupo_contador= findViewById(R.id.maximo_grupo_contador);
        enviandoUsuario  = findViewById(R.id.texto_mostrar_enviar);

        storageReference =FirebaseStorage.getInstance().getReference().child("Image Files");
        RootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        currentUserID =mAuth.getCurrentUser().getUid();
        messagemSenderID = currentUserID;

        Contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mCreate = findViewById(R.id.creargrupo);
        mCreate.setEnabled(false);


        initializeRecyclerView();
        getContactList();
        calcularFecha();


        Intent intent = getIntent();
        String type = intent.getType();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                multi = false;
                isImage = "image";
                typeFile = "image";
                messageNotify = "Te a enviado una imagen";
                handleSendArchivos(intent);
            }else if (type.startsWith("video/")){
                multi = false;
                isImage = "mp4";
                typeFile = "mp4";
                messageNotify = "Te a enviado un Video";
                handleSendArchivos(intent);
            }else if (type.startsWith("audio/")){
                multi = false;
                isImage = "mp3";
                typeFile = "mp3";
                messageNotify = "Te a enviado un Audio";
                handleSendArchivos(intent);
            }else if (type.startsWith("application/pdf")){
                multi = false;
                isImage = "pdf";
                typeFile = "pdf";
                messageNotify = "Te a enviado un Archivo PDF";
                handleSendArchivos(intent);
            }else if (type.startsWith("application/msword")){
                multi = false;
                isImage ="docx";
                typeFile = "docx";
                messageNotify = "Te a enviado un archivo WORD";
                handleSendArchivos(intent);
            }else if (type.startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document")){
                multi = false;
                isImage = "docx";
                typeFile = "docx";
                messageNotify = "Te a enviado un archivo WORD";
                handleSendArchivos(intent);
            }else if (type.startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")){
                multi = false;
                isImage = "xlsx";
                typeFile = "xlsx";
                messageNotify = "Te a enviado un archivo EXCEL";
                handleSendArchivos(intent);
            }
        }
        if (Intent.ACTION_SEND_MULTIPLE.equals(action) && getIntent().hasExtra(Intent.EXTRA_STREAM)) {
            ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            multi = true;
//            Intent i = new Intent(this, RequestListingActivity.class);
//            i.putExtra(PARAM_MULTIPLE_IMAGE, list);
//            startActivity(i);
//            finish();
            for (Parcelable p : list){
                prueba  = p.toString();
                imageUri = Uri.parse(prueba);
                paths.add(imageUri);
            }
        }
    }


    private void getContactList(){

        FirebaseRecyclerOptions options = new
                FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(Contactsref,Contacts.class)//currentuserid.child(currentUserID)
                .build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull final Contacts contacts) {

                final String usersId = getRef(position).getKey();
                final String id = getRef(position).getKey();
                final String[] email = new String[1];

                UsersRef.child(usersId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){



                            String nombre = dataSnapshot.child("name").getValue().toString();

                            UserObject mContacts = new UserObject(usersId, nombre, email[0]);
                            userList.add(mContacts);


                            holder.mName.setText(nombre);
//                            if (dataSnapshot.child("email").exists()) {
//                               email[0] = dataSnapshot.child("email").getValue().toString();
//                                holder.mPhone.setText(email[0]);
//                            }

                            if (dataSnapshot.child("id").exists()){
                                String  phone  = dataSnapshot.child("id").getValue().toString();
                                holder.mPhone.setText(phone);
                                holder.mPhone.setVisibility(View.GONE);
                            }
                            if (dataSnapshot.child("image").exists()){
                                String image = dataSnapshot.child("image").getValue().toString();
                                try {
                                    Picasso.get().load(image)
                                            .resize(50,50)
                                            .placeholder(R.drawable.profile_image)
                                            .into(holder.imagenPeril, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    holder.progressBar.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(Exception e) {
                                                    Picasso.get().load(R.mipmap.ic_launcher).into(holder.imagenPeril);
                                                }
                                            });
                                }catch (Exception e){
                                    System.out.println("Errror----"+e);
                                }

                            }
                            holder.mAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        userList.get(holder.getAdapterPosition()).setSelected(true);
                                        contador += 1;
                                        // Toast.makeText(CrearGrupoChatActivity.this, ""+ userList.get(holder.getAdapterPosition()).getUid(), Toast.LENGTH_SHORT).show();
                                        maximo_grupo_contador.setText("Usuarios a enviar: " + contador );
                                        mCreate.setEnabled(true);
                                    }else {
                                        userList.get(holder.getAdapterPosition()).setSelected(false);
                                        contador -= 1;
                                        maximo_grupo_contador.setText("Usuarios a enviar: " + contador );
                                        mCreate.setEnabled(false);
                                    }
                                    if (contador>=50){
                                        Toast.makeText(SendImageUsersActivity.this, "Hola recuerda son 50 personas que puede enviar nomas", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });


                            UserObject mContact = new UserObject(usersId, nombre, email[0]);
                            contactList.add(mContact);
                            getUserDetails(mContact);

                            mCreate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // createChat();
                                    progressBar.show();
                                    mCreate.setEnabled(false);
                                    if (multi){
                                        MultiSend();
                                    }else {
                                        notify = true;
//                                        EnviarImages();
                                        new EnviarArchvios().execute();
                                    }

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, null, false);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutView.setLayoutParams(lp);

                ContactsViewHolder rcv = new ContactsViewHolder(layoutView);
                return rcv;
            }
        };
        mUserList.setAdapter(adapter);
        adapter.startListening();



    }

    private void MultiSend() {

        new Thread(){
            @Override
            public void run() {
                super.run();

                for (Uri imageUri : paths){
                    for(UserObject mUser : userList){
                    if(mUser.getSelected()){
                            messageReciverID  = mUser.getUid();
                            final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                            final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                            DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                                    .child(messagemSenderID).child(messageReciverID).push();

                            final String messagePushID = userMessagerKeyRef.getKey();

                            final StorageReference filePath = storageReference.child(messagePushID + ".jpg");

                            final File file = new File(SiliCompressor.with(SendImageUsersActivity.this)
                                    .compress(FileUtils.getPath(SendImageUsersActivity.this,imageUri),
                                            new File(SendImageUsersActivity.this.getCacheDir(),"temp")));

                            Uri uri = Uri.fromFile(file);

                            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()){
                                        throw  task.getException();
                                    }
                                    return filePath.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()){
                                        try {
                                            Uri dowloadUrl = task.getResult();
                                            Map messageTextBody = new HashMap();
                                            messageTextBody.put("message", dowloadUrl.toString());
                                            messageTextBody.put("name", imageUri.getLastPathSegment());
                                            messageTextBody.put("type", "image");
                                            messageTextBody.put("from", messagemSenderID);
                                            messageTextBody.put("to", messageReciverID);
                                            messageTextBody.put("messageID", messagePushID);
                                            messageTextBody.put("time", saveCutrrentTime);
                                            messageTextBody.put("date", saveCurrentData);
                                            messageTextBody.put("sender", messagemSenderID);
                                            messageTextBody.put("receiver", messageReciverID);
                                            messageTextBody.put("isseen",false);


                                            Map messageBodyDetails = new HashMap();
                                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                            messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {

                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(SendImageUsersActivity.this, "Imagen Enviada", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Toast.makeText(SendImageUsersActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

//                                    loadingBar.dismiss();
                                                    }
                                                    file.delete();
                                                }
                                            });
                                        } catch (Exception e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(SendImageUsersActivity.this, "" + e, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            System.out.println("MENSAJE " + e);
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                loadingBar.dismiss();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SendImageUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.cancel();
                    }
                });
            }
        }.start();
    }

    class  ContactsViewHolder extends RecyclerView.ViewHolder{
        TextView mName, mPhone;
        LinearLayout mLayout;
        CheckBox mAdd;
        CircleImageView imagenPeril;
        ProgressBar progressBar;

        public ContactsViewHolder(@NonNull final View view) {
            super(view);

            mName = view.findViewById(R.id.name);
            mPhone = view.findViewById(R.id.phone);
            mAdd = view.findViewById(R.id.add);
            mLayout = view.findViewById(R.id.layout);
            imagenPeril = view.findViewById(R.id.imagen_para_creargrupo);
            progressBar = view.findViewById(R.id.proges_dialog);
        }
    }


    private void getUserDetails(UserObject mContact) {
        DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = mUserDB.orderByChild("email").equalTo(mContact.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String  phone = "",
                            name = "";
                    for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        if(childSnapshot.child("email").getValue()!=null)
                            phone = childSnapshot.child("email").getValue().toString();
                        if(childSnapshot.child("name").getValue()!=null)
                            name = childSnapshot.child("name").getValue().toString();


                        UserObject mUser = new UserObject(childSnapshot.getKey(), name, phone);
                        if (name.equals(phone))
                            for(UserObject mContactIterator : contactList){
                                if(mContactIterator.getPhone().equals(mUser.getPhone())){
                                    mUser.setName(mContactIterator.getName());
                                }
                            }

                        userList.add(mUser);
                        mUserListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @SuppressLint("WrongConstant")
    private void initializeRecyclerView() {
        mUserList= findViewById(R.id.recycler_creargrupos);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }


    private void handleSendArchivos(Intent intent) {
        imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
//            Intent i = new Intent(this, DataAddActivity.class);
//            i.putExtra(PARAM_IMAGE, imageUri.toString());
//            startActivity(i);
//            finish();

            try {
                stream = this.getContentResolver().openInputStream(imageUri);
                try {
                    System.out.println(stream.available());
                    float valor  =stream.available();
                    int s = Math.round(valor);
                    size = s / (1024 * 1024);
                    System.out.println(valor);
                    System.out.println(size);
                    stream.close();
                    stream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            catch (FileNotFoundException fileEx) {
                fileEx.fillInStackTrace();
            }
        } else {
            Toast.makeText(this, "Error occured, URI is invalid", Toast.LENGTH_LONG).show();
        }
    }

    private void calcularFecha(){
        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());
    }

    File file;
    Uri uri;
    StorageReference filePath;

    private void EnviarImages(){

        new Thread(){
            @Override
            public void run() {
                super.run();

                for(UserObject mUser : userList){
                    if(mUser.getSelected()){
                        notify = true;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enviandoUsuario.setText("!!Enviado el archivo a  " + mUser.getName() + "  espera porfavor....");
                            }
                        });

                        messageReciverID  = mUser.getUid();
                        final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                        final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                        DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                                .child(messagemSenderID).child(messageReciverID).push();

                        final String messagePushID = userMessagerKeyRef.getKey();



                       if (isImage.equals("image")){
                           filePath = storageReference.child(messagePushID + ".jpg");
                            file = new File(SiliCompressor.with(SendImageUsersActivity.this)
                                   .compress(FileUtils.getPath(SendImageUsersActivity.this,imageUri),
                                           new File(SendImageUsersActivity.this.getCacheDir(),"temp")));

                            uri = Uri.fromFile(file);
                           System.out.println("imageSize" + file.length());
                       }else if (isImage.equals("mp4")){
                           filePath = storageReference.child(messagePushID + ".mp4");
                           file = new File(imageUri.getPath());
                           uri = imageUri;
                       }else if (isImage.equals("mp3")){
                           filePath = storageReference.child(messagePushID + ".mp3");
                           file = new File(imageUri.getPath());
                           uri = imageUri;
                       }

                        filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()){
                                    throw  task.getException();
                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()){
                                    try {
                                        Uri dowloadUrl = task.getResult();
                                        Map messageTextBody = new HashMap();
                                        messageTextBody.put("message", dowloadUrl.toString());
                                        messageTextBody.put("name", imageUri.getLastPathSegment());
                                        messageTextBody.put("type", typeFile);
                                        messageTextBody.put("from", messagemSenderID);
                                        messageTextBody.put("to", messageReciverID);
                                        messageTextBody.put("messageID", messagePushID);
                                        messageTextBody.put("time", saveCutrrentTime);
                                        messageTextBody.put("date", saveCurrentData);
                                        messageTextBody.put("sender", messagemSenderID);
                                        messageTextBody.put("receiver", messageReciverID);
                                        messageTextBody.put("isseen",false);


                                        Map messageBodyDetails = new HashMap();
                                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                        messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(SendImageUsersActivity.this, "Imagen Enviada", Toast.LENGTH_SHORT).show();

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    enviandoUsuario.setText("!!Exito tu contacto  " + mUser.getName() + " recivio el archivo correctamente ");
                                                                }
                                                            });
                                                        }
                                                    });

                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(SendImageUsersActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

//                                    loadingBar.dismiss();
                                                }
                                            }
                                        });
                                    } catch (Exception e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(SendImageUsersActivity.this, "" + e, Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        System.out.println("MENSAJE " + e);
                                    }

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                    final String msg = messageNotify;

                                    reference = FirebaseDatabase.getInstance().getReference("Users").child(messagemSenderID);
                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                            if (notify) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sendNotifiaction(messageReciverID, contacts.getName(), msg);
                                                    }
                                                });
                                            }
                                            notify = false;
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                loadingBar.dismiss();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(SendImageUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.cancel();
                        mCreate.setEnabled(true);
                    }
                });
            }
        }.start();
    }


    private void sendNotifiaction(String receiver, final String username, final String message) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {


                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Token token = snapshot.getValue(Token.class);
                            Data data = new Data(messagemSenderID, R.mipmap.ic_launcher, username + ": " + message, "Nuevo Mensaje",
                                    messageReciverID);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new retrofit2.Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(SendImageUsersActivity.this, "Lo sentimos por el momento no se pudo enviar la notificacion!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                        }
                                    });
                        }
                    } catch (Exception e) {
                        System.out.println("Error" + e);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private class EnviarArchvios  extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            for(UserObject mUser : userList){
                if(mUser.getSelected()){
                    notify = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enviandoUsuario.setText("!!Enviando el archivo a  " + mUser.getName() + "  espera porfavor....");
                        }
                    });

                    messageReciverID  = mUser.getUid();
                    final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                    final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                    DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                            .child(messagemSenderID).child(messageReciverID).push();

                    final String messagePushID = userMessagerKeyRef.getKey();



                    if (isImage.equals("image")){
                        filePath = storageReference.child(messagePushID + ".jpg");
                        file = new File(SiliCompressor.with(SendImageUsersActivity.this)
                                .compress(FileUtils.getPath(SendImageUsersActivity.this,imageUri),
                                        new File(SendImageUsersActivity.this.getCacheDir(),"temp")));

                        uri = Uri.fromFile(file);
                        float valor  =file.length();
                        int s = Math.round(valor);
                        size = s / (1024 * 1024);
                    }else if (isImage.equals("mp4")){
                        filePath = storageReference.child(messagePushID + ".mp4");
                        file = new File(Objects.requireNonNull(imageUri.getPath()));
                        try {
                            System.out.println("sizeVideo"+file.length());
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        uri = imageUri;
                    }else if (isImage.equals("mp3")){
                        filePath = storageReference.child(messagePushID + ".mp3");
                        file = new File(Objects.requireNonNull(imageUri.getPath()));
                        uri = imageUri;
                    }else if (isImage.equals("pdf")){
                        filePath = storageReference.child(messagePushID + ".pdf");
                        file = new File(Objects.requireNonNull(imageUri.getPath()));
                        uri = imageUri;
                    }else if (isImage.equals("docx")){
                        filePath = storageReference.child(messagePushID + ".docx");
                        file = new File(Objects.requireNonNull(imageUri.getPath()));
                        uri = imageUri;
                    }else if (isImage.equals("xlsx")){
                        filePath = storageReference.child(messagePushID + ".xlsx");
                        file = new File(Objects.requireNonNull(imageUri.getPath()));
                        uri = imageUri;
                    }

                    if (size <= 10){
                        filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){
                                try {
                                    Uri dowloadUrl = task.getResult();
                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", dowloadUrl.toString());
                                    messageTextBody.put("name", imageUri.getLastPathSegment());
                                    messageTextBody.put("type", typeFile);
                                    messageTextBody.put("from", messagemSenderID);
                                    messageTextBody.put("to", messageReciverID);
                                    messageTextBody.put("messageID", messagePushID);
                                    messageTextBody.put("time", saveCutrrentTime);
                                    messageTextBody.put("date", saveCurrentData);
                                    messageTextBody.put("sender", messagemSenderID);
                                    messageTextBody.put("receiver", messageReciverID);
                                    messageTextBody.put("isseen",false);
                                    messageTextBody.put("type_responder","");
                                    messageTextBody.put("msg_responder_nombre_responder","");
                                    messageTextBody.put("msg_sender_responder","");


                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if (task.isSuccessful()) {

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(SendImageUsersActivity.this, "Archivo Enviado", Toast.LENGTH_SHORT).show();
                                                        filePath = null;
                                                        enviandoUsuario.setText("!!Exito tu contacto  " + mUser.getName() + " recivio el archivo correctamente ");
                                                    }
                                                });

                                            } else {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(SendImageUsersActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

//                                    loadingBar.dismiss();
                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SendImageUsersActivity.this, "" + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    System.out.println("MENSAJE " + e);
                                }

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                final String msg = messageNotify;

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(messagemSenderID);
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                        if (notify) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
                                                }
                                            });
                                        }
                                        notify = false;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                loadingBar.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SendImageUsersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                enviandoUsuario.setText("!!Error al enviar a  " + mUser.getName() + " archivo pesado para enviar \n " +
                                        "por el momento solo puedes enviar archivos que no superen los 10MB ");
                                Toast.makeText(SendImageUsersActivity.this, "Porfavor selecciona un archivo con peso de 10MB como Maximo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.cancel();
                    mCreate.setEnabled(true);
                }
            });
        }
    }

    private void unsegundo(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}