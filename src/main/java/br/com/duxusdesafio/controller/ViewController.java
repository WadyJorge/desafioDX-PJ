package br.com.duxusdesafio.controller;

import br.com.duxusdesafio.model.Integrante;
import br.com.duxusdesafio.repository.IntegranteRepository;
import br.com.duxusdesafio.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private ApiService apiService;

    @GetMapping("/")
    public String redirecionarParaCadastro() {
        return "redirect:/cadastro-integrante";
    }

    @GetMapping("/cadastro-integrante")
    public String cadastroIntegranteForm(Model model) {
        model.addAttribute("integrante", new Integrante());
        return "cadastro-integrante";
    }

    @PostMapping("/cadastro-integrante")
    public String cadastrarIntegrante(@ModelAttribute Integrante integrante, Model model) {
        apiService.cadastrarIntegrante(integrante);
        model.addAttribute("mensagem", "Integrante cadastrado com sucesso!");
        model.addAttribute("integrante", new Integrante());
        return "cadastro-integrante";
    }

    @GetMapping("/montar-time")
    public String montarTime(Model model) {
        model.addAttribute("integrantes", integranteRepository.findAll());
        return "montar-time";
    }

    @GetMapping("/consultas")
    public String consultas(Model model) {
        model.addAttribute("integrantes", integranteRepository.findAll());
        return "consultas";
    }
}
