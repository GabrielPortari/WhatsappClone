package com.example.whatsappclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.MensagemAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Mensagem;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private TextView nomeChat;
    private CircleImageView fotoChat;
    private EditText editMensagem;
    private ImageView imageCamera;
    private FloatingActionButton fabChat;
    private Usuario contato;
    private Grupo grupo;

    private RecyclerView recyclerMensagem;
    private MensagemAdapter mensagemAdapter;

    private DatabaseReference databaseReference;
    private DatabaseReference mensagemReference;
    private StorageReference storageReference;
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
        imageCamera = findViewById(R.id.imageView_chat);
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
            if(bundle.containsKey("grupoSelecionado")){
                /*RECUPERAÇÃO DE DADOS PARA UMA CONVERSA EM GRUPO*/
                grupo = (Grupo) bundle.getSerializable("grupoSelecionado");
                nomeChat.setText(grupo.getNome());

                String foto = grupo.getFoto();
                if(foto != null){
                    Uri url = Uri.parse(grupo.getFoto());
                    Glide.with(ChatActivity.this).load(url).into(fotoChat);
                }else{
                    fotoChat.setImageResource(R.drawable.padrao);
                }

            }else{
                /*RECUPERAÇÃO DE DADOS PARA UMA CONVERSA 1:1*/
                contato = (Usuario) bundle.getSerializable("usuarioSelecionado");
                nomeChat.setText(contato.getNome());
                String foto = contato.getFoto();
                if (foto != null) {
                    Uri url = Uri.parse(contato.getFoto());
                    Glide.with(ChatActivity.this).load(url).into(fotoChat);
                }else{
                    fotoChat.setImageResource(R.drawable.padrao);
                }
                //Recuperar dados do usuario que recebe
                idUsuarioRecebe = Base64Custom.codeBase64(contato.getEmail());
            }
        }

        //Configuração adapter
        mensagemAdapter = new MensagemAdapter(mensagens, getApplicationContext());

        //Configuração recycler
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagem.setLayoutManager(layoutManager);
        recyclerMensagem.setHasFixedSize(true);
        recyclerMensagem.setAdapter(mensagemAdapter);

        //Configurações reference
        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
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
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if(intent.resolveActivity(getPackageManager()) != null){ //NAO ABRE A CAMERA NO EMULADOR POR CAUSA DO IF
                cameraActivityResultLauncher.launch(intent);
                //}
            }
        });

    }
    public void enviarMensagem(View v){
        String textMsg = editMensagem.getText().toString();
        if(!textMsg.isEmpty()){

            if(contato != null){
                //Criar mensagem
                Mensagem mensagem = new Mensagem();
                mensagem.setMensagem(textMsg);
                mensagem.setIdUsuario(idUsuarioEnvia);

                //Salvar mensagem no firebase
                salvarMensagemFirebase(idUsuarioEnvia, idUsuarioRecebe, mensagem);
                editMensagem.setText("");

                //Salvar conversa
                Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

                salvarConversa(idUsuarioEnvia, idUsuarioRecebe, contato, mensagem, false);
                salvarConversa(idUsuarioRecebe, idUsuarioEnvia, usuarioLogado, mensagem, false);
            }else{
                for(Usuario membroGrupo : grupo.getMembros()){
                    String idMembroGrupo = Base64Custom.codeBase64(membroGrupo.getEmail());
                    String idUsuario = UsuarioFirebase.getIdUsuario();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuario);
                    mensagem.setMensagem(textMsg);

                    //Salvar mensagem para os membros
                    salvarMensagemFirebase(idMembroGrupo, grupo.getId(), mensagem);

                    salvarConversa(idMembroGrupo, grupo.getId(), contato, mensagem, true);
                }
            }

        }else{
            Toast.makeText(ChatActivity.this, "Digite uma mensagem", Toast.LENGTH_SHORT).show();
        }
    }
    private void salvarConversa(String idEnvia, String idRecebe, Usuario usuarioExibido, Mensagem mensagem, Boolean isGroup){

        Conversa conversa = new Conversa();

        conversa.setIdUsuarioQueEnvia(idEnvia);
        conversa.setIdUsuarioQueRecebe(idRecebe);
        conversa.setUltimaMensagem(mensagem.getMensagem());

        if(isGroup){ //Salvar conversa de grupo
            conversa.set_isGrupo(true);
            conversa.setGrupo(grupo);
        }else{ //Salvar conversa 1:1
            conversa.set_isGrupo(false);
            conversa.setUsuarioExibido(usuarioExibido);
        }

        conversa.salvarNoFirebase();
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
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Bitmap image = null;
                        try{
                            image = (Bitmap) result.getData().getExtras().get("data");
                            if(image != null){
                                //Recuperar dados da imagem para o firebase
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                byte[] dataImage = baos.toByteArray();

                                //Gerar id unico para nome da imagem
                                String idImage = UUID.randomUUID().toString();

                                StorageReference imgRef = storageReference.child("imagens")
                                        .child("fotos")
                                        .child(idUsuarioEnvia)
                                        .child(idImage);

                                UploadTask uploadTask = imgRef.putBytes(dataImage);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Erro", "Falha ao fazer o upload");
                                        Toast.makeText(ChatActivity.this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        imgRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();

                                                //Criar a mensagem
                                                Mensagem mensagem = new Mensagem();
                                                mensagem.setIdUsuario(idUsuarioEnvia);
                                                mensagem.setMensagem("imagem.jpeg");
                                                mensagem.setImagem(url.toString());

                                                //Salvar mensagem no firebase
                                                salvarMensagemFirebase(idUsuarioEnvia, idUsuarioRecebe, mensagem);
                                            }
                                        });
                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}