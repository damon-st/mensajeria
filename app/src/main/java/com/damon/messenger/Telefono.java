package com.damon.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.damon.messenger.Activitys.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Telefono extends AppCompatActivity {

    private CountryCodePicker ccp;

    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextBtn;
    private String checker ="",phoneNumber="";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog  loadingBar;

    private DatabaseReference RootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueAndNextBtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

        RootReference = FirebaseDatabase.getInstance().getReference();

        continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (continueAndNextBtn.getText().equals("Submit")|| checker.equals("Code Sent")){
                       String verificationCode = codeText.getText().toString();
                       if (verificationCode.equals("")){
                           Toast.makeText(Telefono.this, "Por Favor escribe el Codigo de verificacion", Toast.LENGTH_SHORT).show();
                       }else {
                           loadingBar.setTitle("Verficiando el Codigo");
                           loadingBar.setMessage("Por favor espera,estamos verificando el Codigo");
                           loadingBar.setCanceledOnTouchOutside(false);
                           loadingBar.show();

                           PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationCode);

                           signInWithPhoneAuthCredential(credential);
                       }
                }else {
                    phoneNumber = ccp.getFullNumberWithPlus();

                    if (!phoneNumber.equals("")){
                        loadingBar.setTitle("Verficiando El numero");
                        loadingBar.setMessage("Por favor espera,estamos verificando su numero");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        //  Número de teléfono para verificar
                                60,                 //  Duración del tiempo de espera
                                TimeUnit.SECONDS,   // Unidad de tiempo de espera
                                Telefono.this,               // Actividad (para enlace de devolución de llamada)
                                mCallbacks);        // OnVerificationStateChangedCallbacks

                    }else {
                        Toast.makeText(Telefono.this, "Por favor escribe un numero Valido", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                     signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Telefono.this, "Invalido Numero", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);

                continueAndNextBtn.setText("Continuar");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendToken = forceResendingToken;
                relativeLayout.setVisibility(View.GONE);

                checker="Code Sent";
                continueAndNextBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);

                loadingBar.dismiss();
                Toast.makeText(Telefono.this, "El codigo a sido enviado,Escribelo aqui", Toast.LENGTH_SHORT).show();
            }
        };

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();


                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                            String currentUserId = mAuth.getCurrentUser().getUid();
                            RootReference.child("Users").child(userid).setValue("");

                            RootReference.child("Users").child(userid).child("device_token")
                                    .setValue(deviceToken);

                            HashMap<String,Object> userMap = new HashMap<>();
                            userMap.put("phone",phoneText.getText().toString());
                            userMap.put("email",phoneText.getText().toString());
                            // ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(userMap);//esto es para acutlizar en la bvase de datos
                            ref.child(userid).updateChildren(userMap);




                            enviaraMainActivity();
                            Toast.makeText(Telefono.this, "Cuenta Creada Coreectamente", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        } else {
                            loadingBar.dismiss();
                            String e = task.getException().toString();
                            Toast.makeText(Telefono.this, "Error.."+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void enviaraMainActivity(){
        Intent intent = new Intent(Telefono.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }
}
