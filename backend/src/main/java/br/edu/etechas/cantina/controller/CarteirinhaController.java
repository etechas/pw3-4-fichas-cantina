package br.edu.etechas.cantina.controller;

import br.edu.etechas.cantina.model.CarteirinhaAluno;
import br.edu.etechas.cantina.service.CarteirinhaService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/carteirinhas")
@CrossOrigin(origins = "http://localhost:4200")
public class CarteirinhaController {
    private final CarteirinhaService service;

    public CarteirinhaController(CarteirinhaService service) {
        this.service = service;
    }

    public record Operacao(BigDecimal valor) {}

    @GetMapping
    public List<CarteirinhaAluno> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public CarteirinhaAluno buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarteirinhaAluno cadastrar(@RequestBody CarteirinhaAluno dados) {
        return service.cadastrar(dados);
    }

    @PutMapping("/{id}")
    public CarteirinhaAluno atualizar(@PathVariable Long id, @RequestBody CarteirinhaAluno dados) {
        return service.atualizar(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PostMapping("/{id}/debitar")
    public CarteirinhaAluno debitar(@PathVariable Long id, @RequestBody Operacao dados) {
        return service.debitar(id, dados.valor());
    }

    @PostMapping("/{id}/recarregar")
    public CarteirinhaAluno recarregar(@PathVariable Long id, @RequestBody Operacao dados) {
        return service.recarregar(id, dados.valor());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarErro(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).body(Map.of("mensagem", erro.getReason()));
    }
}
