package org.matcris.footyfix.repository;

import org.matcris.footyfix.domain.PlayerImage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PlayerImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PlayerImageRepository extends JpaRepository<PlayerImage, Long> {}
