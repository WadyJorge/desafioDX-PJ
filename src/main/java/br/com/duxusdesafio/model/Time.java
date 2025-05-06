package br.com.duxusdesafio.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time")
public class Time {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_time", nullable = false)
    private Long id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @OneToMany(mappedBy = "time", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ComposicaoTime> composicoes = new ArrayList<>();

    public Time() {}

    public Time(LocalDate data, List<ComposicaoTime> composicoes) {
        this.data = data;
        this.composicoes = composicoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public List<ComposicaoTime> getComposicoes() {
        return composicoes;
    }

    public void setComposicoes(List<ComposicaoTime> composicoes) {
        this.composicoes = composicoes;
    }
}
