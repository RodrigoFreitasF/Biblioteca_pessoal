package com.example.bibliotecapessoal;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bibliotecapessoal.model.Livro;
import com.example.bibliotecapessoal.persistencia.ILivroDAO;
import com.example.bibliotecapessoal.persistencia.LivroDAO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

public class CadastroLivroActivity extends AppCompatActivity {

    public static final String EXTRA_LIVRO = "livroSelecionado";
    private static final int ANO_MINIMO = 1450;

    private ImageButton btnVoltar;
    private TextView txtTituloTela;
    private TextView txtSubtituloFormulario;
    private TextInputLayout tilTitulo;
    private TextInputLayout tilAutor;
    private TextInputLayout tilAno;
    private TextInputEditText edtTitulo;
    private TextInputEditText edtAutor;
    private TextInputEditText edtAno;
    private MaterialCardView cardStatusLeitura;
    private TextView txtDescStatus;
    private MaterialSwitch chkLido;
    private MaterialButton btnSalvar;
    private MaterialButton btnExcluirCadastro;

    private Livro livroAtual;
    private ILivroDAO livroDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_livro);

        NavegacaoMenu.configurarEdgeToEdge(
                this,
                findViewById(R.id.main),
                findViewById(R.id.headerCadastro)
        );

        btnVoltar = findViewById(R.id.btnVoltar);
        txtTituloTela = findViewById(R.id.txtTituloTela);
        txtSubtituloFormulario = findViewById(R.id.txtSubtituloFormulario);
        tilTitulo = findViewById(R.id.tilTitulo);
        tilAutor = findViewById(R.id.tilAutor);
        tilAno = findViewById(R.id.tilAno);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        edtAno = findViewById(R.id.edtAno);
        cardStatusLeitura = findViewById(R.id.cardStatusLeitura);
        txtDescStatus = findViewById(R.id.txtDescStatus);
        chkLido = findViewById(R.id.chkLido);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnExcluirCadastro = findViewById(R.id.btnExcluirCadastro);

        livroDAO = new LivroDAO(getApplicationContext());

        configurarLimpezaErros(edtTitulo, tilTitulo);
        configurarLimpezaErros(edtAutor, tilAutor);
        configurarLimpezaErros(edtAno, tilAno);

        cardStatusLeitura.setOnClickListener(v -> chkLido.toggle());
        chkLido.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarDescricaoStatus(isChecked));

        btnVoltar.setOnClickListener(v ->
                NavegacaoMenu.irPara(this, new Intent(this, ListaLivrosActivity.class))
        );

        // Verifica se foi enviado um livro via Intent para edição
        livroAtual = (Livro) getIntent().getSerializableExtra(EXTRA_LIVRO);
        if (livroAtual != null) {
            preencherCamposParaEdicao(livroAtual);
        } else {
            atualizarDescricaoStatus(false);
        }

        btnSalvar.setOnClickListener(v -> salvarOuAtualizarLivro());
        btnExcluirCadastro.setOnClickListener(v -> confirmarExclusao());

        NavegacaoMenu.configurar(this, R.id.nav_novo);
    }

    private void configurarLimpezaErros(TextInputEditText campo, TextInputLayout layout) {
        campo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void atualizarDescricaoStatus(boolean lido) {
        txtDescStatus.setText(lido ? R.string.desc_status_lido : R.string.desc_status_nao_lido);
    }

    private void preencherCamposParaEdicao(Livro livro) {
        btnVoltar.setVisibility(View.VISIBLE);
        btnExcluirCadastro.setVisibility(View.VISIBLE);
        txtTituloTela.setText(R.string.titulo_editar_livro);
        txtSubtituloFormulario.setText(R.string.subtitulo_editar_livro);
        btnSalvar.setText(R.string.btn_atualizar);

        edtTitulo.setText(livro.getTitulo());
        edtAutor.setText(livro.getAutor());
        edtAno.setText(String.valueOf(livro.getAno()));
        chkLido.setChecked(livro.isLido());
        atualizarDescricaoStatus(livro.isLido());
    }

    private void salvarOuAtualizarLivro() {
        String titulo = edtTitulo.getText() != null ? edtTitulo.getText().toString().trim() : "";
        String autor = edtAutor.getText() != null ? edtAutor.getText().toString().trim() : "";
        String textoAno = edtAno.getText() != null ? edtAno.getText().toString().trim() : "";
        boolean lido = chkLido.isChecked();

        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

        boolean valido = true;
        View primeiroInvalido = null;

        if (titulo.isEmpty()) {
            tilTitulo.setError(getString(R.string.erro_titulo_obrigatorio));
            primeiroInvalido = edtTitulo;
            valido = false;
        } else {
            tilTitulo.setError(null);
        }

        if (autor.isEmpty()) {
            tilAutor.setError(getString(R.string.erro_autor_obrigatorio));
            if (primeiroInvalido == null) {
                primeiroInvalido = edtAutor;
            }
            valido = false;
        } else {
            tilAutor.setError(null);
        }

        int ano = 0;
        if (textoAno.isEmpty()) {
            tilAno.setError(getString(R.string.erro_ano_obrigatorio));
            if (primeiroInvalido == null) {
                primeiroInvalido = edtAno;
            }
            valido = false;
        } else {
            try {
                ano = Integer.parseInt(textoAno);
                if (ano < ANO_MINIMO || ano > anoAtual) {
                    tilAno.setError(getString(R.string.erro_ano_intervalo, ANO_MINIMO, anoAtual));
                    if (primeiroInvalido == null) {
                        primeiroInvalido = edtAno;
                    }
                    valido = false;
                } else {
                    tilAno.setError(null);
                }
            } catch (NumberFormatException e) {
                tilAno.setError(getString(R.string.erro_ano_intervalo, ANO_MINIMO, anoAtual));
                if (primeiroInvalido == null) {
                    primeiroInvalido = edtAno;
                }
                valido = false;
            }
        }

        if (!valido) {
            if (primeiroInvalido != null) {
                primeiroInvalido.requestFocus();
            }
            return;
        }

        if (livroAtual != null) {
            Livro livroEditado = new Livro(livroAtual.getId(), titulo, autor, ano, lido);
            if (livroDAO.atualizar(livroEditado)) {
                Toast.makeText(getApplicationContext(), R.string.msg_livro_atualizado, Toast.LENGTH_SHORT).show();
                NavegacaoMenu.irPara(this, new Intent(this, ListaLivrosActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.msg_erro_atualizar, Toast.LENGTH_SHORT).show();
            }
        } else {
            Livro novoLivro = new Livro(null, titulo, autor, ano, lido);
            if (livroDAO.salvar(novoLivro)) {
                Toast.makeText(getApplicationContext(), R.string.msg_livro_salvo, Toast.LENGTH_SHORT).show();
                NavegacaoMenu.irPara(this, new Intent(this, ListaLivrosActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), R.string.msg_erro_salvar, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void confirmarExclusao() {
        if (livroAtual == null) {
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialog_excluir_titulo)
                .setMessage(getString(R.string.dialog_excluir_mensagem, livroAtual.getTitulo()))
                .setPositiveButton(R.string.dialog_btn_excluir, (dialog, which) -> {
                    if (livroDAO.excluir(livroAtual)) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.msg_livro_excluido, livroAtual.getTitulo()),
                                Toast.LENGTH_SHORT
                        ).show();
                        NavegacaoMenu.irPara(this, new Intent(this, ListaLivrosActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.msg_erro_excluir, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.dialog_btn_cancelar, null)
                .show();
    }
}
