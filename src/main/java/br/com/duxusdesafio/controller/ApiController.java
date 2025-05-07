package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.ComposicaoTimeRepository;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiService apiService;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private ComposicaoTimeRepository composicaoTimeRepository;

    // Endpoint para cadastrar um integrante
    @PostMapping("/integrante")
    public ResponseEntity<Integrante> cadastrarIntegrante(@RequestBody Integrante integrante) {
        Integrante novoIntegrante = apiService.cadastrarIntegrante(integrante);
        return ResponseEntity.ok(novoIntegrante);
    }

    // Endpoint para retornar todos os integrantes cadastrados.
    @GetMapping("/integrantes")
    public ResponseEntity<List<Integrante>> listarIntegrantes() {
        return ResponseEntity.ok(integranteRepository.findAll());
    }

    // Endpoint para cadastrar um time
    @PostMapping("/time")
    public ResponseEntity<Time> cadastrarTime(@RequestBody Map<String, Object> request) {
        List<Map<String, String>> integrantesMap = (List<Map<String, String>>) request.get("integrantes");

        // Verifica se a data foi fornecida no request. Caso contrário, usa a data atual.
        String dataStr = (String) request.get("data");
        LocalDate data = (dataStr != null && !dataStr.isEmpty()) ? LocalDate.parse(dataStr) : LocalDate.now();

        // Primeiro persista os integrantes individualmente
        List<Integrante> integrantes = integrantesMap.stream()
                .map(map -> {
                    String idStr = map.get("id");
                    if (idStr != null) {
                        Long id = Long.parseLong(idStr);
                        return integranteRepository.findById(id).orElseThrow(() -> new RuntimeException("Integrante não encontrado!" + id));
                    } else {
                        return apiService.cadastrarIntegrante(new Integrante(
                                map.get("franquia"),
                                map.get("nome"),
                                map.get("funcao")
                        ));
                    }
                })
                .collect(Collectors.toList());

        Time novoTime = apiService.cadastrarTime(integrantes, data);
        return ResponseEntity.ok(novoTime);
    }

    /**
     * Endpoint para retornar todos os times cadastrados com seus integrantes.
     */
    @GetMapping("/times")
    public ResponseEntity<List<Map<String, Object>>> listarTodosTimes() {
        List<Time> todosTimes = timeRepository.findAll();

        List<Map<String, Object>> response = todosTimes.stream()
                .map(time -> {
                    Map<String, Object> timeMap = new HashMap<>();
                    timeMap.put("id", time.getId());
                    timeMap.put("data", time.getData().toString());

                    List<Map<String, String>> integrantes = time.getComposicoes().stream()
                            .map(composicao -> {
                                Map<String, String> integranteMap = new HashMap<>();
                                integranteMap.put("id", composicao.getIntegrante().getId().toString());
                                integranteMap.put("nome", composicao.getIntegrante().getNome());
                                integranteMap.put("franquia", composicao.getIntegrante().getFranquia());
                                integranteMap.put("funcao", composicao.getIntegrante().getFuncao());
                                return integranteMap;
                            })
                            .collect(Collectors.toList());

                    timeMap.put("integrantes", integrantes);
                    return timeMap;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para retornar o time na data solicitada.
     */
    @GetMapping("/time-da-data")
    public ResponseEntity<Map<String, Object>> getTimeDaData(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        // Buscar todos os times do banco para processar
        List<Time> todosTimes = timeRepository.findAll();

        // Chama o serviço para obter o time na data específica
        Time time = apiService.timeDaData(data, todosTimes);

        if (time == null) {
            return ResponseEntity.notFound().build();
        }

        // Formatar a resposta conforme esperado
        Map<String, Object> response = new HashMap<>();
        response.put("data", time.getData().toString());  // Retorna a data como string no formato yyyy-MM-dd

        // Acessa corretamente o nome do integrante
        List<String> integrantesNomes = time.getComposicoes().stream()
                .map(composicao -> composicao.getIntegrante().getNome()) // Certifique-se de pegar o nome do integrante
                .collect(Collectors.toList());

        response.put("integrantes", integrantesNomes);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para retornar o integrante mais usado no período.
     */
    @GetMapping("/integrante-mais-usado")
    public ResponseEntity<Map<String, String>> getIntegranteMaisUsado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        List<Time> todosOsTimes = timeRepository.findAll();
        Integrante integrante = apiService.integranteMaisUsado(dataInicial, dataFinal, todosOsTimes);

        if (integrante == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Collections.singletonMap("integrante", integrante.getNome()));
    }

    /**
     * Endpoint para retornar os integrantes do time mais comum no período.
     */
    @GetMapping("/time-mais-comum")
    public ResponseEntity<Map<String, Object>> getTimeMaisComum(
            @RequestParam(value = "dataInicial", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(value = "dataFinal", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        // Buscar todos os times do banco para processar
        List<Time> todosTimes = timeRepository.findAll();

        List<String> integrantes = apiService.integrantesDoTimeMaisComum(dataInicial, dataFinal, todosTimes);

        Map<String, Object> response = new HashMap<>();
        response.put("integrantes", integrantes);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para retornar a função mais comum nos times dentro do período.
     */
    @GetMapping("/funcao-mais-comum")
    public ResponseEntity<Map<String, String>> getFuncaoMaisComum(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        List<Time> todosOsTimes = timeRepository.findAll(); // <-- aqui está o terceiro argumento

        String funcao = apiService.funcaoMaisComum(dataInicial, dataFinal, todosOsTimes);

        if (funcao == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Collections.singletonMap("Função", funcao));
    }

    /**
     * Endpoint para retornar a franquia mais famosa dentro do período.
     */
    @GetMapping("/franquia-mais-famosa")
    public ResponseEntity<Map<String, String>> getFranquiaMaisFamosa(
            @RequestParam(value = "dataInicial", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(value = "dataFinal", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        // Buscar todos os times do banco para processar
        List<Time> todosTimes = timeRepository.findAll();

        String franquia = apiService.franquiaMaisFamosa(dataInicial, dataFinal, todosTimes);

        if (franquia == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("Franquia", franquia);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para retornar a contagem de franquias dentro do período.
     */
    @GetMapping("/contagem-por-franquia")
    public ResponseEntity<Map<String, Long>> getContagemPorFranquia(
            @RequestParam(value = "dataInicial", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(value = "dataFinal", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        // Buscar todos os times do banco para processar
        List<Time> todosTimes = timeRepository.findAll();

        Map<String, Long> franquiaCount = apiService.contagemPorFranquia(dataInicial, dataFinal, todosTimes);

        return ResponseEntity.ok(franquiaCount);
    }

    /**
     * Endpoint para retornar a contagem de funções dentro do período.
     */
    @GetMapping("/contagem-por-funcao")
    public ResponseEntity<Map<String, Long>> getContagemPorFuncao(
            @RequestParam(value = "dataInicial", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam(value = "dataFinal", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        // Buscar todos os times do banco para processar
        List<Time> todosTimes = timeRepository.findAll();

        Map<String, Long> funcaoCount = apiService.contagemPorFuncao(dataInicial, dataFinal, todosTimes);

        return ResponseEntity.ok(funcaoCount);
    }


}
