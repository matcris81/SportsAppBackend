package org.matcris.footyfix.repository;

import org.matcris.footyfix.domain.Game;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Game entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {}
