package org.matcris.footyfix.repository;

import org.matcris.footyfix.domain.Venue;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Venue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {}
