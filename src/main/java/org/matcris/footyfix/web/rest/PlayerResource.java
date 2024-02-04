package org.matcris.footyfix.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.matcris.footyfix.domain.Player;
import org.matcris.footyfix.repository.PlayerRepository;
import org.matcris.footyfix.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
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

    public PlayerResource(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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
        if (player.getId() != null) {
            throw new BadRequestAlertException("A new player cannot already have an ID", ENTITY_NAME, "idexists");
        }
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
                if (player.getName() != null) {
                    existingPlayer.setName(player.getName());
                }
                if (player.getUsername() != null) {
                    existingPlayer.setUsername(player.getUsername());
                }
                if (player.getEmail() != null) {
                    existingPlayer.setEmail(player.getEmail());
                }
                if (player.getPassword() != null) {
                    existingPlayer.setPassword(player.getPassword());
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
