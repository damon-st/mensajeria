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
import android.widget.Toast;

import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {


    private Button sendverificationCodeButton , VerifyButton;
    private EditText InputPhoneNumber , InputVerificationCode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        mAuth = FirebaseAuth.getInstance();

        sendverificationCodeButton =findViewById(R.id.send_ver_code_button);
        VerifyButton = findViewById(R.id.verify_button);
        InputPhoneNumber = findViewById(R.id.phone_number_input);
        InputVerificationCode = findViewById(R.id.verification_code_input);
        loadingBar = new ProgressDialog(this);



        sendverificationCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = InputPhoneNumber.getText().toString();


                if (TextUtils.isEmpty(phoneNumber)){

                    Toast.makeText(PhoneLoginActivity.this, "Ingresa el numero", Toast.LENGTH_SHORT).show();
                }else {

                    loadingBar.setTitle("Verificando Numero");
                    loadingBar.setMessage("Porfavor espera estamos verificando...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,                              // Número de teléfono para verificar
                            60,                                     // Duración del tiempo de espera
                            TimeUnit.SECONDS,                       // Unidad de tiempo de espera
                            PhoneLoginActivity.this,               // Actividad (para enlace de devolución de llamada)
                            callbacks);                       // OnVerificationStateChangedCallbacks
                }
            }
        });

        VerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendverificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);

                String verificationCode = InputVerificationCode.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Porfavor escribe el codigo", Toast.LENGTH_SHORT).show();
                }else {

                    loadingBar.setTitle("Verificando Codigo");
                    loadingBar.setMessage("Porfavor espera estamos verificando...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                loadingBar.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Numero incorrecto ,Porfavor escribe el numero correcto", Toast.LENGTH_SHORT).show();

                sendverificationCodeButton.setVisibility(View.VISIBLE);
                InputPhoneNumber.setVisibility(View.VISIBLE);


                VerifyButton.setVisibility(View.INVISIBLE);
                InputVerificationCode.setVisibility(View.INVISIBLE);

            }
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                loadingBar.dismiss();
                // Guarde la identificación de verificación y el reenvío del token para que podamos usarlos más tarde
                mVerificationId = verificationId;
                mResendToken = token;

                Toast.makeText(PhoneLoginActivity.this, "El codigo a sido enviado, verificalo ", Toast.LENGTH_SHORT).show();

                sendverificationCodeButton.setVisibility(View.INVISIBLE);
                InputPhoneNumber.setVisibility(View.INVISIBLE);


                VerifyButton.setVisibility(View.VISIBLE);
                InputVerificationCode.setVisibility(View.VISIBLE);

            }
        };

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicie sesión correctamente, actualice la interfaz de usuario con la información del usuario que inició sesión
                            loadingBar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Felicidades Inicio Correcto", Toast.LENGTH_SHORT).show();
                            SendToUserToMainActivity();

                        } else {

                            String messeng = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error"+messeng, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SendToUserToMainActivity() {
        Intent loginintent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        startActivity(loginintent);
        finish();
    }
}
