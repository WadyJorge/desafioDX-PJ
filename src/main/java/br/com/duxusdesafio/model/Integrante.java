package br.com.duxusdesafio.model;

import javax.persistence.*;

@Entity
@Table(name = "integrante")
public class Integrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integrante", nullable = false)
    private Long id;

    @Column(name = "franquia", nullable = false)
    private String franquia;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "funcao", nullable = false)
    private String funcao;

    public Integrante() {}

    public Integrante(String franquia, String nome, String funcao) {
        this.franquia = franquia;
        this.nome = nome;
        this.funcao = funcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFranquia() {
        return franquia;
    }

    public void setFranquia(String franquia) {
        this.franquia = franquia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}
