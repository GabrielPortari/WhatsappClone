package com.example.whatsappclone.model;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Conversa {
    private String idUsuarioQueEnvia;
    private String idUsuarioQueRecebe;
    private String ultimaMensagem;
    private Usuario usuarioExibido;

    public Conversa() {
    }

    public void salvarNoFirebase(){
        //Salva a ultima mensagem de uma conversa no firebase
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference conversaReference = databaseReference.child("conversas");

        conversaReference.child(this.getIdUsuarioQueEnvia())
                .child(this.getIdUsuarioQueRecebe())
                .setValue(this);
    }
    public String getIdUsuarioQueEnvia() {
        return idUsuarioQueEnvia;
    }

    public void setIdUsuarioQueEnvia(String idUsuarioQueEnvia) {
        this.idUsuarioQueEnvia = idUsuarioQueEnvia;
    }

    public String getIdUsuarioQueRecebe() {
        return idUsuarioQueRecebe;
    }

    public void setIdUsuarioQueRecebe(String idUsuarioQueRecebe) {
        this.idUsuarioQueRecebe = idUsuarioQueRecebe;
    }

    public String getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(String ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }

    public Usuario getUsuarioExibido() {
        return usuarioExibido;
    }

    public void setUsuarioExibido(Usuario usuarioExibido) {
        this.usuarioExibido = usuarioExibido;
    }
}
