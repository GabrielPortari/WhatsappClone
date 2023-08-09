package com.example.whatsappclone.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MensagemAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Mensagem;
import com.example.whatsappclone.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView nomeChat;
    private CircleImageView fotoChat;
    private EditText editMensagem;
    private Usuario contato;
    private FloatingActionButton fabChat;
    private RecyclerView recyclerMensagem;
    private MensagemAdapter mensagemAdapter;
    private DatabaseReference databaseReference;
    private DatabaseReference mensagemReference;
    private ChildEventListener childEventListenerMensagens;

    private List<Mensagem> mensagens = new ArrayList<>();
    private String idUsuarioEnvia;
    private String idUsuarioRecebe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Configuraçoes iniciais
        nomeChat = findViewById(R.id.texNome_chat);
        fotoChat = findViewById(R.id.circleImageView_chat);
        fabChat = findViewById(R.id.fab_enviarMsg);
        editMensagem = findViewById(R.id.editText_mensagem);
        recyclerMensagem = findViewById(R.id.recyclerMensagens);

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera dados do usuario logado
        idUsuarioEnvia = UsuarioFirebase.getIdUsuario();

        //Recuperar Dados do usuario clicado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            contato = (Usuario) bundle.getSerializable("usuarioSelecionado");
            nomeChat.setText(contato.getNome());
            String foto = contato.getFoto();
            if (foto != null) {
                Uri uri = Uri.parse(contato.getFoto());
                Glide.with(ChatActivity.this).load(uri).into(fotoChat);
            }else{
                fotoChat.setImageResource(R.drawable.padrao);
            }
            //Recuperar dados do usuario que recebe
            idUsuarioRecebe = Base64Custom.codeBase64(contato.getEmail());
        }

        //Configuração adapter
        mensagemAdapter = new MensagemAdapter(mensagens, getApplicationContext());

        //Configuração recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagem.setLayoutManager(layoutManager);
        recyclerMensagem.setHasFixedSize(true);
        recyclerMensagem.setAdapter(mensagemAdapter);

        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        mensagemReference = databaseReference
                .child("mensagens")
                .child(idUsuarioEnvia)
                .child(idUsuarioRecebe);
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensagem(v);
            }
        });

    }
    public void enviarMensagem(View v){
        String textMsg = editMensagem.getText().toString();
        if(!textMsg.isEmpty()){
            Mensagem mensagem = new Mensagem();
            mensagem.setMensagem(textMsg);
            mensagem.setIdUsuario(idUsuarioEnvia);

            salvarMensagemFirebase(idUsuarioEnvia, idUsuarioRecebe, mensagem);
            salvarMensagemFirebase(idUsuarioRecebe, idUsuarioEnvia, mensagem);

            editMensagem.setText("");
        }else{
            Toast.makeText(ChatActivity.this, "Digite uma mensagem", Toast.LENGTH_SHORT).show();
        }
    }
    private void salvarMensagemFirebase(String idEnvia, String idRecebe, Mensagem mensagem){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference mensagemRef = databaseReference.child("mensagens");

        mensagemRef
                .child(idEnvia)
                .child(idRecebe)
                .push()
                .setValue(mensagem);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagemReference.removeEventListener(childEventListenerMensagens);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    private void recuperarMensagens(){
        childEventListenerMensagens = mensagemReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensagem mensagem = snapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                mensagemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}