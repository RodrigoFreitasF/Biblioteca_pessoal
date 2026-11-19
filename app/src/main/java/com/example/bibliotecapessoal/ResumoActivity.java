package com.example.bibliotecapessoal;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bibliotecapessoal.persistencia.ILivroDAO;
import com.example.bibliotecapessoal.persistencia.LivroDAO;

public class ResumoActivity extends AppCompatActivity {

    private TextView txtTotalLivros;
    private TextView txtLivrosLidos;
    private TextView txtLivrosNaoLidos;

    private ILivroDAO livroDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumo);

        // Troca o padding inferior por 0, pois o BottomNavigationView ja trata a margem inferior
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        txtTotalLivros = findViewById(R.id.txtTotalLivros);
        txtLivrosLidos = findViewById(R.id.txtLivrosLidos);
        txtLivrosNaoLidos = findViewById(R.id.txtLivrosNaoLidos);

        livroDAO = new LivroDAO(getApplicationContext());
        carregarEstatisticas();

        NavegacaoMenu.configurar(this, R.id.nav_resumo);
    }

    private void carregarEstatisticas() {
        int total = livroDAO.contar(null);
        int lidos = livroDAO.contar(true);
        int naoLidos = livroDAO.contar(false);

        txtTotalLivros.setText(getString(R.string.resumo_total_livros, total));
        txtLivrosLidos.setText(getString(R.string.resumo_livros_lidos, lidos));
        txtLivrosNaoLidos.setText(getString(R.string.resumo_livros_nao_lidos, naoLidos));
    }
}
