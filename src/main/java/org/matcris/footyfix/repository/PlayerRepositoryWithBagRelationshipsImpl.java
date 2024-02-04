package org.matcris.footyfix.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.matcris.footyfix.domain.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PlayerRepositoryWithBagRelationshipsImpl implements PlayerRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Player> fetchBagRelationships(Optional<Player> player) {
        return player.map(this::fetchGames).map(this::fetchVenues);
    }

    @Override
    public Page<Player> fetchBagRelationships(Page<Player> players) {
        return new PageImpl<>(fetchBagRelationships(players.getContent()), players.getPageable(), players.getTotalElements());
    }

    @Override
    public List<Player> fetchBagRelationships(List<Player> players) {
        return Optional.of(players).map(this::fetchGames).map(this::fetchVenues).orElse(Collections.emptyList());
    }

    Player fetchGames(Player result) {
        return entityManager
            .createQuery("select player from Player player left join fetch player.games where player.id = :id", Player.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Player> fetchGames(List<Player> players) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, players.size()).forEach(index -> order.put(players.get(index).getId(), index));
        List<Player> result = entityManager
            .createQuery("select player from Player player left join fetch player.games where player in :players", Player.class)
            .setParameter("players", players)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Player fetchVenues(Player result) {
        return entityManager
            .createQuery("select player from Player player left join fetch player.venues where player.id = :id", Player.class)
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Player> fetchVenues(List<Player> players) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, players.size()).forEach(index -> order.put(players.get(index).getId(), index));
        List<Player> result = entityManager
            .createQuery("select player from Player player left join fetch player.venues where player in :players", Player.class)
            .setParameter("players", players)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
