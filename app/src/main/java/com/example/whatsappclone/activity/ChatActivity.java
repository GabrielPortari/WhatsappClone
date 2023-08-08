package com.example.whatsappclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView nomeChat;
    private CircleImageView fotoChat;
    private Usuario usuarioConversa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Configuraçoes iniciais
        nomeChat = findViewById(R.id.texNome_chat);
        fotoChat = findViewById(R.id.circleImageView_chat);

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar Dados do usuario clicado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            usuarioConversa = (Usuario) bundle.getSerializable("usuarioSelecionado");
            nomeChat.setText(usuarioConversa.getNome());
            String foto = usuarioConversa.getFoto();
            if (foto != null) {
                Uri uri = Uri.parse(usuarioConversa.getFoto());
                Glide.with(ChatActivity.this).load(uri).into(fotoChat);
            }else{
                fotoChat.setImageResource(R.drawable.padrao);
            }
        }

    }
}