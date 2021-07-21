package com.damon.messenger.SettingsPerfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.damon.messenger.Activitys.LoginActivity;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.Activitys.VideoChatActivity;
import com.damon.messenger.R;
import com.damon.messenger.Telefono;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;


public class Login extends AppCompatActivity {

    //aqui crearemos la conexion con la base de datos para la autentificacion de inicio
    // private FirebaseUser currentUser;
    //creamos un firebaseAuth que se encarga de la autentificacion d inicio
    private FirebaseAuth mAuth;
    //creamos el dialogo que se mostrala el proceso de inicio al usuario
    private ProgressDialog loadignBar;
    private Button  PhoneLoginButton,NeddNewYouAccountLink;
    private ImageButton Loginbutton;
    private EditText UserEmail,UserPassword;
    private TextView ForgetPasswordLink,textInfo;


    private DatabaseReference usersRef;
    private static final int RC_VIDEO_APP_PERM = 124;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);


        dialog = new Dialog(this);

        mAuth = FirebaseAuth.getInstance();//creamos su instancia
        //   currentUser = mAuth.getCurrentUser();//recupera si ya hay iniciado la cesion

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ForgetPasswordLink = findViewById(R.id.forgert_password_link);


        InitializeFields();

        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ResetPasswordActivity.class));
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
                Intent loginIntent = new Intent(Login.this, Telefono.class);
                startActivity(loginIntent);
            }
        });
      //  requestPermissions();

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
                                                    Toast.makeText(Login.this, "Inicio de Seccion Correcto..", Toast.LENGTH_SHORT).show();
                                                    loadignBar.dismiss();
                                                }

                                            }
                                        });

                            } else {
                                String messenge = task.getException().toString();
                               // Toast.makeText(Login.this, "Error:"+messenge, Toast.LENGTH_SHORT).show();
                                loadignBar.dismiss();
                                System.out.println(messenge);
                                if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.")){
                                    mostrarDialog("Por Favor Introdusca una direccion de correo Electronica Valida");
                                }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.")){
                                    mostrarDialog("La contraseña Ingresada es incorrecta por favor Intenta Nueva Mente");
                                }else if (messenge.equals("com.google.firebase.FirebaseTooManyRequestsException: We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts. Please try again later. ]")){
                                    mostrarDialog("Lo sentimos as utilizado demasiados intentos y emos bloqueado las peticiones por favor intentalo mas tarde");
                                    Loginbutton.setEnabled(false);
                                    textInfo.setText("As utilizado demasiados intentos de inicio de seccion porfavor Intenta mas tarde");
                                }else if (messenge.equals("com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.")){
                                    mostrarDialog("El correo ingresado no existe en nuestra base de Datos por favor revisalo e intenta nueva mente");
                                }else {
                                    mostrarDialog(messenge);
                                }
                            }
                        }
                    });

        }


    }

    private void InitializeFields() {

        Loginbutton =findViewById(R.id.signin);
        PhoneLoginButton = findViewById(R.id.telefono);
        UserEmail = findViewById(R.id.email);
        UserPassword = findViewById(R.id.password);
        NeddNewYouAccountLink = findViewById(R.id.signup);
        ForgetPasswordLink = findViewById(R.id.checkbox);
        loadignBar = new ProgressDialog(this);
        textInfo = findViewById(R.id.texto_info);

    }
//    //esto son los permisos para el video chat primero asemos la comprobvacions
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults, Login.this);
//    }
//
//    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
//    private void requestPermissions(){
//        String [] perms = {Manifest.permission.INTERNET};
//        if (EasyPermissions.hasPermissions(this,perms)){
//            //aqui inisialimoas si son correctos los permisios
//
//        }else {
//            EasyPermissions.requestPermissions(this,"Esta app nesesita los permisos de camara , Porfavor aceptalos.",RC_VIDEO_APP_PERM,perms);
//        }
//    }


    //metodo para ir al main activiti
    private void SendToMainActivity() {
        Intent MainIntent= new Intent(Login.this, MainActivity.class);
        MainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(MainIntent);
        finish();
    }

    private void SendToRegisterActivity(){
        //metodo spara ir a crear la cuenta
        Intent registerIntent= new Intent(Login.this, Register.class);
        startActivity(registerIntent);
    }

    private void mostrarDialog(String message){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_error);
        ImageView imagen = dialog.findViewById(R.id.imagen_error);
        Glide.with(Login.this).load(R.drawable.tenor).into(imagen);
        titulo.setText(message);
        dialog.show();
    }
}
