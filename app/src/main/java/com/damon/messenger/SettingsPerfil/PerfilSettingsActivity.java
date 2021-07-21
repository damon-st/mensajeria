package com.damon.messenger.SettingsPerfil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.Activitys.ImageViewerActivity;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PerfilSettingsActivity extends AppCompatActivity {

    private CircleImageView prolifeImageView;
    private TextView fullNameEditText , userPhoneEditText;
    private TextView  closeTextBtn, saveTextButton,settings_correo;

    private FloatingActionButton prolifeChangeTextBtn ;

    private Uri imageUri;
    private String myUrl ="";
    private StorageReference storageProlifePictureRef;
    private String checker="";
    private StorageTask uploadTask;

    private Dialog dialog;
    private ImageButton edt_name,edt_status_user;
    private Button sing_out_count;


    String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();

    Bitmap  bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_settings);

        storageProlifePictureRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        dialog = new Dialog(this);

        prolifeImageView = findViewById(R.id.settings_prolife_image);
        fullNameEditText = findViewById(R.id.settings_full_name);
        userPhoneEditText = findViewById(R.id.settings_phone_numer);
        prolifeChangeTextBtn = findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings);
        saveTextButton= findViewById(R.id.update_settings);
        edt_name = findViewById(R.id.edt_nombre_user);
        edt_status_user = findViewById(R.id.edt_status_user);
        sing_out_count = findViewById(R.id.btn_log_out);

        settings_correo = findViewById(R.id.settings_correo);

        userInfoDisplay(prolifeImageView,fullNameEditText,userPhoneEditText,settings_correo);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    UserInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });

        prolifeChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";
                //Comience a recortar la actividad para la imagen adquirida previamente guardada en el dispositivo
                CropImage.activity(imageUri)
//                        .setAspectRatio(1,1)
                        .start(PerfilSettingsActivity.this);



            }
        });

        edt_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizardataDialog(fullNameEditText);
            }
        });

        edt_status_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizardataDialog(userPhoneEditText);
            }
        });

    }

    private void ActualizardataDialog(TextView type){
        dialog.setContentView(R.layout.dialog_perfil_update);
        MaterialEditText materialEditText = dialog.findViewById(R.id.nuevo_dato);
        Button confirmar, cancelar;
        confirmar = dialog.findViewById(R.id.confirmar_update);
        cancelar = dialog.findViewById(R.id.cancelar_update);



        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(materialEditText.getText().toString())){
                    Toast.makeText(PerfilSettingsActivity.this, "Deves llenar el campo caso contrario preciona el boton de cancelar", Toast.LENGTH_SHORT).show();
                }else {
                    type.setText(materialEditText.getText().toString());
                    dialog.dismiss();
                }
            }
        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void updateOnlyUserInfo() {

        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString().toLowerCase());
        userMap.put("mystatus",userPhoneEditText.getText().toString());
        userMap.put("status",userPhoneEditText.getText().toString());
        userMap.put("search",fullNameEditText.getText().toString().toLowerCase());
        userMap.put("id",auth);
//        userMap.put("image","https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/grupo.jpg?alt=media&token=b349eb42-c14d-4df5-9d35-a58821be11d4");
        userMap.put("contact","not saved");
        ref.child(auth).updateChildren(userMap);//esto es para acutlizar en la bvase de datos



        startActivity(new Intent( PerfilSettingsActivity.this, MainActivity.class));
        Toast.makeText(PerfilSettingsActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
        finish();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE&&resultCode ==RESULT_OK&&data!=null){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();//aqui recueramos
            prolifeImageView.setImageURI(imageUri);//aqui asignamos





        }else {
            Toast.makeText(this, "Error, Intenta Nuevamente", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PerfilSettingsActivity.this,PerfilSettingsActivity.class));
            finish();
        }
    }

    private void UserInfoSaved() {
        //metodopara guardar o sdatos
        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Nombre es obligatrio", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(this, "Telefono es obligatorio", Toast.LENGTH_SHORT).show();
        }else if (checker.equals("clicked")){
            uploadImage();
        }
    }

    private void uploadImage() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //subir iamgen e acutalizar
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Actualizando perfil");
        progressDialog.setMessage("Espera porfavor,actualizando");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri != null){
            final  StorageReference  fileRef= storageProlifePictureRef
                    .child(auth+".jpg");

            //utilizmaos esto apra la comprecion
            File tumb_filePath = new File(imageUri.getPath());


            try {
                bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .compressToBitmap(tumb_filePath);
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
            final  byte[] thumb_byte =byteArrayOutputStream.toByteArray();


           // uploadTask = fileRef.putFile(imageUri);
            uploadTask = fileRef.putBytes(thumb_byte);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri dowloadUri = task.getResult();
                        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap = new HashMap<>();
                        userMap.put("name",fullNameEditText.getText().toString().toLowerCase());
                        userMap.put("mystatus",userPhoneEditText.getText().toString());
                        userMap.put("image",dowloadUri.toString());
                        userMap.put("search",fullNameEditText.getText().toString().toLowerCase());
                        userMap.put("id",auth);
                        userMap.put("status",userPhoneEditText.getText().toString().toLowerCase());
                        userMap.put("contact","not saved");
                        // ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);//esto es para acutlizar en la bvase de datos
                        ref.child(auth).updateChildren(userMap);

                        progressDialog.dismiss();
                        startActivity(new Intent( PerfilSettingsActivity.this,MainActivity.class));
                        Toast.makeText(PerfilSettingsActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(PerfilSettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

//            uploadTask.continueWithTask(new Continuation() {
//                @Override
//                public Object then(@NonNull Task task) throws Exception {
//                    if (!task.isSuccessful()){
//                        throw  task.getException();
//                    }
//
//                    return fileRef.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task) {
//                    if (task.isSuccessful()){
//
//                        Uri downloadUri = task.getResult();
//                        myUrl = downloadUri.toString();
//
//                        // DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Users");
//                        String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
//
//                        HashMap<String,Object> userMap = new HashMap<>();
//                        userMap.put("name",fullNameEditText.getText().toString().toLowerCase());
//                        userMap.put("mystatus",userPhoneEditText.getText().toString());
//                        userMap.put("image",myUrl);
//                        userMap.put("search",fullNameEditText.getText().toString().toLowerCase());
//                        userMap.put("id",auth);
//                        userMap.put("status",userPhoneEditText.getText().toString().toLowerCase());
//                        userMap.put("contact","not saved");
//                        // ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);//esto es para acutlizar en la bvase de datos
//                        ref.child(auth).updateChildren(userMap);
//
//                        progressDialog.dismiss();
//                        startActivity(new Intent( PerfilSettingsActivity.this,MainActivity.class));
//                        Toast.makeText(PerfilSettingsActivity.this, "Perfil Actualizado", Toast.LENGTH_SHORT).show();
//                        finish();
//
//                    }else {
//                        progressDialog.dismiss();
//                        Toast.makeText(PerfilSettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

        }else {
            Toast.makeText(this, "imagen no esta seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(final CircleImageView prolifeImageView, final TextView fullNameEditText, final TextView userPhoneEditText, final TextView settings_correo) {

        // DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentonlineUser.getPhone());
        final String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UsersRef.child(auth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //primero verimificacmos si existe
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("image").exists()){
                        final String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString().toLowerCase();
                        String phone = dataSnapshot.child("mystatus").getValue().toString();
                        String email = dataSnapshot.child("email").getValue().toString();

                        Picasso.get().load(image).placeholder(R.mipmap.ic_launcher).into(prolifeImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        settings_correo.setText(email);

                        prolifeImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(PerfilSettingsActivity.this, ImageViewerActivity.class);
                                intent.putExtra("url",image);
                                startActivity(intent);
                            }
                        });

                    }
                    if (dataSnapshot.child("phone").exists()){
                        String  phone = dataSnapshot.child("phone").getValue().toString();

                        settings_correo.setText(phone);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
