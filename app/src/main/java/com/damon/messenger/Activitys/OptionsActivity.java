package com.damon.messenger.Activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.damon.messenger.R;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.join;

public class OptionsActivity extends AppCompatActivity {

    private TextView logut,settings,recomendar_amigos,calificar,reportar;

    private LinearLayout llprivacyPolicy,linear_huella;

    private Switch huella;
    private Dialog dialogPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);


        dialogPrivacy = new Dialog(this);
        settings = findViewById(R.id.settings);
        recomendar_amigos = findViewById(R.id.recomendar_amigos);
        calificar = findViewById(R.id.calificar);
        reportar = findViewById(R.id.reportar);
        huella = findViewById(R.id.activar_huella_digital);

        llprivacyPolicy = (LinearLayout) findViewById(R.id.llprivacypolicy);
        linear_huella = findViewById(R.id.huella_liner_layout);



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Privacidad");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        recomendar_amigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String shareappPackageName = getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola te recomiendo  " + getResources().getString(R.string.app_name) + " App at: https://play.google.com/store/apps/details?id=" + shareappPackageName);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        reportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;
                PackageManager manager = getApplicationContext().getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String version = info.versionName;
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.developer_email)});
                i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + version);
                i.putExtra(Intent.EXTRA_TEXT,
                        "\n" + " Dispositivo :" + getDeviceName() +
                                "\n" + " Version Sistema:" + Build.VERSION.SDK_INT +
                                "\n" + " Largo Pantalla  :" + height + "px" +
                                "\n" + " Ancho Pantalla  :" + width + "px" +
                                "\n\n" + "Cual fue el Problema? Porfavor contactanos para ayudarte y que la app sea mejor para ti!" +
                                "\n");
                startActivity(Intent.createChooser(i, "Enviar Email"));
            }
        });



        llprivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog dialog = new AlertDialog.Builder(OptionsActivity.this,R.style.AlertDialog)
//                        .setTitle(R.string.PRIVACYPOLICY)
//                        .setMessage(R.string.privacy_message)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        })
//                        .setIcon(R.drawable.ic_info_outline_black_24dp)
//                        .show();
//                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
//                textView.setScroller(new Scroller(OptionsActivity.this));
//                textView.setVerticalScrollBarEnabled(true);
//                textView.setMovementMethod(new ScrollingMovementMethod());
                dialogPrivacy.setContentView(R.layout.dialog_polity);
                TextView link = dialogPrivacy.findViewById(R.id.texto_info_privacy);
                String ulr = getString(R.string.polity_link);
                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ulr));
                        startActivity(intent);
                    }
                });
                dialogPrivacy.show();
                Window window = dialogPrivacy.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });

        try {
            // Inicializando tanto Android Keyguard Manager como Fingerprint Manager
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!fingerprintManager.isHardwareDetected()){
                    /**
                     * Se mostrará un mensaje de error si el dispositivo no contiene el hardware de huellas digitales.
                     * Sin embargo, si planea implementar un método de autenticación predeterminado,
                     * puede redirigir al usuario a una actividad de autenticación predeterminada desde aquí.
                     * Ejemplo:
                     * Intención de intención = nueva intención (esto, DefaultAuthenticationActivity.class);
                     * startActivity (intento);
                     */
                    linear_huella.setVisibility(View.GONE);
                }else{
                    linear_huella.setVisibility(View.VISIBLE);
                    //creamos un preferencia para guardar datos de si a activado o no para que se muestre
                    SharedPreferences preferences = getSharedPreferences("guardado", Context.MODE_PRIVATE);

                    huella.setChecked(preferences.getBoolean("values",false));


                    huella.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (huella.isChecked()){
                                //aqui vemos si a activado el switch para que pueda tener la contraseña
                                guardarPreferencias();
                                guardarEstado();
                                Toast.makeText(OptionsActivity.this, "Activacion de la huella exitosamente", Toast.LENGTH_SHORT).show();
                            }else {
                                RevocarPreferencias();
                                noguradadoEstado();
                                Toast.makeText(OptionsActivity.this, "Desactivacion de la huella exitosamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void guardarEstado(){
        SharedPreferences.Editor preferences = getSharedPreferences("guardado", Context.MODE_PRIVATE).edit();
        preferences.putBoolean("values",true);
        preferences.apply();
        huella.setChecked(true);

    }

    private void noguradadoEstado(){
        SharedPreferences.Editor preferences = getSharedPreferences("guardado", Context.MODE_PRIVATE).edit();
        preferences.putBoolean("values",false);
        preferences.apply();
        huella.setChecked(false);
    }

    private void guardarPreferencias() {
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user","user");
        editor.apply();
    }
    private void RevocarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user","no");
        editor.apply();
    }

    private String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }
}
