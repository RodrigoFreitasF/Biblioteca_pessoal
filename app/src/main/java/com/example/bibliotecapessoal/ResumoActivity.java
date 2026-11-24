package com.example.bibliotecapessoal;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bibliotecapessoal.persistencia.ILivroDAO;
import com.example.bibliotecapessoal.persistencia.LivroDAO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class ResumoActivity extends AppCompatActivity {

    private TextView txtPorcentagem;
    private LinearProgressIndicator progressLeitura;
    private TextView txtProgressoDetalhe;
    private TextView txtMensagemResumo;
    private TextView txtTotalLivros;
    private TextView txtLivrosLidos;
    private TextView txtLivrosNaoLidos;

    private ILivroDAO livroDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumo);

        NavegacaoMenu.configurarEdgeToEdge(
                this,
                findViewById(R.id.main),
                findViewById(R.id.headerResumo)
        );

        txtPorcentagem = findViewById(R.id.txtPorcentagem);
        progressLeitura = findViewById(R.id.progressLeitura);
        txtProgressoDetalhe = findViewById(R.id.txtProgressoDetalhe);
        txtMensagemResumo = findViewById(R.id.txtMensagemResumo);
        txtTotalLivros = findViewById(R.id.txtTotalLivros);
        txtLivrosLidos = findViewById(R.id.txtLivrosLidos);
        txtLivrosNaoLidos = findViewById(R.id.txtLivrosNaoLidos);

        MaterialCardView cardTotal = findViewById(R.id.cardTotal);
        MaterialCardView cardLidos = findViewById(R.id.cardLidos);
        MaterialCardView cardNaoLidos = findViewById(R.id.cardNaoLidos);
        MaterialButton btnIrEstante = findViewById(R.id.btnIrEstante);
        MaterialButton btnIrNovoLivro = findViewById(R.id.btnIrNovoLivro);

        cardTotal.setOnClickListener(v -> abrirListaComFiltro(ListaLivrosActivity.FILTRO_TODOS));
        cardLidos.setOnClickListener(v -> abrirListaComFiltro(ListaLivrosActivity.FILTRO_LIDOS));
        cardNaoLidos.setOnClickListener(v -> abrirListaComFiltro(ListaLivrosActivity.FILTRO_NAO_LIDOS));
        btnIrEstante.setOnClickListener(v -> abrirListaComFiltro(ListaLivrosActivity.FILTRO_TODOS));
        btnIrNovoLivro.setOnClickListener(v ->
                NavegacaoMenu.irPara(this, new Intent(this, CadastroLivroActivity.class))
        );

        livroDAO = new LivroDAO(getApplicationContext());
        NavegacaoMenu.configurar(this, R.id.nav_resumo);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEstatisticas();
    }

    private void abrirListaComFiltro(int filtro) {
        Intent intent = new Intent(this, ListaLivrosActivity.class);
        intent.putExtra(ListaLivrosActivity.EXTRA_FILTRO_INICIAL, filtro);
        NavegacaoMenu.irPara(this, intent);
    }

    private void carregarEstatisticas() {
        int total = livroDAO.contar(null);
        int lidos = livroDAO.contar(true);
        int naoLidos = livroDAO.contar(false);
        int porcentagem = total > 0 ? Math.round((lidos * 100f) / total) : 0;

        txtPorcentagem.setText(getString(R.string.resumo_porcentagem, porcentagem));
        txtProgressoDetalhe.setText(getString(R.string.resumo_progresso_detalhe, lidos, total));
        txtTotalLivros.setText(String.valueOf(total));
        txtLivrosLidos.setText(String.valueOf(lidos));
        txtLivrosNaoLidos.setText(String.valueOf(naoLidos));

        ObjectAnimator animator = ObjectAnimator.ofInt(progressLeitura, "progress", 0, porcentagem);
        animator.setDuration(500);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();

        if (total == 0) {
            txtMensagemResumo.setText(R.string.resumo_msg_vazio);
        } else if (lidos == 0) {
            txtMensagemResumo.setText(R.string.resumo_msg_inicio);
        } else if (lidos == total) {
            txtMensagemResumo.setText(R.string.resumo_msg_completo);
        } else if (porcentagem >= 75) {
            txtMensagemResumo.setText(R.string.resumo_msg_quase);
        } else {
            txtMensagemResumo.setText(R.string.resumo_msg_andamento);
        }
    }
}
