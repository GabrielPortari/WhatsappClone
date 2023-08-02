package com.example.whatsappclone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {
    private static DatabaseReference dbReference;
    private static FirebaseAuth auth;

    public static DatabaseReference getFirebaseDatabaseReference(){
        if(dbReference == null){
            dbReference = FirebaseDatabase.getInstance().getReference();
        }
        return dbReference;
    }
    public static FirebaseAuth getFirebaseAuthReference(){
        if(auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }
}
