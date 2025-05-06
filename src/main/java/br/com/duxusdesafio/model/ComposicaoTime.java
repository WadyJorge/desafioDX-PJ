package br.com.duxusdesafio.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "composicao_time")
public class ComposicaoTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_composicao", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_time", nullable = false)
    @JsonBackReference
    private Time time;

    @ManyToOne
    @JoinColumn(name = "id_integrante", nullable = false)
    private Integrante integrante;

    public ComposicaoTime() {
    }

    public ComposicaoTime(Time time, Integrante integrante) {
        this.time = time;
        this.integrante = integrante;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integrante getIntegrante() {
        return integrante;
    }

    public void setIntegrante(Integrante integrante) {
        this.integrante = integrante;
    }
}
