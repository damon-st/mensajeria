package com.damon.messenger.interfaces;

import android.view.View;

import com.damon.messenger.Model.Messages;

public interface OnClickListener {
    void onItemClick(View view, Messages msg, int pos );
    void onItemLongClick(View view,Messages msg,int pos);
}
