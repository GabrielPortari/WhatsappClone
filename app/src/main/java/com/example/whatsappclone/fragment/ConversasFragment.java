package com.example.whatsappclone.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.activity.ChatActivity;
import com.example.whatsappclone.adapter.ContatosAdapter;
import com.example.whatsappclone.adapter.ConversasAdapter;
import com.example.whatsappclone.config.ConfiguracaoFirebase;
import com.example.whatsappclone.helper.RecyclerItemClickListener;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Usuario;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class ConversasFragment extends Fragment {

    private RecyclerView recyclerConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter conversasAdapter, buscaAdapter;
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

        recyclerConversas.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerConversas,
                new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Recupera o usuario/grupo de acordo com a conversa selecionada
                Conversa conversaSelecionada = listaConversas.get(position);

                if(conversaSelecionada.getIsGroup()){
                    Grupo grupoSelecionado = conversaSelecionada.getGrupo();
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("grupoSelecionado", grupoSelecionado);
                    startActivity(intent);
                }else{
                    Usuario usuarioSelecionado = conversaSelecionada.getUsuarioExibido();
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("usuarioSelecionado", usuarioSelecionado);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversa();
    }
    public void buscaAoDigitar(String s){
        List<Conversa> listaBusca = new ArrayList<>();

        for(Conversa c : listaConversas){
            String buscaNome = c.getUsuarioExibido().getNome();
            String buscaMensagem = c.getUltimaMensagem();

            if(buscaNome.contains(s) || buscaMensagem.contains(s)) {
                listaBusca.add(c);
            }
        }
        /*
        buscaAdapter = new ConversasAdapter(listaBusca, getActivity());
        recyclerConversas.setAdapter(buscaAdapter);
        conversasAdapter.notifyDataSetChanged();
        */
        //RECYCLER ESTÁ ACESSANDO POSIÇÃO NULA, E NÃO ESTÁ FUNCIONANDO CORRETAMENTE


        Log.i("INFO BUSCA", "BUSCANDO: " + s);
    }

    public void recarregarConversas(){
        /*
        conversasAdapter = new ConversasAdapter(listaConversas, getActivity());
        recyclerConversas.setAdapter(conversasAdapter);
        conversasAdapter.notifyDataSetChanged();
        */
        //RECYCLER ESTÁ ACESSANDO POSIÇÃO NULA, E NÃO ESTÁ FUNCIONANDO CORRETAMENTE
        Log.i("INFO BUSCA", "BUSCA FECHADA");
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