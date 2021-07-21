package com.damon.messenger.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.damon.messenger.Activitys.ImageViewerActivity;
import com.damon.messenger.Activitys.MainActivity;
import com.damon.messenger.Activitys.VideoActivity;
import com.damon.messenger.Activitys.ViewGifGrande;
import com.damon.messenger.Model.Messages;
import com.damon.messenger.PlayVideo;
import com.damon.messenger.R;
import com.damon.messenger.interfaces.EstaFocusMsg;
import com.damon.messenger.interfaces.OnClickListener;
import com.damon.messenger.interfaces.onClickResMsg;
import com.damon.messenger.util.AES;
import com.damon.messenger.util.AudioService;
import com.damon.messenger.util.TimeAgo;
import com.damon.messenger.viewholders.FechaHeaderViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
            implements StickyRecyclerHeadersAdapter<FechaHeaderViewHolder> {

private List<Messages> usermessagesList;
private FirebaseAuth mAuth;
private DatabaseReference usersRef;
private Activity context;
private FirebaseStorage reference;

private String uri;
private AES aes;
private String date;


    private EstaFocusMsg estaFocusMsg;
    private  onClickResMsg onClickResMsg;
    private AudioService audioService;
    private SparseBooleanArray selected_items;
    private OnClickListener onClickListener =null;
    private int current_selected_idx = -1;



    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public MessageAdapter(List<Messages>usermessagesList, Activity context,onClickResMsg onClickResMsg,EstaFocusMsg estaFocusMsg){
    this.usermessagesList = usermessagesList;
    this.context = context;
    this.audioService = new AudioService(context);
    selected_items = new SparseBooleanArray();
    this.onClickResMsg = onClickResMsg;
    this.estaFocusMsg = estaFocusMsg;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{


        public TextView senderMessageText , reciverMessageText,duration_audio,duration_audio_receiver,nombreUsuarioRespuesta,
        texto_aresponder_sender,texto_respuesta_sender,nombreUsuarioRespuestaReceiver,texto_aresponder_receiver,texto_respuesta_receiver,
        msg_img_sender,msg_img_receiver;
        public CircleImageView reciverProfileImage;
        public ImageView messageSenderPicture,messageReceiverPicture,img_responder_sender,img_responder_receiver;
        public TextView txt_seen1, txt_seen,make_msg_sender,time_video_sender,time_video_receiver;
        public ImageView visto;
        public RelativeLayout lyt_parent;
        public LinearLayout layout_voice,layout_voice_receiver;
        public ImageButton btn_play_auido;
        public CardView card_sender,card_receiver;
        public ImageView play_btn_sender,play_btn_receiver,voice_video_sender,voice_video_receiver;
        public VideoView videoViewSender,videoViewReceiver;




        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_text);
            reciverMessageText = itemView.findViewById(R.id.reciver_message_text);
            reciverProfileImage = itemView.findViewById(R.id.message_profile_image);
            messageSenderPicture = itemView.findViewById(R.id.message_sender_imageView);
            messageReceiverPicture = itemView.findViewById(R.id.message_receiver_imageView);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            txt_seen1 = itemView.findViewById(R.id.txt_seen1);
            visto = itemView.findViewById(R.id.visto);
            make_msg_sender = itemView.findViewById(R.id.make_msg);
            lyt_parent = itemView.findViewById(R.id.layout_msg);
            layout_voice = itemView.findViewById(R.id.layout_voice);
            btn_play_auido = itemView.findViewById(R.id.btn_play_chat);
            duration_audio = itemView.findViewById(R.id.duration);
            layout_voice_receiver = itemView.findViewById(R.id.layout_voice_receiver);
            duration_audio_receiver = itemView.findViewById(R.id.duration_receiver);
            card_sender = itemView.findViewById(R.id.carview_msg_sender);
            nombreUsuarioRespuesta = itemView.findViewById(R.id.nombre_usuario_respuesta);
            texto_aresponder_sender = itemView.findViewById(R.id.texto_msg_antiguo);
            texto_respuesta_sender = itemView.findViewById(R.id.texto_msg);
            img_responder_sender = itemView.findViewById(R.id.img_msg_antiguo);
            card_receiver = itemView.findViewById(R.id.carview_msg_receiver);
            nombreUsuarioRespuestaReceiver = itemView.findViewById(R.id.nombre_res_receiver);
            img_responder_receiver = itemView.findViewById(R.id.img_msg_receiver);
            texto_aresponder_receiver = itemView.findViewById(R.id.texto_msg_receiver);
            texto_respuesta_receiver = itemView.findViewById(R.id.texto_msg_new_receiver);
            play_btn_sender = itemView.findViewById(R.id.play_btn_video_sender);
            play_btn_receiver = itemView.findViewById(R.id.play_btn_video_receiver);
            videoViewSender = itemView.findViewById(R.id.videoSender);
            voice_video_sender = itemView.findViewById(R.id.voice_sender_video);
            videoViewReceiver = itemView.findViewById(R.id.videoReceiver);
            voice_video_receiver = itemView.findViewById(R.id.voice_receiver_video);
            time_video_sender= itemView.findViewById(R.id.sender_time_video);
            time_video_receiver = itemView.findViewById(R.id.receiver_time_video);
            msg_img_sender = itemView.findViewById(R.id.msg_sender_img);
            msg_img_receiver = itemView.findViewById(R.id.msg_receiver_img);

        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_messages_layout,viewGroup,false);

      mAuth = FirebaseAuth.getInstance();


      return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int i) {

    final String messageSenderID = mAuth.getCurrentUser().getUid();
    final Messages messages = usermessagesList.get(i);
    aes = new AES("lv39eptlvuhaqqsr");

    final String fromUserID = messages.getFrom();
    String formMessageType = messages.getType();
    String type_respuesta = messages.getType();
    boolean Vista = messages.isIsseen();


//    if (i == usermessagesList.size()-1 ){
//        if (messages.isIsseen()){
//            holder.txt_seen.setText("seen");
//        }else {
//            holder.txt_seen.setText("delivered");
//        }
//    }else {
//        holder.txt_seen.setVisibility(View.GONE);
//    }


    reference = FirebaseStorage.getInstance();
    usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
    usersRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.hasChild("image")){
                String reciverImage = dataSnapshot.child("image").getValue().toString();

                Picasso.get().load(reciverImage).resize(50,50).placeholder(R.drawable.profile_image).into(holder.reciverProfileImage);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
        //los datos guardados para los background de los textview de senderMessage
        SharedPreferences preferences= holder.itemView.getContext().getSharedPreferences("color",Context.MODE_PRIVATE);
        final String color = preferences.getString("color","no existe");
        //los datos guardados para los textos de colores
        SharedPreferences textoColor = holder.itemView.getContext().getSharedPreferences("texto",Context.MODE_PRIVATE);
        final String texto =textoColor.getString("texto","no existe");

        //aqui empieza los colores para el backgroun del TextView Del SenderMessage
        if (color.equals("morado")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_morado_message_sender);
            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else if (color.equals("amarillo")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_amarillo_personalizado_message);
            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else if (color.equals("rosado")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_rosa_personalizado_message);
            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else if (color.equals("azul")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_azul_oscuro_personalizado_message);
            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else if (color.equals("celeste")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_azul_personalizado_message);
//            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else if (color.equals("verde")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_verde_personalizao_message);
            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else if (color.equals("rojo")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_rojo_personalizado_messages);
        }else if (color.equals("blanco")){
            holder.senderMessageText.setBackgroundResource(R.drawable.color_blanco_personalizado);
        }else {
            holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
        }
        //aqui termina los colores para el backgroun del TextView Del SenderMessage


        // desde aqui empieza los colores apra el texto
        if (texto.equals("negro")){
            holder.senderMessageText.setTextColor(Color.parseColor("#000000"));
        }else if (texto.equals("azul")){
            holder.senderMessageText.setTextColor(Color.parseColor("#3F51B5"));
        }else if (texto.equals("amarillo")){
            holder.senderMessageText.setTextColor(Color.parseColor("#FFEB3B"));
        }else if (texto.equals("rojo")){
            holder.senderMessageText.setTextColor(Color.parseColor("#000000"));
        }else if (texto.equals("verde")){
            holder.senderMessageText.setTextColor(Color.parseColor("#4CAF50"));
        }else if (texto.equals("blanco")){
            holder.senderMessageText.setTextColor(Color.parseColor("#ffffff"));
        }else {
            holder.senderMessageText.setTextColor(Color.parseColor("#000000"));
        }
        //aqui termina los colores del texto

        holder.reciverMessageText.setVisibility(View.GONE);//estas son para mostrar o no texto
        holder.reciverProfileImage.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.card_sender.setVisibility(View.GONE);
        holder.card_receiver.setVisibility(View.GONE);

        holder.messageSenderPicture.setVisibility(View.GONE);//aqui serra las imagenes
        holder.messageReceiverPicture.setVisibility(View.GONE);
        holder.play_btn_receiver.setVisibility(View.GONE);
        holder.play_btn_sender.setVisibility(View.GONE);
        holder.videoViewSender.setVisibility(View.GONE);
        holder.voice_video_sender.setVisibility(View.GONE);
        holder.voice_video_receiver.setVisibility(View.GONE);
        holder.videoViewReceiver.setVisibility(View.GONE);
        holder.time_video_sender.setVisibility(View.GONE);
        holder.time_video_receiver.setVisibility(View.GONE);
        holder.msg_img_sender.setVisibility(View.GONE);
        holder.msg_img_receiver.setVisibility(View.GONE);


        holder.lyt_parent.setActivated(selected_items.get(i,false));

        if (fromUserID.equals(messageSenderID)) {
            if (messages.isIsseen()) {
                holder.visto.setVisibility(View.VISIBLE);
                holder.txt_seen1.setVisibility(View.VISIBLE);
                holder.txt_seen1.setText("Visto");
                holder.visto.setImageResource(R.drawable.visto);
            }else {
                holder.txt_seen1.setVisibility(View.VISIBLE);
                holder.txt_seen1.setText("No visto");
                holder.visto.setVisibility(View.VISIBLE);
                holder.visto.setImageResource(R.drawable.novisto);
            }

        }else {
            holder.visto.setVisibility(View.GONE);
            holder.txt_seen1.setVisibility(View.GONE);
//            holder.txt_seen.setVisibility(View.GONE);
//            if (messages.isIsseen()) {
//                holder.txt_seen1.setText("Visto");
//            }else {
//                holder.txt_seen1.setText("No visto");
//            }
        }



        if (formMessageType.equals("text")){

        if (fromUserID.equals(messageSenderID)){

            holder.senderMessageText.setVisibility(View.VISIBLE);
          //  holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
         //   holder.senderMessageText.setTextColor(Color.BLACK);//OPCIONAL
            try {
                String msg = aes.decrypt(messages.getMessage());
                holder.senderMessageText.setText(msg+"\n \n"+messages.getTime());
                date = messages.getDate()+ "";
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            holder.reciverMessageText.setVisibility(View.VISIBLE);
            holder.reciverProfileImage.setVisibility(View.VISIBLE);
            //holder.reciverMessageText.setVisibility(View.VISIBLE);

            holder.reciverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
         //   holder.reciverMessageText.setTextColor(Color.BLACK);//OPCIONAL
            try {
                String msg = aes.decrypt(messages.getMessage());
                holder.reciverMessageText.setText(msg+"\n "+messages.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    } else if (formMessageType.equals("respuesta")){
            if (fromUserID.equals(messageSenderID)){


                holder.card_sender.setVisibility(View.VISIBLE);
                //  holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                //   holder.senderMessageText.setTextColor(Color.BLACK);//OPCIONAL
                try {
                    String type = messages.getType_responder();
                    if (type.equals("text")){
                        String msg = aes.decrypt(messages.getMessage());
                        String msgAresponder = messages.getMsg_sender_responder();
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_aresponder_sender.setText(msgAresponder);
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                    }else if (type.equals("image")){
                        String msg = aes.decrypt(messages.getMessage());
                        String img = messages.getMsg_sender_responder();
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(img).into(holder.img_responder_sender);
                    }else if (type.equals("pdf")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(R.drawable.pdficonodialogo).into(holder.img_responder_sender);
                    }else if (type.equals("xlsx")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(R.drawable.excel_logo).into(holder.img_responder_sender);
                    }else if (type.equals("docx")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(R.drawable.wordiconodialogo).into(holder.img_responder_sender);
                    }else if (type.equals("mp3")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db").into(holder.img_responder_sender);
                    }else if (type.equals("mp4")){
                        String msg = aes.decrypt(messages.getMessage());
                        String img = messages.getMsg_sender_responder();
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(img).into(holder.img_responder_sender);
                    }else if (type.equals("gif")){
                        String msg = aes.decrypt(messages.getMessage());
                        String img = messages.getMsg_sender_responder();
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(img).override(100,100).into(holder.img_responder_sender);
                    }else if (type.equals("audio")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.texto_aresponder_sender.setVisibility(View.GONE);
                        holder.img_responder_sender.setVisibility(View.VISIBLE);
                        holder.nombreUsuarioRespuesta.setText(messages.getMsg_responder_nombre_responder());
                        holder.texto_respuesta_sender.setText(msg + "\n " + messages.getTime());
                        Glide.with(context.getApplicationContext()).load(R.drawable.play_button).into(holder.img_responder_sender);
                    }

                    date = messages.getDate()+ "";
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else {


                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.card_receiver.setVisibility(View.VISIBLE);
                //holder.reciverMessageText.setVisibility(View.VISIBLE);

//                holder.reciverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                //   holder.reciverMessageText.setTextColor(Color.BLACK);//OPCIONAL
                try {
                    String type = messages.getType_responder();
                    if (type.equals("text")){
                        String msg = aes.decrypt(messages.getMessage());
                        String msgAresponder = messages.getMsg_sender_responder();
                        holder.texto_respuesta_receiver.setText(msg+"\n "+messages.getTime());
                        holder.texto_aresponder_receiver.setText(msgAresponder);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                    }else if (type.equals("image")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(messages.getMsg_sender_responder()).into(holder.img_responder_receiver);
                    }else if (type.equals("pdf")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(R.drawable.pdficonodialogo).into(holder.img_responder_receiver);
                    }else if (type.equals("docx")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(R.drawable.wordiconodialogo).into(holder.img_responder_receiver);
                    }else if (type.equals("mp3")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db").into(holder.img_responder_receiver);
                    }else if (type.equals("mp4")){
                        String msg = aes.decrypt(messages.getMessage());
                        String msgAresponder = messages.getMsg_sender_responder();
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(msgAresponder).into(holder.img_responder_receiver);
                    }else if (type.equals("gif")){
                        String msg = aes.decrypt(messages.getMessage());
                        String msgAresponder = messages.getMsg_sender_responder();
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(msgAresponder).override(100,100).into(holder.img_responder_receiver);
                    }else if (type.equals("xlsx")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(R.drawable.excel_logo).into(holder.img_responder_receiver);
                    }else if (type.equals("audio")){
                        String msg = aes.decrypt(messages.getMessage());
                        holder.img_responder_receiver.setVisibility(View.VISIBLE);
                        holder.texto_aresponder_receiver.setVisibility(View.GONE);
                        holder.texto_respuesta_receiver.setText(msg);
                        holder.nombreUsuarioRespuestaReceiver.setText(messages.getMsg_responder_nombre_responder());
                        Glide.with(context.getApplicationContext()).load(R.drawable.play_button).into(holder.img_responder_receiver);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else if (formMessageType.equals("image")){
            //esto es para las imagenes
            if (fromUserID.equals(messageSenderID)){

                holder.messageSenderPicture.setVisibility(View.VISIBLE);

                     Picasso.get().load(messages.getMessage()).resize(150,150).into(holder.messageSenderPicture, new Callback() {
                         @Override
                         public void onSuccess() {

                         }

                         @Override
                         public void onError(Exception e) {
                            Picasso.get().load(R.mipmap.ic_launcher).into(holder.messageSenderPicture);
                         }
                     });
                     if (messages.getMsgImage() != null &&!TextUtils.isEmpty(messages.getMsgImage())){
                         holder.msg_img_sender.setVisibility(View.VISIBLE);
                         holder.msg_img_sender.setText(messages.getMsgImage() + "\n" + messages.getTime());
                     }

           //Picasso.get().load(messages.getMessage()).resize(1000, 1000).centerInside().into(holder.messageSenderPicture);

            }else {
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
               Picasso.get().load(messages.getMessage()).resize(150,150).into(holder.messageReceiverPicture, new Callback() {
                   @Override
                   public void onSuccess() {

                   }

                   @Override
                   public void onError(Exception e) {
                        Picasso.get().load(R.mipmap.ic_launcher).into(holder.messageReceiverPicture);
                   }
               });

               if (messages.getMsgImage()!=null && !TextUtils.isEmpty(messages.getMsgImage())){
                   holder.msg_img_receiver.setVisibility(View.VISIBLE);
                   holder.msg_img_receiver.setText(messages.getMsgImage()+"\n" + messages.getTime());
               }


            //  Picasso.get().load(messages.getMessage()).resize(150, 150).centerInside().into(holder.messageSenderPicture);
            }
        }else if (formMessageType.equals("pdf")){
            if (fromUserID.equals(messageSenderID)){
              //esto es para archivos y lo descargara el que envia
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
              //  holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load(R.drawable.pdficonodialogo)
                        .into(holder.messageSenderPicture);


            }else {
                //y esto es apra quien recive
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                  //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load(R.drawable.pdficonodialogo)
                        .into(holder.messageReceiverPicture);

            }
        }else if (formMessageType.equals("docx")){
            if (fromUserID.equals(messageSenderID)){
                //esto es para archivos y lo descargara el que envia
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                //  holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load(R.drawable.wordiconodialogo)
                        .into(holder.messageSenderPicture);


            }else {
                //y esto es apra quien recive
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load(R.drawable.wordiconodialogo)
                        .into(holder.messageReceiverPicture);

            }

        } else if (formMessageType.equals("xlsx")){
            if (fromUserID.equals(messageSenderID)){
                //esto es para archivos y lo descargara el que envia
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                //  holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load(R.drawable.excel_logo)
                        .into(holder.messageSenderPicture);


            }else {
                //y esto es apra quien recive
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
//                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                Picasso.get()
                        .load(R.drawable.excel_logo)
                        .into(holder.messageReceiverPicture);

            }
        } else if (formMessageType.equals("audio")){
            //aquii es para que muestrre la foto del audio
            if (fromUserID.equals(messageSenderID)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db")
                        .into(holder.messageSenderPicture);
            }else {
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db")
                        .into(holder.messageReceiverPicture);

            }

        }else if (formMessageType.equals("mp3")){
            if (fromUserID.equals(messageSenderID)){
                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db")
                        .into(holder.messageSenderPicture);
            }else {
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load("https://firebasestorage.googleapis.com/v0/b/messenger-72201.appspot.com/o/audio.png?alt=media&token=61c50cd7-8e97-435a-87c1-08334c47b2db")
                        .into(holder.messageReceiverPicture);

            }
        } else if (formMessageType.equals("gif")){
            if (fromUserID.equals(messageSenderID)){

                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                try {
                    Glide.with(context.getApplicationContext()).load(messages.getMessage()).override(100,100).into(holder.messageSenderPicture);
                }catch (Exception e){
                    e.printStackTrace();
                }
                //Picasso.get().load(messages.getMessage()).resize(1000, 1000).centerInside().into(holder.messageSenderPicture);
            }else {
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
                holder.messageReceiverPicture.setVisibility(View.VISIBLE);

                try {
                    Glide.with(context.getApplicationContext()).load(messages.getMessage()).override(100,100).into(holder.messageReceiverPicture);
                }catch (Exception e){
                    e.printStackTrace();
                }



                //  Picasso.get().load(messages.getMessage()).resize(150, 150).centerInside().into(holder.messageSenderPicture);
            }
        }else if (formMessageType.equals("mp4")){
            if (fromUserID.equals(messageSenderID)){
                //esto es para archivos y lo descargara el que envia
//                holder.messageSenderPicture.setVisibility(View.VISIBLE);
                try {
                    holder.videoViewSender.setVideoPath(messages.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }
                holder.videoViewSender.setVisibility(View.VISIBLE);
                holder.voice_video_sender.setVisibility(View.VISIBLE);
                holder.time_video_sender.setVisibility(View.VISIBLE);
                holder.videoViewSender.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setVolume(0,0);
                        mp.start();
                        mp.setLooping(true);

                        holder.time_video_sender.setText(getTimeVideo(mp.getDuration()/1000));

                        float videoRatio = mp.getVideoWidth()/(float)mp.getVideoHeight();
                        float screenRatio = holder.videoViewSender.getWidth() / (float) holder.videoViewSender.getHeight();

                        float scale = videoRatio / screenRatio;
                        if (scale >=1f){
                            holder.videoViewSender.setScaleX(scale);
                        }else {
                            holder.videoViewSender.setScaleY(1f/scale);
                        }
                    }
                });
                //  holder.messageSenderPicture.setBackgroundResource(R.drawable.file);
                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
                holder.play_btn_sender.setVisibility(View.VISIBLE);
                holder.play_btn_sender.setAlpha(0.4f);
//                try {
//                    Glide.with(context.getApplicationContext()).load(messages.getMessage()).override(150,150).into(holder.messageSenderPicture);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }


            }else {
                //y esto es apra quien recive
                holder.reciverProfileImage.setVisibility(View.VISIBLE);
           //     holder.messageReceiverPicture.setVisibility(View.VISIBLE);
                try {
                    holder.videoViewReceiver.setVideoPath(messages.getMessage());
                }catch (Exception e){
                    e.printStackTrace();
                }
                holder.videoViewReceiver.setVisibility(View.VISIBLE);
                holder.time_video_receiver.setVisibility(View.VISIBLE);
                holder.videoViewReceiver.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setVolume(0,0);
                        mp.start();
                        mp.setLooping(true);

                        holder.time_video_receiver.setText(getTimeVideo(mp.getDuration()/1000));

                        float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                        float screenRatio = holder.videoViewReceiver.getWidth() / (float) holder.videoViewReceiver.getHeight();
                        float scale = videoRatio / screenRatio;
                        if ( scale >= 1f){
                            holder.videoViewReceiver.setScaleX(scale);
                        }else {
                            holder.videoViewReceiver.setScaleY(1f/scale);
                        }
                    }
                });

                holder.voice_video_receiver.setVisibility(View.VISIBLE);
                holder.play_btn_receiver.setVisibility(View.VISIBLE);
                holder.play_btn_receiver.setAlpha(0.4f);
//                holder.messageReceiverPicture.setBackgroundResource(R.drawable.file);

                //estas linea es para que descargue desde la base de datos la imagen del icono de archivos
//                try {
//                    Glide.with(context.getApplicationContext()).load(messages.getMessage()).override(150,150).into(holder.messageReceiverPicture);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

            }
        }

        if (usermessagesList.get(i).getType() != null){
//            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_MOVE){
//                        estaFocusMsg.onFocusChangeMsg(v,true,messages);
//                    }
//                    return false;
//                }
//            });
        }

          //desde aqui espesamso aser para eleimizar mensajes
        if (fromUserID.equals(messageSenderID)){
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
                    if (usermessagesList.get(i).getType().equals("audio")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //aqui es para poder ver el audio
                                // mandamos la vista que es este contextes la url del audio el titulo y el formato;
                                Uri title=Uri.parse(usermessagesList.get(i).getSender());
                                String foto = title.toString();
                                final Uri url = Uri.parse(usermessagesList.get(i).getMessage());
                                String u = url.toString();
                                PlayVideo playVideo = new PlayVideo();
                                playVideo.play(holder.itemView.getContext(),u,fromUserID,".mp3");
//                                holder.btn_play_auido.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_pause_circle_filled_24));
//                                audioService.playAudioFromUrl(u, new AudioService.OnPlayCallBack() {
//                                    @Override
//                                    public void onFinished() {
//                                        holder.btn_play_auido.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_play_circle_filled_24));
//                                    }
//                                });

                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar el Audio para mi",
                                        "Cancel",
                                        "Eliminar Audio para todos"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){

                                            DeleteSentMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }else if (position==2){
                                            DeleteMessageForEveryone(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });

                    }else if (usermessagesList.get(i).getType().equals("mp3")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //aqui es para poder ver el audio
                                // mandamos la vista que es este contextes la url del audio el titulo y el formato;
                                Uri title=Uri.parse(usermessagesList.get(i).getSender());
                                String foto = title.toString();
                                final Uri url = Uri.parse(usermessagesList.get(i).getMessage());
                                String u = url.toString();
                                PlayVideo playVideo = new PlayVideo();
                                playVideo.play(holder.itemView.getContext(),u,fromUserID,".mp3");
                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar el Audio para mi",
                                        "Cancel",
                                        "Eliminar Audio para todos"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){

                                            DeleteSentMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }else if (position==2){
                                            DeleteMessageForEveryone(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });
                    }

                   else if (usermessagesList.get(i).getType().equals("pdf")||usermessagesList.get(i).getType().equals("docx")||usermessagesList.get(i).getType().equals("xlsx")){

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // i es la posicion que esta alado del holder en onBindViewHolder
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(usermessagesList.get(i).getMessage()));
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar Archivo para mi",
                                        "Cancel",
                                        "Eliminar el Archivo para todos"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){

                                            DeleteSentMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(), MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }else if (position==2){
                                            DeleteMessageForEveryone(i,holder);
                                            EliminarImage(usermessagesList.get(i).getMessage());
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });


                    } else   if (usermessagesList.get(i).getType().equals("text")){

                        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (onClickListener == null) return;
                                onClickListener.onItemClick(v,messages,i);
                            }
                        });
                        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                if (onClickListener == null) return false;
                                onClickListener.onItemLongClick(v,messages,i);
                                return true;
                            }
                        });
//
//                        holder.itemView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //para eliminar mensajes
//                                CharSequence options[] = new CharSequence[]{
//                                        "Eliminar para mi",
//                                        "Cancel",
//                                        "Eliminar Mensaje para todos"
//                                };
//                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
//                                builder.setTitle("Eliminar Mensaje");
//                                builder.setItems(options, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int position) {
//
//                                        if (position ==0){
//                                            DeleteSentMessages(i,holder);
//
//                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
//                                            holder.itemView.getContext().startActivity(intent);
//                                        }else if (position==2){
//                                            DeleteMessageForEveryone(i,holder);
//
//                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
//                                            holder.itemView.getContext().startActivity(intent);
//                                        }
//
//                                    }
//                                });
//                                builder.show();
//                            }
//                        });

                    } else if (usermessagesList.get(i).getType().equals("image")){

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(holder.itemView.getContext(), ImageViewerActivity.class);
                                intent.putExtra("url",usermessagesList.get(i).getMessage());
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar Imagen para mi",
                                        "Cancel",
                                        "Eliminar la Imagen para todos"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteSentMessages(i,holder);

                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }else if (position==2){
                                            DeleteMessageForEveryone(i,holder);
                                            EliminarImage(usermessagesList.get(i).getMessage());
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });

                    }else if (usermessagesList.get(i).getType().equals("gif")){

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, ViewGifGrande.class);
                                intent.putExtra("gif",usermessagesList.get(i).getMessage());
                                context.startActivity(intent);
                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                //para eliminar mensajes
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar el Gif para mi",
                                        "Cancel",
                                        "Eliminar Gif para todos"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteSentMessages(i,holder);

                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }else if (position==2){
                                            DeleteMessageForEveryone(i,holder);

                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });
                    }else if (usermessagesList.get(i).getType().equals("mp4")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, VideoActivity.class);
                                intent.putExtra("url",usermessagesList.get(i).getMessage());
                                context.startActivity(intent);
                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                //para eliminar mensajes
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar el Video para mi",
                                        "Cancel",
                                        "Eliminar Video para todos"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteSentMessages(i,holder);

                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }else if (position==2){
                                            DeleteMessageForEveryone(i,holder);
                                            EliminarImage(usermessagesList.get(i).getMessage());
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });
                    }else if (usermessagesList.get(i).getType().equals("respuesta")){
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               onClickResMsg.onClickResMsg(messages.getPosition(),v);
                           }
                       });
                    }

//                }
//            });
        }else {//este el si es el usuario reciviente
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

                    if (usermessagesList.get(i).getType().equals("audio")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //aqui es para poder ver el audio
                                // mandamos la vista que es este contextes la url del audio el titulo y el formato;
                                String title="";
                                final Uri url = Uri.parse(usermessagesList.get(i).getMessage());
                                String u = url.toString();
                                PlayVideo playVideo = new PlayVideo();
                                playVideo.play(holder.itemView.getContext(),u,fromUserID,".mp3");
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel",
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });

                    }else if (usermessagesList.get(i).getType().equals("mp3")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //aqui es para poder ver el audio
                                // mandamos la vista que es este contextes la url del audio el titulo y el formato;
                                String title="";
                                final Uri url = Uri.parse(usermessagesList.get(i).getMessage());
                                String u = url.toString();
                                PlayVideo playVideo = new PlayVideo();
                                playVideo.play(holder.itemView.getContext(),u,fromUserID,".mp3");
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel",
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });
                    }else if (usermessagesList.get(i).getType().equals("pdf")||usermessagesList.get(i).getType().equals("docx")||usermessagesList.get(i).getType().equals("xlsx")){

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //i es la posicion que esta alado del holder en onBindViewHolder
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(usermessagesList.get(i).getMessage()));
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel",
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });


                    } else   if (usermessagesList.get(i).getType().equals("text")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //para eliminar mensajes
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel",
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                            }
                        });


                    } else if (usermessagesList.get(i).getType().equals("image")){

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(holder.itemView.getContext(),ImageViewerActivity.class);
                                intent.putExtra("url",usermessagesList.get(i).getMessage());
                                holder.itemView.getContext().startActivity(intent);
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);

                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);
                                        }

                                    }
                                });
                                builder.show();

                                return false;
                            }
                        });

                    }else if (usermessagesList.get(i).getType().equals("gif")){

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context,ViewGifGrande.class);
                                intent.putExtra("gif",usermessagesList.get(i).getMessage());
                                context.startActivity(intent);
                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                //para eliminar mensajes
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel",
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });
                    }else if (usermessagesList.get(i).getType().equals("mp4")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context,VideoActivity.class);
                                intent.putExtra("url",usermessagesList.get(i).getMessage());
                                context.startActivity(intent);
                            }
                        });

                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                //para eliminar mensajes
                                CharSequence options[] = new CharSequence[]{
                                        "Eliminar para mi",
                                        "Cancel",
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Eliminar Mensaje");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if (position ==0){
                                            DeleteReceiveMessages(i,holder);
                                            Intent intent = new Intent(holder.itemView.getContext(),MainActivity.class);
                                            holder.itemView.getContext().startActivity(intent);

                                        }
                                    }
                                });
                                builder.show();
                                return false;
                            }
                        });
                    }else if (usermessagesList.get(i).getType().equals("respuesta")){
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickResMsg.onClickResMsg(messages.getPosition(),v);
                            }
                        });
                    }
//                }
//            });
        }

        toogleCheckedIcon(holder,i);
    }

    private void toogleCheckedIcon(MessageViewHolder holder, int i) {
        if (selected_items.get(i,false)){
            holder.make_msg_sender.setVisibility(View.VISIBLE);
            if (current_selected_idx == i) resetCurrentIndex();
        }else {
            holder.make_msg_sender.setVisibility(View.GONE);
            if (current_selected_idx == i) resetCurrentIndex();
        }
    }

    public void toggleSelection(int pos){
        current_selected_idx = pos;
        if (selected_items.get(pos,false)){
            selected_items.delete(pos);
        }else {
            selected_items.put(pos,true);
        }
        notifyItemChanged(pos);
    }

    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++){
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public String messageID(int position){
        return usermessagesList.get(position).getMessageID();
    }

    public String messageTO(int postion){
        return usermessagesList.get(postion).getTo();
    }
    public String messageFrom(int position){
        return usermessagesList.get(position).getFrom();
    }

    public void clearSelections(){
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount(){
        return selected_items.size();
    }

    public Messages getItem(int position){
        return usermessagesList.get(position);
    }


    private void resetCurrentIndex(){
        current_selected_idx = -1;
    }

    private void EliminarImage(String message) {
        final StorageReference ref = reference.getReferenceFromUrl(message);
        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(context, "Exito", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Error al Eliminar la Imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public long getHeaderId(int position) {
        return usermessagesList.get(position).getFechaTransfor().getDay();
    }

    @Override
    public FechaHeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view  =LayoutInflater.from(context).inflate(R.layout.header_fecha_chat,parent,false);
        return new FechaHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(FechaHeaderViewHolder holder, int i) {
        Messages messages = usermessagesList.get(i);
        holder.fecha_msg.setText(formatDate(messages.getFechaTransfor(),messages.getFecha()));
    }

    private String formatDate (Date date,long fecha){
        TimeAgo timeAgo = new TimeAgo();
       return timeAgo.getTimeAgo(fecha,date);
    }

    @Override
    public int getItemCount() {
       return usermessagesList.size();
    }

    private void DeleteSentMessages(final int position ,final MessageViewHolder holder){
  //metodo para eliminar los mensajes
         DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
         rootRef.child("Messages")
                 .child(usermessagesList.get(position).getFrom())
                 .child(usermessagesList.get(position).getTo())
                 .child(usermessagesList.get(position).getMessageID())
                 .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
             }
         });
    }

    private void DeleteReceiveMessages(final int position ,final MessageViewHolder holder){
        //metodo para yo  eliminar los mensajes para
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(usermessagesList.get(position).getTo())
                .child(usermessagesList.get(position).getFrom())
                .child(usermessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void DeleteMessageForEveryone(final int position ,final MessageViewHolder holder){
        //metodo para eliminar los mensajes para todos
        final DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(usermessagesList.get(position).getTo())
                .child(usermessagesList.get(position).getFrom())
                .child(usermessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    rootRef.child("Messages")
                            .child(usermessagesList.get(position).getFrom())
                            .child(usermessagesList.get(position).getTo())
                            .child(usermessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                              if (task.isSuccessful()){
                                  Toast.makeText(holder.itemView.getContext(), "Eliminado Correcto", Toast.LENGTH_SHORT).show();
                              }
                        }
                    });


                }else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean DeleteMessages(final String messageID, final String to, final String from){
        //metodo para eliminar los mensajes para todos

        String message = EncrypMessage("mensaje eliminado");
        final HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("message",message);

        final DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(to)
                .child(from)
                .child(messageID)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    rootRef.child("Messages")
                            .child(from)
                            .child(to)
                            .child(messageID)
                            .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                            }
                        }
                    });


                }else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return true;
    }
    private String  EncrypMessage(String msg){
        aes = new AES("lv39eptlvuhaqqsr");
        try {
            String cadenaEncryp = aes.encrypt(msg);
            return cadenaEncryp;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void DeleteMsg(int id){
        usermessagesList.remove(id);
        notifyItemChanged(id);
    }

    public String fecha(){
        return date;
    }

    private String getTimeVideo(int seconds){
        int hr = seconds / 3600;
        int rem = seconds % 3600;
        int mn = rem / 60;
        int sec = rem % 60;
        return String.format("%02d",hr)+  ":" + String.format("%02d",mn)+ ":" + String.format("%02d",sec);
    }
}
