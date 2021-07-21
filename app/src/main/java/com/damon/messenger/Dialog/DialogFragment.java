package com.damon.messenger.Dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.damon.messenger.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragment extends androidx.fragment.app.DialogFragment {

   Activity activity;
   Button confirmar,cancelar;
   ImageView imagen,imageView;



    public DialogFragment() {
        // Required empty public constructor
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_blank, container, false);
//    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogoEliminar();
    }

    private AlertDialog crearDialogoEliminar() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater =getActivity().getLayoutInflater();
        View v=inflater.inflate(R.layout.fragment_blank,  null);
        builder.setView(v);

        confirmar = v.findViewById(R.id.botonconfirmar);
        cancelar = v.findViewById(R.id.botoncancelar);
        imagen = v.findViewById(R.id.imagenGif);
        imageView = v.findViewById(R.id.cerrar);

        Glide.with(activity).load(R.drawable.tenor).into(imagen);

        eventoBotones();
        return  builder.create();
    }

    private void eventoBotones() {

        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "hola", Toast.LENGTH_SHORT).show();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 dismiss();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof  Activity){
            this.activity = (Activity)context;
        }else {
            throw  new RuntimeException(context.toString()
            + " must implement OnFragmentInteractionListener");
        }
    }
}
