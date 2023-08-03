package com.example.whatsappclone.helper;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class UsuarioFirebase {
    public static String getIdUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuthReference();
        String email = usuario.getCurrentUser().getEmail();
        String id = Base64Custom.codeBase64(email);
        return id;
    }
}
