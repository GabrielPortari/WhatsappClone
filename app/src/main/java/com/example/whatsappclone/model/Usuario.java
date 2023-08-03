package com.example.whatsappclone.model;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String id;

    public Usuario() {
    }
    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference usuario = databaseReference.child("usuarios").child(getId());

        usuario.setValue(this);
    }
    public String getNome() {
        return nome;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
