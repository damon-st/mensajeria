package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail,UserPassword;
    private TextView AlreadyHaveAccountLink;

    //Creamos un bojeto de la clase firebaseAuth que se encarga de la creacion
    private FirebaseAuth mAuth;
    private DatabaseReference RootReference;

    private ProgressDialog loadignBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


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
                                   hashMap.put("device_token",deviceToken);

                                   RootReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful()){
                                               SendToMainActivity();
                                               Toast.makeText(RegisterActivity.this, "Cuenta Creada Coreectamente", Toast.LENGTH_SHORT).show();
                                               loadignBar.dismiss();
                                           }
                                       }
                                   });



                               }else {
                                   String messenge = task.getException().toString();
                                   Toast.makeText(RegisterActivity.this, "Error:"+messenge, Toast.LENGTH_SHORT).show();
                                   loadignBar.dismiss();//para que se pueda mostrar
                               }
                           }
                       });
        }
    }
    //metodo para ir al main activiti
    private void SendToMainActivity() {
        Intent MainIntent= new Intent(RegisterActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void InitializeFields() {

        UserEmail  = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        CreateAccountButton = findViewById(R.id.register_button);
        AlreadyHaveAccountLink = findViewById(R.id.already_have_account_link);

        loadignBar = new ProgressDialog(this);


    }
    private void SendToLoginActivity(){
        //metodo spara ir a crear ala activity login en caso de que si tenga una cuenta
        Intent LoginIntent= new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(LoginIntent);
    }
}
