package com.example.bibliotecapessoal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bibliotecapessoal.R;
import com.example.bibliotecapessoal.model.Livro;

import java.util.ArrayList;
import java.util.List;

public class AdapterLivro extends RecyclerView.Adapter<AdapterLivro.MyViewHolder> {

    private List<Livro> listaLivros;

    public AdapterLivro(List<Livro> listaLivros) {
        this.listaLivros = (listaLivros != null) ? listaLivros : new ArrayList<>();
    }

    public void atualizarLista(List<Livro> novaLista) {
        this.listaLivros = (novaLista != null) ? novaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Livro getLivro(int position) {
        return listaLivros.get(position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_livro, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Livro livro = listaLivros.get(position);

        holder.txtTituloLivro.setText(livro.getTitulo());
        holder.txtAutorAno.setText(
                context.getString(R.string.formato_autor_ano, livro.getAutor(), livro.getAno())
        );

        if (livro.isLido()) {
            holder.txtStatusLido.setText(context.getString(R.string.status_lido));
            holder.txtStatusLido.setTextColor(ContextCompat.getColor(context, R.color.badge_lido_texto));
            holder.txtStatusLido.setBackgroundResource(R.drawable.bg_badge_lido);
        } else {
            holder.txtStatusLido.setText(context.getString(R.string.status_nao_lido));
            holder.txtStatusLido.setTextColor(ContextCompat.getColor(context, R.color.badge_nao_lido_texto));
            holder.txtStatusLido.setBackgroundResource(R.drawable.bg_badge_nao_lido);
        }
    }

    @Override
    public int getItemCount() {
        return listaLivros != null ? listaLivros.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTituloLivro;
        TextView txtAutorAno;
        TextView txtStatusLido;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTituloLivro = itemView.findViewById(R.id.txtTituloLivro);
            txtAutorAno = itemView.findViewById(R.id.txtAutorAno);
            txtStatusLido = itemView.findViewById(R.id.txtStatusLido);
        }
    }
}
