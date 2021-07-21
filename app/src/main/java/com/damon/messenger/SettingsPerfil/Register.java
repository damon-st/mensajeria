package com.damon.messenger.SettingsPerfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private ImageButton CreateAccountButton;
    private EditText UserEmail,UserPassword;
    private Button AlreadyHaveAccountLink;

    //Creamos un bojeto de la clase firebaseAuth que se encarga de la creacion
    private FirebaseAuth mAuth;
    private DatabaseReference RootReference;

    private Dialog dialog;

    private ProgressDialog loadignBar;

    private static final String APP_ID ="ca-app-pub-1691614301371531~7301440527";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);


        dialog  = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();//creamos una instancia con el objeto
        RootReference = FirebaseDatabase.getInstance().getReference();

        InitializeFields();
        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToLoginActivity();
            }
        });


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }
        });

        MobileAds.initialize(this,APP_ID);
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void CreateNewAccount() {
        //aqui cremaos la cuenta
        final String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Porfavor ingresa el correo",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Porfavor ingresa la contraseña",Toast.LENGTH_SHORT).show();

        }
        else {
            //metodos para creacion de dialogo para el usuario
            //un mensaje flotante con proseso
            loadignBar.setTitle("Creando Nueva Cuenta");
            loadignBar.setMessage("Porfavor espera,porque estamos creando tu cuenta....");
            loadignBar.setCanceledOnTouchOutside(true);
            loadignBar.show();

            //metodo para crear la cuenta con la conexion con firebase
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                assert firebaseUser != null;
                                String userid = firebaseUser.getUid();

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                //estas dos lineas que estan aqui son para enviar los datos ala
                                //base de datos para crear una base de datos con una clave llamada
                                //"Users"

                                String currentUserId = mAuth.getCurrentUser().getUid();
                                RootReference.child("Users").child(userid).setValue("");

                                RootReference.child("Users").child(userid).child("device_token")
                                        .setValue(deviceToken);



                                RootReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("email",email);
                                hashMap.put("name","Usuario");
                                hashMap.put("mystatus","");
                                hashMap.put("status","");
                                hashMap.put("image","https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/grupo.jpg?alt=media&token=b349eb42-c14d-4df5-9d35-a58821be11d4");
                                hashMap.put("device_token",deviceToken);

                                RootReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            SendToMainActivity();
                                            Toast.makeText(Register.this, "Cuenta Creada Coreectamente", Toast.LENGTH_SHORT).show();
                                            loadignBar.dismiss();
                                        }
                                    }
                                });



                            }else {
                                String messenge = task.getException().toString();
                                //Toast.makeText(Register.this, "Error:"+messenge, Toast.LENGTH_SHORT).show();
                                loadignBar.dismiss();//para que se pueda mostrar
                                System.out.println(messenge);
                                if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")){
                                    mostrarDialogo("Por favor Introduce una direccion de correo Electronica Valida");
                                }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]")){
                                    mostrarDialogo("Por favor Introduce una contraseña de minimo 6 caracteres ");
                                }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.")){
                                    mostrarDialogo("El correo ingresado ya esta en uso por favor revisalo e intenta nuevamente con otro correo electronico");
                                }else {
                                    mostrarDialogo(messenge);
                                }
                            }
                        }
                    });
        }
    }
    //metodo para ir al main activiti
    private void SendToMainActivity() {
        Intent MainIntent= new Intent(Register.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void InitializeFields() {

        UserEmail  = findViewById(R.id.email);
        UserPassword = findViewById(R.id.password);
        CreateAccountButton = findViewById(R.id.signup);
        AlreadyHaveAccountLink = findViewById(R.id.signin);

        loadignBar = new ProgressDialog(this);


    }
    private void SendToLoginActivity(){
        //metodo spara ir a crear ala activity login en caso de que si tenga una cuenta
        Intent LoginIntent= new Intent(Register.this, Login.class);
        startActivity(LoginIntent);
        finish();
    }

    private void mostrarDialogo(String mensaje){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_error);
        ImageView imageView = dialog.findViewById(R.id.imagen_error);

        Glide.with(Register.this).load(R.drawable.tenor).into(imageView);
        titulo.setText(mensaje);
        dialog.show();
    }
}
