package br.edu.etechas.cantina.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CarteirinhaAluno {
    public enum Status { ATIVA, BLOQUEADA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeAluno;

    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
}
