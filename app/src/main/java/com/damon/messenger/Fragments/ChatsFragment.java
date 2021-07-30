package com.damon.messenger.Fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.damon.messenger.Activitys.ChatActivity;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.Activitys.UserProfileActivity;
import com.damon.messenger.Adapters.ChatListAdapter;
import com.damon.messenger.Adapters.MessageAdapter;
import com.damon.messenger.Model.ChatObject;
import com.damon.messenger.Model.Contacts;
import com.damon.messenger.Model.Messages;
import com.damon.messenger.Model.UserObject;
import com.damon.messenger.Notifications.Token;
import com.damon.messenger.R;
import com.damon.messenger.call.newcall.BaseActivity;
import com.damon.messenger.call.newcall.CallScreenActivity;
import com.damon.messenger.call.newcall.SinchService;
import com.damon.messenger.call.videocall.CallScreenActivityVideo;
import com.damon.messenger.util.AES;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sinch.android.rtc.MissingPermissionException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private View PrivateChatsView;
    private RecyclerView    chatList;

    private DatabaseReference chatsRef,UsersRef, meesages;
    private FirebaseAuth mAuth;
    private String currentUserID;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    String theLastMessage,date_msg,type_msg,conectadoUser,status;
    private boolean ischat;

    TextView last_msg;
    private List<String> followingList;
    private List<Contacts> mUsers;


    private View groupFragmentView;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;//creamos un adaptador
    private ArrayList<String>list_of_groups = new ArrayList<>();

    private DatabaseReference GropuRef;

    public ChatsFragment() {
        // Requerido el constructor
    }

    ArrayList<ChatObject> chatList1;

    private ProgressBar progressBar;//es para la carga de articulo
    private boolean si ,visto;





    //esto agrege resien
    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;

    private static final String APP_ID ="ca-app-pub-1691614301371531~7301440527";
    FirebaseRecyclerOptions<Contacts> options;
    FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter;

    private String nombresender,imageSender;
    private Dialog dialogUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);

        dialogUser = new Dialog(PrivateChatsView.getContext());

        chatList = PrivateChatsView.findViewById(R.id.chats_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        chatsRef.keepSynced(true);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UsersRef.keepSynced(true);

        updateToken(FirebaseInstanceId.getInstance().getToken());
        progressBar = PrivateChatsView.findViewById(R.id.progress_circular);

        GropuRef = FirebaseDatabase.getInstance().getReference().child("Gropus");
        GropuRef.keepSynced(true);


        //   InitializeFields();
        // checkFollowing();

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                String curretGroupName = parent.getItemAtPosition(position).toString();
//
//                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
//                groupChatIntent.putExtra("groupName",curretGroupName);//aqui mandamos datos a gropuchatactivity
//                startActivity(groupChatIntent);
//
//            }
//        });


        initializeRecyclerView();

        getUserChatList();

        setCurrentUserID();





        return PrivateChatsView;
    }

    private void RetriveAndDisplayGropus() {
        GropuRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();// este metodo se usa para crear las listas que ay de grupos
                Iterator iterator  = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }

                list_of_groups.clear();
                list_of_groups.addAll(set);//aqui se añade la lista set que creamos arriba
                arrayAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {

        listView = PrivateChatsView.findViewById(R.id.list_view);
        //mandamos el contexto y buscamos una vista y mandamso el array de la lista
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_groups);
        listView.setAdapter(arrayAdapter);//aqui en la listview mandamnos el adaptador que creamos arriba

    }

    private void updateToken(String  token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);

        reference.child(currentUserID).setValue(token1);
    }

    @Override
    public void onStart() {
        super.onStart();
        options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef,Contacts.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull final Contacts model) {
                final String usersID = getRef(position).getKey();
                final String[] retImage = {"default_image"};


//                        if (ischat){
//                            lastMessage(usersID,holder.last_msg);
//                        }else {
//                            holder.last_msg.setText("f");
//                        }


                UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            si = true;
                            if (dataSnapshot.hasChild("image")){

                                retImage[0] =dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0])
                                                .resize(90,90)
                                                .into(holder.profileimage, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
                                                        holder.progressBar.setVisibility(View.GONE);
                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.profileimage, new Callback() {
                                                            @Override
                                                            public void onSuccess() {

                                                            }

                                                            @Override
                                                            public void onError(Exception e) {
                                                                Log.e("ERROR",e.getMessage());
                                                            }
                                                        });
                                                    }
                                                });


                                final String retName = dataSnapshot.child("name").getValue().toString();
                                final String status = dataSnapshot.child("status").getValue().toString();

                                holder.userName.setText(retName);
//                                        holder.userStatus.setText("Ultima vez conectado"+"\n"+"Date"+"Time");





                                //condicion para crear la notificacionj
                                        if(dataSnapshot.child("userSate").hasChild("state")){

                                            String state = dataSnapshot.child("userSate").child("state").getValue().toString();
                                            String date = dataSnapshot.child("userSate").child("date").getValue().toString();
                                            String time = dataSnapshot.child("userSate").child("time").getValue().toString();
                                            if (state.equals("online")){

                                                conectadoUser = "Conectado";
                                            }
                                            else if (state.equals("offline")){

                                                conectadoUser = "Ultima vez conectado" + "\n"+ date +" "+ time;
                                            }
                                        }else {
                                            conectadoUser = "Desconectado";
                                        }

                                lastMessage(currentUserID,holder.userStatus,usersID,holder.last_msg,holder.img_visto);


                                holder.profileimage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setDialogUser(holder.userName.getText().toString(),retImage[0],usersID,conectadoUser,status);
                                    }
                                });


                                        holder.linear_chat_contact.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                                Intent chatintent = new Intent(getContext(), ChatActivity.class);
                                                chatintent.putExtra("userid",usersID);
                                                chatintent.putExtra("name",retName);
                                                chatintent.putExtra("image", retImage[0]);

                                                try {
                                                    startActivity(chatintent);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                progressBar.setVisibility(View.GONE);


            }


            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_display_custom_chat,viewGroup,false);
                return new ChatsViewHolder(view);

            }
        };
        chatList.setAdapter(adapter);
        adapter.startListening();
    }





    public static class  ChatsViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileimage ;
        TextView userStatus ,userName,last_msg;
        ImageView img_visto;
        ProgressBar progressBar;
        LinearLayout linear_chat_contact;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

               userName = itemView.findViewById(R.id.user_profile_name);
               userStatus = itemView.findViewById(R.id.user_status);
               profileimage =itemView.findViewById(R.id.users_profile_image);
               last_msg = itemView.findViewById(R.id.last_msg);
               progressBar = itemView.findViewById(R.id.proges_dialog_chat);
               img_visto = itemView.findViewById(R.id.ic_visto_chat);
               linear_chat_contact = itemView.findViewById(R.id.linear_chat_contact);

        }
    }

    private void lastMessage(final String userid, final TextView last_msg, final String receiver, TextView dateTv, ImageView img_visto){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages");

        reference.child(userid).child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Messages chat = snapshot.getValue(Messages.class);
                    if (firebaseUser != null && chat != null) {
                        type_msg = chat.getType();
                        System.out.println("chat"+chat);
                        if (chat.getReceiver().equals(userid) && chat.getSender().equals(receiver) ||
                                chat.getReceiver().equals(receiver) && chat.getSender().equals(userid)) {
                            switch (type_msg){
                                case "text":
                                    theLastMessage = DecryptMessage(chat.getMessage());
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "image":
                                    theLastMessage = "Te a enviado una imagen";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "pdf":
                                    theLastMessage = "Te a enviado un archivo pdf";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "xlsx":
                                    theLastMessage = "Te a enviado un archivo excel";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "mp4":
                                    theLastMessage = "Te a enviado un video";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "mp3":
                                    theLastMessage = "Te a enviado un audio";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "docx":
                                    theLastMessage = "Te a enviado un archivo word";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    System.out.println("lastMessage"+theLastMessage);
                                    break;
                                case "respuesta":
                                    theLastMessage = DecryptMessage(chat.getMessage());
                                    visto = chat.isIsseen();
                                    date_msg = chat.getDate();
                                    break;
                                case "gif":
                                    theLastMessage = "Te a enviado un gif";
                                    date_msg = chat.getDate();
                                    visto = chat.isIsseen();
                                    break;

                            }
                        }else {
                            theLastMessage = "";
                            date_msg= "";
                            visto = false;
                        }
                    }
                }

                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("Ningun Mensaje");
                        dateTv.setText("");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        dateTv.setText(date_msg);
                        if (visto){
                            img_visto.setVisibility(View.VISIBLE);
                            img_visto.setImageResource(R.drawable.visto);
                        }else {
                            img_visto.setVisibility(View.VISIBLE);
                            img_visto.setImageResource(R.drawable.novisto);
                        }
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Contacts")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                ;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                // readPosts();
                    RetriveAndDisplayGropus();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
//esto agrege para ver la lista del grupo
    private void getUserChatList(){
        DatabaseReference mUserChatDB = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("chat");

        mUserChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        ChatObject mChat = new ChatObject(childSnapshot.getKey());
                        boolean  exists = false;
                        for (ChatObject mChatIterator : chatList1){
                            if (mChatIterator.getChatId().equals(mChat.getChatId()))
                                exists = true;
                        }
                        if (exists)
                            continue;
                        chatList1.add(mChat);
                        getChatData(mChat.getChatId());
                        progressBar.setVisibility(View.GONE);

                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//esto agrege para ver la lista del grupo
    private void getUserData(UserObject mUser) {
        DatabaseReference mUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
        mUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserObject mUser = new UserObject(dataSnapshot.getKey());

                if(dataSnapshot.child("notificationKey").getValue() != null)
                    mUser.setNotificationKey(dataSnapshot.child("notificationKey").getValue().toString());

                for(ChatObject mChat : chatList1){
                    for (UserObject mUserIt : mChat.getUserObjectArrayList()){
                        if(mUserIt.getUid().equals(mUser.getUid())){
                            mUserIt.setNotificationKey(mUser.getNotificationKey());
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                mChatListAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("WrongConstant")
    private void initializeRecyclerView() {
        chatList1 = new ArrayList<>();
        mChatList= PrivateChatsView.findViewById(R.id.list_view);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList1);
        mChatList.setAdapter(mChatListAdapter);
    }
//esto agrege resien
    private void getChatData(String chatId) {
        DatabaseReference mChatDB = FirebaseDatabase.getInstance().getReference().child("chat").child(chatId).child("info");
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String chatId = "";

                    if(dataSnapshot.child("id").getValue() != null)
                        chatId = dataSnapshot.child("id").getValue().toString();

                    for(DataSnapshot userSnapshot : dataSnapshot.child("users").getChildren()){
                        for(ChatObject mChat : chatList1){
                            if(mChat.getChatId().equals(chatId)){
                                UserObject mUser = new UserObject(userSnapshot.getKey());
                                mChat.addUserToArrayList(mUser);
                                getUserData(mUser);
                            }
                        }

                    }

                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStop() {
        if (adapter !=null){
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    public void onDestroy() {
        if (adapter!=null){
            adapter.stopListening();
        }
        super.onDestroy();
    }

    AES aes = new AES("lv39eptlvuhaqqsr");
    private String  DecryptMessage(String msg){

        try {
            String cadenaEncryp = aes.decrypt(msg);
            return cadenaEncryp;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void setDialogUser(String name,String img, String idUser,String  fechaConectado,String  status){
        dialogUser.setContentView(R.layout.dialog_chat_user);
        TextView nameUser = dialogUser.findViewById(R.id.nombre_user_contact);
        ImageView imgUser = dialogUser.findViewById(R.id.img_user_contact);
        ImageView imgChat = dialogUser.findViewById(R.id.img_chat_user);
        ImageView imgInfo = dialogUser.findViewById(R.id.img_info_user);
        ImageView imgCall = dialogUser.findViewById(R.id.img_call_user);
        ImageView imgVideCall = dialogUser.findViewById(R.id.img_videocall_user);

        nameUser.setText(name);
        Glide.with(getActivity()).load(img).into(imgUser);

        imgChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatintent = new Intent(getContext(), ChatActivity.class);
                chatintent.putExtra("userid",idUser);
                chatintent.putExtra("name",name);
                chatintent.putExtra("image", img);
                getActivity().startActivity(chatintent);
                dialogUser.dismiss();
            }
        });

        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra("url",img);
                intent.putExtra("name",name);
                intent.putExtra("status",status);
                intent.putExtra("tiempo",fechaConectado);
                intent.putExtra("messagemSenderID",currentUserID);
                intent.putExtra("messageReciverID",idUser);
                startActivity(intent);
            }
        });

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relaizar("audio",idUser);
                callButtonClicked(idUser,name,img);
            }
        });

        imgVideCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relaizar("video",idUser);
                try {
                    callButtonClickedVideo(idUser,name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        dialogUser.show();
    }

    private void setCurrentUserID(){
        UsersRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    imageSender = snapshot.child("image").getValue().toString();
                    nombresender = snapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void callButtonClicked(String messageReciverID,String nombreReceiver,String imageReceiver) {
        String userName = messageReciverID;
        if (userName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        try {
//            com.sinch.android.rtc.calling.Call call = getSinchServiceInterface().callUserVideo(userName);
            com.sinch.android.rtc.calling.Call call = ((BaseActivity)getActivity()).getSinchServiceInterface().callUser(userName);
            if (call == null) {
                // Service failed for some reason, show a Toast and abort
                Toast.makeText(getActivity(), "El servicio no se inicia. Intente detener el servicio e iniciarlo de nuevo antes "
                        + "hacer una llamada.", Toast.LENGTH_LONG).show();
                return;
            }
            String callId = call.getCallId();
            Intent callScreen = new Intent(getActivity(), CallScreenActivity.class);
            callScreen.putExtra(SinchService.CALL_ID, callId);
            callScreen.putExtra("name",nombreReceiver);
            callScreen.putExtra("receiverID",messageReciverID);
            callScreen.putExtra("image",imageReceiver);
            startActivity(callScreen);
        } catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{e.getRequiredPermission()}, 0);
        }
    }

    private void callButtonClickedVideo(String messageReciverID,String nombreReceiver) throws Exception {
        String userName = messageReciverID;
        if (userName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a user to call", Toast.LENGTH_LONG).show();
            return;
        }

        com.sinch.android.rtc.calling.Call call =((BaseActivity)getActivity()).getSinchServiceInterface().callUserVideo(userName);
        String callId = call.getCallId();

        Intent callScreen = new Intent(getActivity(), CallScreenActivityVideo.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra("name",nombreReceiver);
        startActivity(callScreen);
    }


    private void relaizar(String type,String messageReciverID){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("type",type);
        hashMap.put("name",nombresender);
        hashMap.put("image",imageSender);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("call").child(messageReciverID);
        reference.updateChildren(hashMap);
    }
}
