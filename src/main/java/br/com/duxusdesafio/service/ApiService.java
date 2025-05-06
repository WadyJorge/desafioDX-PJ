package br.com.duxusdesafio.service;

import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service que possui as regras de negócio para o processamento dos dados solicitados no desafio.
 * Além disso, realiza o cadastro de integrantes e times.
 */
@Service
public class ApiService {

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private TimeRepository timeRepository;

    /**
     * Vai cadastrar um novo integrante no sistema.
     */
    public Integrante cadastrarIntegrante(Integrante integrante) {
        // Salvando o integrante no banco de dados
        return integranteRepository.save(integrante);
    }

    /**
     * Vai cadastrar um novo time no sistema.
     */
    public Time cadastrarTime(Time time) {
        // Salvando o time no banco de dados
        return timeRepository.save(time);
    }

    /**
     * Vai retornar um Time, com a composição do time daquela data
     */
    public Time timeDaData(LocalDate data, List<Time> todosOsTimes) {
        for (Time time : todosOsTimes) {
            if (time.getData().equals(data)) {
                return time;
            }
        }
        return null;
    }

    /**
     * Vai retornar o integrante que estiver presente na maior quantidade de times
     * dentro do período
     */
    public Integrante integranteMaisUsado(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<Integrante, Long> contador = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (Integrante integrante : time.getIntegrantes()) {
                    contador.put(integrante, contador.getOrDefault(integrante, 0L) + 1);
                }
            }
        }

        return contador.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);  // Retorna o integrante mais usado
    }

    /**
     * Vai retornar uma lista com os nomes dos integrantes do time mais comum
     * dentro do período
     */
    public List<String> integrantesDoTimeMaisComum(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<List<Integrante>, Long> timeCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                List<Integrante> integrantes = time.getIntegrantes();
                timeCount.put(integrantes, timeCount.getOrDefault(integrantes, 0L) + 1);
            }
        }

        List<Integrante> timeMaisComum = timeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Collections.emptyList());

        return timeMaisComum.stream()
                .map(Integrante::getNome)
                .collect(Collectors.toList());
    }

    /**
     * Vai retornar a função mais comum nos times dentro do período
     */
    public String funcaoMaisComum(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> funcaoCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (Integrante integrante : time.getIntegrantes()) {
                    String funcao = integrante.getFuncao();
                    funcaoCount.put(funcao, funcaoCount.getOrDefault(funcao, 0L) + 1);
                }
            }
        }

        return funcaoCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Vai retornar o nome da Franquia mais comum nos times dentro do período
     */
    public String franquiaMaisFamosa(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> franquiaCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (Integrante integrante : time.getIntegrantes()) {
                    String franquia = integrante.getFranquia();
                    franquiaCount.put(franquia, franquiaCount.getOrDefault(franquia, 0L) + 1);
                }
            }
        }

        return franquiaCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Vai retornar o número (quantidade) de Franquias dentro do período
     */
    public Map<String, Long> contagemPorFranquia(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> franquiaCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (Integrante integrante : time.getIntegrantes()) {
                    String franquia = integrante.getFranquia();
                    franquiaCount.put(franquia, franquiaCount.getOrDefault(franquia, 0L) + 1);
                }
            }
        }

        return franquiaCount;
    }

    /**
     * Vai retornar o número (quantidade) de Funções dentro do período
     */
    public Map<String, Long> contagemPorFuncao(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> funcaoCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (Integrante integrante : time.getIntegrantes()) {
                    String funcao = integrante.getFuncao();
                    funcaoCount.put(funcao, funcaoCount.getOrDefault(funcao, 0L) + 1);
                }
            }
        }

        return funcaoCount;
    }
}
