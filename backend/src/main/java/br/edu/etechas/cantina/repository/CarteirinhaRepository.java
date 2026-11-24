package br.edu.etechas.cantina.repository;

import br.edu.etechas.cantina.model.CarteirinhaAluno;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CarteirinhaRepository extends JpaRepository<CarteirinhaAluno, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CarteirinhaAluno c where c.id = :id")
    Optional<CarteirinhaAluno> buscarParaAlterar(@Param("id") Long id);
}
