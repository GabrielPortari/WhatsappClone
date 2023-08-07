package com.example.whatsappclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Permissao;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigActivity extends AppCompatActivity {
    private String[] permissoes = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButton_camera, imageButton_galeria;
    //private static final int SELECAO_CAMERA = 100;
    //private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageView;
    private EditText editNome;
    private ImageView imageAttNome;
    private StorageReference storageReference;
    private String idUsuario;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        //definição dos findviewbyid
        imageButton_galeria = findViewById(R.id.imageButton_galeria);
        imageButton_camera = findViewById(R.id.imageButton_camera);
        circleImageView = findViewById(R.id.circleImageView_config);
        editNome = findViewById(R.id.editNome_config);
        imageAttNome = findViewById(R.id.imageAtualizaNome_config);

        //configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        idUsuario = UsuarioFirebase.getIdUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        //Validação das permissões
        Permissao.validarPermissao(permissoes, this, 1);

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar dados do usuario
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        Uri url = user.getPhotoUrl();
        if(url != null){
            Glide.with(this).load(url).into(circleImageView);
        }else{
            circleImageView.setImageResource(R.drawable.padrao);
        }
        editNome.setText(user.getDisplayName());

        //Listeners dos botões de camera e galeria
        imageButton_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chamando intent para abrir a câmera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //if(intent.resolveActivity(getPackageManager()) != null){ //NAO ABRE A CAMERA NO EMULADOR POR CAUSA DO IF
                    cameraActivityResultLauncher.launch(intent);
                //}
            }
        });
        imageButton_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //if(intent.resolveActivity(getPackageManager()) != null){ //NAO ABRE A GALERIA NO EMULADOR POR CAUSA DO IF
                    galeriaActivityResultLauncher.launch(intent);
                //}
            }
        });
        imageAttNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = editNome.getText().toString();
                boolean sucesso = UsuarioFirebase.atualizaNomeUsuario(nome);
                if(sucesso){
                    usuarioLogado.setNome(nome);
                    usuarioLogado.atualizarUsuario();
                    Toast.makeText(ConfigActivity.this, "Dados atualizados com sucesso", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacao();
            }
        }
    }

    private void alertaValidacao(){
        /*
        * Quando as aulas foram gravadas, as permissões se comportavam de forma diferente, logo o que foi feito abaixo
        * possivelmente teve algumas funcionalidades alteradas, e as permissões se comportam de forma diferente
        *
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para continuar utilizando o app, as permissões devem ser aceitadas");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        */
    }
    public void atualizaFotoUsuario(Uri url){
        boolean sucess = UsuarioFirebase.atualizaFotoUsuario(url);
        if (sucess) {
            usuarioLogado.setFoto(url.toString());
            usuarioLogado.atualizarUsuario();
            Toast.makeText(this, "Foto alterada", Toast.LENGTH_SHORT).show();
        }
    }
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Bitmap img = null;
                        try{
                            img = (Bitmap) result.getData().getExtras().get("data");
                            if(img != null){
                                circleImageView.setImageBitmap(img);

                                //Recuperar dados da imagem para o firebase
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                byte[] dataImage = baos.toByteArray();

                                //Salvar no firebase
                                final StorageReference imagemRef = storageReference
                                        .child("imagens")
                                        .child("perfil")
                                        .child(idUsuario)
                                        .child("perfil.jpg");
                                UploadTask uploadTask = imagemRef.putBytes(dataImage);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("FB STORAGE", "Falha ao fazer upload da imagem");
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.i("FB STORAGE", "Sucesso ao fazer upload da imagem");
                                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();
                                                atualizaFotoUsuario(url);
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

    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Bitmap img = null;
                        try{
                            Uri localImg = result.getData().getData();
                            img = MediaStore.Images.Media.getBitmap(getContentResolver(), localImg);
                            if(img != null){

                                circleImageView.setImageURI(localImg);
                                //Recuperar dados da imagem para o firebase
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                img.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                byte[] dataImage = baos.toByteArray();

                                //Salvar no firebase
                                final StorageReference imagemRef = storageReference
                                        .child("imagens")
                                        .child("perfil")
                                        .child(idUsuario)
                                        .child("perfil.jpg");
                                UploadTask uploadTask = imagemRef.putBytes(dataImage);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("FB STORAGE", "Falha ao fazer upload da imagem");
                                        Toast.makeText(ConfigActivity.this, "Falha ao fazer upload da imagem", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Log.i("FB STORAGE", "Sucesso ao fazer upload da imagem");
                                        imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                Uri url = task.getResult();
                                                atualizaFotoUsuario(url);
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