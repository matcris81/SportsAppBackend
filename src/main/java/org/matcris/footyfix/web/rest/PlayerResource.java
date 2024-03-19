package org.matcris.footyfix.web.rest;

//import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.matcris.footyfix.domain.Game;
import org.matcris.footyfix.domain.Player;
import org.matcris.footyfix.domain.PlayerImage;
import org.matcris.footyfix.domain.Venue;
import org.matcris.footyfix.repository.GameRepository;
import org.matcris.footyfix.repository.PlayerImageRepository;
import org.matcris.footyfix.repository.PlayerRepository;
import org.matcris.footyfix.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.matcris.footyfix.domain.Player}.
 */
@RestController
@RequestMapping("/api/players")
@Transactional
public class PlayerResource {

    private final Logger log = LoggerFactory.getLogger(PlayerResource.class);

    private static final String ENTITY_NAME = "player";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    //    private final FirebaseMessaging firebaseMessaging;

    public PlayerResource(
        PlayerRepository playerRepository,
        GameRepository gameRepository
        //        , FirebaseMessaging firebaseMessaging
    ) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        //        this.firebaseMessaging = firebaseMessaging;
    }

    @Transactional
    public boolean addPlayerToGame(Long gameId, Player newPlayer) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("Game not found"));

        int currentTotalPlayers = game.getPlayers().size();
        int gameSize = game.getSize();

        // Check if the game is full but can replace a fake player with a real one
        if (currentTotalPlayers >= gameSize - 1 && !newPlayer.getIsFake()) {
            boolean replaced = replaceOneFakePlayer(game, newPlayer);
            if (!replaced && currentTotalPlayers == gameSize - 1) {
                // If no fake player was replaced but there's still room, add the player normally
                game.getPlayers().add(newPlayer);
                gameRepository.save(game);
                return true;
            }
            return replaced;
        } else if (currentTotalPlayers == gameSize - 1) {
            // If there's room, add the player normally
            game.getPlayers().add(newPlayer);
            gameRepository.save(game);
            return true;
        }

        // If the game is at capacity and no replacement occurred, return false
        return false;
    }

    private boolean replaceOneFakePlayer(Game game, Player realPlayer) {
        for (Player player : game.getPlayers()) {
            if (player.getIsFake()) {
                // Remove the fake player and add the real player
                game.getPlayers().remove(player);
                playerRepository.delete(player); // Remove fake player from the database

                game.getPlayers().add(realPlayer); // Add the real player to the game
                gameRepository.save(game);
                return true; // Indicate that the replacement was successful
            }
        }
        return false; // No fake player was found to replace
    }

    /**
     * {@code POST  /players} : Create a new player.
     *
     * @param player the player to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new player, or with status {@code 400 (Bad Request)} if the player has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Player> createPlayer(@Valid @RequestBody Player player) throws URISyntaxException {
        log.debug("REST request to save Player : {}", player);
        Player result = playerRepository.save(player);
        return ResponseEntity
            .created(new URI("/api/players/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /players/:id} : Updates an existing player.
     *
     * @param id the id of the player to save.
     * @param player the player to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated player,
     * or with status {@code 400 (Bad Request)} if the player is not valid,
     * or with status {@code 500 (Internal Server Error)} if the player couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody Player player
    ) throws URISyntaxException {
        log.debug("REST request to update Player : {}, {}", id, player);
        if (player.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, player.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Player result = playerRepository.save(player);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, player.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /players/:id} : Partial updates given fields of an existing player, field will ignore if it is null
     *
     * @param id the id of the player to save.
     * @param player the player to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated player,
     * or with status {@code 400 (Bad Request)} if the player is not valid,
     * or with status {@code 404 (Not Found)} if the player is not found,
     * or with status {@code 500 (Internal Server Error)} if the player couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Player> partialUpdatePlayer(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Player player
    ) throws URISyntaxException {
        log.debug("REST request to partial update Player partially : {}, {}", id, player);
        if (player.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, player.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Player> result = playerRepository
            .findById(player.getId())
            .map(existingPlayer -> {
                if (player.getUsername() != null) {
                    existingPlayer.setUsername(player.getUsername());
                }
                if (player.getEmail() != null) {
                    existingPlayer.setEmail(player.getEmail());
                }
                if (player.getPassword() != null) {
                    existingPlayer.setPassword(player.getPassword());
                }
                if (player.getVenues() != null && !player.getVenues().isEmpty()) {
                    existingPlayer.addVenue(player.getVenue());
                }
                if (player.getGames() != null && !player.getGames().isEmpty()) {
                    existingPlayer.addGame(player.getGame());
                    addPlayerToGame(player.getGame().getId(), existingPlayer);
                }
                if (player.getPayments() != null && !player.getPayments().isEmpty()) {
                    existingPlayer.addPayment(player.getPayment());
                }
                if (player.getPlayerImage() != null) {
                    existingPlayer.setPlayerImage(player.getPlayerImage());
                }
                if (player.getDob() != null) {
                    existingPlayer.setDob(player.getDob());
                }
                if (player.getGender() != null) {
                    existingPlayer.setGender(player.getGender());
                }
                if (player.getPhoneNumber() != null) {
                    existingPlayer.setPhoneNumber(player.getPhoneNumber());
                }

                return existingPlayer;
            })
            .map(playerRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, player.getId()));
    }

    @PatchMapping(value = "/remove/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Player> removeFromPlayer(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody Player player
    ) throws URISyntaxException {
        log.debug("REST request to partial update Player partially : {}, {}", id, player);
        if (player.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, player.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Player> result = playerRepository
            .findById(player.getId())
            .map(existingPlayer -> {
                if (player.getVenues() != null && !player.getVenues().isEmpty()) {
                    existingPlayer.removeVenue(player.getVenue());
                    // remove subscription for venue
                }
                if (player.getGames() != null && !player.getGames().isEmpty()) {
                    existingPlayer.removeGame(player.getGame());
                    // remove subscription for game
                }

                return existingPlayer;
            })
            .map(playerRepository::save);

        return ResponseUtil.wrapOrNotFound(result, HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, player.getId()));
    }

    @PatchMapping("/{id}/subtract-balance")
    public ResponseEntity<String> subtractBalance(@PathVariable("id") String id, @RequestBody BigDecimal amount) {
        int updatedRows = playerRepository.subtractPlayerBalance(id, amount);
        if (updatedRows > 0) {
            return ResponseEntity.ok().body("Player balance updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to update player balance.");
        }
    }

    @PatchMapping("/{id}/add-balance")
    public ResponseEntity<String> addBalance(@PathVariable("id") String id, @RequestBody BigDecimal amount) {
        int updatedRows = playerRepository.addPlayerBalance(id, amount);
        if (updatedRows > 0) {
            return ResponseEntity.ok().body("Player balance updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to update player balance.");
        }
    }

    /**
     * {@code GET  /players} : get all the players.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of players in body.
     */
    @GetMapping("")
    public List<Player> getAllPlayers(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        log.debug("REST request to get all Players");
        if (eagerload) {
            return playerRepository.findAllWithEagerRelationships();
        } else {
            return playerRepository.findAll();
        }
    }

    /**
     * {@code GET  /players/:id} : get the "id" player.
     *
     * @param id the id of the player to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the player, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable("id") String id) {
        log.debug("REST request to get Player : {}", id);
        Optional<Player> player = playerRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(player);
    }

    @GetMapping("/{playerId}/likes-venue/{venueId}")
    public ResponseEntity<Boolean> checkPlayerLikesVenue(@PathVariable String playerId, @PathVariable Long venueId) {
        boolean result = playerRepository.existsByPlayerIdAndVenueId(playerId, venueId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/venues")
    public ResponseEntity<List<Venue>> getAllVenuesLikedByPlayer(@PathVariable("id") String id) {
        log.debug("REST request to get all Venues liked by Player : {}", id);
        List<Venue> venues = playerRepository.findVenuesByPlayerId(id);
        return ResponseEntity.ok().body(venues);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getUserBalance(@PathVariable("id") String id) {
        log.debug("REST request to get all Venues liked by Player : {}", id);
        BigDecimal balance = playerRepository.findBalanceByPlayerId(id);
        return ResponseEntity.ok(balance);
    }

    /**
     * {@code DELETE  /players/:id} : delete the "id" player.
     *
     * @param id the id of the player to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") String id) {
        log.debug("REST request to delete Player : {}", id);
        playerRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }
}
