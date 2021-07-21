package com.damon.messenger.editorimagen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Notifications.APIService;
import com.damon.messenger.Notifications.Client;
import com.damon.messenger.Notifications.Data;
import com.damon.messenger.Notifications.MyResponse;
import com.damon.messenger.Notifications.Sender;
import com.damon.messenger.Notifications.Token;
import com.damon.messenger.R;
import com.damon.messenger.editorimagen.base.BaseActivity;
import com.damon.messenger.editorimagen.filters.FilterListener;
import com.damon.messenger.editorimagen.filters.FilterViewAdapter;
import com.damon.messenger.editorimagen.tools.EditingToolsAdapter;
import com.damon.messenger.editorimagen.tools.ToolType;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditImageActivity extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    private static final String TAG = EditImageActivity.class.getSimpleName();
    public  final String FILE_PROVIDER_AUTHORITY ="com.damon.messenger.provider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private Typeface mWonderFont;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this,this);
    private ConstraintLayout mRootView;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private boolean mIsFilterVisible;

    private String messagemSenderID,messageReciverID,saveCutrrentTime;
    private String messageReciverRef,messageSenderRef,messagePushID,saveCurrentData;
    private String path;
    private StorageReference storageReference;
    private DatabaseReference userMessagerKeyRef;
    private DatabaseReference RootRef;
    private StorageReference filePath ;
    private APIService apiService;
    private boolean notify;

    DatabaseReference reference ;
    private EditText edtmsgImg;
    private String msgImg = "";
    private Date fechaEnviado;
    private long fechaTiempo;


    @Nullable
    @VisibleForTesting
    Uri mSaveImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_edit_image);

        fechaEnviado = new Date();
        fechaTiempo = fechaEnviado.getTime();

        initViews();

        path = getIntent().getStringExtra("path");
        messagemSenderID = getIntent().getStringExtra("messagemSenderID");
        messageReciverID = getIntent().getStringExtra("messageReciverID");
        messageReciverRef = getIntent().getStringExtra("messageReciverRef");
        messageSenderRef = getIntent().getStringExtra("messageSenderRef");
        saveCutrrentTime = getIntent().getStringExtra("saveCutrrentTime");
        saveCurrentData = getIntent().getStringExtra("saveCurrentData");

        RootRef = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("Document Files");
        userMessagerKeyRef = RootRef.child("Messages").child(messagemSenderID).child(messageReciverID).push();
        messagePushID = userMessagerKeyRef.getKey();
        filePath = storageReference.child(messagePushID + "." + "png");
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        reference = FirebaseDatabase.getInstance().getReference();


        handleIntentImage(mPhotoEditorView.getSource());

//        mWonderFont = Typeface.createFromAsset(getAssets(), "beyond_wonderland.ttf");

        edtmsgImg = findViewById(R.id.edt_add_msg_img);
        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        LinearLayoutManager llmTools = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvTools.setLayoutManager(llmTools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);


        //Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build(); // build photo editor sdk

        mPhotoEditor.setOnPhotoEditorListener(this);

        //Set Image Dynamically
        // mPhotoEditorView.getSource().setImageResource(R.drawable.color_palette);

        if (path != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = Uri.parse(path);
                        System.out.println("path" + path);
                        System.out.println("uri"+uri);
                        final File tumb_filePath = new File(uri.getPath());
                        float valor  =tumb_filePath.length();
                        int s = Math.round(valor);
                        int size = s / (1024 * 1024);

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        if (size >=2) {
                            try {
                                bitmap = new Compressor(EditImageActivity.this)
                                        .setMaxWidth(bitmap.getWidth() / 2)
                                        .setMaxHeight(bitmap.getHeight() / 2)
                                        .setQuality(90)
                                        .compressToBitmap(tumb_filePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permiso Necesario");
            builder.setMessage(rationale);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(EditImageActivity.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    private void handleIntentImage(ImageView source) {
        Intent intent = getIntent();
        if (intent != null) {
            String intentType = intent.getType();
            if (intentType != null && intentType.startsWith("image/")) {
                Uri imageUri = intent.getData();
                if (imageUri != null) {
                    source.setImageURI(imageUri);
                }
            }
        }
    }

    private void initViews() {
        ImageView imgUndo;
        ImageView imgRedo;
        ImageView imgCamera;
        ImageView imgGallery;
        ImageView imgSave;
        ImageView imgClose;
        ImageView imgShare;

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);

        imgUndo = findViewById(R.id.imgUndo);
        imgUndo.setOnClickListener(this);

        imgRedo = findViewById(R.id.imgRedo);
        imgRedo.setOnClickListener(this);

        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(this);

        imgGallery = findViewById(R.id.imgGallery);
        imgGallery.setOnClickListener(this);

        imgSave = findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgClose = findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);

        imgShare = findViewById(R.id.imgShare);
        imgShare.setOnClickListener(this);

    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(rootView, inputText, styleBuilder);
                mTxtCurrentTool.setText(R.string.label_text);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onRemoveViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [" + viewType + "]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.imgUndo:
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                mPhotoEditor.redo();
                break;

            case R.id.imgSave:
                saveImage();
                break;

            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgShare:
                shareImage();
                break;

            case R.id.imgCamera:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;

            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_REQUEST);
                break;
        }
    }

    private void shareImage() {
        if (mSaveImageUri == null) {
            showSnackbar(getString(R.string.msg_save_image_to_share));
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, buildFileProviderUri(mSaveImageUri));
        startActivity(Intent.createChooser(intent, getString(R.string.msg_share_image)));
    }

    private Uri buildFileProviderUri(@NonNull Uri uri) {
        return FileProvider.getUriForFile(this,
                FILE_PROVIDER_AUTHORITY,
                new File(uri.getPath()));
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (ActivityCompat.checkSelfPermission(EditImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Permiso Necesario", 1001);
            }else {
                showLoading("Preparando para Enviar...");
//                File file;
//                String filePrefix = UUID.randomUUID().toString();
//                String fileExtn = ".jpg";
//                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
//                    file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+File.separator+getString(R.string.app_name));
//                    // folder = new File(getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath());
//
//                }else {
//                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+File.separator+getString(R.string.app_name));
//                    // folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
//                }
//
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
//
//                File dest = new File(file, filePrefix + fileExtn);
                File file = new File(Environment.getExternalStorageDirectory()
                        + File.separator + ""
                        + System.currentTimeMillis() + ".png");


                try {
                    file.createNewFile();

                    SaveSettings saveSettings = new SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build();

                    mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
//                            hideLoading();
                            showSnackbar("Imagen Guardada Preparando Para Enviar...");
                            mSaveImageUri = Uri.fromFile(new File(imagePath));
                            mPhotoEditorView.getSource().setImageURI(mSaveImageUri);
                            senImage(mSaveImageUri);
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            hideLoading();
                            showSnackbar("Fallo al guardar la imagen");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    hideLoading();
                    showSnackbar(e.getMessage());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mPhotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        mPhotoEditorView.getSource().setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
        mTxtCurrentTool.setText(R.string.label_brush);
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
    }

    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
    }

    @Override
    public void isPermissionGranted(boolean isGranted, String permission) {
        if (isGranted) {
            saveImage();
        }
    }

    private void showSaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_save_image));
        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveImage();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Descartar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();

    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onToolSelected(ToolType toolType) {
        switch (toolType) {
            case BRUSH:
                mPhotoEditor.setBrushDrawingMode(true);
                mTxtCurrentTool.setText(R.string.label_brush);
                mPropertiesBSFragment.show(getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                break;
            case TEXT:
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                        styleBuilder.withTextColor(colorCode);

                        mPhotoEditor.addText(inputText, styleBuilder);
                        mTxtCurrentTool.setText(R.string.label_text);
                    }
                });
                break;
            case ERASER:
                mPhotoEditor.brushEraser();
                mTxtCurrentTool.setText(R.string.label_eraser_mode);
                break;
            case FILTER:
                mTxtCurrentTool.setText(R.string.label_filter);
                showFilter(true);
                break;
            case EMOJI:
                mEmojiBSFragment.show(getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;
            case STICKER:
                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
                break;
        }
    }


    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        } else if (!mPhotoEditor.isCacheEmpty()) {
            showSaveDialog();
        } else {
            super.onBackPressed();
        }
    }

    void  senImage(Uri fileUri) {
        new Thread(){
            @Override
            public void run() {
                super.run();

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mProgressDialog.show();
//                    }
//                });

                msgImg = edtmsgImg.getText().toString();
                notify = true;
                final File file = new File(SiliCompressor.with(EditImageActivity.this)
                    .compress(FileUtils.getPath(EditImageActivity.this,fileUri),
                            new File(EditImageActivity.this.getCacheDir(),"temp")));

            Uri uri = Uri.fromFile(file);

                filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
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
                            messageTextBody.put("type", "image");
                            messageTextBody.put("from", messagemSenderID);
                            messageTextBody.put("to", messageReciverID);
                            messageTextBody.put("messageID", messagePushID);
                            messageTextBody.put("time", saveCutrrentTime);
                            messageTextBody.put("date", saveCurrentData);
                            messageTextBody.put("sender", messagemSenderID);
                            messageTextBody.put("receiver", messageReciverID);
                            messageTextBody.put("isseen", false);
                            messageTextBody.put("type_responder","");
                            messageTextBody.put("msg_responder_nombre_responder","");
                            messageTextBody.put("msg_sender_responder","");
                            messageTextBody.put("position",0);
                            messageTextBody.put("msgImage",msgImg);
                            messageTextBody.put("fecha",fechaTiempo);



                            Map messageBodyDetails = new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
                            messageBodyDetails.put(messageReciverRef + "/" + messagePushID, messageTextBody);

                            RootRef.updateChildren(messageBodyDetails);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoading();
                                }
                            });



                            final String msg = "Te a enviado una imagen";

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(messagemSenderID);
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

                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(EditImageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        //  file.delete();
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
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.code() == 200) {
                                                if (response.body().success != 1) {
                                                    Toast.makeText(EditImageActivity.this, "No se puedo enviar la notificacion lo sentimos!", Toast.LENGTH_SHORT).show();
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


}
