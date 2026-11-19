package com.example.bibliotecapessoal.persistencia;

import com.example.bibliotecapessoal.model.Livro;

import java.util.List;

public interface ILivroDAO {
    boolean salvar(Livro livro);
    boolean atualizar(Livro livro);
    boolean excluir(Livro livro);
    List<Livro> listarTodos();
    int contar(Boolean lido);
}
