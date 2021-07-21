package com.damon.messenger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.damon.messenger.Activitys.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {


    private Context context;


    // Constructor
    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }


    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        this.update("Error de Autenticación de huellas dactilares\n" + errString, false);
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        this.update("Ayuda de Autenticación de huellas dactilares\n" + helpString, false);
    }


    @Override
    public void onAuthenticationFailed() {
        this.update("Fallo al autenticar con la huella dactilar.", false);
    }


    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Éxito al autenticar con la huella dactilar.", true);
        Intent intent = new Intent(context , MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        ((Activity)context).finish();
        try {
            this.finalize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    public void update(String e, Boolean success){
        TextView textView = (TextView) ((Activity)context).findViewById(R.id.errorText);
        ImageView view = (ImageView)((Activity)context).findViewById(R.id.icon);
        textView.setText(e);
        if(success){
            textView.setTextColor(ContextCompat.getColor(context,R.color.successText));
            view.setImageResource(R.drawable.ic_fingertprint_correct);
        }else {
            view.setImageResource(R.drawable.ic_fingertprint_incorrect);
        }
    }
}
