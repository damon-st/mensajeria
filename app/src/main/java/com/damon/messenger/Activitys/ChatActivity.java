package com.damon.messenger.Activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anstrontechnologies.corehelper.AnstronCoreHelper;
import com.bhargavms.dotloader.DotLoader;
import com.bumptech.glide.Glide;
import com.damon.messenger.Adapters.GifEditText;
import com.damon.messenger.Adapters.MessageAdapter;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Model.Messages;
import com.damon.messenger.Notifications.APIService;
import com.damon.messenger.Notifications.Client;
import com.damon.messenger.Notifications.Data;
import com.damon.messenger.Notifications.MyResponse;
import com.damon.messenger.Notifications.Sender;
import com.damon.messenger.Notifications.Token;
import com.damon.messenger.R;
import com.damon.messenger.call.newcall.BaseActivity;
import com.damon.messenger.call.newcall.CallScreenActivity;
import com.damon.messenger.call.newcall.SinchService;
import com.damon.messenger.call.videocall.CallScreenActivityVideo;
import com.damon.messenger.call.videocall.Datos;
import com.damon.messenger.editorimagen.EditImageActivity;
import com.damon.messenger.interfaces.EstaFocusMsg;
import com.damon.messenger.interfaces.OnClickListener;
import com.damon.messenger.interfaces.VideoPlaying;
import com.damon.messenger.interfaces.onClickResMsg;
import com.damon.messenger.util.AES;
import com.damon.messenger.util.MarqueTextView;
import com.damon.messenger.util.Tools;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.iceteck.silicompressorr.Util;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.SinchError;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity implements GiphyDialogFragment.GifSelectionListener, SinchService.StartFailedListener, onClickResMsg,
        EstaFocusMsg, VideoPlaying {

    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    Bundle bundle = new Bundle();
    String directory;
    ImageView imagen;
    File fileImagen;
    public static final int GALLERY_PICTURE = 1;
    private DotLoader dotLoader;
    private static final int REC_CODE_SPEECH_INPUT = 100;//esto es para la deteccion de voz

    private Dialog dialog;
    private ImageView galeria,word,pdf,excel,video,close_respuesta;
    private static final int REQUEST_CORD_PERMISSION = 332;

    private String idGif;

    private String archivoSalidad =null;
    private ProgressDialog mProgress;

    private String messageReciverID, messageReciverName, messageReciverImage, messagemSenderID;
    private TextView userName,date_msg;
    private CircleImageView userImage;
    private MarqueTextView userLassSenn;

    private Toolbar ChatToolbar;
    Bitmap bitmap;

    private ImageButton  sendeFilesButton;
    private AppCompatEditText messageInputText;
    private FloatingActionButton sendMessageButton,btn_last_msg;

    private DatabaseReference RootRef;

    private FirebaseAuth mAuth;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView usersMessagesList;

    private String saveCutrrentTime, saveCurrentData;
    private String checker = "", myUrl = "";
    private StorageTask uploadTask;//encaraga de enviar archivos base de datos FIREBASE
    private Uri fileUri;

    private ProgressDialog loadingBar;
    private StorageReference storageReference;
    DatabaseReference reference,escribiendoRef;
    APIService apiService;

    boolean notify = false;

    private FirebaseUser fuser;

    private String currentUserID;
    private String sTime;

    private String  calledBy="", typeArchivo;
    private DatabaseReference UsersRef;

    String escribir = "no";

    private MediaPlayer mediaPlayer,mpReceiver;


    ImageView llamar, videos ,btn_camera;

    private MediaRecorder grabacion;
    private boolean scrolledDate;


    ValueEventListener seenListener;
    Intent intent;
    private RecordButton imagenGrabarAudio;
    private RecordView recordView ;
    private String salir;
    private ImageView btnEmoji;

    Bitmap thumn_bitmap;


    AnstronCoreHelper coreHelper;
    List<String> arrayFechas;
    ArrayList<String> arrayArchivosUsers = new ArrayList<>();


    private SwipeRefreshLayout mRefreshLayout;
    public  static int TOTAL_ITEMS_TO_LOAD = 10;
    private  int mCurrentPage =1;

    private int itemPost = 0;

    private String mLastKey = "";
    private String mPrevKey = "",fecha;

    private List<String> listKeyDelete;

    GifEditText textogif;

    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    private boolean sonar = false;
    private ProgressDialog mSpinner;
    private String nombreReceiver,nombresender,imageReceiver,imageSender;
    private String fechaConectado;

    GetMessages getMessages = new GetMessages();

    private TextView nombre_user_responder,message_responder;
    private LinearLayout responder_linar_layout;
    private ImageView image_responder;
    private boolean respuesta = false;

    private final int PHOTO_EDITOR_REQUEST_CODE = 231;// Any integer value as a request code.


    private Date fechaEnviar;
    private long fechaTiempo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fechaEnviar = new Date();
        fechaTiempo = fechaEnviar.getTime();


        escribiendoRef = FirebaseDatabase.getInstance().getReference().child("Escribiendo");
        //inisiclaimso el giphy
        Giphy.INSTANCE.configure(ChatActivity.this,"gNkg9wKhhwakDD1CpFNakqSxLaXLDa6H",false);

        listKeyDelete = new ArrayList<>();
        mRefreshLayout = findViewById(R.id.swiperefresh);

        dialog = new Dialog(this);//instanciamos la vista en la cuela va amostrar el dialogo

//        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
//
//
//        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, 1000);
        }

        arrayFechas = new ArrayList<>();

        actionModeCallback = new ActionModeCallback();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        messageReciverID = getIntent().getExtras().get("userid").toString();

        if (notify == true) {

            messageReciverName = getIntent().getExtras().get("name").toString();
            messageReciverImage = getIntent().getExtras().get("image").toString();
        } else {

        }
        mAuth = FirebaseAuth.getInstance();
        messagemSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        InitializeControls();

        userName.setText(messageReciverName);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(messageReciverImage).resize(47,47).into(userImage);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                notify = true;
                if (respuesta){
                    SendMessageResputa();
                }else {
                    SendMessage();
                }

            }
        });

        DisplayLastSeen();//metodo para mostrar los datos del usuario en el actionbar

        //boton para llamar
//        llamar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this,R.style.AlertDialog)
//                        .setTitle(R.string.MENSAJEDEAVISO)
//                        .setMessage(R.string.mensajedeaviso)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//
//                                    Intent intent = new Intent(getApplicationContext(),CallingActivity.class);
//                                    intent.putExtra("id",messageReciverID);
//                                    intent.putExtra("senderID",messagemSenderID);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                    dialog.dismiss();
//
//                            }
//                        })
//                        .setIcon(R.drawable.ic_info_outline_black_24dp)
//                        .show();
//                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
//                textView.setScroller(new Scroller(ChatActivity.this));
//                textView.setVerticalScrollBarEnabled(true);
//                textView.setMovementMethod(new ScrollingMovementMethod());
//
//            }
//        });

        llamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ChatActivity.this,
                        android.Manifest.permission.RECORD_AUDIO) !=
                        PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission
                        (ChatActivity.this, android.Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions(ChatActivity.this,
                            new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                            1);
                }
//                Intent intent = new Intent(getApplicationContext(), Call2.class);
//                intent.putExtra("callerId", messagemSenderID);
//                intent.putExtra("recipientId", messageReciverID);
//                startActivity(intent);

//                Intent intent = new Intent(getApplicationContext(),CallingActivity.class);
//                intent.putExtra("id",messageReciverID);
//                intent.putExtra("senderID",messagemSenderID);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
                //startActivity(new Intent(ChatActivity.this, IniciarLogin.class));
                Datos.setDATOS("audio");
                relaizar("audio");
                try {
                    loginClicked();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1000);

                }


                Datos.setDATOS("video");
                relaizar("video");
                try {
                    callButtonClickedVideo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        sendeFilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codigo para crear las optiones apra enviar archivos
                DialogoInterfasGaleria();
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(messageReciverID);

        try {
            seenMessages(messageReciverID);//esto es para ver si ya vio el mensaje
        } catch (Exception e) {
            e.printStackTrace();
        }

        mProgress = new ProgressDialog(this);



        //referencia para cmaviar la imagen de fondo

        ImagenFondoUser();

      //  TalvezSoluciondeLasImagenes();//es el metodo para mostrar los mensajes en el recyvlerView
        coreHelper = new AnstronCoreHelper(this);


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentPage++;

                itemPost = 0;

                loadMoreMessages();

            }
        });



     //   textogif.onCreateInputConnection(new EditorInfo());

//        textogif = findViewById(R.id.textogif);

//        textogif.onCreateInputConnection(new EditorInfo());



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = MediaPlayer.create(ChatActivity.this,R.raw.send);
                mediaPlayer.setVolume(0.5f,0.5f);
                mpReceiver = MediaPlayer.create(ChatActivity.this,R.raw.receiver);
                mpReceiver.setVolume(0.2f,0.2f);
            }
        },1000);

        mSpinner = new ProgressDialog(this);

        SacarNombre();


        getMessages.execute();//es el metodo para mostrar los mensajes en el recyvlerView

    }

    private void SacarNombre(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference sacarNombre = FirebaseDatabase.getInstance().getReference().child("Users");
                sacarNombre.child(messagemSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            nombresender = dataSnapshot.child("name").getValue().toString();
                            imageSender = dataSnapshot.child("image").getValue().toString();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void ImagenFondoUser() {
     runOnUiThread(new Runnable() {
         @Override
         public void run() {
             final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Fondo");

             reference.child(messagemSenderID).addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(DataSnapshot dataSnapshot) {

                     if (dataSnapshot.child("image").exists()){

                         String image = dataSnapshot.child("image").getValue().toString();

                         try {
                             Picasso.get().load(image).into(imagen);
                         }catch (Exception e){
                             e.printStackTrace();
                         }


                     }else {
//                    Picasso.get().load(R.drawable.fondoverde).into(imagen);
                     }
                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });
         }
     });
    }


    private void loadMoreMessages() {

        DatabaseReference messageRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                listKeyDelete.add(dataSnapshot.getKey());

                if (!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPost++,messages);
                }else {
                    mPrevKey = mLastKey;
                }

                if (itemPost ==1){

                    mLastKey = messageKey;
                }




                Log.d("TOTLKEYS","LAST KEY:" + mLastKey+ "| Prev key:" + mPrevKey+ "| Messga jkey"+ messageKey);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.notifyDataSetChanged();
                    }
                });


//                usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());
//                        usersMessagesList.scrollToPosition(messagesList.size()-1);

                mRefreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);

                try {
                    if (messages.getType().equals("image") || messages.getType().equals("mp4")
                            || messages.getType().equals("gif")){
                        arrayArchivosUsers.add(messages.getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Messages messages = dataSnapshot.getValue(Messages.class);
//                String key = dataSnapshot.getKey();
//                int index = listKeyDelete.indexOf(key);
//                try {
//                    messagesList.set(index,messages);
//                    messageAdapter.notifyDataSetChanged();
//                    if (sonar && messages.getReceiver().equals(FirebaseAuth.getInstance().getUid())){
//                        mpReceiver.start();
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = listKeyDelete.indexOf(dataSnapshot.getKey());
                try {
                    listKeyDelete.remove(index);
                    messagesList.remove(index);
                    messageAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void startRecord(){
        checker ="audio";
        notify = true;
        if (grabacion == null){
            archivoSalidad = Environment.getExternalStorageDirectory().getAbsolutePath() ;
            archivoSalidad += "/Grabacion.mp3";
            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
//            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            grabacion.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            grabacion.setOutputFile(archivoSalidad);

            try {
                grabacion.prepare();
                grabacion.start();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ChatActivity.this, "Error al grabar...", Toast.LENGTH_SHORT).show();
            }
//            imagenGrabarAudio.setImageResource(R.drawable.ic_terminargrabar);
        }
//        else if (grabacion !=null){
//
//        }
    }

    private void stopRecord(){

        try{
            if (grabacion !=null){
                grabacion.stop();
                grabacion.reset();
                grabacion.release();
                grabacion = null;

                uploadAudio();

            }else {
                Toast.makeText(this, "No audio", Toast.LENGTH_SHORT).show();
            }

        }catch(RuntimeException stopException){
            //handle cleanup here
            System.out.println("ERROR---"+stopException.getMessage());
            grabacion.reset();
            Toast.makeText(getApplicationContext(), "Stop Recording Error :" + stopException.getMessage(), Toast.LENGTH_LONG).show();

        }


//            imagenGrabarAudio.setImageResource(R.drawable.ic_grabar);
    }


    private void DialogoInterfasGaleria() {

        // le asignamos una vista la cual es el yaout creado
        dialog.setContentView(R.layout.enviararchivosladialogo);
        galeria = dialog.findViewById(R.id.iconogaleria);//utiliozamos las variable creadas utilizamos la isntancia de dialogo para buscar el id de los imageView
        word = dialog.findViewById(R.id.iconoword);
        pdf = dialog.findViewById(R.id.iconopdf);
        excel = dialog.findViewById(R.id.btn_excel);
        video = dialog.findViewById(R.id.videos);



        galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //esta sera para imgenes
               checker = "mp3";
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("audio/*");
                startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
            }
        });
        word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //esta sera para archivos Word
                checker = "docx";
                Intent word = new Intent();
                word.setAction(Intent.ACTION_GET_CONTENT);
                word.setType("application/msword");
                startActivityForResult(word.createChooser(word, "Select WORD File"), 438);
            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //esta sera para archivos pdf
                checker = "pdf";
                Intent pdf = new Intent();
                pdf.setAction(Intent.ACTION_GET_CONTENT);
                pdf.setType("application/pdf");
                startActivityForResult(pdf.createChooser(pdf, "Select PDF File"), 438);
            }
        });
        excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "xlsx";
                Intent excels = new Intent();
                excels.setAction(Intent.ACTION_GET_CONTENT);
                excels.addCategory(Intent.CATEGORY_OPENABLE);
                excels.setType("application/vnd.ms-excel");
                startActivityForResult(excels.createChooser(excels, "Select PDF File"), 438);
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="mp4";
                if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
                }else {
                    Intent excels = new Intent();
                    excels.setAction(Intent.ACTION_GET_CONTENT);
                    excels.addCategory(Intent.CATEGORY_OPENABLE);
                    excels.setType("video/mp4");
                    startActivityForResult(excels.createChooser(excels, "Select PDF File"), 438);
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//recuperamos la vista asignamso un bacjgfroiu
        dialog.show();//mostramos la ventana al precionar el boton
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permiso Necesario");
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.Ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    private void uploadAudio(){
        //aqui es para enviar audio
        mProgress.setMessage("Enviando Audio");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();
        storageReference = FirebaseStorage.getInstance().getReference().child("audio");
        final StorageReference filePath = storageReference.child(messagemSenderID+".mp3");

        fileUri = Uri.fromFile(new File(archivoSalidad));
        filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    mProgress.dismiss();
                    Toast.makeText(ChatActivity.this, "Exito", Toast.LENGTH_SHORT).show();

                    if (checker.equals("audio")){

                        storageReference = FirebaseStorage.getInstance().getReference().child("audio");
                        final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                        final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                        DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                                .child(messagemSenderID).child(messageReciverID).push();

                        final String messagePushID = userMessagerKeyRef.getKey();

                        final StorageReference filePath = storageReference.child(messagePushID + ".mp3");


                        uploadTask = filePath.putFile(fileUri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    try {

                                        Uri dowloadUrl = task.getResult();
                                        myUrl = dowloadUrl.toString();

                                        Map messageTextBody = new HashMap();
                                        messageTextBody.put("message", myUrl);
                                        messageTextBody.put("name", fileUri.getLastPathSegment());
                                        messageTextBody.put("type", checker);
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
                                        messageTextBody.put("position",0);
                                        messageTextBody.put("msgImage","");


                                        Map messageBodyDetails = new HashMap();
                                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                        messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                } else {

                                                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                                messageInputText.setText("");
                                            }
                                        });


                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                                        final String msg = "Te a enviado un audio";

                                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                                if (notify) {
                                                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
                                                }
                                                notify = false;
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    } catch (Exception e) {
                                        Toast.makeText(ChatActivity.this, "" + e, Toast.LENGTH_LONG).show();
                                        System.out.println("MENSAJE " + e);
                                    }


                                }
                            }
                        });




                    }

                }else {
                    mProgress.dismiss();
                    Toast.makeText(ChatActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }



//    private void seenMessages(final String userid){
//        RootRef = FirebaseDatabase.getInstance().getReference("Messages");
//        seenListener = RootRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Messages chat = snapshot.getValue(Messages.class);
//                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("isseen", true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }



    private void InitializeControls() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingBar = new ProgressDialog(ChatActivity.this);
                ChatToolbar = findViewById(R.id.chat_toolbar);
                setSupportActionBar(ChatToolbar);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowCustomEnabled(true);

                LayoutInflater layoutInflater = (LayoutInflater) ChatActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View actionbarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
                actionBar.setCustomView(actionbarView);

                ChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

                responder_linar_layout = findViewById(R.id.linear_responder);
                nombre_user_responder = findViewById(R.id.nombreUsuario);
                message_responder = findViewById(R.id.texto_responder);
                image_responder = findViewById(R.id.imagen_responder);

                userImage = findViewById(R.id.custom_profile_IMAGE);
                userName = findViewById(R.id.custom_profile_name);
                userLassSenn = findViewById(R.id.custom_user_last_seen);

                btn_last_msg = findViewById(R.id.btn_last_msg);
                sendMessageButton = findViewById(R.id.enviar_message_button);
                messageInputText = findViewById(R.id.input_message);
                sendeFilesButton = findViewById(R.id.enviar_files_button);
                close_respuesta = findViewById(R.id.close_respuesta);

                imagenGrabarAudio = findViewById(R.id.grabarAudio);
                recordView = findViewById(R.id.record_view);
                btnEmoji = findViewById(R.id.btn_emoji);
                btn_camera = findViewById(R.id.btn_camera);

                messageAdapter = new MessageAdapter(messagesList,ChatActivity.this,ChatActivity.this,ChatActivity.this,ChatActivity.this::isPlaying);
                usersMessagesList = findViewById(R.id.private_messages_list_of_users);
                linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                usersMessagesList.setLayoutManager(linearLayoutManager);
                usersMessagesList.setAdapter(messageAdapter);

                usersMessagesList.addItemDecoration(new StickyRecyclerHeadersDecoration(messageAdapter));

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        usersMessagesList.addItemDecoration(dividerItemDecoration);
                imagen = findViewById(R.id.imagen_cambiar);

                llamar = findViewById(R.id.llamada);
                videos = findViewById(R.id.video);
                date_msg = findViewById(R.id.fecha_msg);


                final int  i [] = {R.drawable.fondoverde};

                dotLoader = findViewById(R.id.dot_loader);
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker = "image";
                CropImage.activity(fileUri).start(ChatActivity.this);
            }
        });

        btnEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mostrarGif();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });



        usersMessagesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ){
                    scrolledDate = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int  currentItems = linearLayoutManager.getChildCount();
                int  totalItems = linearLayoutManager.getItemCount();
                int  scrollOutitems = linearLayoutManager.findFirstVisibleItemPosition();
                if (scrolledDate &&(currentItems + scrollOutitems == totalItems)){
//                    for (String da : arrayFechas){
//                        System.out.println("fechas" + da);
//                        date_msg.setText(da);
//                    }
//                    new ActualizarFechas().execute();
                    mostarBtnLast();
                    // actualizar();
                    scrolledDate = false;
                }
            }
        });
        //   usersMessagesList.setBackgroundResource(i[0]);

//        ImageView imageView  = findViewById(R.id.fondo);

//        imageView.setImageResource(R.drawable.profile_activity);



        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
//        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
//        saveCurrentData = currentData.format(calendar.getTime());

        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            saveCurrentData = dateFormat.format(date.getTime());
        }
        catch (Exception e) {
            e.printStackTrace();
        }



        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());


        messageInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, final int after) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (after > 0) {
//                                    if (dotLoader.getVisibility() != View.VISIBLE) {
//                                        dotLoader.setVisibility(View.VISIBLE);
//                                    }
                            sendMessageButton.setVisibility(View.VISIBLE);
                            imagenGrabarAudio.setVisibility(View.INVISIBLE);
                            sendeFilesButton.setVisibility(View.GONE);

                            crearEscribiendo(messagemSenderID,messageReciverID,"si");

                        } else {
//                            if (dotLoader.getVisibility() != View.INVISIBLE) {
//                                dotLoader.setVisibility(View.INVISIBLE);
//                            }
                            sendMessageButton.setVisibility(View.INVISIBLE);
                            imagenGrabarAudio.setVisibility(View.VISIBLE);
                            sendeFilesButton.setVisibility(View.VISIBLE);
                            crearEscribiendo(messagemSenderID,messageReciverID,"no");
                        }
                    }
                });
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        messageInputText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                iniciarEntradaVoz();
                return false;
            }
        });


        escribiendoRef.child(messagemSenderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String sender = dataSnapshot.child("senderID").getValue().toString();
                    String receiver = dataSnapshot.child("receiverID").getValue().toString();
                    String escribiendo = dataSnapshot.child("escribiendo").getValue().toString();

//                    Toast.makeText(ChatActivity.this, ""+fuser.getUid(), Toast.LENGTH_SHORT).show();
                    if (receiver.equals(fuser.getUid()) && escribiendo.equals("si")){

                        if (dotLoader.getVisibility() != View.VISIBLE) {
                            dotLoader.setVisibility(View.VISIBLE);
                        }
                    }else {
                        if (dotLoader.getVisibility() != View.INVISIBLE) {
                            dotLoader.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        messageAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onItemClick(View view, Messages msg, int pos) {
                if (messageAdapter.getSelectedItemCount() > 0){
                    enableActionMode(pos);
                }else {
                    Messages messages = messageAdapter.getItem(pos);

                }
            }

            @Override
            public void onItemLongClick(View view, Messages msg, int pos) {
                enableActionMode(pos);
            }
        });


        imagenGrabarAudio.setRecordView(recordView);
        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                if (!checkPermissionFromDevice()){
                    btnEmoji.setVisibility(View.INVISIBLE);
                    sendeFilesButton.setVisibility(View.INVISIBLE);
                    btn_camera.setVisibility(View.INVISIBLE);
                    sendMessageButton.setVisibility(View.INVISIBLE);

                    startRecord();

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null){
                        vibrator.vibrate(100);
                    }
                }else {
                    requestPermission();
                }

            }

            @Override
            public void onCancel() {
                try {

                    try {
                        grabacion.stop();
                        grabacion.reset();
                        grabacion.release();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinish(long recordTime) {
                btnEmoji.setVisibility(View.VISIBLE);
                sendeFilesButton.setVisibility(View.VISIBLE);
                btn_camera.setVisibility(View.VISIBLE);
                sendMessageButton.setVisibility(View.VISIBLE);

                //Stop Recording..
                try {
                    sTime = getTime((int) recordTime/1000);
                    stopRecord();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onLessThanSecond() {
                btnEmoji.setVisibility(View.VISIBLE);
                sendeFilesButton.setVisibility(View.VISIBLE);
                btn_camera.setVisibility(View.VISIBLE);
                sendMessageButton.setVisibility(View.VISIBLE);
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                btnEmoji.setVisibility(View.VISIBLE);
                sendeFilesButton.setVisibility(View.VISIBLE);
                btn_camera.setVisibility(View.VISIBLE);
                sendMessageButton.setVisibility(View.VISIBLE);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(usersMessagesList);

    }


    boolean mostarBtn;
    private void mostarBtnLast() {
        btn_last_msg.setVisibility(View.VISIBLE);

        if (btn_last_msg.getVisibility() == View.VISIBLE){
            btn_last_msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());
                    btn_last_msg.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public  void crearEscribiendo(String senderUid, String receiverID,String  escribiendo){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("senderID",senderUid);
        hashMap.put("receiverID",receiverID);
        hashMap.put("escribiendo",escribiendo);

        escribiendoRef.child(receiverID).updateChildren(hashMap);
    }


    Messages archiveMessae = new Messages();
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null){
                        vibrator.vibrate(100);
                    }
                    archiveMessae = messagesList.get(position);
                    MostarParaResponder(archiveMessae,position);
                    System.out.println("archivo"+archiveMessae.getMessage());
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(ChatActivity.this,c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(ChatActivity.this,R.color.colorPrimaryDark))
                    .addSwipeLeftActionIcon(R.drawable.ic_responder)
                    .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(),android.R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    String msgResponder;
    String type;
    String nombre_responder;
    int posicionContestar;
    private void MostarParaResponder(Messages archiveMessae, int position) {
        responder_linar_layout.setVisibility(View.VISIBLE);
        type = archiveMessae.getType();
        String user = archiveMessae.getSender();
        respuesta = true;
        posicionContestar = position;

        if (user.equals(messagemSenderID)){
            nombre_user_responder.setText("TU");
            nombre_responder  = nombresender;
        }else if (user.equals(messageReciverID)){
            nombre_user_responder.setText("Responder a "+nombreReceiver);
            nombre_responder  = nombreReceiver;
        }

        if (type.equals("respuesta")){
            try {
                type = "text";
                msgResponder =  aes.decrypt(archiveMessae.getMessage());
                message_responder.setVisibility(View.VISIBLE);
                message_responder.setText(msgResponder);
                image_responder.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (type.equals("text")){
            try {
                image_responder.setVisibility(View.GONE);
                message_responder.setVisibility(View.VISIBLE);
                msgResponder = aes.decrypt(archiveMessae.getMessage());
                message_responder.setText(msgResponder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (type.equals("image")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load(msgResponder).into(image_responder);
        }else if (type.equals("mp4")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.icon_video).into(image_responder);
        }else if (type.equals("gif")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Glide.with(this).load(msgResponder).override(100,100).into(image_responder);
        }else if (type.equals("docx")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.wordiconodialogo).into(image_responder);
        }else if (type.equals("mp3")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db").into(image_responder);
        }else if (type.equals("xlsx")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.excel_logo).into(image_responder);
        }else if (type.equals("pdf")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.pdficonodialogo).into(image_responder);
        }else if (type.equals("audio")){
            msgResponder = archiveMessae.getMessage();
            image_responder.setVisibility(View.VISIBLE);
            message_responder.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.play_button).into(image_responder);
        }

        close_respuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                respuesta = false;
                msgResponder = "";
                responder_linar_layout.setVisibility(View.GONE);
            }
        });


    }

    private void SendMessageResputa() {
        loadingBar.setTitle("Enviando Respusta Mensaje");//aqui saldra el dialog bar que es un visor de tiempo para que vea el usuario
        loadingBar.setMessage("Por favor espera...");// ya que mostrara el progreso
        loadingBar.setCanceledOnTouchOutside(false);// aqui no permitira al usuario tocar la pantalla asta que aya terminado
        loadingBar.show();//aqui sera para que se pueda mostrar el show permite eso
        sonar = true;
        String messageText = messageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Debes escribir el mesnaje", Toast.LENGTH_SHORT).show();
        } else {

            String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
            String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

            DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                    .child(messagemSenderID).child(messageReciverID).push();

            String messagePushID = userMessagerKeyRef.getKey();

            String msgEncryp = EncrypMessage(messageText);
//            String encypMsg = EncrypMessage(msgResponder);

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", msgEncryp);
            messageTextBody.put("type", "respuesta");
            messageTextBody.put("from", messagemSenderID);
            messageTextBody.put("to", messageReciverID);
            messageTextBody.put("messageID", messagePushID);
            messageTextBody.put("time", saveCutrrentTime);
            messageTextBody.put("date", saveCurrentData);
            messageTextBody.put("sender", messagemSenderID);
            messageTextBody.put("receiver", messageReciverID);
            messageTextBody.put("isseen",false);
            messageTextBody.put("type_responder",type);
            messageTextBody.put("msg_responder_nombre_responder",nombre_responder);
            messageTextBody.put("msg_sender_responder",msgResponder);
            messageTextBody.put("position",posicionContestar);
            messageTextBody.put("msgImage","");
            messageTextBody.put("fecha",fechaTiempo);



            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
            messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                        mediaPlayer.start();
                        loadingBar.dismiss();
                        respuesta = false;
                        responder_linar_layout.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                    messageInputText.setText("");
                }
            });

            final String msg = messageText;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Contacts contacts = dataSnapshot.getValue(Contacts.class);
                    if (notify) {
                        sendNotifiaction(messageReciverID, contacts.getName(), msg);
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


//    private void escribiendo(){
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
//
//        reference.child(messageReciverID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child("contact").exists()){
//                    final HashMap<String ,Object> escribiendo = new HashMap<>();
//                    escribiendo.put("contact",escribir);
//
//                    reference.child(messagemSenderID)
//                            .updateChildren(escribiendo)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                    if (task.isSuccessful()){
//                                        final HashMap<String ,Object> escribiendo = new HashMap<>();
//                                        escribiendo.put("contact",escribir);
//                                        reference.child(messageReciverID)
//                                                .updateChildren(escribiendo);
//                                    }
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void iniciarEntradaVoz() {

        //para el aduio a texto
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());//aqui recuerpar la vboz que tenga conficifguar en el dfispditivo movil
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hola dime algo wey");
        try {
            startActivityForResult(intent,REC_CODE_SPEECH_INPUT);
        }catch (ActivityNotFoundException e){

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_STORAGE_READ_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Gracias aora ya puedes descargar e enviar archivos", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Por favor conseda los permisos", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Gracias ahora ya puedes buscar archivos", Toast.LENGTH_SHORT).show();
                }else {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, 1000);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //sera encargada de cargar las imgenes para enviar
        //esto es el resultado del audio cargado a texto
        if (requestCode == REC_CODE_SPEECH_INPUT && resultCode ==RESULT_OK && null!=data){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            messageInputText.setText(result.get(0));
        }

        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK){
            fileUri = data.getData();
            if (checker.equals("fondo")){
                loadingBar.setTitle("Cambiando Fondo de Chat");//aqui saldra el dialog bar que es un visor de tiempo para que vea el usuario
                loadingBar.setMessage("Por favor espera...");// ya que mostrara el progreso
                loadingBar.setCanceledOnTouchOutside(false);// aqui no permitira al usuario tocar la pantalla asta que aya terminado
                loadingBar.show();//aqui sera para que se pueda mostrar el show permite eso
                RelativeLayout relativeLayout = findViewById(R.id.cambiarfondo);
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(
                        fileUri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();


                directory = fileUri.getPath().toString();

                bitmap = BitmapFactory.decodeFile(filePath);
                imagen.setImageBitmap(bitmap);


                System.out.println("Directorio"+directory);

                storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");

                final StorageReference filePaths = storageReference.child(messagemSenderID + "." + "jpg");


//                File tumb_filePath = new File(fileUri.getPath());
//
//
//                try {
//                    bitmap = new Compressor(this)
//                            .setMaxWidth(200)
//                            .setMaxHeight(200)
//                            .setQuality(80)
//                            .compressToBitmap(tumb_filePath);
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
//                final  byte[] thumb_byte =byteArrayOutputStream.toByteArray();

                final File file = new File(SiliCompressor.with(this)
                        .compress(FileUtils.getPath(this,fileUri),
                                new File(this.getCacheDir(),"temp")));
                Uri uri = Uri.fromFile(file);

               uploadTask = filePaths.putFile(uri);



                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();
                        }
                        return filePaths.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            myUrl = task.getResult().toString();
                            loadingBar.dismiss();
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        myUrl = uri.toString();
                        Map messageTextBody = new HashMap();
                        messageTextBody.put("image", myUrl);
                        messageTextBody.put("id",messagemSenderID);
                        RootRef.child("Fondo").child(messagemSenderID).updateChildren(messageTextBody);
                        fileUri = null;
                    }
                });

//                filePaths.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//
//                        if (task.isSuccessful()){
//                            Map messageTextBody = new HashMap();
//                            messageTextBody.put("image", task.getResult().getDownloadUrl().toString());
//                            messageTextBody.put("id",messagemSenderID);
//                            RootRef.child("Fondo").child(messagemSenderID).updateChildren(messageTextBody);
//                            loadingBar.dismiss();
//                        }
//                    }
//                });

//                imagen.buildDrawingCache();
//                Bitmap bitmap = imagen.getDrawingCache();
//
//                /***** COMPARTIR IMAGEN *****/
//                try {
//                    File file = new File(getCacheDir(), bitmap + ".png");
//                    FileOutputStream fOut = null;
//                    fOut = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//                    fOut.flush();
//                    fOut.close();
//                    file.setReadable(true, false);
//                    Uri i  = Uri.fromFile(file);
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

            }

        }
        if (requestCode == 438 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            loadingBar.setTitle("Enviando Archivo");//aqui saldra el dialog bar que es un visor de tiempo para que vea el usuario
            loadingBar.setMessage("Por favor espera, enviando Archivo...");// ya que mostrara el progreso
            loadingBar.setCanceledOnTouchOutside(false);// aqui no permitira al usuario tocar la pantalla asta que aya terminado
            loadingBar.show();//aqui sera para que se pueda mostrar el show permite eso

            fileUri = data.getData();



            if (!checker.equals("image") && !checker.equals("mp4") && !checker.endsWith("mp3")) {
                //aqui es para archivos
                notify = true;
                sonar = true;
                storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                        .child(messagemSenderID).child(messageReciverID).push();

                final String messagePushID = userMessagerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);

 /*               codigo de la otra libreria
//                final File file = new File(SiliCompressor.with(this)
//                        .compress(FileUtils.getPath(this,fileUri),
//                        new File(this.getCacheDir(),"temp")));
//
//                Uri uri = Uri.fromFile(file);
//                filePath.child(coreHelper.getFileNameFromUri(uri)).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                */


//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileUri.getPath());
//                System.out.println(file);
////                if (!file.isFile())return;
//                System.out.println(fileUri.getPath().indexOf(0));
//                System.out.println("Name: "+ file.getTotalSpace());
//                System.out.println(getFileSizeBytes(file));
//                System.out.println(getFileSizeKiloBytes(file));
//                System.out.println(getFileSizeMegaBytes(file));

                Cursor returnCursor =
                        getContentResolver().query(fileUri, null, null, null, null);
                /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                float valor  =returnCursor.getLong(sizeIndex);
                int s = Math.round(valor);
                int da = s / (1024 * 1024);
                System.out.println(valor);
                System.out.println(da);
                returnCursor.close();
                if (da <= 10){
                        filePath.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                if (task.isSuccessful()) {
                                    Uri dowloadUrl = task.getResult();
                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", dowloadUrl.toString());
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", checker);
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
                                    messageTextBody.put("position",0);
                                    messageTextBody.put("msgImage","");
                                    messageTextBody.put("fecha",fechaTiempo);




                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                    RootRef.updateChildren(messageBodyDetails);
                                    loadingBar.dismiss();
                                    mediaPlayer.start();
                                    fileUri = null;
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                //  file.delete();
                            }
                        });
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        final String msg = "Te a enviado un documento";

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                if (notify) {
                                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                }else {
                    loadingBar.dismiss();
                    Toast.makeText(this, "Archivo muy Grande maximo 10 MB", Toast.LENGTH_LONG).show();
                }

            }else if (checker.equals("mp3")){
                //aqui es para archivos
                notify = true;
                sonar = true;
                storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                        .child(messagemSenderID).child(messageReciverID).push();

                final String messagePushID = userMessagerKeyRef.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);


                Cursor returnCursor =
                        getContentResolver().query(fileUri, null, null, null, null);
                /*
                 * Get the column indexes of the data in the Cursor,
                 * move to the first row in the Cursor, get the data,
                 * and display it.
                 */
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                float valor  =returnCursor.getLong(sizeIndex);
                int s = Math.round(valor);
                int da = s / (1024 * 1024);
                System.out.println(valor);
                System.out.println(da);
                returnCursor.close();
                try {
                    typeArchivo = Util.getFilePath(this, fileUri);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                if (da <= 10){
                    if (typeArchivo.endsWith(".mp3")|| typeArchivo.endsWith(".mpeg") || typeArchivo.endsWith(".aac")||typeArchivo.endsWith(".wav") ){

                        filePath.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                if (task.isSuccessful()) {
                                    Uri dowloadUrl = task.getResult();
                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", dowloadUrl.toString());
                                    messageTextBody.put("name", fileUri.getLastPathSegment());
                                    messageTextBody.put("type", checker);
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
                                    messageTextBody.put("position",0);
                                    messageTextBody.put("msgImage","");
                                    messageTextBody.put("fecha",fechaTiempo);


                                    Map messageBodyDetails = new HashMap();
                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                                    messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                                    RootRef.updateChildren(messageBodyDetails);
                                    loadingBar.dismiss();
                                    mediaPlayer.start();
                                    fileUri = null;
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                //  file.delete();
                            }
                        });



                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

                        final String msg = "Te a enviado un Audio";

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                if (notify) {
                                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
                                }
                                notify = false;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }else {
                        Toast.makeText(this, "Por favor Seleciona Un video", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    loadingBar.dismiss();
                    Toast.makeText(this, "Archivo muy Grande maximo 10 MB", Toast.LENGTH_LONG).show();
                }

            } else  if (checker.equals("mp4")){
                //aqui es para archivos
                notify = true;
                sonar = true;
//                storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
                final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;
                  Intent intent  = new Intent(this,TrimmerActivity.class);
                  intent.putExtra("video_path", com.damon.trimmervideo.utils.FileUtils.getPath(this,fileUri));
                  intent.putExtra("duration",getMediaDuration(fileUri));
                  intent.putExtra("messageSenderRef",messageSenderRef);
                  intent.putExtra("messageReciverRef", messageReciverRef);
                  intent.putExtra("messageReciverID",messageReciverID);
                  intent.putExtra("messagemSenderID",messagemSenderID);
                  intent.putExtra("saveCutrrentTime",saveCutrrentTime);
                  intent.putExtra("saveCurrentData",saveCurrentData);
                  startActivity(intent);
                  loadingBar.dismiss();
                  fileUri = null;



//
//                DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
//                        .child(messagemSenderID).child(messageReciverID).push();
//
//                final String messagePushID = userMessagerKeyRef.getKey();
//
//                final StorageReference filePath = storageReference.child(messagePushID + "." + checker);
//
//
//                Cursor returnCursor =
//                        getContentResolver().query(fileUri, null, null, null, null);
//                /*
//                 * Get the column indexes of the data in the Cursor,
//                 * move to the first row in the Cursor, get the data,
//                 * and display it.
//                 */
//                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
//                returnCursor.moveToFirst();
//                float valor  =returnCursor.getLong(sizeIndex);
//                int s = Math.round(valor);
//                int da = s / (1024 * 1024);
//                System.out.println(valor);
//                System.out.println(da);
//                returnCursor.close();
//                System.out.println(fileUri.getPath().endsWith(".mp4"));
//                System.out.println(fileUri.getPath());
//                System.out.println(fileUri.getUserInfo());
//                try {
//                    typeArchivo = Util.getFilePath(this, fileUri);
//                } catch (URISyntaxException e) {
//                    e.printStackTrace();
//                }
//
//                if (da <= 10){
//                    if (typeArchivo.endsWith(".mp4")){
//
//                        filePath.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                            @Override
//                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                if (!task.isSuccessful()){
//                                    throw task.getException();
//                                }
//                                return filePath.getDownloadUrl();
//                            }
//                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.isSuccessful()) {
//                                    Uri dowloadUrl = task.getResult();
//                                    Map messageTextBody = new HashMap();
//                                    messageTextBody.put("message", dowloadUrl.toString());
//                                    messageTextBody.put("name", fileUri.getLastPathSegment());
//                                    messageTextBody.put("type", checker);
//                                    messageTextBody.put("from", messagemSenderID);
//                                    messageTextBody.put("to", messageReciverID);
//                                    messageTextBody.put("messageID", messagePushID);
//                                    messageTextBody.put("time", saveCutrrentTime);
//                                    messageTextBody.put("date", saveCurrentData);
//                                    messageTextBody.put("sender", messagemSenderID);
//                                    messageTextBody.put("receiver", messageReciverID);
//                                    messageTextBody.put("isseen",false);
//
//
//                                    Map messageBodyDetails = new HashMap();
//                                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//                                    messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);
//
//                                    RootRef.updateChildren(messageBodyDetails);
//                                    loadingBar.dismiss();
//                                    mediaPlayer.start();
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                loadingBar.dismiss();
//                                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                //  file.delete();
//                            }
//                        });
//
//
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//                        final String msg = "Te a enviado un Video";
//
//                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                        reference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Contacts contacts = dataSnapshot.getValue(Contacts.class);
//                                if (notify) {
//                                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
//                                }
//                                notify = false;
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }else {
//                        Toast.makeText(this, "Por favor Seleciona Un video", Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    loadingBar.dismiss();
//                    Toast.makeText(this, "Archivo muy Grande maximo 10 MB", Toast.LENGTH_LONG).show();
//                }
            }else {
                Toast.makeText(this, "No Seleccionado.Error..", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            loadingBar.setTitle("Enviando Imagen");//aqui saldra el dialog bar que es un visor de tiempo para que vea el usuario
            loadingBar.setMessage("Por favor espera...");// ya que mostrara el progreso
            loadingBar.setCanceledOnTouchOutside(false);// aqui no permitira al usuario tocar la pantalla asta que aya terminado
            loadingBar.show();//aqui sera para que se pueda mostrar el show permite eso
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            fileUri = result.getUri();//aqui recueramos



            notify = true;
            sonar = true;
//                storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
            final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
            final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;
            Intent intent  = new Intent(this, EditImageActivity.class);
            intent.putExtra("path", fileUri.toString());
            intent.putExtra("messageSenderRef",messageSenderRef);
            intent.putExtra("messageReciverRef", messageReciverRef);
            intent.putExtra("messageReciverID",messageReciverID);
            intent.putExtra("messagemSenderID",messagemSenderID);
            intent.putExtra("saveCutrrentTime",saveCutrrentTime);
            intent.putExtra("saveCurrentData",saveCurrentData);
            startActivity(intent);
            loadingBar.dismiss();
            fileUri = null;

//            storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
//            final String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
//            final String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;
//
//            DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
//                    .child(messagemSenderID).child(messageReciverID).push();
//
//            final String messagePushID = userMessagerKeyRef.getKey();
//
//            final StorageReference filePath = storageReference.child(messagePushID + ".jpg");
//
//
//            final File file = new File(SiliCompressor.with(this)
//                    .compress(FileUtils.getPath(this,fileUri),
//                            new File(this.getCacheDir(),"temp")));
//
//            Uri uri = Uri.fromFile(file);
//
//            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()){
//                        throw  task.getException();
//                    }
//                    return filePath.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()){
//                        try {
//                            Uri dowloadUrl = task.getResult();
//                            Map messageTextBody = new HashMap();
//                            messageTextBody.put("message", dowloadUrl.toString());
//                            messageTextBody.put("name", fileUri.getLastPathSegment());
//                            messageTextBody.put("type", checker);
//                            messageTextBody.put("from", messagemSenderID);
//                            messageTextBody.put("to", messageReciverID);
//                            messageTextBody.put("messageID", messagePushID);
//                            messageTextBody.put("time", saveCutrrentTime);
//                            messageTextBody.put("date", saveCurrentData);
//                            messageTextBody.put("sender", messagemSenderID);
//                            messageTextBody.put("receiver", messageReciverID);
//                            messageTextBody.put("isseen",false);
//                            messageTextBody.put("type_responder","");
//                            messageTextBody.put("msg_responder_nombre_responder","");
//                            messageTextBody.put("msg_sender_responder","");
//
//
//                            Map messageBodyDetails = new HashMap();
//                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
//                            messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);
//
//                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task) {
//                                    if (task.isSuccessful()) {
//
//                                        Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
//                                        loadingBar.dismiss();
//                                        mediaPlayer.start();
//                                        fileUri = null;
//
//                                    } else {
//
//                                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                                        loadingBar.dismiss();
//                                    }
//                                    file.delete();
//                                    messageInputText.setText("");
//                                }
//                            });
//                        } catch (Exception e) {
//                            Toast.makeText(ChatActivity.this, "" + e, Toast.LENGTH_LONG).show();
//                            System.out.println("MENSAJE " + e);
//                        }
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//                        final String msg = "Te a enviado una imagen";
//
//                        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                        reference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Contacts contacts = dataSnapshot.getValue(Contacts.class);
//                                if (notify) {
//                                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
//                                }
//                                notify = false;
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    loadingBar.dismiss();
//                    Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }


    }

    private static String getFileSizeMegaBytes(File file) {
        return (double) file.length() / (1024 * 1024) + " mb";
    }

    private static String getFileSizeKiloBytes(File file) {
        return (double) file.length() / 1024 + "  kb";
    }

    private static String getFileSizeBytes(File file) {
        return file.length() + " bytes";
    }


    private void currentUser(String messageReciverID) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", messageReciverID);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        crearEscribiendo(messagemSenderID,messageReciverID,"no");
        currentUser(messageReciverID);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        RootRef.removeEventListener(seenListener);
        currentUser("none");
//        reference.removeEventListener(seenListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("offline");
        }

    }

    private void DisplayLastSeen() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //metodo para la ultima coneccion para que sea en el chat
                RootRef.child("Users").child(messageReciverID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //condicion para crear la notificacionj
                                if (dataSnapshot.child("userSate").hasChild("state")) {

                                    String state = dataSnapshot.child("userSate").child("state").getValue().toString();
                                    String date = dataSnapshot.child("userSate").child("date").getValue().toString();
                                    String time = dataSnapshot.child("userSate").child("time").getValue().toString();
                                    fechaConectado = time + " - " + date;
                                    if (state.equals("online")) {

                                        userLassSenn.setText("conectado");
                                    } else if (state.equals("offline")) {

                                        userLassSenn.setText("Ultima vez conectado " + date + "\n" + time);
                                    }
                                } else {
                                    userLassSenn.setText("Desconectado");
                                }
                                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                                userName.setText(contacts.getName());
                                if (dataSnapshot.child("image").exists()) {
                                    final String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();
                                    nombreReceiver = dataSnapshot.child("name").getValue().toString();
                                    imageReceiver = dataSnapshot.child("image").getValue().toString();
                                    String  status = dataSnapshot.child("status").getValue().toString();
                                    Picasso.get().load(retrieveProfileImage).resize(47,47).into(userImage);
                                    userImage.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ChatActivity.this,UserProfileActivity.class);
                                            intent.putExtra("url",retrieveProfileImage);
                                            intent.putExtra("name",nombreReceiver);
                                            intent.putExtra("status",status);
                                            intent.putExtra("tiempo",fechaConectado);
                                            intent.putExtra("messagemSenderID",messagemSenderID);
                                            intent.putExtra("messageReciverID",messageReciverID);
//                                    intent.putStringArrayListExtra("msg",arrayArchivosUsers);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });

    }

    @Override
    protected void onStop() {
        if (fuser.getUid() != null){
            crearEscribiendo(messagemSenderID,messageReciverID,"no");
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        seenMessages(messageReciverID);
        updateUserStates("online");

    //    checkForRecevingCall();//para la video llamada
    }


    private void TalvezSoluciondeLasImagenes(){

//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//
//            }
//        }.start();
    }

    private void SendMessage() {
        loadingBar.setTitle("Enviando Mensaje");//aqui saldra el dialog bar que es un visor de tiempo para que vea el usuario
        loadingBar.setMessage("Por favor espera...");// ya que mostrara el progreso
        loadingBar.setCanceledOnTouchOutside(false);// aqui no permitira al usuario tocar la pantalla asta que aya terminado
        loadingBar.show();//aqui sera para que se pueda mostrar el show permite eso
        sonar = true;
        String messageText = messageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Debes escribir el mesnaje", Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
                    String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

                    DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                            .child(messagemSenderID).child(messageReciverID).push();

                    String messagePushID = userMessagerKeyRef.getKey();

                    String msgEncryp = EncrypMessage(messageText);

                    Map messageTextBody = new HashMap();
                    messageTextBody.put("message", msgEncryp);
                    messageTextBody.put("type", "text");
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
                    messageTextBody.put("position",0);
                    messageTextBody.put("msgImage","");
                    messageTextBody.put("fecha",fechaTiempo);



                    Map messageBodyDetails = new HashMap();
                    messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                    messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                    RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
//                        Toast.makeText(ChatActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                                mediaPlayer.start();
                                loadingBar.dismiss();
                            } else {
                                Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            messageInputText.setText("");
                        }
                    });

                    final String msg = messageText;

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Contacts contacts = dataSnapshot.getValue(Contacts.class);
                            if (notify) {
                                sendNotifiaction(messageReciverID, contacts.getName(), msg);
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
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
                            Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "Nuevo Mensaje",
                                    messageReciverID);

                            Sender sender = new Sender(data, token.getToken());

                            apiService.sendNotification(sender)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(ChatActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
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

    private void seenMessages(final String userid) throws Exception{
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reference = RootRef.child("Messages")
                        .child(messagemSenderID).child(userid);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                if (snapshot.child("isseen").exists()){
                                    Messages messages = snapshot.getValue(Messages.class);
                                    if (messages !=null && messages.getReceiver() != null &&
                                            messages.getSender() != null &&
                                            messages.getReceiver().equals(fuser.getUid())
                                        //&& messages.getSender().equals(userid)
//                   || messages.getSender().equals(fuser.getUid()) && messages.getReceiver().equals(userid)
                                    ) {
                                        HashMap<String , Object> hashMap = new HashMap<>();
                                        hashMap.put("isseen",true);
                                        snapshot.getRef().updateChildren(hashMap);
                                    }
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });

                reference = RootRef.child("Messages")
                        .child(messageReciverID).child(messagemSenderID);

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                if (snapshot.child("isseen").exists()){
                                    Messages messages = snapshot.getValue(Messages.class);
                                    if (messages !=null && messages.getReceiver() != null &&
                                            messages.getSender() != null &&
                                            messages.getReceiver().equals(fuser.getUid())
//                   || messages.getSender().equals(fuser.getUid()) && messages.getReceiver().equals(userid)
                                    ) {
                                        HashMap<String , Object> hashMap = new HashMap<>();
                                        hashMap.put("isseen",true);
                                        snapshot.getRef().updateChildren(hashMap);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });
            }
        });
    }

    private void checkForRecevingCall() {
//cdodigo para las llamdas

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat");

        reference.child(messagemSenderID)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ringing")) {

                            calledBy = dataSnapshot.child("ringing").getValue().toString();

                            Intent intent = new Intent(ChatActivity.this, CallingActivity.class);
                            intent.putExtra("id",calledBy);
                            intent.putExtra("senderID",messagemSenderID);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.chat_cambiar_fondo,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId() == R.id.chat_cambiar_fondo){
             Toast.makeText(this, "Bien seleciona una imagen para ponerla de fondo ", Toast.LENGTH_SHORT).show();
             cambiarfondo();
         }
         return true;

    }

    private void cambiarfondo() {

        checker = "fondo";
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_PICTURE);

    }

    private void deletecalls(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("call").child(messageReciverID);
        reference.removeValue();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        reference.removeEventListener(seenListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            crearEscribiendo(messagemSenderID,messageReciverID,"no");
            updateUserStates("offline");
            try {
                deletecalls();
            }catch (Exception e){
                e.printStackTrace();
            }
            if (mpReceiver!=null && mediaPlayer!=null){
                try {
                    mpReceiver.stop();
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            if (grabacion != null){
                try {
                    grabacion.stop();
                    grabacion.reset();
                    grabacion.release();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            getMessages.cancel(true);
            deleteCache(this);
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        crearEscribiendo(messagemSenderID,messageReciverID,"no");
        if (exoPlayer != null ){
            exoPlayer.release();
        }
        finish();
    }

    private void updateUserStates(String state) {
        //aqui sera para poner al ora aslo ususario su iltimas conexcion
        String saveCutrrentTime, saveCurrentData;

        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());

        //aqui creamos una lista con la llave y objeto para mandar ala base de datos Firebase
        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCutrrentTime);
        onlineStateMap.put("date", saveCurrentData);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();
        //aqui creamos la referencia con la vase de datos y nombre dela nueva tabla o dato para labase de datos
        RootRef.child("Users").child(currentUserID).child("userSate")
                .updateChildren(onlineStateMap);



    }


    //metodo para abrir los gif
    private   void mostrarGif() throws InstantiationException, IllegalAccessException {
        GiphyDialogFragment.class.newInstance().show(getSupportFragmentManager(),"giphy_dialog");
    }

    @Override
    public void didSearchTerm(@NotNull String s) {
        //Callback for search terms
    }

    @Override
    public void onDismissed() {
        //Your user dismissed the dialog without selecting a GIF

    }

    @Override
    public void onGifSelected(@NotNull Media media, @org.jetbrains.annotations.Nullable String s) {
        //Your user tapped a GIF
        idGif = media.getId();
        enviarGif(idGif);
    }

    private void enviarGif(String idGif) {
        notify = true;
        sonar = true;
        System.out.println("https://media.giphy.com/media/"+idGif+"/giphy.gif");
        String  id = "https://media.giphy.com/media/"+idGif+"/giphy.gif";
        String messageSenderRef = "Messages/" + messagemSenderID + "/" + messageReciverID;
        String messageReciverRef = "Messages/" + messageReciverID + "/" + messagemSenderID;

        DatabaseReference userMessagerKeyRef = RootRef.child("Messages")
                .child(messagemSenderID).child(messageReciverID).push();

        String messagePushID = userMessagerKeyRef.getKey();

        Map messageTextBody = new HashMap();
        messageTextBody.put("message", id);
        messageTextBody.put("type", "gif");
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
        messageTextBody.put("position",0);
        messageTextBody.put("msgImage","");
        messageTextBody.put("fecha",fechaTiempo);


        Map messageBodyDetails = new HashMap();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

        RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ChatActivity.this, "Gif Enviado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        final String msg = "Te a enviado un gif";

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contacts contacts = dataSnapshot.getValue(Contacts.class);
                if (notify) {
                    sendNotifiaction(messageReciverID, contacts.getName(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    AES aes = new AES("lv39eptlvuhaqqsr");
    private String  EncrypMessage(String msg){

        try {
            String cadenaEncryp = aes.encrypt(msg);
            return cadenaEncryp;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onClickResMsg(int position,View view) {
        mCurrentPage++;

        itemPost = 0;

        TOTAL_ITEMS_TO_LOAD = TOTAL_ITEMS_TO_LOAD*3;
        loadMoreMessages();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                usersMessagesList.smoothScrollToPosition(position);
            }
        },1000);

    }

    @Override
    public void onFocusChangeMsg(View view, boolean focus, Messages messages) {
        System.out.println("isFocus" + focus);
        System.out.println("Mensajes" + messages);
//        if (focus){
//            date_msg.setText(messages.getDate());
//        }
    }

    SimpleExoPlayer exoPlayer;

    @Override
    public void isPlaying(SimpleExoPlayer simpleExoPlayer, boolean isplaying) {
        if (simpleExoPlayer != null ){
            exoPlayer = simpleExoPlayer;
        }
    }


    private class ActionModeCallback implements ActionMode.Callback{

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(ChatActivity.this,R.color.successText);
            mode.getMenuInflater().inflate(R.menu.delete_msg_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.delete_msg_multi){
                //code aqui
                sonar = false;
                deleteMessage();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            messageAdapter.clearSelections();
            actionMode = null;
            Tools.setSystemBarColor(ChatActivity.this,R.color.colorPrimary);
        }
    }

    private void deleteMessage() {
        sonar = false;
        List<Integer> seletectItemPostions = messageAdapter.getSelectedItems();
        for (int i = seletectItemPostions.size()-1; i>=0; i--){
            messageAdapter.DeleteMessages(messageAdapter.messageID(seletectItemPostions.get(i)),
                    messageAdapter.messageTO(seletectItemPostions.get(i)),
                    messageAdapter.messageFrom(seletectItemPostions.get(i)));
        }
        Toast.makeText(this, "Exito al Eliminar", Toast.LENGTH_SHORT).show();
//        finish();
    }

    private void enableActionMode(int position){
        if (actionMode == null){
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        messageAdapter.toggleSelection(position);
        int count = messageAdapter.getSelectedItemCount();
        if (count ==0){
            actionMode.finish();
        }else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked() throws Exception{
//        mLoginName.setText(FirebaseAuth.getInstance().getUid());
        String userName = messagemSenderID;

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!userName.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            openPlaceCallActivity();
        }
    }

    private void openPlaceCallActivity() {
//        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
//        startActivity(mainActivity);
        mSpinner.dismiss();
        callButtonClicked();
    }

    private void showSpinner() {
        mSpinner.setTitle("Conectando");
        mSpinner.setMessage("Porfavor Espera...");
        mSpinner.show();
    }

    private void callButtonClicked() {
        String userName = messageReciverID;
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
//            com.sinch.android.rtc.calling.Call call = getSinchServiceInterface().callUserVideo(userName);
            com.sinch.android.rtc.calling.Call call = getSinchServiceInterface().callUser(userName);
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(this, "El servicio no se inicia. Intente detener el servicio e iniciarlo de nuevo antes "
                        + "hacer una llamada.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(this,CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra("name",nombreReceiver);
            callScreen.putExtra("receiverID",messageReciverID);
            callScreen.putExtra("image",imageReceiver);
            startActivity(callScreen);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
        }
    }

    private void callButtonClickedVideo() throws Exception {
        String userName = messageReciverID;
        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        com.sinch.android.rtc.calling.Call call = getSinchServiceInterface().callUserVideo(userName);
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, CallScreenActivityVideo.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("name",nombreReceiver);
        startActivity(callScreen);
    }

    private void relaizar(String type){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("type",type);
        hashMap.put("name",nombresender);
        hashMap.put("image",imageSender);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("call").child(messageReciverID);
        reference.updateChildren(hashMap);
    }


    private class ActualizarFechas extends AsyncTask<Void,Integer,Void>{


        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < arrayFechas.size() ; i++){
                unSegundo();
                int finalI = i;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        date_msg.setText(arrayFechas.get(finalI));
                    }
                });
                if (isCancelled())break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    private void unSegundo(){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void actualizar(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i< arrayFechas.size() ; i++ ){
                    unSegundo();
                    int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            date_msg.setText(arrayFechas.get(finalI));
                        }
                    });
                }
            }
        }.start();
    }

    private boolean checkPermissionFromDevice() {
        int write_external_strorage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_strorage_result == PackageManager.PERMISSION_DENIED || record_audio_result == PackageManager.PERMISSION_DENIED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_CORD_PERMISSION);
    }

    @SuppressLint("DefaultLocale")
    private String getHumanTimeText(long milliseconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    public String getTime(int seconds) {
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d", hr) + ":" + String.format("%02d", mn) + ":" + String.format("%02d", sec);
    }


    DatabaseReference messageRef;
    Query messageQuery;

    private class GetMessages extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            messageQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);

                    arrayFechas.add(messages.getDate());
                    listKeyDelete.add(dataSnapshot.getKey());
                    itemPost++;
                    if (itemPost ==1){
                        String messageKey = dataSnapshot.getKey();

                        mLastKey = messageKey;
                        mPrevKey = messageKey;
                    }

                    fecha = messages.getDate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                            usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());
                        }
                    });
//                        usersMessagesList.scrollToPosition(messagesList.size()-1);

                    mRefreshLayout.setRefreshing(false);
                    try {
                        if (messages.getType().equals("image") || messages.getType().equals("mp4")
                         || messages.getType().equals("gif")){
                            arrayArchivosUsers.add(messages.getMessage());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    String key = dataSnapshot.getKey();
                    int index = listKeyDelete.indexOf(key);
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messagesList.set(index,messages);
                                messageAdapter.notifyDataSetChanged();
                                if (sonar && messages.getReceiver().equals(FirebaseAuth.getInstance().getUid())){
                                    mpReceiver.start();
                                }
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    int index = listKeyDelete.indexOf(dataSnapshot.getKey());
                    try {
                        listKeyDelete.remove(index);
                        messagesList.remove(index);
                        messageAdapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            messageRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID);

            messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
            messageQuery.keepSynced(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    private int  getMediaDuration(Uri uriOfFile)  {
        MediaPlayer mp = MediaPlayer.create(this,uriOfFile);
        int duration = mp.getDuration();
        return  duration;
    }
}
