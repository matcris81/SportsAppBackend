package org.matcris.footyfix.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.matcris.footyfix.web.rest.TestUtil.sameInstant;
import static org.matcris.footyfix.web.rest.TestUtil.sameNumber;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matcris.footyfix.IntegrationTest;
import org.matcris.footyfix.domain.Game;
import org.matcris.footyfix.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GameResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GameResourceIT {

    private static final ZonedDateTime DEFAULT_GAME_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_GAME_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(2);

    private static final Integer DEFAULT_SIZE = 1;
    private static final Integer UPDATED_SIZE = 2;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_VENUE_ID = 1;
    private static final Integer UPDATED_VENUE_ID = 2;

    private static final Integer DEFAULT_SPORT_ID = 1;
    private static final Integer UPDATED_SPORT_ID = 2;

    private static final String ENTITY_API_URL = "/api/games";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGameMockMvc;

    private Game game;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Game createEntity(EntityManager em) {
        Game game = new Game()
            .gameDate(DEFAULT_GAME_DATE)
            .price(DEFAULT_PRICE)
            .size(DEFAULT_SIZE)
            .description(DEFAULT_DESCRIPTION)
            .venueId(DEFAULT_VENUE_ID)
            .sportId(DEFAULT_SPORT_ID);
        return game;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Game createUpdatedEntity(EntityManager em) {
        Game game = new Game()
            .gameDate(UPDATED_GAME_DATE)
            .price(UPDATED_PRICE)
            .size(UPDATED_SIZE)
            .description(UPDATED_DESCRIPTION)
            .venueId(UPDATED_VENUE_ID)
            .sportId(UPDATED_SPORT_ID);
        return game;
    }

    @BeforeEach
    public void initTest() {
        game = createEntity(em);
    }

    @Test
    @Transactional
    void createGame() throws Exception {
        int databaseSizeBeforeCreate = gameRepository.findAll().size();
        // Create the Game
        restGameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(game)))
            .andExpect(status().isCreated());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeCreate + 1);
        Game testGame = gameList.get(gameList.size() - 1);
        assertThat(testGame.getGameDate()).isEqualTo(DEFAULT_GAME_DATE);
        assertThat(testGame.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testGame.getSize()).isEqualTo(DEFAULT_SIZE);
        assertThat(testGame.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testGame.getVenueId()).isEqualTo(DEFAULT_VENUE_ID);
        assertThat(testGame.getSportId()).isEqualTo(DEFAULT_SPORT_ID);
    }

    @Test
    @Transactional
    void createGameWithExistingId() throws Exception {
        // Create the Game with an existing ID
        game.setId(1L);

        int databaseSizeBeforeCreate = gameRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGameMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(game)))
            .andExpect(status().isBadRequest());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGames() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        // Get all the gameList
        restGameMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(game.getId().intValue())))
            .andExpect(jsonPath("$.[*].gameDate").value(hasItem(sameInstant(DEFAULT_GAME_DATE))))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].venueId").value(hasItem(DEFAULT_VENUE_ID)))
            .andExpect(jsonPath("$.[*].sportId").value(hasItem(DEFAULT_SPORT_ID)));
    }

    @Test
    @Transactional
    void getGame() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        // Get the game
        restGameMockMvc
            .perform(get(ENTITY_API_URL_ID, game.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(game.getId().intValue()))
            .andExpect(jsonPath("$.gameDate").value(sameInstant(DEFAULT_GAME_DATE)))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.size").value(DEFAULT_SIZE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.venueId").value(DEFAULT_VENUE_ID))
            .andExpect(jsonPath("$.sportId").value(DEFAULT_SPORT_ID));
    }

    @Test
    @Transactional
    void getNonExistingGame() throws Exception {
        // Get the game
        restGameMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGame() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        int databaseSizeBeforeUpdate = gameRepository.findAll().size();

        // Update the game
        Game updatedGame = gameRepository.findById(game.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGame are not directly saved in db
        em.detach(updatedGame);
        updatedGame
            .gameDate(UPDATED_GAME_DATE)
            .price(UPDATED_PRICE)
            .size(UPDATED_SIZE)
            .description(UPDATED_DESCRIPTION)
            .venueId(UPDATED_VENUE_ID)
            .sportId(UPDATED_SPORT_ID);

        restGameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGame.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGame))
            )
            .andExpect(status().isOk());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
        Game testGame = gameList.get(gameList.size() - 1);
        assertThat(testGame.getGameDate()).isEqualTo(UPDATED_GAME_DATE);
        assertThat(testGame.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testGame.getSize()).isEqualTo(UPDATED_SIZE);
        assertThat(testGame.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGame.getVenueId()).isEqualTo(UPDATED_VENUE_ID);
        assertThat(testGame.getSportId()).isEqualTo(UPDATED_SPORT_ID);
    }

    @Test
    @Transactional
    void putNonExistingGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();
        game.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, game.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(game))
            )
            .andExpect(status().isBadRequest());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();
        game.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(game))
            )
            .andExpect(status().isBadRequest());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();
        game.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(game)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGameWithPatch() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        int databaseSizeBeforeUpdate = gameRepository.findAll().size();

        // Update the game using partial update
        Game partialUpdatedGame = new Game();
        partialUpdatedGame.setId(game.getId());

        partialUpdatedGame.gameDate(UPDATED_GAME_DATE).description(UPDATED_DESCRIPTION).sportId(UPDATED_SPORT_ID);

        restGameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGame.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGame))
            )
            .andExpect(status().isOk());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
        Game testGame = gameList.get(gameList.size() - 1);
        assertThat(testGame.getGameDate()).isEqualTo(UPDATED_GAME_DATE);
        assertThat(testGame.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testGame.getSize()).isEqualTo(DEFAULT_SIZE);
        assertThat(testGame.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGame.getVenueId()).isEqualTo(DEFAULT_VENUE_ID);
        assertThat(testGame.getSportId()).isEqualTo(UPDATED_SPORT_ID);
    }

    @Test
    @Transactional
    void fullUpdateGameWithPatch() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        int databaseSizeBeforeUpdate = gameRepository.findAll().size();

        // Update the game using partial update
        Game partialUpdatedGame = new Game();
        partialUpdatedGame.setId(game.getId());

        partialUpdatedGame
            .gameDate(UPDATED_GAME_DATE)
            .price(UPDATED_PRICE)
            .size(UPDATED_SIZE)
            .description(UPDATED_DESCRIPTION)
            .venueId(UPDATED_VENUE_ID)
            .sportId(UPDATED_SPORT_ID);

        restGameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGame.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGame))
            )
            .andExpect(status().isOk());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
        Game testGame = gameList.get(gameList.size() - 1);
        assertThat(testGame.getGameDate()).isEqualTo(UPDATED_GAME_DATE);
        assertThat(testGame.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testGame.getSize()).isEqualTo(UPDATED_SIZE);
        assertThat(testGame.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGame.getVenueId()).isEqualTo(UPDATED_VENUE_ID);
        assertThat(testGame.getSportId()).isEqualTo(UPDATED_SPORT_ID);
    }

    @Test
    @Transactional
    void patchNonExistingGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();
        game.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, game.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(game))
            )
            .andExpect(status().isBadRequest());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();
        game.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(game))
            )
            .andExpect(status().isBadRequest());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGame() throws Exception {
        int databaseSizeBeforeUpdate = gameRepository.findAll().size();
        game.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGameMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(game)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Game in the database
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGame() throws Exception {
        // Initialize the database
        gameRepository.saveAndFlush(game);

        int databaseSizeBeforeDelete = gameRepository.findAll().size();

        // Delete the game
        restGameMockMvc
            .perform(delete(ENTITY_API_URL_ID, game.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Game> gameList = gameRepository.findAll();
        assertThat(gameList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
