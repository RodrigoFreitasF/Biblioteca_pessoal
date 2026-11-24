package com.example.bibliotecapessoal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NavegacaoMenu {

    // Configura ícones claros na barra de status e integra o cabeçalho em tela cheia (Edge-to-Edge)
    public static void configurarEdgeToEdge(final Activity activity, final View root, final View header) {
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(activity.getWindow(), activity.getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }

        final int headerPaddingTopOriginal = header.getPaddingTop();
        final int headerPaddingLeftOriginal = header.getPaddingLeft();
        final int headerPaddingRightOriginal = header.getPaddingRight();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            header.setPadding(
                    headerPaddingLeftOriginal + systemBars.left,
                    headerPaddingTopOriginal + systemBars.top,
                    headerPaddingRightOriginal + systemBars.right,
                    header.getPaddingBottom()
            );
            v.setPadding(0, 0, 0, 0);
            return insets;
        });
    }

    // Marca o item da tela atual e define para onde cada item do menu leva
    public static void configurar(final Activity activity, final int itemAtual) {
        BottomNavigationView nav = activity.findViewById(R.id.bottomNav);
        nav.setSelectedItemId(itemAtual); // ANTES do listener, senão dispara uma navegação

        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == itemAtual) {
                    return true;
                }
                if (id == R.id.nav_livros) {
                    irPara(activity, new Intent(activity, ListaLivrosActivity.class));
                } else if (id == R.id.nav_novo) {
                    irPara(activity, new Intent(activity, CadastroLivroActivity.class));
                } else if (id == R.id.nav_resumo) {
                    irPara(activity, new Intent(activity, ResumoActivity.class));
                }
                return true;
            }
        });
    }

    // Abre a tela de destino com transição suave (cross-fade) e encerra a atual
    public static void irPara(Activity origem, Intent intent) {
        Bundle options = ActivityOptionsCompat
                .makeCustomAnimation(origem, R.anim.fade_in, R.anim.fade_out)
                .toBundle();
        origem.startActivity(intent, options);
        origem.finish();
    }
}
