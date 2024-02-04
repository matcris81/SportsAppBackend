package org.matcris.footyfix.repository;

import java.util.List;
import java.util.Optional;
import org.matcris.footyfix.domain.Player;
import org.springframework.data.domain.Page;

public interface PlayerRepositoryWithBagRelationships {
    Optional<Player> fetchBagRelationships(Optional<Player> player);

    List<Player> fetchBagRelationships(List<Player> players);

    Page<Player> fetchBagRelationships(Page<Player> players);
}
