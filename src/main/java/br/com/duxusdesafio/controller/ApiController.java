package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ApiService apiService;

    // Endpoint para cadastrar um integrante
    @PostMapping("/integrante")
    public ResponseEntity<Integrante> cadastrarIntegrante(@RequestBody Integrante integrante) {
        Integrante novoIntegrante = apiService.cadastrarIntegrante(integrante);
        return ResponseEntity.ok(novoIntegrante);
    }

    // Endpoint para cadastrar um time
    @PostMapping("/time")
    public ResponseEntity<Time> cadastrarTime(@RequestBody Time time) {
        Time novoTime = apiService.cadastrarTime(time);
        return ResponseEntity.ok(novoTime);
    }

    /**
     * Endpoint para retornar o time na data solicitada.
     */
    @GetMapping("/time-da-data")
    public ResponseEntity<Time> getTimeDaData(@RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                              @RequestParam("todosTimes") List<Time> todosTimes) {
        Time time = apiService.timeDaData(data, todosTimes);
        return time != null ? ResponseEntity.ok(time) : ResponseEntity.notFound().build();
    }

    /**
     * Endpoint para retornar o integrante mais usado no período.
     */
    @GetMapping("/integrante-mais-usado")
    public ResponseEntity<Integrante> getIntegranteMaisUsado(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam("todosTimes") List<Time> todosTimes) {

        Integrante integrante = apiService.integranteMaisUsado(dataInicial, dataFinal, todosTimes);
        return integrante != null ? ResponseEntity.ok(integrante) : ResponseEntity.notFound().build();
    }

    /**
     * Endpoint para retornar os integrantes do time mais comum no período.
     */
    @GetMapping("/integrantes-do-time-mais-comum")
    public ResponseEntity<List<String>> getIntegrantesDoTimeMaisComum(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam("todosTimes") List<Time> todosTimes) {

        List<String> integrantes = apiService.integrantesDoTimeMaisComum(dataInicial, dataFinal, todosTimes);
        return ResponseEntity.ok(integrantes);
    }

    /**
     * Endpoint para retornar a função mais comum nos times dentro do período.
     */
    @GetMapping("/funcao-mais-comum")
    public ResponseEntity<String> getFuncaoMaisComum(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam("todosTimes") List<Time> todosTimes) {

        String funcao = apiService.funcaoMaisComum(dataInicial, dataFinal, todosTimes);
        return funcao != null ? ResponseEntity.ok(funcao) : ResponseEntity.notFound().build();
    }

    /**
     * Endpoint para retornar a franquia mais famosa dentro do período.
     */
    @GetMapping("/franquia-mais-famosa")
    public ResponseEntity<String> getFranquiaMaisFamosa(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam("todosTimes") List<Time> todosTimes) {

        String franquia = apiService.franquiaMaisFamosa(dataInicial, dataFinal, todosTimes);
        return franquia != null ? ResponseEntity.ok(franquia) : ResponseEntity.notFound().build();
    }

    /**
     * Endpoint para retornar a contagem de franquias dentro do período.
     */
    @GetMapping("/contagem-por-franquia")
    public ResponseEntity<Map<String, Long>> getContagemPorFranquia(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam("todosTimes") List<Time> todosTimes) {

        Map<String, Long> franquiaCount = apiService.contagemPorFranquia(dataInicial, dataFinal, todosTimes);
        return ResponseEntity.ok(franquiaCount);
    }

    /**
     * Endpoint para retornar a contagem de funções dentro do período.
     */
    @GetMapping("/contagem-por-funcao")
    public ResponseEntity<Map<String, Long>> getContagemPorFuncao(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            @RequestParam("todosTimes") List<Time> todosTimes) {

        Map<String, Long> funcaoCount = apiService.contagemPorFuncao(dataInicial, dataFinal, todosTimes);
        return ResponseEntity.ok(funcaoCount);
    }
}
