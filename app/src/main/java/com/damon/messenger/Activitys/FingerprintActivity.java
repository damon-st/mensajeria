package com.damon.messenger.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.widget.TextView;

import com.damon.messenger.FingerprintHandler;
import com.damon.messenger.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerprintActivity extends AppCompatActivity {

    private KeyStore keyStore;
    // Variable utilizada para almacenar la clave en el contenedor Android Keystore
    private static final String KEY_NAME = "pruebaHuella";
    private Cipher cipher;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Initializing both Android Keyguard Manager and Fingerprint Manager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        }


        textView = (TextView) findViewById(R.id.errorText);


        // Check whether the device has a Fingerprint sensor.
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
                textView.setText("Su dispositivo no tiene un sensor de huellas digitales");
            }else {
                // Comprueba si el permiso de huellas digitales está configurado en el manifiesto
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                    textView.setText("El permiso de autenticación de huellas digitales no está habilitado");
                }else{
                    // Compruebe si al menos una huella digital está registrada
                    if (!fingerprintManager.hasEnrolledFingerprints()) {
                        textView.setText("Registre al menos una huella digital en Configuración");
                    }else{
                        // Comprueba si la seguridad de la pantalla de bloqueo está habilitada o no
                        if (!keyguardManager.isKeyguardSecure()) {
                            textView.setText("La seguridad de la pantalla de bloqueo no está habilitada en Configuración");
                        }else{
                            generateKey();
                            if (cipherInit()) {
                                FingerprintManager.CryptoObject cryptoObject = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    cryptoObject = new FingerprintManager.CryptoObject(cipher);
                                }
                                FingerprintHandler helper = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    helper = new FingerprintHandler(this);
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    helper.startAuth(fingerprintManager, cryptoObject);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }


        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }


        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }


        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}
