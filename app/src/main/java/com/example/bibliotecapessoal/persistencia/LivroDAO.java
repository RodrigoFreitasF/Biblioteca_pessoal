package com.example.bibliotecapessoal.persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.bibliotecapessoal.model.Livro;

import java.util.ArrayList;
import java.util.List;

public class LivroDAO implements ILivroDAO {
    private final SQLiteDatabase escreve;
    private final SQLiteDatabase le;

    public LivroDAO(Context context) {
        DbHelper db = new DbHelper(context);
        escreve = db.getWritableDatabase();
        le = db.getReadableDatabase();
    }

    @Override
    public boolean salvar(Livro livro) {
        if (livro == null) {
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COL_TITULO, livro.getTitulo());
        cv.put(DbHelper.COL_AUTOR, livro.getAutor());
        cv.put(DbHelper.COL_ANO, livro.getAno());
        cv.put(DbHelper.COL_LIDO, livro.isLido() ? 1 : 0);
        try {
            long resultado = escreve.insert(DbHelper.TABELA_LIVROS, null, cv);
            if (resultado != -1) {
                Log.i("InfoDB", "Sucesso ao salvar o registro");
                return true;
            }
            Log.e("InfoDB", "Falha ao salvar o registro: retorno -1");
            return false;
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao salvar o registro. Erro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(Livro livro) {
        if (livro == null || livro.getId() == null) {
            Log.e("InfoDB", "Erro ao atualizar: ID do livro e nulo");
            return false;
        }
        ContentValues cv = new ContentValues();
        cv.put(DbHelper.COL_TITULO, livro.getTitulo());
        cv.put(DbHelper.COL_AUTOR, livro.getAutor());
        cv.put(DbHelper.COL_ANO, livro.getAno());
        cv.put(DbHelper.COL_LIDO, livro.isLido() ? 1 : 0);
        try {
            String[] args = {String.valueOf(livro.getId())};
            int linhasAfetadas = escreve.update(
                    DbHelper.TABELA_LIVROS,
                    cv,
                    DbHelper.COL_ID + " = ?",
                    args
            );
            if (linhasAfetadas > 0) {
                Log.i("InfoDB", "Sucesso ao atualizar o registro");
                return true;
            }
            Log.e("InfoDB", "Nenhum registro atualizado");
            return false;
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao atualizar o registro. Erro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean excluir(Livro livro) {
        if (livro == null || livro.getId() == null) {
            Log.e("InfoDB", "Erro ao excluir: ID do livro e nulo");
            return false;
        }
        try {
            String[] args = {String.valueOf(livro.getId())};
            int linhasAfetadas = escreve.delete(
                    DbHelper.TABELA_LIVROS,
                    DbHelper.COL_ID + " = ?",
                    args
            );
            if (linhasAfetadas > 0) {
                Log.i("InfoDB", "Sucesso ao excluir o registro");
                return true;
            }
            Log.e("InfoDB", "Nenhum registro excluido");
            return false;
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao excluir o registro. Erro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Livro> listarTodos() {
        List<Livro> lstLivros = new ArrayList<>();
        String sql = "SELECT * FROM " + DbHelper.TABELA_LIVROS
                + " ORDER BY " + DbHelper.COL_TITULO + " COLLATE NOCASE ASC;";
        try (Cursor cursor = le.rawQuery(sql, null)) {
            int indexId = cursor.getColumnIndexOrThrow(DbHelper.COL_ID);
            int indexTitulo = cursor.getColumnIndexOrThrow(DbHelper.COL_TITULO);
            int indexAutor = cursor.getColumnIndexOrThrow(DbHelper.COL_AUTOR);
            int indexAno = cursor.getColumnIndexOrThrow(DbHelper.COL_ANO);
            int indexLido = cursor.getColumnIndexOrThrow(DbHelper.COL_LIDO);

            while (cursor.moveToNext()) {
                Livro livro = new Livro();
                livro.setId(cursor.getLong(indexId));
                livro.setTitulo(cursor.getString(indexTitulo));
                livro.setAutor(cursor.getString(indexAutor));
                livro.setAno(cursor.getInt(indexAno));
                livro.setLido(cursor.getInt(indexLido) == 1);
                lstLivros.add(livro);
            }
            Log.i("InfoDB", "Sucesso ao pesquisar os registros");
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao listar os registros: " + e.getMessage());
        }
        return lstLivros;
    }

    @Override
    public int contar(Boolean lido) {
        String sql;
        String[] args;
        if (lido == null) {
            sql = "SELECT COUNT(*) FROM " + DbHelper.TABELA_LIVROS + ";";
            args = null;
        } else {
            sql = "SELECT COUNT(*) FROM " + DbHelper.TABELA_LIVROS
                    + " WHERE " + DbHelper.COL_LIDO + " = ?;";
            args = new String[]{lido ? "1" : "0"};
        }
        try (Cursor cursor = le.rawQuery(sql, args)) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e("InfoDB", "Erro ao contar os registros: " + e.getMessage());
        }
        return 0;
    }
}
