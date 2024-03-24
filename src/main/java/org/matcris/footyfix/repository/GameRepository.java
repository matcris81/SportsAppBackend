package org.matcris.footyfix.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.matcris.footyfix.domain.Game;
import org.matcris.footyfix.domain.Player;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Game entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE FUNCTION('DATE', g.gameDate) = :date ORDER BY g.gameDate ASC")
    List<Game> findAllByGameDateOrdered(@Param("date") LocalDate date);

    @Query(
        "SELECT g FROM Game g WHERE FUNCTION('DATE', g.gameDate) = CURRENT_DATE AND g.gameDate > CURRENT_TIMESTAMP ORDER BY g.gameDate ASC"
    )
    List<Game> findTodayUpcomingGames();

    @Query("SELECT g FROM Game g WHERE g.venueId = :venueId ORDER BY g.gameDate ASC")
    List<Game> findGamesByVenueOrdered(@Param("venueId") Integer venueId);

    @Query("SELECT g FROM Game g JOIN g.players p WHERE p.id = :userId")
    List<Game> findAllGamesByUserId(@Param("userId") String userId);

    List<Game> findByVenueIdOrderByGameDateAsc(Integer venueId);

    @Query("SELECT COUNT(p) FROM Game g LEFT JOIN g.players p WHERE g.id = :gameId")
    Long countPlayersByGame(@Param("gameId") Long gameId);

    @Query("SELECT DISTINCT p FROM Game g JOIN g.players p WHERE g.id = :gameId")
    List<Player> findPlayersByGameId(@Param("gameId") Long gameId);

    // In GameRepository.java
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Game g JOIN g.players p WHERE g.id = :gameId AND p.id = :userId")
    boolean existsByGameIdAndUserId(@Param("gameId") Long gameId, @Param("userId") String userId);

    @Query("SELECT g FROM Game g WHERE g.gameDate BETWEEN :start AND :end ORDER BY g.gameDate ASC")
    List<Game> findGamesWithinRange(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

    @Query("SELECT DISTINCT g FROM Game g JOIN g.players p WHERE g.gameDate > :start AND g.gameDate <= :end AND p.isFake = TRUE")
    List<Game> findGamesStartingInNext24HoursWithFakePlayers(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

    List<Game> findAllByGameDateBefore(ZonedDateTime dateTime);
    //}

    //    @Query("SELECT g FROM Game g WHERE g.gameDate BETWEEN :start AND :end ORDER BY g.gameDate ASC")
    //    List<Game> findGamesWithinRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    //    @Query("SELECT DISTINCT g FROM Game g JOIN g.players p WHERE g.gameDate > CURRENT_TIMESTAMP AND g.gameDate <= (CURRENT_TIMESTAMP + 1) AND p.isFake = TRUE")
    //    List<Game> findGamesStartingInNext24HoursWithFakePlayers();

    //    @Query("SELECT DISTINCT g FROM Game g JOIN g.players p WHERE g.gameDate > :start AND g.gameDate <= :end AND p.isFake = TRUE")
    //    List<Game> findGamesStartingInNext24HoursWithFakePlayers(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
