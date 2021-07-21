package com.damon.messenger.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.damon.messenger.Model.Messages;
import com.damon.messenger.R;

import java.util.ArrayList;
import java.util.List;

public class ArchivesUserAdapter extends RecyclerView.Adapter<ArchivesUserAdapter.ArchivesUserViewHolder> {


    List<Messages> archiveList = new ArrayList<>();
    Activity activity;
    int tamano;

    public ArchivesUserAdapter(List<Messages> archiveList, Activity activity) {
        this.archiveList = archiveList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ArchivesUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.img_archives_layout,parent,false);
        return new ArchivesUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchivesUserViewHolder holder, int position) {
        Messages messages = archiveList.get(position);
        String  type = messages.getType();
        System.out.println("type"+ type);
        String  msg = messages.getMessage();


        switch (type){
            case "image":
                Glide.with(activity.getApplicationContext()).load(msg).into(holder.img_archive);
                break;
            case "mp4":
                Glide.with(activity.getApplicationContext()).load(msg).into(holder.img_archive);
                break;
            case "gif":
                Glide.with(activity.getApplicationContext()).load(msg).into(holder.img_archive);
                break;
            case "pdf":
                Glide.with(activity.getApplicationContext()).load(R.drawable.pdficonodialogo).into(holder.img_archive);
                break;
            case "xlsx":
                Glide.with(activity.getApplicationContext()).load(R.drawable.excel_logo).into(holder.img_archive);
                break;
            case "docx":
                Glide.with(activity.getApplicationContext()).load(R.drawable.wordiconodialogo).into(holder.img_archive);
                break;
            case "mp3":
                Glide.with(activity.getApplicationContext()).load(R.drawable.play_button).into(holder.img_archive);
                break;
            case "audio":
                Glide.with(activity.getApplicationContext()).load(R.drawable.play_button).into(holder.img_archive);
                break;
            default:
                holder.layout.removeAllViews();
                break;
//            case "text":
//                holder.layout.removeAllViews();
//                break;
//            case "respuesta":
//                holder.layout.removeAllViews();
//                break;
        }
    }

    @Override
    public int getItemCount() {
        return archiveList.size();
    }


     class ArchivesUserViewHolder extends RecyclerView.ViewHolder{

        ImageView img_archive;
         RelativeLayout layout;
        public ArchivesUserViewHolder(@NonNull View itemView) {
            super(itemView);
            img_archive = itemView.findViewById(R.id.img_archivos);
            layout = itemView.findViewById(R.id.img_archivos_layout);
        }
    }
}
