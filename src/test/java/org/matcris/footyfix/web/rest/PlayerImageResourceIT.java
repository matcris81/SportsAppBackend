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
import org.matcris.footyfix.domain.PlayerImage;
import org.matcris.footyfix.repository.PlayerImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PlayerImageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PlayerImageResourceIT {

    private static final String DEFAULT_IMAGE_DATA = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_DATA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/player-images";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PlayerImageRepository playerImageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPlayerImageMockMvc;

    private PlayerImage playerImage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerImage createEntity(EntityManager em) {
        PlayerImage playerImage = new PlayerImage().imageData(DEFAULT_IMAGE_DATA);
        return playerImage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PlayerImage createUpdatedEntity(EntityManager em) {
        PlayerImage playerImage = new PlayerImage().imageData(UPDATED_IMAGE_DATA);
        return playerImage;
    }

    @BeforeEach
    public void initTest() {
        playerImage = createEntity(em);
    }

    @Test
    @Transactional
    void createPlayerImage() throws Exception {
        int databaseSizeBeforeCreate = playerImageRepository.findAll().size();
        // Create the PlayerImage
        restPlayerImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerImage)))
            .andExpect(status().isCreated());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeCreate + 1);
        PlayerImage testPlayerImage = playerImageList.get(playerImageList.size() - 1);
        assertThat(testPlayerImage.getImageData()).isEqualTo(DEFAULT_IMAGE_DATA);
    }

    @Test
    @Transactional
    void createPlayerImageWithExistingId() throws Exception {
        // Create the PlayerImage with an existing ID
        playerImage.setId(1L);

        int databaseSizeBeforeCreate = playerImageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlayerImageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerImage)))
            .andExpect(status().isBadRequest());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPlayerImages() throws Exception {
        // Initialize the database
        playerImageRepository.saveAndFlush(playerImage);

        // Get all the playerImageList
        restPlayerImageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(playerImage.getId().intValue())))
            .andExpect(jsonPath("$.[*].imageData").value(hasItem(DEFAULT_IMAGE_DATA.toString())));
    }

    @Test
    @Transactional
    void getPlayerImage() throws Exception {
        // Initialize the database
        playerImageRepository.saveAndFlush(playerImage);

        // Get the playerImage
        restPlayerImageMockMvc
            .perform(get(ENTITY_API_URL_ID, playerImage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(playerImage.getId().intValue()))
            .andExpect(jsonPath("$.imageData").value(DEFAULT_IMAGE_DATA.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPlayerImage() throws Exception {
        // Get the playerImage
        restPlayerImageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPlayerImage() throws Exception {
        // Initialize the database
        playerImageRepository.saveAndFlush(playerImage);

        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();

        // Update the playerImage
        PlayerImage updatedPlayerImage = playerImageRepository.findById(playerImage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPlayerImage are not directly saved in db
        em.detach(updatedPlayerImage);
        updatedPlayerImage.imageData(UPDATED_IMAGE_DATA);

        restPlayerImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPlayerImage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPlayerImage))
            )
            .andExpect(status().isOk());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
        PlayerImage testPlayerImage = playerImageList.get(playerImageList.size() - 1);
        assertThat(testPlayerImage.getImageData()).isEqualTo(UPDATED_IMAGE_DATA);
    }

    @Test
    @Transactional
    void putNonExistingPlayerImage() throws Exception {
        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();
        playerImage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, playerImage.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerImage))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPlayerImage() throws Exception {
        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();
        playerImage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerImageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(playerImage))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPlayerImage() throws Exception {
        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();
        playerImage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerImageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(playerImage)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePlayerImageWithPatch() throws Exception {
        // Initialize the database
        playerImageRepository.saveAndFlush(playerImage);

        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();

        // Update the playerImage using partial update
        PlayerImage partialUpdatedPlayerImage = new PlayerImage();
        partialUpdatedPlayerImage.setId(playerImage.getId());

        partialUpdatedPlayerImage.imageData(UPDATED_IMAGE_DATA);

        restPlayerImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayerImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayerImage))
            )
            .andExpect(status().isOk());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
        PlayerImage testPlayerImage = playerImageList.get(playerImageList.size() - 1);
        assertThat(testPlayerImage.getImageData()).isEqualTo(UPDATED_IMAGE_DATA);
    }

    @Test
    @Transactional
    void fullUpdatePlayerImageWithPatch() throws Exception {
        // Initialize the database
        playerImageRepository.saveAndFlush(playerImage);

        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();

        // Update the playerImage using partial update
        PlayerImage partialUpdatedPlayerImage = new PlayerImage();
        partialUpdatedPlayerImage.setId(playerImage.getId());

        partialUpdatedPlayerImage.imageData(UPDATED_IMAGE_DATA);

        restPlayerImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPlayerImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPlayerImage))
            )
            .andExpect(status().isOk());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
        PlayerImage testPlayerImage = playerImageList.get(playerImageList.size() - 1);
        assertThat(testPlayerImage.getImageData()).isEqualTo(UPDATED_IMAGE_DATA);
    }

    @Test
    @Transactional
    void patchNonExistingPlayerImage() throws Exception {
        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();
        playerImage.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlayerImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, playerImage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerImage))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPlayerImage() throws Exception {
        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();
        playerImage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerImageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(playerImage))
            )
            .andExpect(status().isBadRequest());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPlayerImage() throws Exception {
        int databaseSizeBeforeUpdate = playerImageRepository.findAll().size();
        playerImage.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPlayerImageMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(playerImage))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the PlayerImage in the database
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePlayerImage() throws Exception {
        // Initialize the database
        playerImageRepository.saveAndFlush(playerImage);

        int databaseSizeBeforeDelete = playerImageRepository.findAll().size();

        // Delete the playerImage
        restPlayerImageMockMvc
            .perform(delete(ENTITY_API_URL_ID, playerImage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PlayerImage> playerImageList = playerImageRepository.findAll();
        assertThat(playerImageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
