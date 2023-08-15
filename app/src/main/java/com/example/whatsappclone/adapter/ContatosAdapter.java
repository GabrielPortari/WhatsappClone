package com.example.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContatosAdapter extends RecyclerView.Adapter<ContatosAdapter.MyViewHolder> {
    private List<Usuario> contatos;
    private Context context;

    public ContatosAdapter(List<Usuario> listaUsuarios, Context c) {
        this.contatos = listaUsuarios;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_contatos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = contatos.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();
        holder.textNome.setText(usuario.getNome());
        holder.textEmail.setText(usuario.getEmail());
        if(usuario.getFoto() != null){
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context).load(uri).into(holder.circleImageView);
        }else{
            if(cabecalho){
                holder.circleImageView.setImageResource(R.drawable.icone_grupo);
                holder.textEmail.setVisibility(View.GONE);
            }else{
                holder.circleImageView.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView textNome, textEmail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circleImage_contatos);
            textNome = itemView.findViewById(R.id.textNome_contatoItemRecycler);
            textEmail = itemView.findViewById(R.id.textEmail_contatoItemRecycler);

        }
    }
}
