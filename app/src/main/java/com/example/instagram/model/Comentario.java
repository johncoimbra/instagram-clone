package com.example.instagram.model;

import com.example.instagram.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class Comentario {
    private String idComentario, idPostagem, idUsuario, caminhoFoto, nomeUsuario, comentario;

    public Comentario() {
    }

    public boolean salvar(){
       /*
       + Comentarios
            + id_postagem
                + id_comentario
                    + comentario
        */
        DatabaseReference comentariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comentarios")
                .child(getIdPostagem());
        String chaveComentario = comentariosRef.push().getKey();
        setIdComentario(chaveComentario);
        comentariosRef.child(getIdComentario()).setValue(this);

        return true;
    }

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
