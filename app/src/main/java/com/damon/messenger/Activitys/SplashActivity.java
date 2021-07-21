package com.damon.messenger.Activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.damon.messenger.R;
import com.damon.messenger.call.newcall.BaseActivity;
import com.damon.messenger.call.newcall.SinchService;
import com.sinch.android.rtc.SinchError;

public class SplashActivity extends BaseActivity implements SinchService.StartFailedListener {
//esta clase es la encargada de creaun una vreve trancicion
    private final int DURACION_SPLASH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //el splsh_activity ay que remplazar con la actividad de esta clase
        new Handler().postDelayed(new Runnable(){
            public void run(){             //remplazamos con la actividad  y remplazamos con la actividad ala que queremos ir
                loginClicked("user");
            };
        }, DURACION_SPLASH);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onServiceConnected() {

        getSinchServiceInterface().setStartListener(this);
    }


    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
//        if (mSpinner != null) {
//            mSpinner.dismiss();
//        }
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked(String uid) {
//        mLoginName.setText(FirebaseAuth.getInstance().getUid());
        String userName = uid;

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!userName.equals(getSinchServiceInterface().getUserName())) {
            getSinchServiceInterface().stopClient();
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            openPlaceCallActivity();
        }
    }

    private void openPlaceCallActivity() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
        final String user = preferences.getString("user","no existe");
        if (user.equals("user")){
            Intent intent = new Intent(SplashActivity.this, FingerprintActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showSpinner() {
//
//        mSpinner.setTitle("Conectando con Servidores");
//        mSpinner.setMessage("Por favor Espera...");
//        mSpinner.show();
    }

}
