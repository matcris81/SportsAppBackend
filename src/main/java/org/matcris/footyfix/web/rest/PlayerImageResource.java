package org.matcris.footyfix.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.matcris.footyfix.domain.PlayerImage;
import org.matcris.footyfix.repository.PlayerImageRepository;
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
 * REST controller for managing {@link org.matcris.footyfix.domain.PlayerImage}.
 */
@RestController
@RequestMapping("/api/player-images")
@Transactional
public class PlayerImageResource {

    private final Logger log = LoggerFactory.getLogger(PlayerImageResource.class);

    private static final String ENTITY_NAME = "playerImage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PlayerImageRepository playerImageRepository;

    public PlayerImageResource(PlayerImageRepository playerImageRepository) {
        this.playerImageRepository = playerImageRepository;
    }

    /**
     * {@code POST  /player-images} : Create a new playerImage.
     *
     * @param playerImage the playerImage to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new playerImage, or with status {@code 400 (Bad Request)} if the playerImage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PlayerImage> createPlayerImage(@RequestBody PlayerImage playerImage) throws URISyntaxException {
        log.debug("REST request to save PlayerImage : {}", playerImage);
        if (playerImage.getId() != null) {
            throw new BadRequestAlertException("A new playerImage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PlayerImage result = playerImageRepository.save(playerImage);
        return ResponseEntity
            .created(new URI("/api/player-images/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /player-images/:id} : Updates an existing playerImage.
     *
     * @param id the id of the playerImage to save.
     * @param playerImage the playerImage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerImage,
     * or with status {@code 400 (Bad Request)} if the playerImage is not valid,
     * or with status {@code 500 (Internal Server Error)} if the playerImage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerImage> updatePlayerImage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerImage playerImage
    ) throws URISyntaxException {
        log.debug("REST request to update PlayerImage : {}, {}", id, playerImage);
        if (playerImage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerImage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PlayerImage result = playerImageRepository.save(playerImage);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, playerImage.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /player-images/:id} : Partial updates given fields of an existing playerImage, field will ignore if it is null
     *
     * @param id the id of the playerImage to save.
     * @param playerImage the playerImage to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated playerImage,
     * or with status {@code 400 (Bad Request)} if the playerImage is not valid,
     * or with status {@code 404 (Not Found)} if the playerImage is not found,
     * or with status {@code 500 (Internal Server Error)} if the playerImage couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PlayerImage> partialUpdatePlayerImage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PlayerImage playerImage
    ) throws URISyntaxException {
        log.debug("REST request to partial update PlayerImage partially : {}, {}", id, playerImage);
        if (playerImage.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, playerImage.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!playerImageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PlayerImage> result = playerImageRepository
            .findById(playerImage.getId())
            .map(existingPlayerImage -> {
                if (playerImage.getImageData() != null) {
                    existingPlayerImage.setImageData(playerImage.getImageData());
                }

                return existingPlayerImage;
            })
            .map(playerImageRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, playerImage.getId().toString())
        );
    }

    /**
     * {@code GET  /player-images} : get all the playerImages.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of playerImages in body.
     */
    @GetMapping("")
    public List<PlayerImage> getAllPlayerImages(@RequestParam(name = "filter", required = false) String filter) {
        if ("player-is-null".equals(filter)) {
            log.debug("REST request to get all PlayerImages where player is null");
            return StreamSupport
                .stream(playerImageRepository.findAll().spliterator(), false)
                .filter(playerImage -> playerImage.getPlayer() == null)
                .toList();
        }
        log.debug("REST request to get all PlayerImages");
        return playerImageRepository.findAll();
    }

    /**
     * {@code GET  /player-images/:id} : get the "id" playerImage.
     *
     * @param id the id of the playerImage to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the playerImage, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerImage> getPlayerImage(@PathVariable("id") Long id) {
        log.debug("REST request to get PlayerImage : {}", id);
        Optional<PlayerImage> playerImage = playerImageRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(playerImage);
    }

    /**
     * {@code DELETE  /player-images/:id} : delete the "id" playerImage.
     *
     * @param id the id of the playerImage to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerImage(@PathVariable("id") Long id) {
        log.debug("REST request to delete PlayerImage : {}", id);
        playerImageRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
