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
        //Gera um id unico para o grupo sempre que um objeto grupo é criado
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference grupoReference = databaseReference.child("grupos");

        String idGrupoFirebase = grupoReference.push().getKey();
        setId(idGrupoFirebase);
    }
    public void salvarNoFirebase(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        DatabaseReference grupoReference = databaseReference.child("grupos");

        grupoReference.child(getId()).setValue(this);

        //Salvar a conversa para todos os membros do grupo
        for(Usuario membro : getMembros()){
            String idEnvia = Base64Custom.codeBase64(membro.getEmail());
            String idRecebe = getId();

            Conversa conversa = new Conversa();
            conversa.setIdUsuarioQueEnvia(idEnvia);
            conversa.setIdUsuarioQueRecebe(idRecebe);
            conversa.setUltimaMensagem("");
            conversa.setIsGroup(true);
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
