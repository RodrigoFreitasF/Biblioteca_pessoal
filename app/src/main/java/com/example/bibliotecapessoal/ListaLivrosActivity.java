package com.example.bibliotecapessoal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bibliotecapessoal.adapter.AdapterLivro;
import com.example.bibliotecapessoal.model.Livro;
import com.example.bibliotecapessoal.persistencia.ILivroDAO;
import com.example.bibliotecapessoal.persistencia.LivroDAO;

import java.util.ArrayList;
import java.util.List;

public class ListaLivrosActivity extends AppCompatActivity {

    private RecyclerView rvListaLivros;
    private LinearLayout layoutListaVazia;

    private AdapterLivro adapterLivro;
    private ILivroDAO livroDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_livros);

        // Troca o padding inferior por 0, pois o BottomNavigationView ja trata a margem inferior
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        rvListaLivros = findViewById(R.id.rvListaLivros);
        layoutListaVazia = findViewById(R.id.layoutListaVazia);

        livroDAO = new LivroDAO(getApplicationContext());

        adapterLivro = new AdapterLivro(new ArrayList<>());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvListaLivros.setLayoutManager(layoutManager);
        rvListaLivros.setHasFixedSize(true);
        rvListaLivros.setAdapter(adapterLivro);

        configurarEventosRecyclerView();
        NavegacaoMenu.configurar(this, R.id.nav_livros);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarListaLivros();
    }

    private void carregarListaLivros() {
        List<Livro> livros = livroDAO.listarTodos();
        adapterLivro.atualizarLista(livros);

        if (livros.isEmpty()) {
            rvListaLivros.setVisibility(View.GONE);
            layoutListaVazia.setVisibility(View.VISIBLE);
        } else {
            rvListaLivros.setVisibility(View.VISIBLE);
            layoutListaVazia.setVisibility(View.GONE);
        }
    }

    private void configurarEventosRecyclerView() {
        rvListaLivros.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                rvListaLivros,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Clique simples: abre o cadastro preenchido para edicao
                        Livro livro = adapterLivro.getLivro(position);
                        Intent intent = new Intent(ListaLivrosActivity.this, CadastroLivroActivity.class);
                        intent.putExtra(CadastroLivroActivity.EXTRA_LIVRO, livro);
                        NavegacaoMenu.irPara(ListaLivrosActivity.this, intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Clique longo: solicita confirmacao e exclui o livro
                        Livro livro = adapterLivro.getLivro(position);
                        exibirDialogoConfirmacaoExclusao(livro);
                    }
                }
        ));
    }

    private void exibirDialogoConfirmacaoExclusao(final Livro livro) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ListaLivrosActivity.this);
        dialog.setTitle(getString(R.string.dialog_excluir_titulo));
        dialog.setMessage(getString(R.string.dialog_excluir_mensagem, livro.getTitulo()));

        dialog.setPositiveButton(getString(R.string.dialog_btn_excluir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (livroDAO.excluir(livro)) {
                    carregarListaLivros();
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.msg_livro_excluido),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.msg_erro_excluir),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        dialog.setNegativeButton(getString(R.string.dialog_btn_cancelar), null);
        dialog.create();
        dialog.show();
    }
}
