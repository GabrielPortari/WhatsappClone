package com.example.whatsappclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.GrupoSelecionadoAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CadastrarGrupoActivity extends AppCompatActivity {
    private List<Usuario> listaMembrosSelecionados = new ArrayList<>();
    private TextView text_quantidadeParticipantes;
    private EditText edit_nomeGrupo;
    private FloatingActionButton fab_criarGrupo;
    private Toolbar toolbar;
    private ImageView imageGrupo;

    private StorageReference storageReference;

    private GrupoSelecionadoAdapter grupoSelecionadoAdapter;
    private RecyclerView recyclerMembrosSelecionados;

    private Grupo grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_grupo);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Grupo");
        toolbar.setSubtitle("Defina o nome:");
        toolbar.setTitleTextColor(getColor(R.color.white));
        toolbar.setSubtitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        text_quantidadeParticipantes = findViewById(R.id.text_quantidadeParticipantes);
        fab_criarGrupo = findViewById(R.id.fab_confirmarCriarGrupo);
        recyclerMembrosSelecionados = findViewById(R.id.recycler_membrosParticipantes);
        imageGrupo = findViewById(R.id.image_perfilGrupo);
        edit_nomeGrupo = findViewById(R.id.editText_nomeGrupo);
        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        grupo = new Grupo();

        if (getIntent().getExtras() != null) {
            List<Usuario> membros = (List<Usuario>) getIntent().getExtras().getSerializable("membros");
            listaMembrosSelecionados.addAll(membros);
            text_quantidadeParticipantes.setText("Participantes: " + listaMembrosSelecionados.size());
        }

        grupoSelecionadoAdapter = new GrupoSelecionadoAdapter(listaMembrosSelecionados, getApplicationContext());
        RecyclerView.LayoutManager layoutManagerHorizontal = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, false
        );
        recyclerMembrosSelecionados.setLayoutManager(layoutManagerHorizontal);
        recyclerMembrosSelecionados.setHasFixedSize(true);
        recyclerMembrosSelecionados.setAdapter(grupoSelecionadoAdapter);

        imageGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //if(intent.resolveActivity(getPackageManager()) != null){ //NAO ABRE A GALERIA NO EMULADOR POR CAUSA DO IF
                galeriaActivityResultLauncher.launch(intent);
                //}
            }
        });

        fab_criarGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeGrupo = edit_nomeGrupo.getText().toString();

                Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
                listaMembrosSelecionados.add(usuarioLogado); //Adiciona a si mesmo na lista dos grupos

                grupo.setMembros(listaMembrosSelecionados);
                grupo.setNome(nomeGrupo);
                grupo.salvarNoFirebase();

            }
        });
    }
    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap imagemSelecionada;
                        try {
                            Uri localImagemSelecionada = result.getData().getData();
                            imagemSelecionada = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                            if(imagemSelecionada != null){
                                imageGrupo.setImageBitmap(imagemSelecionada);

                                //Salvar imagem no firebase
                                    //Configuração dos dados da imagem para salvar no firebase
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagemSelecionada.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                byte[] imageData = baos.toByteArray();
                                    //Cria o caminho no banco de dados
                                StorageReference imagemRef = storageReference
                                        .child("imagens")
                                        .child("grupos")
                                        .child(grupo.getId() + ".jpeg");
                                    //Criação da task de upload
                                UploadTask uploadTask = imagemRef.putBytes(imageData);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CadastrarGrupoActivity.this, "Erro ao salvar imagem do grupo", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(CadastrarGrupoActivity.this, "Grupo criado com sucesso", Toast.LENGTH_SHORT).show();
                                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();
                                                grupo.setFoto(url.toString());
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
            });
}