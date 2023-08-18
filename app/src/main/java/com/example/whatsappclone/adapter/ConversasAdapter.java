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
import com.example.whatsappclone.model.Conversa;
import com.example.whatsappclone.model.Grupo;
import com.example.whatsappclone.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {
    private List<Conversa> conversaList;
    private Context context;

    public ConversasAdapter(List<Conversa> conversaList, Context context) {
        this.conversaList = conversaList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_contatos, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Conversa conversa = conversaList.get(position);
        holder.textMensagem.setText(conversa.getUltimaMensagem());

        if(conversa.get_isGrupo()){
            Grupo grupo = conversa.getGrupo();
            holder.textNome.setText(grupo.getNome());

            if(grupo.getFoto() != null){
                Uri uri = Uri.parse(grupo.getFoto());
                Glide.with(context).load(uri).into(holder.circleImageView);
            }else{
                holder.circleImageView.setImageResource(R.drawable.padrao);
            }

        }else{
            Usuario usuario = conversa.getUsuarioExibido();
            holder.textNome.setText(usuario.getNome());

            if(usuario.getFoto() != null){
                Uri uri = Uri.parse(usuario.getFoto());
                Glide.with(context).load(uri).into(holder.circleImageView);
            }else{
                holder.circleImageView.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return conversaList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView textNome, textMensagem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.circleImage_contatos);
            textNome = itemView.findViewById(R.id.textNome_contatoItemRecycler);
            textMensagem = itemView.findViewById(R.id.textEmail_contatoItemRecycler);
        }
    }
}
