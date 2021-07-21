package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.damon.messenger.Notifications.Data;
import com.damon.messenger.R;
import com.damon.messenger.SettingsPerfil.Login;
import com.damon.messenger.SettingsPerfil.PerfilSettingsActivity;
import com.damon.messenger.Adapters.TabsAccessorAdapter;
import com.damon.messenger.call.newcall.BaseActivity;
import com.damon.messenger.call.newcall.PlaceCallActivity;
import com.damon.messenger.call.newcall.SinchService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements SinchService.StartFailedListener {
    //crear un obejo de tipo toolbar que es la parte de arriba de toda aplicaicon
    private Toolbar mToolbar;
    //crear un objeto de tipo viewpager que sera el conetido donde veremos los usuarios contactos etc
    private ViewPager myViewPager;
    //la tabla es donde ira ubicado los nombres de los contenidos que al pulsar nos enviara al fragment
    private TabLayout myTabLayout;
    //cremaos un objeto de la clase tabsaccesadapter que tiene los items y title de los fragments
    private TabsAccessorAdapter myTabsAccessorAdapter;

    //aqui crearemos la conexion con la base de datos para la autentificacion de inicio


    private FirebaseAuth mAuth;

    private DatabaseReference RootRef;

    private String currentUserID;

    private String  calledBy="";

    private TextView userName, userLassSenn;
    private CircleImageView userImage;
    private ImageView llamada;
    private String salir;

    private static final String APP_ID ="ca-app-pub-1691614301371531~7301440527";
    private InterstitialAd mIntertitialAd;
    ProgressDialog mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//           // ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
//
//
//
//        }



        mSpinner = new ProgressDialog(this);
        FirebaseApp.initializeApp(MainActivity.this);

        mAuth = FirebaseAuth.getInstance();//instanciamos la clase de inico

        RootRef = FirebaseDatabase.getInstance().getReference();//creanos la conexion con la base de datos


        mToolbar = findViewById(R.id.main_page_toolbar);//inicializamos el toolbar
        setSupportActionBar(mToolbar);//añadimos el toolbar
        getSupportActionBar().setTitle("");//añadimos el titulo

        //desde aqui es para poner lo que es la foto en el actvion bar
        ActionBar actionBar = getSupportActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionbarView = layoutInflater.inflate(R.layout.custom_main_layout, null);
        actionBar.setCustomView(actionbarView);


        userName = findViewById(R.id.custom_nombre_app);


        myViewPager = findViewById(R.id.main_tabs_pager);//inicializamos el viewpager
        myTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);//aqui pasamos el parametro de la calse adapter


        myTabLayout = findViewById(R.id.main_tabs);//esta tabla crea los 3 bloques contacto chats etc
        myTabLayout.setupWithViewPager(myViewPager);//aqui pasamos la vista que tendra cada tabla



        MobileAds.initialize(this,APP_ID);

        AdRequest adRequest = new AdRequest.Builder().build();
        mIntertitialAd = new InterstitialAd(this);
        mIntertitialAd.setAdUnitId(getResources().getString(R.string.intestalAnuncio));
        mIntertitialAd.loadAd(adRequest);


        mIntertitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mIntertitialAd.loadAd(new AdRequest.Builder().build());
            }

        });



//        mIntertitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when the ad is displayed.
//            }
//
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when the interstitial ad is closed.
//                Toast.makeText(MainActivity.this, "Gracias", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        //esta condicion es para evaluar si ay alguna persona iniciada cecion en el telefono
        //sino esta iniciado ninguna secion nos enviara al login para ingresar
        if (currentUser == null) {
            SendToLoginActivity();
        } else {
            updateUserStates("online");
            VerifyUserExistance();
            new Handler().postDelayed(new Runnable(){
                public void run(){
                    loginClicked(currentUser.getUid());
                };
            }, 2000);
        }
    }

//    @Override
//    protected void onStop() {
//        //ojo sin estos inicializacion se crasheaira la app
//        super.onStop();
//        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
//        if (currentUser != null) {
//            updateUserStates("offline");
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("offline");
        }
    }

    @Override
    protected void onDestroy() {
        //ojo sin estos inicializacion se crasheaira la app
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();//linea que funciona como conector si ubo inico de sesion
        if (currentUser != null) {
            updateUserStates("offline");
        }
        Glide.get(this).clearMemory();
        deleteCache(this);
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    //metodo para vereficar si existe un usuario ya con datos igualkes
    private void VerifyUserExistance() {

        final String currentUserId = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("name").exists()) {
                    //  Toast.makeText(MainActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
//                      if (dataSnapshot.child("image").exists()){
//                          String  imageUrl = dataSnapshot.child("image").getValue().toString();
//                          Picasso.get().load(imageUrl).into(userImage);

//                      }

              //      checkForRecevingCall(currentUserId);


                } else {
                    SendToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void SendToLoginActivity() {
        //este metodo es para dirigirse ala activity login
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    //metodo para poder ver las opcioones en el activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    //metodo para ver las opciones que tiene el menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_option) {

            updateUserStates("offline");

            mAuth.signOut();//este metodo es para cerrar la secion actual que tengas
            SendToLoginActivity();
        }
        if (item.getItemId() == R.id.main_settings_option) {
            if (mIntertitialAd.isLoaded()){
                mIntertitialAd.show();
            }else {

            }
            SendToSettingsActivity();



        }
        if (item.getItemId() == R.id.main_find_friends_option) {
            if (mIntertitialAd.isLoaded()){
                mIntertitialAd.show();
            }else {

            }

            SendToFindFriendsActivity();

        }
        if (item.getItemId() == R.id.main_create_group_option) {
            // RequestNewGroup();
            if (mIntertitialAd.isLoaded()){
                mIntertitialAd.show();
            }else {

            }
            Intent intent = new Intent(getApplicationContext(), CrearGrupoChatActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.main_options) {
            if (mIntertitialAd.isLoaded()){
                mIntertitialAd.show();
            }else {

            }
            Intent intent = new Intent(getApplicationContext(), NuevoSettings.class);
            startActivity(intent);
        }

        return true;
    }


    private void RequestNewGroup() {
        //AQUI ESTAMOS CREANDO UNA PANTALLA FLOTANTE DE DIALOGO QUE SERA QUIEN ALMACENE EL GRUPO A CREAR
        //ESTA ALERTA DIALOG ES PARA AMOSTRAR EL RECUDRO DONDE ESCRIBIREMOS EL NOMBRE DEL GRUPO
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Ingresa El nombre del Grupo:");//AQUI SERA EL DIALOGO QUE MOSTRARA
        final EditText groupNameField = new EditText(MainActivity.this);//DONDE LO MOSTRARA
        groupNameField.setHint("Hola ingresa aqui el nombre del grupo");// LO QUE IRA DENTRO DONDE SE ESCRIBIRA
        builder.setView(groupNameField);//AQUI ES PARA PODER VER EN LA PANTALLA LO QUE CREAMOS

        //ESTE POSITIVEBUTTON ES PARA CONFIRMAR LA CREACION
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String grooupName = groupNameField.getText().toString();//asenmos un casting
                if (TextUtils.isEmpty(grooupName)) {//este condicion es para ver si escribio o no algo
                    Toast.makeText(MainActivity.this, "Porfavor escribe el nombre del grupo", Toast.LENGTH_SHORT).show();
                } else {
                    CreateNewGroup(grooupName);//aqui le estamos pasando un parametro que es el nombre del grupo
                }

            }
        });
        //ESTE NEGATIVEBUTTON ES PARA CANCELAR LA CREACION
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {

                dialogInterface.cancel();// ESTE ES DIALOGO DE CANCELAR LA OPERACION PARA NO CRAR EL GRUPO

            }
        });

        builder.show();//esto es para mostrar
    }

    private void CreateNewGroup(final String grooupName) {
        //ESTE METODO ES QUIEN SE ENCARGARA DE VERIFICAR SI EL GRUPO SE CREA O NO
        RootRef.child("Gropus").child(grooupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(MainActivity.this, grooupName + "Se a creado correctamente el grupo ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void SendToSettingsActivity() {
        Intent settingsintent = new Intent(MainActivity.this, PerfilSettingsActivity.class);

        startActivity(settingsintent);

    }

    public void SendToFindFriendsActivity() {
        Intent findFriends = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(findFriends);

    }

    private void updateUserStates(String state) {
        //aqui sera para poner al ora aslo ususario su iltimas conexcion
        String saveCutrrentTime, saveCurrentData;

        Calendar calendar = Calendar.getInstance();
        //aqui estamos creando faroma del calendario dia mes año
        SimpleDateFormat currentData = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentData = currentData.format(calendar.getTime());
        //aqui estamos creando forma hora minuto y segundo
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCutrrentTime = currentTime.format(calendar.getTime());

        //aqui creamos una lista con la llave y objeto para mandar ala base de datos Firebase
        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCutrrentTime);
        onlineStateMap.put("date", saveCurrentData);
        onlineStateMap.put("state", state);

        currentUserID = mAuth.getCurrentUser().getUid();
        //aqui creamos la referencia con la vase de datos y nombre dela nueva tabla o dato para labase de datos
        RootRef.child("Users").child(currentUserID).child("userSate")
                .updateChildren(onlineStateMap);



    }


    private void checkForRecevingCall(String currentUserID) {
////cdodigo para las llamdas

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        RootRef = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.child(currentUserID)
                .child("Ringing")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("ringing")) {

                            calledBy = dataSnapshot.child("ringing").getValue().toString();

                            Intent intent = new Intent(MainActivity.this, CallingActivity.class);
                            intent.putExtra("id", calledBy);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//    }


    }

    @Override
    public void onBackPressed() {
        if (myViewPager.getCurrentItem() >=1){
            myViewPager.setCurrentItem(0);
        }else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onServiceConnected() {

        getSinchServiceInterface().setStartListener(this);
    }


    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
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
        mSpinner.dismiss();
//        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
//        startActivity(mainActivity);
    }

    private void showSpinner() {

        mSpinner.setTitle("Conectando con Servidores");
        mSpinner.setMessage("Por favor Espera...");
        mSpinner.show();
    }


}
