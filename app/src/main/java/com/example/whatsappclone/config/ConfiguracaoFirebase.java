package com.example.whatsappclone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracaoFirebase {
    private static DatabaseReference dbReference;
    private static StorageReference storage;
    private static FirebaseAuth auth;

    public static DatabaseReference getFirebaseDatabaseReference(){
        if(dbReference == null){
            dbReference = FirebaseDatabase.getInstance().getReference();
        }
        return dbReference;
    }
    public static StorageReference getFirebaseStorageReference(){
        if(storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
    public static FirebaseAuth getFirebaseAuthReference(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
