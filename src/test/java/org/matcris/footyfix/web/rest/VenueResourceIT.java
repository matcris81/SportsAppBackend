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
import org.matcris.footyfix.domain.Venue;
import org.matcris.footyfix.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VenueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VenueResourceIT {

    private static final String DEFAULT_VENUE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_VENUE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/venues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVenueMockMvc;

    private Venue venue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Venue createEntity(EntityManager em) {
        Venue venue = new Venue().venueName(DEFAULT_VENUE_NAME).address(DEFAULT_ADDRESS);
        return venue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Venue createUpdatedEntity(EntityManager em) {
        Venue venue = new Venue().venueName(UPDATED_VENUE_NAME).address(UPDATED_ADDRESS);
        return venue;
    }

    @BeforeEach
    public void initTest() {
        venue = createEntity(em);
    }

    @Test
    @Transactional
    void createVenue() throws Exception {
        int databaseSizeBeforeCreate = venueRepository.findAll().size();
        // Create the Venue
        restVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(venue)))
            .andExpect(status().isCreated());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeCreate + 1);
        Venue testVenue = venueList.get(venueList.size() - 1);
        assertThat(testVenue.getVenueName()).isEqualTo(DEFAULT_VENUE_NAME);
        assertThat(testVenue.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void createVenueWithExistingId() throws Exception {
        // Create the Venue with an existing ID
        venue.setId(1L);

        int databaseSizeBeforeCreate = venueRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVenueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(venue)))
            .andExpect(status().isBadRequest());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVenues() throws Exception {
        // Initialize the database
        venueRepository.saveAndFlush(venue);

        // Get all the venueList
        restVenueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(venue.getId().intValue())))
            .andExpect(jsonPath("$.[*].venueName").value(hasItem(DEFAULT_VENUE_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    @Test
    @Transactional
    void getVenue() throws Exception {
        // Initialize the database
        venueRepository.saveAndFlush(venue);

        // Get the venue
        restVenueMockMvc
            .perform(get(ENTITY_API_URL_ID, venue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(venue.getId().intValue()))
            .andExpect(jsonPath("$.venueName").value(DEFAULT_VENUE_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }

    @Test
    @Transactional
    void getNonExistingVenue() throws Exception {
        // Get the venue
        restVenueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVenue() throws Exception {
        // Initialize the database
        venueRepository.saveAndFlush(venue);

        int databaseSizeBeforeUpdate = venueRepository.findAll().size();

        // Update the venue
        Venue updatedVenue = venueRepository.findById(venue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVenue are not directly saved in db
        em.detach(updatedVenue);
        updatedVenue.venueName(UPDATED_VENUE_NAME).address(UPDATED_ADDRESS);

        restVenueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedVenue.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedVenue))
            )
            .andExpect(status().isOk());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
        Venue testVenue = venueList.get(venueList.size() - 1);
        assertThat(testVenue.getVenueName()).isEqualTo(UPDATED_VENUE_NAME);
        assertThat(testVenue.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void putNonExistingVenue() throws Exception {
        int databaseSizeBeforeUpdate = venueRepository.findAll().size();
        venue.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVenueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, venue.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(venue))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVenue() throws Exception {
        int databaseSizeBeforeUpdate = venueRepository.findAll().size();
        venue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(venue))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVenue() throws Exception {
        int databaseSizeBeforeUpdate = venueRepository.findAll().size();
        venue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(venue)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVenueWithPatch() throws Exception {
        // Initialize the database
        venueRepository.saveAndFlush(venue);

        int databaseSizeBeforeUpdate = venueRepository.findAll().size();

        // Update the venue using partial update
        Venue partialUpdatedVenue = new Venue();
        partialUpdatedVenue.setId(venue.getId());

        restVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVenue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVenue))
            )
            .andExpect(status().isOk());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
        Venue testVenue = venueList.get(venueList.size() - 1);
        assertThat(testVenue.getVenueName()).isEqualTo(DEFAULT_VENUE_NAME);
        assertThat(testVenue.getAddress()).isEqualTo(DEFAULT_ADDRESS);
    }

    @Test
    @Transactional
    void fullUpdateVenueWithPatch() throws Exception {
        // Initialize the database
        venueRepository.saveAndFlush(venue);

        int databaseSizeBeforeUpdate = venueRepository.findAll().size();

        // Update the venue using partial update
        Venue partialUpdatedVenue = new Venue();
        partialUpdatedVenue.setId(venue.getId());

        partialUpdatedVenue.venueName(UPDATED_VENUE_NAME).address(UPDATED_ADDRESS);

        restVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVenue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVenue))
            )
            .andExpect(status().isOk());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
        Venue testVenue = venueList.get(venueList.size() - 1);
        assertThat(testVenue.getVenueName()).isEqualTo(UPDATED_VENUE_NAME);
        assertThat(testVenue.getAddress()).isEqualTo(UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    void patchNonExistingVenue() throws Exception {
        int databaseSizeBeforeUpdate = venueRepository.findAll().size();
        venue.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, venue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(venue))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVenue() throws Exception {
        int databaseSizeBeforeUpdate = venueRepository.findAll().size();
        venue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(venue))
            )
            .andExpect(status().isBadRequest());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVenue() throws Exception {
        int databaseSizeBeforeUpdate = venueRepository.findAll().size();
        venue.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVenueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(venue)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Venue in the database
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVenue() throws Exception {
        // Initialize the database
        venueRepository.saveAndFlush(venue);

        int databaseSizeBeforeDelete = venueRepository.findAll().size();

        // Delete the venue
        restVenueMockMvc
            .perform(delete(ENTITY_API_URL_ID, venue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Venue> venueList = venueRepository.findAll();
        assertThat(venueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
