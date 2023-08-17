package com.example.whatsappclone.model;

import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Grupo implements Serializable {
    private String id;
    private String nome;
    private String foto;
    private List<Usuario> membros;

    public Grupo() {
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference grupoReference = databaseReference.child("grupos");

        String idGrupoFirebase = grupoReference.push().getKey();
        setId(idGrupoFirebase);

    }
    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference grupoReference = databaseReference.child("grupos");
        grupoReference.child(getId()).setValue(this);

        for(Usuario membros : getMembros()){
            String idRemetente = Base64Custom.codeBase64(membros.getEmail());
            String idDestinatario = getId();

            Conversa conversa = new Conversa();
            conversa.setIdUsuarioQueEnvia(idRemetente);
            conversa.setIdUsuarioQueRecebe(idDestinatario);
            conversa.setUltimaMensagem("");
            conversa.set_isGrupo(true);
            conversa.setGrupo(this);

            conversa.salvarNoFirebase();
        }
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    public void setMembros(List<Usuario> membros) {
        this.membros = membros;
    }
}
