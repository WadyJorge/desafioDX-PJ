package br.com.duxusdesafio.service;

import br.com.duxusdesafio.model.ComposicaoTime;
import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.model.Time;
import br.com.duxusdesafio.repository.ComposicaoTimeRepository;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.repository.TimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiServiceTest {

    @Autowired
    private ApiService apiService;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private ComposicaoTimeRepository composicaoTimeRepository;

    private List<Time> times;
    private List<Integrante> integrantes;

    @BeforeEach
    void setUp() {
        // Limpa os dados do banco antes de cada teste
        integranteRepository.deleteAll();
        timeRepository.deleteAll();
        composicaoTimeRepository.deleteAll();

        // Criando e salvando Integrantes
        Integrante i1 = integranteRepository.save(new Integrante("Apex Legends", "Ash", "Atacante"));
        Integrante i2 = integranteRepository.save(new Integrante("Apex Legends", "Gibraltar", "Defensor"));
        Integrante i3 = integranteRepository.save(new Integrante("Valorant", "Wattson", "Suporte"));

        // Criando e salvando Times
        Time t1 = timeRepository.save(new Time(LocalDate.of(2024, 1, 10), null));
        Time t2 = timeRepository.save(new Time(LocalDate.of(2024, 1, 15), null));
        Time t3 = timeRepository.save(new Time(LocalDate.of(2024, 1, 20), null));

        // Criando e salvando as Composições dos Times
        composicaoTimeRepository.save(new ComposicaoTime(t1, i1));
        composicaoTimeRepository.save(new ComposicaoTime(t1, i2));
        composicaoTimeRepository.save(new ComposicaoTime(t2, i1));
        composicaoTimeRepository.save(new ComposicaoTime(t2, i2));
        composicaoTimeRepository.save(new ComposicaoTime(t2, i3));
        composicaoTimeRepository.save(new ComposicaoTime(t3, i1));
        composicaoTimeRepository.save(new ComposicaoTime(t3, i2));

        times = Arrays.asList(t1, t2, t3);
        integrantes = Arrays.asList(i1, i2, i3);
    }

    @Test
    void testCadastrarIntegrante() {
        Integrante novo = new Integrante("Apex Legends", "Bangalore", "Atacante");
        Integrante salvo = apiService.cadastrarIntegrante(novo);
        assertNotNull(salvo.getId());
        assertEquals("Bangalore", salvo.getNome());
    }

    @Test
    void testCadastrarTime() {
        Time novoTime = apiService.cadastrarTime(integrantes, LocalDate.of(2024, 2, 1));
        assertNotNull(novoTime.getId());
        assertEquals(3, novoTime.getComposicoes().size());
    }

    @Test
    void testTimeDaData() {
        Time resultado = apiService.timeDaData(LocalDate.of(2024, 1, 15), times);
        assertNotNull(resultado);
        assertEquals(3, resultado.getComposicoes().size());
    }

    @Test
    void testIntegranteMaisUsado() {
        Integrante resultado = apiService.integranteMaisUsado(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                times
        );
        assertNotNull(resultado);
        assertEquals("Ash", resultado.getNome());
    }

    @Test
    void testIntegrantesDoTimeMaisComum() {
        List<String> nomes = apiService.integrantesDoTimeMaisComum(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                times
        );
        assertEquals(2, nomes.size());
        assertTrue(nomes.contains("Ash"));
        assertTrue(nomes.contains("Gibraltar"));
    }

    @Test
    void testFuncaoMaisComum() {
        String resultado = apiService.funcaoMaisComum(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                times
        );
        assertEquals("Atacante", resultado);
    }

    @Test
    void testFranquiaMaisFamosa() {
        String resultado = apiService.franquiaMaisFamosa(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                times
        );
        assertEquals("Apex Legends", resultado);
    }

    @Test
    void testContagemPorFranquia() {
        Map<String, Long> resultado = apiService.contagemPorFranquia(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                times
        );
        assertEquals(2, resultado.size());
        assertEquals(6L, resultado.get("Apex Legends"));
        assertEquals(1L, resultado.get("Valorant"));
    }

    @Test
    void testContagemPorFuncao() {
        Map<String, Long> resultado = apiService.contagemPorFuncao(
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                times
        );
        assertEquals(3, resultado.size());
        assertEquals(3L, resultado.get("Atacante"));
        assertEquals(3L, resultado.get("Defensor"));
        assertEquals(1L, resultado.get("Suporte"));
    }
}
