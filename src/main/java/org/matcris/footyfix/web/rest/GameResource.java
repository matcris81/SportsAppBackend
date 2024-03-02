package org.matcris.footyfix.web.rest;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;
import org.matcris.footyfix.domain.Game;
import org.matcris.footyfix.domain.Player;
import org.matcris.footyfix.domain.Venue;
import org.matcris.footyfix.repository.GameRepository;
import org.matcris.footyfix.repository.VenueRepository;
import org.matcris.footyfix.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.matcris.footyfix.domain.Game}.
 */
@RestController
@RequestMapping("/api/games")
@Transactional
public class GameResource {

    private final Logger log = LoggerFactory.getLogger(GameResource.class);

    private static final String ENTITY_NAME = "game";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GameRepository gameRepository;
    private final FirebaseMessaging firebaseMessaging;

    public GameResource(GameRepository gameRepository, FirebaseMessaging firebaseMessaging) {
        this.gameRepository = gameRepository;
        this.firebaseMessaging = firebaseMessaging;
    }

    /**
     * {@code POST  /games} : Create a new game.
     *
     * @param game the game to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new game, or with status {@code 400 (Bad Request)} if the game has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Game> createGame(@RequestBody Game game) throws URISyntaxException {
        log.debug("REST request to save Game : {}", game);
        if (game.getId() != null) {
            throw new BadRequestAlertException("A new game cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Game result = gameRepository.save(game);
        Notification notification = Notification
            .builder()
            .setTitle("New Game Alert")
            .setBody("A new game has been added to your liked venue!")
            .build();

        Map<String, String> dataPayload = new HashMap<>();
        dataPayload.put("key1", "value1");
        dataPayload.put("key2", "value2");

        String venue = "Venue" + game.getVenueId();

        Message msg = Message.builder().setNotification(notification).putAllData(dataPayload).setTopic(venue).build();

        try {
            log.debug("Sending notification for new game to topic: " + result.getId());
            String id = firebaseMessaging.send(msg);
            log.debug("Notification sent successfully with message ID: " + id);
        } catch (FirebaseMessagingException e) {
            log.debug("Error sending notification", e);
            throw new RuntimeException(e);
        }

        return ResponseEntity
            .created(new URI("/api/games/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /games/:id} : Updates an existing game.
     *
     * @param id the id of the game to save.
     * @param game the game to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated game,
     * or with status {@code 400 (Bad Request)} if the game is not valid,
     * or with status {@code 500 (Internal Server Error)} if the game couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable(value = "id", required = false) final Long id, @RequestBody Game game)
        throws URISyntaxException {
        log.debug("REST request to update Game : {}, {}", id, game);
        if (game.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, game.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Game result = gameRepository.save(game);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, game.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /games/:id} : Partial updates given fields of an existing game, field will ignore if it is null
     *
     * @param id the id of the game to save.
     * @param game the game to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated game,
     * or with status {@code 400 (Bad Request)} if the game is not valid,
     * or with status {@code 404 (Not Found)} if the game is not found,
     * or with status {@code 500 (Internal Server Error)} if the game couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Game> partialUpdateGame(@PathVariable(value = "id", required = false) final Long id, @RequestBody Game game)
        throws URISyntaxException {
        log.debug("REST request to partial update Game partially : {}, {}", id, game);
        if (game.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, game.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gameRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Game> result = gameRepository
            .findById(game.getId())
            .map(existingGame -> {
                if (game.getGameDate() != null) {
                    existingGame.setGameDate(game.getGameDate());
                }
                if (game.getPrice() != null) {
                    existingGame.setPrice(game.getPrice());
                }
                if (game.getSize() != null) {
                    existingGame.setSize(game.getSize());
                }
                if (game.getDescription() != null) {
                    existingGame.setDescription(game.getDescription());
                }
                if (game.getVenueId() != null) {
                    existingGame.setVenueId(game.getVenueId());
                }
                if (game.getSportId() != null) {
                    existingGame.setSportId(game.getSportId());
                }

                return existingGame;
            })
            .map(gameRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, game.getId().toString())
        );
    }

    /**
     * {@code GET  /games} : get all the games.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of games in body.
     */
    @GetMapping("")
    public List<Game> getAllGames() {
        log.debug("REST request to get all Games");
        return gameRepository.findAll();
    }

    /**
     * {@code GET  /games/:id} : get the "id" game.
     *
     * @param id the id of the game to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the game, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGame(@PathVariable("id") Long id) {
        log.debug("REST request to get Game : {}", id);
        Optional<Game> game = gameRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(game);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<Game>> getGamesByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("REST request to get Games by date : {}", date);
        List<Game> games = gameRepository.findAllByGameDateOrdered(date);
        return ResponseEntity.ok().body(games);
    }

    @GetMapping("/earliest-by-venue")
    public ResponseEntity<Game> getEarliestGameByVenue(@RequestParam("venueId") Integer venueId) {
        log.debug("REST request to get earliest Game by venueId : {}", venueId);
        try {
            List<Game> games = gameRepository.findGamesByVenueOrdered(venueId);
            if (!games.isEmpty()) {
                Game firstGame = games.get(0);
                return ResponseUtil.wrapOrNotFound(Optional.of(firstGame));
            }
        } catch (Exception e) {
            log.debug("Error finding earliest game by venue", e);
            // Handle or rethrow the exception
        }
        return ResponseUtil.wrapOrNotFound(Optional.empty());
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<Game>> getGamesByUser(@PathVariable("userId") String userId) {
        log.debug("REST request to get Games by user : {}", userId);
        List<Game> games = gameRepository.findAllGamesByUserId(userId);
        return ResponseEntity.ok().body(games);
    }

    @GetMapping("/by-venue/{venueId}")
    public ResponseEntity<List<Game>> getAllGamesByVenue(@PathVariable Integer venueId) {
        List<Game> games = gameRepository.findByVenueIdOrderByGameDateAsc(venueId);
        return ResponseEntity.ok().body(games);
    }

    @GetMapping("/{id}/players-count")
    public ResponseEntity<Long> getPlayerCountByGame(@PathVariable Long id) {
        Long playerCount = gameRepository.countPlayersByGame(id);
        return ResponseEntity.ok(playerCount);
    }

    @GetMapping("/{id}/get-players")
    public ResponseEntity<List<Player>> getAllPlayers(@PathVariable Long id) {
        List<Player> players = gameRepository.findPlayersByGameId(id);
        return ResponseEntity.ok().body(players);
    }

    /**
     * {@code DELETE  /games/:id} : delete the "id" game.
     *
     * @param id the id of the game to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable("id") Long id) {
        log.debug("REST request to delete Game : {}", id);
        gameRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
