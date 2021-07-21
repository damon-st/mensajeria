package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;
import com.damon.messenger.SettingsPerfil.ResetPasswordActivity;
import com.damon.messenger.Telefono;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {


    //aqui crearemos la conexion con la base de datos para la autentificacion de inicio
   // private FirebaseUser currentUser;
    //creamos un firebaseAuth que se encarga de la autentificacion d inicio
    private FirebaseAuth mAuth;
    //creamos el dialogo que se mostrala el proceso de inicio al usuario
    private ProgressDialog loadignBar;
    private Button Loginbutton ,PhoneLoginButton;
    private MaterialEditText UserEmail,UserPassword;
    private TextView NeddNewYouAccountLink,ForgetPasswordLink;

    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();//creamos su instancia
     //   currentUser = mAuth.getCurrentUser();//recupera si ya hay iniciado la cesion

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ForgetPasswordLink = findViewById(R.id.forgert_password_link);


        InitializeFields();

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

       //boton para crear la cuenta
        NeddNewYouAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToRegisterActivity();
            }
        });

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AllowUserToLogin();

            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(LoginActivity.this, Telefono.class);
                startActivity(loginIntent);
            }
        });


    }

    private void AllowUserToLogin() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"Porfavor ingresa el correo",Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Porfavor ingresa la contraseña",Toast.LENGTH_SHORT).show();

        }
        else{
            //creamos la vista del proceso que se mostrara al usuario
            loadignBar.setTitle("Iniciando....");//este es el titulo a mostral
            loadignBar.setMessage("Porfavor espera....");//el contenido que tendra el cuadro
            loadignBar.setCanceledOnTouchOutside(true);//y aqui es para que no toque la pantalla
            loadignBar.show();//el show es para que se pueda ver en la pantalla

            //aqui comprobamos si es correcto los usuarios o contraseña
            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()){

                                     //este codigo es apra crear las notificaciones
                                 String currentUserID =mAuth.getCurrentUser().getUid();
                                 String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                 usersRef.child(currentUserID).child("device_token")
                                         .setValue(deviceToken)
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if (task.isSuccessful()){

                                                     SendToMainActivity();//si es correcto ira ala activity principal
                                                     Toast.makeText(LoginActivity.this, "Inicio de Seccion Correcto..", Toast.LENGTH_SHORT).show();
                                                     loadignBar.dismiss();
                                                 }

                                             }
                                         });

                             } else {
                            String messenge = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error:"+messenge, Toast.LENGTH_SHORT).show();
                            loadignBar.dismiss();

                        }
                        }
                    });

        }


    }

    private void InitializeFields() {

        Loginbutton =findViewById(R.id.login_button);
        PhoneLoginButton = findViewById(R.id.phone_login_button);
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        NeddNewYouAccountLink = findViewById(R.id.need_new_account_link);
        ForgetPasswordLink = findViewById(R.id.forgert_password_link);
        loadignBar = new ProgressDialog(this);





    }



    //metodo para ir al main activiti
    private void SendToMainActivity() {
        Intent MainIntent= new Intent(LoginActivity.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void SendToRegisterActivity(){
        //metodo spara ir a crear la cuenta
        Intent registerIntent= new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }


}
