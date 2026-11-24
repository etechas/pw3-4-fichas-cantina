package br.edu.etechas.cantina.service;

import br.edu.etechas.cantina.model.CarteirinhaAluno;
import br.edu.etechas.cantina.model.CarteirinhaAluno.Status;
import br.edu.etechas.cantina.repository.CarteirinhaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CarteirinhaService {
    private static final BigDecimal SALDO_MAXIMO = new BigDecimal("9999999999.99");
    private final CarteirinhaRepository repository;

    public CarteirinhaService(CarteirinhaRepository repository) {
        this.repository = repository;
    }

    public List<CarteirinhaAluno> listar() {
        return repository.findAll(Sort.by("id"));
    }

    public CarteirinhaAluno buscar(Long id) {
        return repository.findById(id).orElseThrow(this::naoEncontrada);
    }

    public CarteirinhaAluno cadastrar(CarteirinhaAluno dados) {
        CarteirinhaAluno nova = new CarteirinhaAluno();
        copiarCadastro(nova, dados);
        nova.setSaldo(validarValor(dados.getSaldo(), true));
        return repository.save(nova);
    }

    @Transactional
    public CarteirinhaAluno atualizar(Long id, CarteirinhaAluno dados) {
        CarteirinhaAluno atual = buscarParaAlterar(id);
        copiarCadastro(atual, dados);
        return atual;
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarParaAlterar(id));
    }

    @Transactional
    public CarteirinhaAluno debitar(Long id, BigDecimal valor) {
        BigDecimal debito = validarValor(valor, false);
        CarteirinhaAluno atual = buscarParaAlterar(id);

        if (atual.getStatus() == Status.BLOQUEADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Carteirinha bloqueada para compras.");
        }
        if (atual.getSaldo().compareTo(debito) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Saldo insuficiente para realizar a compra.");
        }

        atual.setSaldo(atual.getSaldo().subtract(debito));
        return atual;
    }

    @Transactional
    public CarteirinhaAluno recarregar(Long id, BigDecimal valor) {
        BigDecimal recarga = validarValor(valor, false);
        CarteirinhaAluno atual = buscarParaAlterar(id);
        atual.setSaldo(validarValor(atual.getSaldo().add(recarga), true));
        return atual;
    }

    private CarteirinhaAluno buscarParaAlterar(Long id) {
        return repository.buscarParaAlterar(id).orElseThrow(this::naoEncontrada);
    }

    private ResponseStatusException naoEncontrada() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Carteirinha não encontrada.");
    }

    private void copiarCadastro(CarteirinhaAluno destino, CarteirinhaAluno dados) {
        destino.setNomeAluno(textoObrigatorio(dados.getNomeAluno(), "Nome do aluno"));
        destino.setMatricula(textoObrigatorio(dados.getMatricula(), "Matrícula"));
        if (dados.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o status da carteirinha.");
        }
        destino.setStatus(dados.getStatus());
    }

    private String textoObrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank() || valor.strip().length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " deve ter entre 1 e 255 caracteres.");
        }
        return valor.strip();
    }

    private BigDecimal validarValor(BigDecimal valor, boolean aceitaZero) {
        if (valor == null || valor.signum() < 0 || (!aceitaZero && valor.signum() == 0)
                || valor.stripTrailingZeros().scale() > 2 || valor.compareTo(SALDO_MAXIMO) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    aceitaZero ? "Informe um saldo válido com até duas casas decimais."
                               : "Informe um valor positivo com até duas casas decimais.");
        }
        return valor.setScale(2);
    }
}
