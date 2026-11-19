package com.example.bibliotecapessoal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bibliotecapessoal.model.Livro;
import com.example.bibliotecapessoal.persistencia.ILivroDAO;
import com.example.bibliotecapessoal.persistencia.LivroDAO;

import java.util.Calendar;

public class CadastroLivroActivity extends AppCompatActivity {

    public static final String EXTRA_LIVRO = "livroSelecionado";
    private static final int ANO_MINIMO = 1450;

    private TextView txtTituloTela;
    private TextView txtSubtituloFormulario;
    private EditText edtTitulo;
    private EditText edtAutor;
    private EditText edtAno;
    private CheckBox chkLido;
    private Button btnSalvar;

    private Livro livroAtual;
    private ILivroDAO livroDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro_livro);

        // Troca o padding inferior por 0, pois o BottomNavigationView ja trata a margem inferior
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        txtTituloTela = findViewById(R.id.txtTituloTela);
        txtSubtituloFormulario = findViewById(R.id.txtSubtituloFormulario);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtAutor = findViewById(R.id.edtAutor);
        edtAno = findViewById(R.id.edtAno);
        chkLido = findViewById(R.id.chkLido);
        btnSalvar = findViewById(R.id.btnSalvar);

        livroDAO = new LivroDAO(getApplicationContext());

        // Verifica se foi enviado um livro via Intent para edicao
        livroAtual = (Livro) getIntent().getSerializableExtra(EXTRA_LIVRO);
        if (livroAtual != null) {
            preencherCamposParaEdicao(livroAtual);
        }

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarOuAtualizarLivro();
            }
        });

        NavegacaoMenu.configurar(this, R.id.nav_novo);
    }

    private void preencherCamposParaEdicao(Livro livro) {
        txtTituloTela.setText(getString(R.string.titulo_editar_livro));
        txtSubtituloFormulario.setText(getString(R.string.subtitulo_editar_livro));
        btnSalvar.setText(getString(R.string.btn_atualizar));
        edtTitulo.setText(livro.getTitulo());
        edtAutor.setText(livro.getAutor());
        edtAno.setText(String.valueOf(livro.getAno()));
        chkLido.setChecked(livro.isLido());
    }

    private void salvarOuAtualizarLivro() {
        String titulo = edtTitulo.getText().toString().trim();
        String autor = edtAutor.getText().toString().trim();
        String textoAno = edtAno.getText().toString().trim();
        boolean lido = chkLido.isChecked();

        // Ano atual obtido dinamicamente via Calendar
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

        boolean valido = true;
        EditText primeiroInvalido = null;

        if (titulo.isEmpty()) {
            edtTitulo.setError(getString(R.string.erro_titulo_obrigatorio));
            primeiroInvalido = edtTitulo;
            valido = false;
        }

        if (autor.isEmpty()) {
            edtAutor.setError(getString(R.string.erro_autor_obrigatorio));
            if (primeiroInvalido == null) {
                primeiroInvalido = edtAutor;
            }
            valido = false;
        }

        int ano = 0;
        if (textoAno.isEmpty()) {
            edtAno.setError(getString(R.string.erro_ano_obrigatorio));
            if (primeiroInvalido == null) {
                primeiroInvalido = edtAno;
            }
            valido = false;
        } else {
            try {
                ano = Integer.parseInt(textoAno);
                if (ano < ANO_MINIMO || ano > anoAtual) {
                    edtAno.setError(getString(R.string.erro_ano_intervalo, ANO_MINIMO, anoAtual));
                    if (primeiroInvalido == null) {
                        primeiroInvalido = edtAno;
                    }
                    valido = false;
                }
            } catch (NumberFormatException e) {
                edtAno.setError(getString(R.string.erro_ano_intervalo, ANO_MINIMO, anoAtual));
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
            // Fluxo de edicao
            Livro livroEditado = new Livro(livroAtual.getId(), titulo, autor, ano, lido);
            if (livroDAO.atualizar(livroEditado)) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.msg_livro_atualizado),
                        Toast.LENGTH_SHORT
                ).show();
                NavegacaoMenu.irPara(this, new Intent(this, ListaLivrosActivity.class));
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.msg_erro_atualizar),
                        Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            // Fluxo de inclusao
            Livro novoLivro = new Livro(null, titulo, autor, ano, lido);
            if (livroDAO.salvar(novoLivro)) {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.msg_livro_salvo),
                        Toast.LENGTH_SHORT
                ).show();
                NavegacaoMenu.irPara(this, new Intent(this, ListaLivrosActivity.class));
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        getString(R.string.msg_erro_salvar),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}
