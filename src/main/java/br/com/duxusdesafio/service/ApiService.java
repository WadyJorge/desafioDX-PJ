package br.com.duxusdesafio.service;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.ComposicaoTimeRepository;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service que possui as regras de negócio para o processamento dos dados solicitados no desafio.
 * Além disso, realiza o cadastro de integrantes e times.
 */
@Service
public class ApiService {

    @Autowired
    private ComposicaoTimeRepository composicaoTimeRepository;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private TimeRepository timeRepository;

    /**
     * Cadastra um novo integrante no sistema.
     */
    public Integrante cadastrarIntegrante(Integrante integrante) {
        return integranteRepository.save(integrante);
    }

    /**
     * Cadastra um novo time no sistema com a lista de integrantes e a data.
     */
    @Transactional
    public Time cadastrarTime(List<Integrante> integrantes, LocalDate data) {
        Optional<Time> timeExistente = timeRepository.findByData(data);

        List<Integrante> integrantesSalvos = new ArrayList<>();

        for (Integrante integrante : integrantes) {
            List<Integrante> existentes = integranteRepository.findByNome(integrante.getNome());

            if (!existentes.isEmpty()) {
                integrantesSalvos.add(existentes.get(0));
            } else {
                integrantesSalvos.add(integranteRepository.save(integrante));
            }
        }

        Time time;
        if (timeExistente.isPresent()) {
            time = timeExistente.get();
            composicaoTimeRepository.deleteByTimeId(time.getId());
        } else {
            time = new Time();
            time.setData(data);
            time = timeRepository.save(time);
        }

        List<ComposicaoTime> composicoes = new ArrayList<>();

        for (Integrante integrante : integrantesSalvos) {
            ComposicaoTime composicao = new ComposicaoTime(time, integrante);
            composicaoTimeRepository.save(composicao);
            composicoes.add(composicao);
        }

        time.setComposicoes(composicoes);

        return time;
    }

    /**
     * Retorna um Time, com a composição do time daquela data
     */
    public Time timeDaData(LocalDate data, List<Time> todosOsTimes) {
        System.out.println("Data recebida: " + data);
        for (Time time : todosOsTimes) {
            System.out.println("Data do time no banco: " + time.getData());
        }
        return todosOsTimes.stream()
                .filter(time -> time.getData().equals(data))
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna o integrante que estiver presente na maior quantidade de times
     * dentro do período
     */
    public Integrante integranteMaisUsado(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<Integrante, Long> contador = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (ComposicaoTime composicao : time.getComposicoes()) {
                    Integrante integrante = composicao.getIntegrante();
                    contador.put(integrante, contador.getOrDefault(integrante, 0L) + 1);
                }
            }
        }

        return contador.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Retorna uma lista com os nomes dos integrantes do time mais comum
     * dentro do período
     */
    public List<String> integrantesDoTimeMaisComum(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<List<String>, Integer> composicaoCounter = new HashMap<>();

        // Filtra os times pelo período
        List<Time> timesDoPeriodo = todosOsTimes.stream()
                .filter(time -> (dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                        (dataFinal == null || !time.getData().isAfter(dataFinal)))
                .collect(Collectors.toList());

        // Para cada time, cria uma lista de nomes de integrantes ordenada
        for (Time time : timesDoPeriodo) {
            List<String> nomesIntegrantes = time.getComposicoes().stream()
                    .map(composicao -> composicao.getIntegrante().getNome())
                    .sorted()
                    .collect(Collectors.toList());

            composicaoCounter.put(nomesIntegrantes, composicaoCounter.getOrDefault(nomesIntegrantes, 0) + 1);
        }

        // Encontra a composição mais comum
        return composicaoCounter.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Collections.emptyList());
    }

    /**
     * Retorna a função mais comum nos times dentro do período
     */
    public String funcaoMaisComum(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> funcaoCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (ComposicaoTime composicao : time.getComposicoes()) {
                    String funcao = composicao.getIntegrante().getFuncao();
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
     * Retorna o nome da Franquia mais comum nos times dentro do período
     */
    public String franquiaMaisFamosa(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> franquiaCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (ComposicaoTime composicao : time.getComposicoes()) {
                    String franquia = composicao.getIntegrante().getFranquia();
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
     * Retorna o número (quantidade) de Franquias dentro do período
     */
    public Map<String, Long> contagemPorFranquia(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> franquiaCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (ComposicaoTime composicao : time.getComposicoes()) {
                    String franquia = composicao.getIntegrante().getFranquia();
                    franquiaCount.put(franquia, franquiaCount.getOrDefault(franquia, 0L) + 1);
                }
            }
        }

        return franquiaCount;
    }

    /**
     * Retorna o número (quantidade) de Funções dentro do período
     */
    public Map<String, Long> contagemPorFuncao(LocalDate dataInicial, LocalDate dataFinal, List<Time> todosOsTimes) {
        Map<String, Long> funcaoCount = new HashMap<>();

        for (Time time : todosOsTimes) {
            if ((dataInicial == null || !time.getData().isBefore(dataInicial)) &&
                    (dataFinal == null || !time.getData().isAfter(dataFinal))) {
                for (ComposicaoTime composicao : time.getComposicoes()) {
                    String funcao = composicao.getIntegrante().getFuncao();
                    funcaoCount.put(funcao, funcaoCount.getOrDefault(funcao, 0L) + 1);
                }
            }
        }

        return funcaoCount;
    }
}
