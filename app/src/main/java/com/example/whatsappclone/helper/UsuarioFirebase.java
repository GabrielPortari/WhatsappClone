package com.example.whatsappclone.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFirebase {
    /*
    Classe utilizada para recuperar informações do usuário logado no app
     */
    public static String getIdUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuthReference();
        String email = usuario.getCurrentUser().getEmail();
        String id = Base64Custom.codeBase64(email);
        return id;
    }
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuthReference();
        return usuario.getCurrentUser();
    }
    public static boolean atualizaFotoUsuario(Uri url){
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar a foto de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static boolean atualizaNomeUsuario(String nome){
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Log.d("Perfil", "Erro ao atualizar o nome de perfil");
                    }
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public static Usuario getDadosUsuarioLogado(){
       FirebaseUser firebaseUser = getUsuarioAtual();
       Usuario usuario = new Usuario();

       usuario.setEmail(firebaseUser.getEmail());
       usuario.setNome(firebaseUser.getDisplayName());

       if(firebaseUser.getPhotoUrl() == null){
        usuario.setFoto("");
       }else{
           usuario.setFoto(firebaseUser.getPhotoUrl().toString());
       }

       return usuario;
    }
}
