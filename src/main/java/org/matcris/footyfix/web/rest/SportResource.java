package org.matcris.footyfix.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.matcris.footyfix.domain.Sport;
import org.matcris.footyfix.repository.SportRepository;
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
 * REST controller for managing {@link org.matcris.footyfix.domain.Sport}.
 */
@RestController
@RequestMapping("/api/sports")
@Transactional
public class SportResource {

    private final Logger log = LoggerFactory.getLogger(SportResource.class);

    private static final String ENTITY_NAME = "sport";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SportRepository sportRepository;

    public SportResource(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }

    /**
     * {@code POST  /sports} : Create a new sport.
     *
     * @param sport the sport to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sport, or with status {@code 400 (Bad Request)} if the sport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Sport> createSport(@Valid @RequestBody Sport sport) throws URISyntaxException {
        log.debug("REST request to save Sport : {}", sport);
        if (sport.getId() != null) {
            throw new BadRequestAlertException("A new sport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sport result = sportRepository.save(sport);
        return ResponseEntity
            .created(new URI("/api/sports/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sports/:id} : Updates an existing sport.
     *
     * @param id the id of the sport to save.
     * @param sport the sport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sport,
     * or with status {@code 400 (Bad Request)} if the sport is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Sport> updateSport(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Sport sport)
        throws URISyntaxException {
        log.debug("REST request to update Sport : {}, {}", id, sport);
        if (sport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Sport result = sportRepository.save(sport);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sport.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sports/:id} : Partial updates given fields of an existing sport, field will ignore if it is null
     *
     * @param id the id of the sport to save.
     * @param sport the sport to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sport,
     * or with status {@code 400 (Bad Request)} if the sport is not valid,
     * or with status {@code 404 (Not Found)} if the sport is not found,
     * or with status {@code 500 (Internal Server Error)} if the sport couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Sport> partialUpdateSport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Sport sport
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sport partially : {}, {}", id, sport);
        if (sport.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sport.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Sport> result = sportRepository
            .findById(sport.getId())
            .map(existingSport -> {
                if (sport.getSportName() != null) {
                    existingSport.setSportName(sport.getSportName());
                }

                return existingSport;
            })
            .map(sportRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, sport.getId().toString())
        );
    }

    /**
     * {@code GET  /sports} : get all the sports.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sports in body.
     */
    @GetMapping("")
    public List<Sport> getAllSports() {
        log.debug("REST request to get all Sports");
        return sportRepository.findAll();
    }

    /**
     * {@code GET  /sports/:id} : get the "id" sport.
     *
     * @param id the id of the sport to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sport, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Sport> getSport(@PathVariable("id") Long id) {
        log.debug("REST request to get Sport : {}", id);
        Optional<Sport> sport = sportRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(sport);
    }

    /**
     * {@code DELETE  /sports/:id} : delete the "id" sport.
     *
     * @param id the id of the sport to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSport(@PathVariable("id") Long id) {
        log.debug("REST request to delete Sport : {}", id);
        sportRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
