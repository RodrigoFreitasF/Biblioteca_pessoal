package com.example.bibliotecapessoal.model;

import java.io.Serializable;

public class Livro implements Serializable {
    private Long id;
    private String titulo;
    private String autor;
    private int ano;
    private boolean lido;

    public Livro() {
    }

    public Livro(Long id, String titulo, String autor, int ano, boolean lido) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.ano = ano;
        this.lido = lido;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }
}
