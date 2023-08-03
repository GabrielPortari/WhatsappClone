package com.example.whatsappclone.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.whatsappclone.R;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.example.whatsappclone.helper.Permissao;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfigActivity extends AppCompatActivity {
    private String[] permissoes = new String[]{
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButton_camera, imageButton_galeria;
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageView;
    private StorageReference storageReference;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        //configurações iniciais
        storageReference = ConfiguracaoFirebase.getFirebaseStorageReference();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //Validação das permissões
        Permissao.validarPermissao(permissoes, this, 1);

        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageButton_galeria = findViewById(R.id.imageButton_galeria);
        imageButton_camera = findViewById(R.id.imageButton_camera);
        circleImageView = findViewById(R.id.circleImageView);
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
        * */
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para continuar utilizando o app, as permissões devem ser aceitadas");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        AlertDialog dialog = builder.create();
        //dialog.show();
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

                                StorageReference imagemRef = storageReference
                                        .child("imagens")
                                        .child("perfil")
                                        .child(idUsuario);
                                        //.child();
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
                        try{
                            Uri localImg = result.getData().getData();
                            circleImageView.setImageURI(localImg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
}