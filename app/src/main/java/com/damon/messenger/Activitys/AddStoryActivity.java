package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class AddStoryActivity extends AppCompatActivity {

    private Uri mImageUri;
    private String myUrl ="";
    private StorageTask storageTask;
    private StorageReference storageReference;

    private Bitmap bitmap;
    private String filepath;
    String myid;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);
         myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference("story");
         reference = FirebaseDatabase.getInstance().getReference();
        CropImage.activity(mImageUri)
                .start(AddStoryActivity.this);

    }

    private String getFileExtension(Uri uri){

        ContentResolver resolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    private void publisherStory(){

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Subiendo");
        pd.show();

        if (mImageUri !=null){
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(mImageUri));



//            bitmap = BitmapFactory.decodeFile(mImageUri.getEncodedPath());
//
//            File tumb_filepath = new File(mImageUri.getPath());
//            try {
//                bitmap = new Compressor(this)
//                        .setMaxWidth(200)
//                        .setMaxHeight(200)
//                        .setQuality(100)
//                        .compressToBitmap(tumb_filepath);
//
//            }catch (IOException e){
//                e.printStackTrace();
//            }
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();




            final File file = new File(SiliCompressor.with(this)
                    .compress(FileUtils.getPath(this,mImageUri),
                            new File(this.getCacheDir(),"temp")));
            Uri uri = Uri.fromFile(file);

            storageTask = imageReference.putFile(uri);

            imageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        try {
                            Uri dowloadUrl = task.getResult();

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
//                            s.child(storyid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task) {
//                                    if (task.isSuccessful()){
//                                        pd.dismiss();
//                                        finish();
//                                    }else {
//                                        pd.dismiss();
//                                        Toast.makeText(AddStoryActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

                            pd.dismiss();
                            crear(dowloadUrl);

                        } catch (Exception e) {
                            Toast.makeText(AddStoryActivity.this, "" + e, Toast.LENGTH_LONG).show();
                            System.out.println("MENSAJE " + e);
                        }
                    }else {
                        pd.dismiss();
                        Toast.makeText(AddStoryActivity.this, "Error"+task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddStoryActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



//            storageTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()){
//                        throw task.getException();
//                    }
//                    return  imageReference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()){
//                        Uri dowloadUri = task.getResult();
//                        myUrl = dowloadUri.toString();
//
//                        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
//                                .child(myid);
//
//                        String storyid = reference.push().getKey();
//                        long timeend = System.currentTimeMillis()+86400000;//1 dia
//
//                        HashMap<String ,Object> hashMap = new HashMap<>();
//                        hashMap.put("imageurl",myUrl);
//                        hashMap.put("timestart", ServerValue.TIMESTAMP);
//                        hashMap.put("timeend",timeend);
//                        hashMap.put("storyid",storyid);
//                        hashMap.put("userid",myid);
//
//                        reference.child(storyid).setValue(hashMap);
//                        pd.dismiss();
//
//                        finish();
//                    }else {
//                        Toast.makeText(AddStoryActivity.this, "Fallo", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
        }else {
            Toast.makeText(this, "No hay imagen Seleccionadad", Toast.LENGTH_SHORT).show();
        }
    }

    private void crear(Uri dowloadUrl) {
        Toast.makeText(this, ""+dowloadUrl.toString(), Toast.LENGTH_SHORT).show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Story");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            mImageUri = result.getUri();

//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(
//                    mImageUri,filePathColumn,null,null,null
//            );
//            cursor.moveToFirst();
//
//            int columIndex = cursor.getColumnIndex(filePathColumn[0]);
//            filepath = cursor.getString(columIndex);
//            cursor.close();

            publisherStory();
        }else {
            Toast.makeText(this, "Ocurrio un problema", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
            finish();
        }
    }
}
