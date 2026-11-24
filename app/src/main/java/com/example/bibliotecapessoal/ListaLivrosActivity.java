package com.example.bibliotecapessoal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bibliotecapessoal.adapter.AdapterLivro;
import com.example.bibliotecapessoal.model.Livro;
import com.example.bibliotecapessoal.persistencia.ILivroDAO;
import com.example.bibliotecapessoal.persistencia.LivroDAO;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListaLivrosActivity extends AppCompatActivity {

    public static final String EXTRA_FILTRO_INICIAL = "filtroInicial";
    public static final int FILTRO_TODOS = 0;
    public static final int FILTRO_LIDOS = 1;
    public static final int FILTRO_NAO_LIDOS = 2;

    private TextView txtSubtituloLista;
    private EditText edtBusca;
    private ImageButton btnLimparBusca;
    private ChipGroup chipGroupFiltro;
    private Chip chipTodos;
    private Chip chipLidos;
    private Chip chipNaoLidos;
    private RecyclerView rvListaLivros;
    private MaterialCardView layoutListaVazia;
    private TextView txtVazioTitulo;
    private TextView txtVazioDescricao;
    private MaterialButton btnAcaoVazio;
    private FloatingActionButton fabAdicionar;
    private BottomNavigationView bottomNav;

    private AdapterLivro adapterLivro;
    private ILivroDAO livroDAO;

    private List<Livro> todosOsLivros = new ArrayList<>();
    private int filtroAtual = FILTRO_TODOS;
    private String termoBusca = "";
    private boolean animacaoInicialExecutada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_livros);

        NavegacaoMenu.configurarEdgeToEdge(
                this,
                findViewById(R.id.main),
                findViewById(R.id.headerLista)
        );

        txtSubtituloLista = findViewById(R.id.txtSubtituloLista);
        edtBusca = findViewById(R.id.edtBusca);
        btnLimparBusca = findViewById(R.id.btnLimparBusca);
        chipGroupFiltro = findViewById(R.id.chipGroupFiltro);
        chipTodos = findViewById(R.id.chipTodos);
        chipLidos = findViewById(R.id.chipLidos);
        chipNaoLidos = findViewById(R.id.chipNaoLidos);
        rvListaLivros = findViewById(R.id.rvListaLivros);
        layoutListaVazia = findViewById(R.id.layoutListaVazia);
        txtVazioTitulo = findViewById(R.id.txtVazioTitulo);
        txtVazioDescricao = findViewById(R.id.txtVazioDescricao);
        btnAcaoVazio = findViewById(R.id.btnAcaoVazio);
        fabAdicionar = findViewById(R.id.fabAdicionar);
        bottomNav = findViewById(R.id.bottomNav);

        livroDAO = new LivroDAO(getApplicationContext());

        adapterLivro = new AdapterLivro(new ArrayList<>(), new AdapterLivro.OnLivroActionListener() {
            @Override
            public void onEditar(Livro livro) {
                Intent intent = new Intent(ListaLivrosActivity.this, CadastroLivroActivity.class);
                intent.putExtra(CadastroLivroActivity.EXTRA_LIVRO, livro);
                NavegacaoMenu.irPara(ListaLivrosActivity.this, intent);
            }

            @Override
            public void onAlternarStatus(Livro livro) {
                alternarStatusLeitura(livro);
            }

            @Override
            public void onExcluir(Livro livro) {
                exibirDialogoConfirmacaoExclusao(livro);
            }
        });

        int spanCount = getResources().getInteger(R.integer.livros_grid_span);
        RecyclerView.LayoutManager layoutManager = (spanCount > 1)
                ? new GridLayoutManager(this, spanCount)
                : new LinearLayoutManager(this);
        rvListaLivros.setLayoutManager(layoutManager);
        rvListaLivros.setAdapter(adapterLivro);

        configurarBusca();
        configurarFiltros();
        aplicarFiltroInicial();

        fabAdicionar.setOnClickListener(v ->
                NavegacaoMenu.irPara(this, new Intent(this, CadastroLivroActivity.class))
        );

        NavegacaoMenu.configurar(this, R.id.nav_livros);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarListaLivros();
    }

    private void aplicarFiltroInicial() {
        int filtroExtra = getIntent().getIntExtra(EXTRA_FILTRO_INICIAL, FILTRO_TODOS);
        filtroAtual = filtroExtra;
        if (filtroExtra == FILTRO_LIDOS) {
            chipLidos.setChecked(true);
        } else if (filtroExtra == FILTRO_NAO_LIDOS) {
            chipNaoLidos.setChecked(true);
        } else {
            chipTodos.setChecked(true);
        }
    }

    private void configurarBusca() {
        edtBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                termoBusca = s != null ? s.toString().trim() : "";
                btnLimparBusca.setVisibility(termoBusca.isEmpty() ? View.GONE : View.VISIBLE);
                aplicarFiltrosNaLista();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnLimparBusca.setOnClickListener(v -> edtBusca.setText(""));
    }

    private void configurarFiltros() {
        chipGroupFiltro.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipLidos) {
                filtroAtual = FILTRO_LIDOS;
            } else if (checkedId == R.id.chipNaoLidos) {
                filtroAtual = FILTRO_NAO_LIDOS;
            } else {
                filtroAtual = FILTRO_TODOS;
            }
            aplicarFiltrosNaLista();
        });
    }

    private void carregarListaLivros() {
        todosOsLivros = livroDAO.listarTodos();
        atualizarContadores();
        aplicarFiltrosNaLista();

        if (!animacaoInicialExecutada && !todosOsLivros.isEmpty()) {
            rvListaLivros.scheduleLayoutAnimation();
            animacaoInicialExecutada = true;
        }
    }

    private void atualizarContadores() {
        int total = todosOsLivros.size();
        int lidos = 0;
        for (Livro livro : todosOsLivros) {
            if (livro.isLido()) {
                lidos++;
            }
        }
        int naoLidos = total - lidos;

        if (total == 0) {
            txtSubtituloLista.setText(R.string.subtitulo_lista_vazia);
        } else if (total == 1) {
            txtSubtituloLista.setText(R.string.subtitulo_lista_contagem_singular);
        } else {
            txtSubtituloLista.setText(getString(R.string.subtitulo_lista_contagem_plural, total));
        }

        chipTodos.setText(getString(R.string.filtro_todos_qtd, total));
        chipLidos.setText(getString(R.string.filtro_lidos_qtd, lidos));
        chipNaoLidos.setText(getString(R.string.filtro_nao_lidos_qtd, naoLidos));
    }

    private void aplicarFiltrosNaLista() {
        List<Livro> filtrados = new ArrayList<>();
        String buscaLower = termoBusca.toLowerCase(Locale.getDefault());

        for (Livro livro : todosOsLivros) {
            if (filtroAtual == FILTRO_LIDOS && !livro.isLido()) {
                continue;
            }
            if (filtroAtual == FILTRO_NAO_LIDOS && livro.isLido()) {
                continue;
            }

            if (!buscaLower.isEmpty()) {
                String titulo = livro.getTitulo() != null ? livro.getTitulo().toLowerCase(Locale.getDefault()) : "";
                String autor = livro.getAutor() != null ? livro.getAutor().toLowerCase(Locale.getDefault()) : "";
                String ano = String.valueOf(livro.getAno());

                if (!titulo.contains(buscaLower) && !autor.contains(buscaLower) && !ano.contains(buscaLower)) {
                    continue;
                }
            }

            filtrados.add(livro);
        }

        adapterLivro.atualizarLista(filtrados);

        if (filtrados.isEmpty()) {
            rvListaLivros.setVisibility(View.GONE);
            layoutListaVazia.setVisibility(View.VISIBLE);
            atualizarEstadoVazio();
        } else {
            rvListaLivros.setVisibility(View.VISIBLE);
            layoutListaVazia.setVisibility(View.GONE);
        }
    }

    private void atualizarEstadoVazio() {
        if (todosOsLivros.isEmpty()) {
            txtVazioTitulo.setText(R.string.lista_vazia_titulo);
            txtVazioDescricao.setText(R.string.lista_vazia_descricao);
            btnAcaoVazio.setText(R.string.btn_cadastrar_primeiro);
            btnAcaoVazio.setOnClickListener(v ->
                    NavegacaoMenu.irPara(this, new Intent(this, CadastroLivroActivity.class))
            );
        } else {
            txtVazioTitulo.setText(R.string.busca_vazia_titulo);
            txtVazioDescricao.setText(R.string.busca_vazia_descricao);
            btnAcaoVazio.setText(R.string.btn_limpar_filtros);
            btnAcaoVazio.setOnClickListener(v -> {
                edtBusca.setText("");
                chipTodos.setChecked(true);
            });
        }
    }

    private void alternarStatusLeitura(Livro livro) {
        boolean novoStatus = !livro.isLido();
        Livro atualizado = new Livro(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getAno(),
                novoStatus
        );

        if (livroDAO.atualizar(atualizado)) {
            carregarListaLivros();
            int msgRes = novoStatus ? R.string.msg_status_lido : R.string.msg_status_nao_lido;
            mostrarSnackbar(getString(msgRes), null, null);
        } else {
            mostrarSnackbar(getString(R.string.msg_erro_atualizar), null, null);
        }
    }

    private void exibirDialogoConfirmacaoExclusao(final Livro livro) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_excluir_titulo)
                .setMessage(getString(R.string.dialog_excluir_mensagem, livro.getTitulo()))
                .setPositiveButton(R.string.dialog_btn_excluir, (dialog, which) -> {
                    if (livroDAO.excluir(livro)) {
                        carregarListaLivros();
                        mostrarSnackbar(
                                getString(R.string.msg_livro_excluido, livro.getTitulo()),
                                getString(R.string.acao_desfazer),
                                v -> {
                                    Livro restaurado = new Livro(
                                            null,
                                            livro.getTitulo(),
                                            livro.getAutor(),
                                            livro.getAno(),
                                            livro.isLido()
                                    );
                                    if (livroDAO.salvar(restaurado)) {
                                        carregarListaLivros();
                                        mostrarSnackbar(getString(R.string.msg_livro_restaurado), null, null);
                                    }
                                }
                        );
                    } else {
                        mostrarSnackbar(getString(R.string.msg_erro_excluir), null, null);
                    }
                })
                .setNegativeButton(R.string.dialog_btn_cancelar, null)
                .show();
    }

    private void mostrarSnackbar(String mensagem, String acao, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main), mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setAnchorView(bottomNav);
        if (acao != null && listener != null) {
            snackbar.setDuration(Snackbar.LENGTH_LONG);
            snackbar.setAction(acao, listener);
        }
        snackbar.show();
    }
}
