package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.damon.messenger.editorimagen.EditImageActivity;
import com.damon.messenger.editorimagen.EmojiBSFragment;
import com.damon.messenger.editorimagen.PropertiesBSFragment;
import com.damon.messenger.editorimagen.StickerBSFragment;
import com.damon.messenger.editorimagen.TextEditorDialogFragment;
import com.damon.messenger.editorimagen.base.BaseActivity;
import com.damon.messenger.editorimagen.filters.FilterListener;
import com.damon.messenger.editorimagen.filters.FilterViewAdapter;
import com.damon.messenger.editorimagen.tools.EditingToolsAdapter;
import com.damon.messenger.editorimagen.tools.ToolType;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;
import ja.burhanrashid52.photoeditor.ViewType;

public class AddStory extends BaseActivity implements OnPhotoEditorListener,
        View.OnClickListener,
        PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        StickerBSFragment.StickerListener, EditingToolsAdapter.OnItemSelected, FilterListener {

    DatabaseReference reference;
    StorageReference storageReference;
    Uri myUri ;
    ProgressDialog progressDialog;
    Uri dowloadUrl;


    private static final String TAG = AddStory.class.getSimpleName();
    public  final String FILE_PROVIDER_AUTHORITY ="com.damon.messenger.provider";
    private PhotoEditor mPhotoEditor;
    private PhotoEditorView mphotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    private StickerBSFragment mStickerBSFragment;
    private TextView mTxtCurrentTool;
    private RecyclerView mRvTools, mRvFilters;
    private EditingToolsAdapter mEditingToolsAdapter = new EditingToolsAdapter(this);
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this,this);
    private ConstraintLayout mRootView;
    private static final int CAMERA_REQUEST =52;
    private static final int PICK_REQUEST = 53;
    private boolean mIsFilterVisible;
    private ConstraintSet mConstraintSet = new ConstraintSet();
    private EditText edtmsgImg;
    private String msgImg = "";

    @NonNull
    @VisibleForTesting
    Uri mSaveImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story2);

        initView();

        progressDialog = new ProgressDialog(this);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference =  FirebaseStorage.getInstance().getReference("story");

        mPropertiesBSFragment = new PropertiesBSFragment();
        mEmojiBSFragment = new EmojiBSFragment();
        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);
        mEmojiBSFragment.setEmojiListener(this);
        mPropertiesBSFragment.setPropertiesChangeListener(this);


        LinearLayoutManager tools = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRvTools.setLayoutManager(tools);
        mRvTools.setAdapter(mEditingToolsAdapter);

        LinearLayoutManager filters = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mRvFilters.setLayoutManager(filters);
        mRvFilters.setAdapter(mFilterViewAdapter);

        mPhotoEditor = new PhotoEditor.Builder(this,mphotoEditorView)
                .setPinchTextScalable(true)
                .build();

        mPhotoEditor.setOnPhotoEditorListener(this);


        CropImage.activity(myUri).start(AddStory.this);

    }

    private void initView(){
        ImageView imgUndo,imgRedo,imgCamera,imgGallery,imgSave,imgClose,imgShare;

        mphotoEditorView = findViewById(R.id.photoEditorView);
        mTxtCurrentTool = findViewById(R.id.txtCurrentTool);
        mRvTools = findViewById(R.id.rvConstraintTools);
        mRvFilters = findViewById(R.id.rvFilterView);
        mRootView = findViewById(R.id.rootView);
        edtmsgImg = findViewById(R.id.edt_add_msg_img);


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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case CAMERA_REQUEST:
                    mPhotoEditor.clearAllViews();
                    Bitmap photo =  (Bitmap) data.getExtras().get("data");
                    mphotoEditorView.getSource().setImageBitmap(photo);
                    break;
                case PICK_REQUEST:
                    try {
                        mPhotoEditor.clearAllViews();
                        Uri uri = data.getData();
                        Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                        mphotoEditorView.getSource().setImageBitmap(image);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    try {
                        mPhotoEditor.clearAllViews();
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        myUri  = result.getUri();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),myUri);
                        mphotoEditorView.getSource().setImageBitmap(bitmap);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void crear(String dowloadUrl, String myid) {
        DatabaseReference s = reference.child("Story").child(myid);
                            String storyid = s.push().getKey();
                            long timeend = System.currentTimeMillis()+86400000;//1 dia

                            HashMap<String ,Object> hashMap = new HashMap<>();
                            hashMap.put("imageurl",dowloadUrl);
                            hashMap.put("timestart", ServerValue.TIMESTAMP);
                            hashMap.put("timeend",timeend);
                            hashMap.put("storyid",storyid);
                            hashMap.put("userid",myid);
                            hashMap.put("msgImage",msgImg);

                            s.child(storyid).updateChildren(hashMap);
                            hideLoading();
                            finish();
    }

    private String getFileExtension(Uri uri){

        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.imgUndo:
                mPhotoEditor.undo();
                break;
            case  R.id.imgRedo:
                mPhotoEditor.redo();
                break;
            case  R.id.imgSave:
                saveImage();
                break;
            case R.id.imgClose:
                onBackPressed();
                break;
            case R.id.imgShare:
                shareImage();
                break;
            case R.id.imgCamera:
                Intent cameraIntet = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntet,CAMERA_REQUEST);
                break;
            case R.id.imgGallery:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_REQUEST);
                break;
        }
    }

    private void shareImage() {
    }

    private void saveImage() {

        if (requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            if (ActivityCompat.checkSelfPermission(AddStory.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            }else {

                showLoading("Preparando para publicar");

                File file = new File(Environment.getExternalStorageDirectory()+
                        File.separator + ""+
                        System.currentTimeMillis() + ".jpg");
                try {

                    file.createNewFile();

                    SaveSettings saveSettings = new SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build();

                    mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String s) {
                            showSnackbar("Imagen Guardada Preparando para pulicar");
                            mSaveImageUri = Uri.fromFile(new File(s));
                            mphotoEditorView.getSource().setImageURI(mSaveImageUri);
                            publicarImagen(mSaveImageUri);
                        }

                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideLoading();
                            showSnackbar("Fallo al guardar la imagen");
                        }
                    });


                }catch (Exception e){
                    e.printStackTrace();
                    hideLoading();
                    showSnackbar(e.getMessage());
                }
            }
        }


    }

    private void publicarImagen(Uri mSaveImageUri) {
        msgImg = edtmsgImg.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                +"."+getFileExtension(mSaveImageUri));

        final File file = new File(SiliCompressor.with(this)
                .compress(FileUtils.getPath(this,mSaveImageUri),
                        new File(this.getCacheDir(),"temp")));
        Uri uri = Uri.fromFile(file);
        UploadTask task = imageReference.putFile(uri);

        task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return  imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    try {
                        String myid = FirebaseAuth.getInstance().getUid();
                        dowloadUrl = task.getResult();
                        progressDialog.dismiss();

//                            DatabaseReference s = reference.child("Story").child(myid);
//
//                            String storyid = s.push().getKey();
//                            long timeend = System.currentTimeMillis()+86400000;//1 dia
//
//                            HashMap<String ,Object> hashMap = new HashMap<>();
//                            hashMap.put("imageurl",dowloadUrl);
//                            hashMap.put("timestart", ServerValue.TIMESTAMP);
//                            hashMap.put("timeend",timeend);
//                            hashMap.put("storyid",storyid);
//                            hashMap.put("userid",myid);
//
//                            s.child(storyid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task) {
//                                    if (task.isSuccessful()){
//                                        progressDialog.dismiss();
//                                        finish();
//                                    }else {
//                                        progressDialog.dismiss();
//                                        Toast.makeText(AddStory.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

                    } catch (Exception e) {
                        Toast.makeText(AddStory.this, "" + e, Toast.LENGTH_LONG).show();
                        System.out.println("MENSAJE " + e);
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(AddStory.this, "Error"+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                crear(uri.toString(),FirebaseAuth.getInstance().getUid());
            }
        });
    }

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
        mTxtCurrentTool.setText(R.string.label_emoji);
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
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
        mTxtCurrentTool.setText(R.string.label_sticker);
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

    private void showFilter(boolean isVisible) {
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
    public void onEditTextChangeListener(View view, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show(this,text,colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                styleBuilder.withTextColor(colorCode);

                mPhotoEditor.editText(view,inputText,styleBuilder);
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


    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permiso Necesario");
            builder.setMessage(rationale);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(AddStory.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton("Cancelar", null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible){
            showFilter(false);
            mTxtCurrentTool.setText(R.string.app_name);
        }else if (!mPhotoEditor.isCacheEmpty()){
            showSaveDialog();
        }else {
            super.onBackPressed();
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
}