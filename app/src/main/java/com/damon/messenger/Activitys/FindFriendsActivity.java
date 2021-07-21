package com.damon.messenger.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.damon.messenger.Model.Contacts;
import com.damon.messenger.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
//en esta clase sera la encaragada de buscar amigos

    private Toolbar mToolbar;
    private RecyclerView FindFriendsrecyclerList;

    private DatabaseReference usersRef;
    private EditText search_users;

    private String Searchinput;

    private Button boton_buscar;

    private ProgressBar progressBar;//es para la carga de articulo


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

       FindFriendsrecyclerList = findViewById(R.id.find_friends_recybleList);//lo inicializamos
       FindFriendsrecyclerList.setLayoutManager(new LinearLayoutManager(this));//aqui creamos un nuevo lista



        boton_buscar = findViewById(R.id.boton_buscar);

        progressBar = findViewById(R.id.progress_circular);



        mToolbar = findViewById(R.id.find_frinds_toolbar);//inicializamos el toobar que es la barrita
        setSupportActionBar(mToolbar);//aque lo asigaamos
        getSupportActionBar().setDisplayShowHomeEnabled(true);//aqui recuperamos para que se muestre
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buscar Amigos");//aqui le asignamso el titulo que tendra
        search_users = findViewById(R.id.search_users);

        boton_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Searchinput =   search_users.getText().toString().toLowerCase();
                 onStart();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseRecyclerOptions<Contacts> options  =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(usersRef.orderByChild("search").startAt(Searchinput).endAt(Searchinput+"\uf8ff"),Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FindFriendsViewHolder holder, final int position, @NonNull final Contacts model) {
                        // aqui utilizamos el inicializador de la calse findfriends
                        //que se encarga de asiganar los nonbre ala lista de contactos
                        final String usersID = getRef(position).getKey();
                        usersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.child("image").exists()){
                                        String nombre = dataSnapshot.child("name").getValue().toString();
                                        String status = dataSnapshot.child("status").getValue().toString();
                                        String image = dataSnapshot.child("image").getValue().toString();

                                        holder.username.setText(nombre);
                                        holder.userStatus.setText(status);
                                        //metodo de la libreia picaso para ver la imgane
                                        Picasso.get().load(image)
                                                .resize(90,90)
                                                .placeholder(R.drawable.profile_image)
                                                .into(holder.prolifeImage, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        holder.progressBar.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.prolifeImage);
                                                    }
                                                });

                                        //este itemView sera quien se encargue de resivir lo que aya tocado el usuario
                                        // el itemView pertenece ala clase FindFriendsView Holder
                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //aqui recuperamos la llave del usuario y la guardamos
                                                String visit_user_id = getRef(position).getKey();
                                                Intent prolifeIntent = new Intent(FindFriendsActivity.this, ProlifeActivity.class);
                                                //aqui estamso enviando la llave
                                                prolifeIntent.putExtra("visit_user_id",visit_user_id);
                                                startActivity(prolifeIntent);
                                            }
                                        });

                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        //aqui creamoas un view i lo infamos con  el laytout que creamos con todos los datos de los uruarios
                       View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                       FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
                       return  viewHolder;

                    }
                };

        FindFriendsrecyclerList.setAdapter(adapter);

        adapter.startListening();

    }
    //creamos una clase aqui
    //para que se pueda mostrar en los usuarios
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        TextView username, userStatus;
        CircleImageView prolifeImage;
        ProgressBar progressBar;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            prolifeImage = itemView.findViewById(R.id.users_profile_image);
            progressBar = itemView.findViewById(R.id.proges_dialog_chat);
        }
    }
}
