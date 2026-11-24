package com.example.bibliotecapessoal.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bibliotecapessoal.R;
import com.example.bibliotecapessoal.model.Livro;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterLivro extends RecyclerView.Adapter<AdapterLivro.MyViewHolder> {

    public interface OnLivroActionListener {
        void onEditar(Livro livro);
        void onAlternarStatus(Livro livro);
        void onExcluir(Livro livro);
    }

    private static final int[] CORES_LOMBADA = {
            R.color.spine_forest,
            R.color.spine_terracotta,
            R.color.spine_slate,
            R.color.spine_burgundy,
            R.color.spine_ochre,
            R.color.spine_moss
    };

    private List<Livro> listaLivros;
    private final OnLivroActionListener listener;

    public AdapterLivro(List<Livro> listaLivros, OnLivroActionListener listener) {
        this.listaLivros = (listaLivros != null) ? listaLivros : new ArrayList<>();
        this.listener = listener;
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

        String titulo = livro.getTitulo() != null ? livro.getTitulo().trim() : "";
        String inicial = titulo.isEmpty() ? "L" : titulo.substring(0, 1).toUpperCase(Locale.getDefault());

        holder.txtInicialLivro.setText(inicial);
        holder.txtTituloLivro.setText(titulo);
        holder.txtAutorAno.setText(
                context.getString(R.string.formato_autor_ano, livro.getAutor(), livro.getAno())
        );

        int corRes = selecionarCorLombada(livro);
        Drawable background = ContextCompat.getDrawable(context, R.drawable.bg_book_cover);
        if (background != null) {
            Drawable wrapped = DrawableCompat.wrap(background.mutate());
            DrawableCompat.setTint(wrapped, ContextCompat.getColor(context, corRes));
            holder.capaLivro.setBackground(wrapped);
        }

        if (livro.isLido()) {
            holder.txtStatusLido.setText(context.getString(R.string.status_lido));
            holder.txtStatusLido.setTextColor(ContextCompat.getColor(context, R.color.badge_lido_texto));
            holder.txtStatusLido.setBackgroundResource(R.drawable.bg_badge_lido);
        } else {
            holder.txtStatusLido.setText(context.getString(R.string.status_nao_lido));
            holder.txtStatusLido.setTextColor(ContextCompat.getColor(context, R.color.badge_nao_lido_texto));
            holder.txtStatusLido.setBackgroundResource(R.drawable.bg_badge_nao_lido);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(livro);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onExcluir(livro);
                return true;
            }
            return false;
        });

        holder.txtStatusLido.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlternarStatus(livro);
            }
        });

        holder.btnExcluirItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExcluir(livro);
            }
        });
    }

    private int selecionarCorLombada(Livro livro) {
        int hash = 0;
        if (livro.getTitulo() != null) {
            hash = livro.getTitulo().hashCode();
        } else if (livro.getId() != null) {
            hash = livro.getId().hashCode();
        }
        int index = Math.abs(hash) % CORES_LOMBADA.length;
        return CORES_LOMBADA[index];
    }

    @Override
    public int getItemCount() {
        return listaLivros != null ? listaLivros.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        FrameLayout capaLivro;
        TextView txtInicialLivro;
        TextView txtTituloLivro;
        TextView txtAutorAno;
        TextView txtStatusLido;
        ImageButton btnExcluirItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            capaLivro = itemView.findViewById(R.id.capaLivro);
            txtInicialLivro = itemView.findViewById(R.id.txtInicialLivro);
            txtTituloLivro = itemView.findViewById(R.id.txtTituloLivro);
            txtAutorAno = itemView.findViewById(R.id.txtAutorAno);
            txtStatusLido = itemView.findViewById(R.id.txtStatusLido);
            btnExcluirItem = itemView.findViewById(R.id.btnExcluirItem);
        }
    }
}
