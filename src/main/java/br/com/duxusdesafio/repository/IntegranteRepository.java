package br.com.duxusdesafio.repository;

import br.com.duxusdesafio.model.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
    List<Integrante> findByNome(String nome);
}
