package com.example.whatsappclone.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.R;
import com.example.whatsappclone.helper.UsuarioFirebase;
import com.example.whatsappclone.model.Mensagem;

import java.util.List;

public class MensagemAdapter extends RecyclerView.Adapter<MensagemAdapter.MyViewHolder> {
    private List<Mensagem> mensagens;
    private Context context;
    private static final int TIPO_ENVIA = 0;
    private static final int TIPO_RECEBE = 1;

    public MensagemAdapter(List<Mensagem> lista, Context c) {
        this.mensagens = lista;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = null;
        if(viewType == TIPO_ENVIA){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_msgenviada, parent, false);
        }else if(viewType == TIPO_RECEBE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_msgrecebida, parent, false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Mensagem mensagem = mensagens.get(position);
        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();

        if( imagem != null){
            Uri uri = Uri.parse(imagem);
            Glide.with(context).load(uri).into(holder.imagem);
            //Caso tenha imagem, não mostra texto
            holder.mensagem.setVisibility(View.GONE);
        }else{
            holder.mensagem.setText(mensagem.getMensagem());
            //Caso tenha mensagem, nao mostra imagem
            holder.imagem.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {
        Mensagem mensagem = mensagens.get(position);
        String idUsuario = UsuarioFirebase.getIdUsuario();
        if(idUsuario.equals(mensagem.getIdUsuario())){
            return TIPO_ENVIA;
        }
        return TIPO_RECEBE;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mensagem;
        ImageView imagem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mensagem = itemView.findViewById(R.id.itemRecycler_msg);
            imagem = itemView.findViewById(R.id.itemRecycler_img);
        }
    }
}
