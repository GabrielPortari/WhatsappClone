package com.example.whatsappclone.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsappclone.R;
import com.example.whatsappclone.adapter.ContatosAdapter;
import com.example.whatsappclone.adapter.ConversasAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Conversa;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class ConversasFragment extends Fragment {

    private RecyclerView recyclerConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter conversasAdapter;
    private DatabaseReference databaseReference, conversasReference;
    private ChildEventListener conversasChildEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //Configurar referencia de conversas;
        String idUsuario = UsuarioFirebase.getIdUsuario();
        databaseReference = ConfiguracaoFirebase.getFirebaseDatabaseReference();
        conversasReference = databaseReference.child("conversas").child(idUsuario);

        //Configuração do adapter
        conversasAdapter = new ConversasAdapter(listaConversas, getActivity());

        //Configuração da recyclerview
        recyclerConversas = view.findViewById(R.id.recyclerConversas);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerConversas.setLayoutManager(layoutManager);
        recyclerConversas.setHasFixedSize(true);
        recyclerConversas.setAdapter(conversasAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversa();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasReference.removeEventListener(conversasChildEventListener);
    }

    public void recuperarConversa(){
        conversasChildEventListener = conversasReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Conversa conversa = snapshot.getValue(Conversa.class);
                listaConversas.add(conversa);
                conversasAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}