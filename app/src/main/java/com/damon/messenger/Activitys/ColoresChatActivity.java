package com.damon.messenger.Activitys;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.damon.messenger.R;

public class ColoresChatActivity extends Activity {

    private ImageView morado,amarillo,rosado,celeste,azul,verde,defecto,rojo,blanco;
    private ImageView blanco_texto,negro_texto,azul_texto,amarillo_texto,verde_texto,rojo_texto;
    private String colorSeleccionado;
    private TextView presentacion;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personalizar_messages_colores);

        morado = findViewById(R.id.color_morado);
        amarillo = findViewById(R.id.color_amarillo);
        rosado = findViewById(R.id.color_rosa);
        celeste = findViewById(R.id.color_celeste);
        azul = findViewById(R.id.color_azul);
        verde = findViewById(R.id.color_verde);
        presentacion = findViewById(R.id.presentacion);
        defecto = findViewById(R.id.color_defecto);
        blanco_texto = findViewById(R.id.color_blanco_texto);
        negro_texto = findViewById(R.id.colo_negro_texto);
        azul_texto = findViewById(R.id.color_azul_texto);
        amarillo_texto = findViewById(R.id.color_amarillo_texto);
        verde_texto = findViewById(R.id.color_verde_texto);
        rojo_texto = findViewById(R.id.color_rojo_texto);
        rojo = findViewById(R.id.color_rojo_imagen);
        blanco = findViewById(R.id.color_blanco_mensaje);

        //desque aqui empieza los colores para las imaeView SenderMessages
        defecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "defecto";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.sender_messages_layout);
            }
        });
        morado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "morado";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_morado_message_sender);

            }
        });
        amarillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "amarillo";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_amarillo_personalizado_message);
            }
        });
        rosado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "rosado";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_rosa_personalizado_message);
            }
        });
        celeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "celeste";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_azul_personalizado_message);
            }
        });
        azul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "azul";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_azul_oscuro_personalizado_message);
            }
        });
        verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "verde";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_verde_personalizao_message);
            }
        });

        rojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "rojo";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_rojo_personalizado_messages);
            }
        });

        blanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "blanco";
                coloSeleccionadoGuaradado(colorSeleccionado);
                presentacion.setBackgroundResource(R.drawable.color_blanco_personalizado);
            }
        });



//   Desde aqui empieza los colores para el texto

        negro_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "negro";
                colorTextoGuardar(colorSeleccionado);
                presentacion.setTextColor(Color.parseColor("#000000"));
            }
        });
        azul_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "azul";
                colorTextoGuardar(colorSeleccionado);
                presentacion.setTextColor(Color.parseColor("#3F51B5"));
            }
        });
        rojo_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado = "rojo";
                colorTextoGuardar(colorSeleccionado);
                presentacion.setTextColor(Color.parseColor("#FF0000"));
            }
        });
        amarillo_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado="amarillo";
                colorTextoGuardar(colorSeleccionado);
                presentacion.setTextColor(Color.parseColor("#FFEB3B"));
            }
        });
        verde_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado="verde";
                colorTextoGuardar(colorSeleccionado);
                presentacion.setTextColor(Color.parseColor("#4CAF50"));
            }
        });
        blanco_texto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSeleccionado="blanco";
                colorTextoGuardar(colorSeleccionado);
                presentacion.setTextColor(Color.parseColor("#ffffff"));
            }
        });



    }
    public void coloSeleccionadoGuaradado(String colorSeleccionado ){
        SharedPreferences color  = getSharedPreferences("color", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = color.edit();
        editor.putString("color",colorSeleccionado);
        editor.apply();
        Toast.makeText(this, "Aplicado el color  "+colorSeleccionado+" Correctamente Ya puedes Verlo", Toast.LENGTH_SHORT).show();
    }
    public void colorTextoGuardar(String  colorSeleccionado){
        SharedPreferences color  = getSharedPreferences("texto", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = color.edit();
        editor.putString("texto",colorSeleccionado);
        editor.apply();
        Toast.makeText(this, "Aplicado el color  "+colorSeleccionado+" Correctamente Ya puedes Verlo", Toast.LENGTH_SHORT).show();
    }
}
