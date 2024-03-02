package org.matcris.footyfix.repository;

import java.util.List;
import org.matcris.footyfix.domain.Payment;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Payment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByPlayerId(String playerId);
}
