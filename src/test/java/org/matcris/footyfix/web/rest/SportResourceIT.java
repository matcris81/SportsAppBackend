package org.matcris.footyfix.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matcris.footyfix.IntegrationTest;
import org.matcris.footyfix.domain.Sport;
import org.matcris.footyfix.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SportResourceIT {

    private static final String DEFAULT_SPORT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SPORT_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SportRepository sportRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSportMockMvc;

    private Sport sport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sport createEntity(EntityManager em) {
        Sport sport = new Sport().sportName(DEFAULT_SPORT_NAME);
        return sport;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sport createUpdatedEntity(EntityManager em) {
        Sport sport = new Sport().sportName(UPDATED_SPORT_NAME);
        return sport;
    }

    @BeforeEach
    public void initTest() {
        sport = createEntity(em);
    }

    @Test
    @Transactional
    void createSport() throws Exception {
        int databaseSizeBeforeCreate = sportRepository.findAll().size();
        // Create the Sport
        restSportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isCreated());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeCreate + 1);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getSportName()).isEqualTo(DEFAULT_SPORT_NAME);
    }

    @Test
    @Transactional
    void createSportWithExistingId() throws Exception {
        // Create the Sport with an existing ID
        sport.setId(1L);

        int databaseSizeBeforeCreate = sportRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSportNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sportRepository.findAll().size();
        // set the field null
        sport.setSportName(null);

        // Create the Sport, which fails.

        restSportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isBadRequest());

        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSports() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get all the sportList
        restSportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sport.getId().intValue())))
            .andExpect(jsonPath("$.[*].sportName").value(hasItem(DEFAULT_SPORT_NAME)));
    }

    @Test
    @Transactional
    void getSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        // Get the sport
        restSportMockMvc
            .perform(get(ENTITY_API_URL_ID, sport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sport.getId().intValue()))
            .andExpect(jsonPath("$.sportName").value(DEFAULT_SPORT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingSport() throws Exception {
        // Get the sport
        restSportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport
        Sport updatedSport = sportRepository.findById(sport.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSport are not directly saved in db
        em.detach(updatedSport);
        updatedSport.sportName(UPDATED_SPORT_NAME);

        restSportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSport))
            )
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getSportName()).isEqualTo(UPDATED_SPORT_NAME);
    }

    @Test
    @Transactional
    void putNonExistingSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sport.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSportWithPatch() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport using partial update
        Sport partialUpdatedSport = new Sport();
        partialUpdatedSport.setId(sport.getId());

        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSport.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSport))
            )
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getSportName()).isEqualTo(DEFAULT_SPORT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSportWithPatch() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeUpdate = sportRepository.findAll().size();

        // Update the sport using partial update
        Sport partialUpdatedSport = new Sport();
        partialUpdatedSport.setId(sport.getId());

        partialUpdatedSport.sportName(UPDATED_SPORT_NAME);

        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSport.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSport))
            )
            .andExpect(status().isOk());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
        Sport testSport = sportList.get(sportList.size() - 1);
        assertThat(testSport.getSportName()).isEqualTo(UPDATED_SPORT_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sport.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sport))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSport() throws Exception {
        int databaseSizeBeforeUpdate = sportRepository.findAll().size();
        sport.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sport)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sport in the database
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSport() throws Exception {
        // Initialize the database
        sportRepository.saveAndFlush(sport);

        int databaseSizeBeforeDelete = sportRepository.findAll().size();

        // Delete the sport
        restSportMockMvc
            .perform(delete(ENTITY_API_URL_ID, sport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sport> sportList = sportRepository.findAll();
        assertThat(sportList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
