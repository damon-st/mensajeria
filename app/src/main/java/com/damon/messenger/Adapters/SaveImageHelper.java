package com.damon.messenger.Adapters;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;

public class SaveImageHelper implements Target {


    private Context context;
    private WeakReference<AlertDialog> alertDialogWeakReference;
    private WeakReference<ContentResolver> contentResolverWeakReference;
    private String name,desc;

    public SaveImageHelper(Context context, AlertDialog alertDialog,ContentResolver WeakReference, String name,String desc ){
        this.context = context;
        this.alertDialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
        this.contentResolverWeakReference = new WeakReference<ContentResolver>(WeakReference);
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        ContentResolver resolver = contentResolverWeakReference.get();
        AlertDialog alertDialog = alertDialogWeakReference.get();
        if (resolver !=null){
            MediaStore.Images.Media.insertImage(resolver,bitmap,name,desc);
            alertDialog.dismiss();
            Toast.makeText(context, "Imagen Descargada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
