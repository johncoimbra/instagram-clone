package com.example.instagram.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static FirebaseAuth referenciaAutenticacao;
    private static DatabaseReference referenciaFirebase;
    private static StorageReference storage;

    //Recuperar a instancia do FirebaseDatabase,
    // o objeto do firebase que nos permite salvar os dados do usuario

    public static DatabaseReference getFirebaseDatabase(){
        if(referenciaFirebase==null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    //Recuperar a instancia do firebase, o objeto do firebase
    // que nos permite fazer autenticacao do usuario

    public static FirebaseAuth getFirebaseAutenticacao(){
        if(referenciaAutenticacao==null){
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
        return referenciaAutenticacao;
    }

    public static StorageReference getFirebaseStorage(){
        if(storage==null) {
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}
