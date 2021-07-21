package com.damon.messenger.interfaces;

import android.view.View;

import com.damon.messenger.Model.Messages;

public interface EstaFocusMsg {
   void onFocusChangeMsg(View view, boolean focus, Messages messages);
}
