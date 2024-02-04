package org.matcris.footyfix.repository;

import java.util.List;
import java.util.Optional;
import org.matcris.footyfix.domain.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Player entity.
 *
 * When extending this class, extend PlayerRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface PlayerRepository extends PlayerRepositoryWithBagRelationships, JpaRepository<Player, String> {
    default Optional<Player> findOneWithEagerRelationships(String id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Player> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Player> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
