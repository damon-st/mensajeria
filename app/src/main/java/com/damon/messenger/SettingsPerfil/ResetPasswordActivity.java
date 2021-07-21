package com.damon.messenger.SettingsPerfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.damon.messenger.Activitys.LoginActivity;
import com.damon.messenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText send_email;
    Button btn_reset;

    FirebaseAuth firebaseAuth;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        dialog  =new Dialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reseto de Contraseña");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        send_email = findViewById(R.id.send_email);
        btn_reset = findViewById(R.id.btn_reset);

        firebaseAuth = FirebaseAuth.getInstance();

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = send_email.getText().toString();

                if (email.equals("")){
                    Toast.makeText(ResetPasswordActivity.this, "Escribe tu correo", Toast.LENGTH_SHORT).show();
                }else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Porfavor Revisa tu correo tiempo de demora 5 minutos en llegar", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPasswordActivity.this, Login.class));
                                finish();
                            }else {
                                String  error = task.getException().getMessage();
                                //Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                System.out.println(error);
                                if (error.equals("The email address is badly formatted.")){
                                    mostrarDialog("Por favor introduce un correo Electronico Valido");
                                }else if (error.equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                    mostrarDialog("El correo ingresado no existe en nuestra base de Datos por favor reviselo e intente nuevamente");
                                }else {
                                    mostrarDialog(error);
                                }
                            }
                        }
                    });
                }
            }
        });

    }

    private void mostrarDialog(String message){
        dialog.setContentView(R.layout.dialogoalerta);
        TextView titulo = dialog.findViewById(R.id.texto_error);
        ImageView imagen = dialog.findViewById(R.id.imagen_error);
        Glide.with(ResetPasswordActivity.this).load(R.drawable.tenor).into(imagen);
        titulo.setText(message);
        dialog.show();
    }
}
