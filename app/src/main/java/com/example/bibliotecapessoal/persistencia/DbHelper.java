package com.example.bibliotecapessoal.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    public static final String NOME_DB = "DB_BIBLIOTECA";
    public static final String TABELA_LIVROS = "livros";
    public static final String COL_ID = "id";
    public static final String COL_TITULO = "titulo";
    public static final String COL_AUTOR = "autor";
    public static final String COL_ANO = "ano";
    public static final String COL_LIDO = "lido";
    public static final String COL_NOTA = "nota";

    public DbHelper(@Nullable Context context) {
        super(context, NOME_DB, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABELA_LIVROS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITULO + " TEXT NOT NULL, "
                + COL_AUTOR + " TEXT NOT NULL, "
                + COL_ANO + " INTEGER NOT NULL, "
                + COL_LIDO + " INTEGER DEFAULT 0);";
        try {
            db.execSQL(sql);
            Log.i("InfoDB", "Tabela criada com sucesso");
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao criar tabela, erro: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            if (oldVersion < 2) {
                String sql = "ALTER TABLE " + TABELA_LIVROS
                        + " ADD COLUMN " + COL_NOTA + " INTEGER;";
                db.execSQL(sql);
                Log.i("InfoDB", "Migracao para versao 2 executada com sucesso");
            }
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao atualizar a tabela: " + e.getMessage());
        }
    }
}
