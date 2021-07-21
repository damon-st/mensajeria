package com.damon.messenger.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.messenger.R;

import org.jetbrains.annotations.NotNull;

public class FechaHeaderViewHolder  extends RecyclerView.ViewHolder {

    public TextView fecha_msg;
    public FechaHeaderViewHolder(@NonNull  View itemView) {
        super(itemView);

        fecha_msg = itemView.findViewById(R.id.fecha_msg);
    }
}
