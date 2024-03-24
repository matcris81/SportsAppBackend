package org.matcris.footyfix.web.rest;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.matcris.footyfix.domain.Game;
import org.matcris.footyfix.domain.Player;
import org.matcris.footyfix.domain.PlayerImage;
import org.matcris.footyfix.repository.GameRepository;
import org.matcris.footyfix.repository.PlayerImageRepository;
import org.matcris.footyfix.repository.PlayerRepository;
import org.matcris.footyfix.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.matcris.footyfix.domain.Game}.
 */
@RestController
@RequestMapping("/api/games")
@Transactional
public class GameResource {

    LocalDate now = LocalDate.now();
    ZonedDateTime nowZoned = now.atStartOfDay(ZoneId.systemDefault());
    ZonedDateTime oneWeekFromNow = nowZoned.plusWeeks(1);
    ZonedDateTime twentyFourHoursFromNow = nowZoned.plusDays(1);

    private final Logger log = LoggerFactory.getLogger(GameResource.class);

    private static final String ENTITY_NAME = "game";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GameRepository gameRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final PlayerImageRepository playerImageRepository;
    private final PlayerRepository playerRepository;

    public GameResource(
        GameRepository gameRepository,
        FirebaseMessaging firebaseMessaging,
        PlayerRepository playerRepository,
        PlayerImageRepository playerImageRepository
    ) {
        this.gameRepository = gameRepository;
        this.firebaseMessaging = firebaseMessaging;
        this.playerRepository = playerRepository;
        this.playerImageRepository = playerImageRepository;
    }

    public String fetchImageAndEncode() {
        byte[] imageBytes = fetchImage();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public PlayerImage saveImageToDatabaseAndReturnId() {
        String encodedImage = fetchImageAndEncode();

        PlayerImage playerImage = new PlayerImage();
        playerImage.setImageData(encodedImage);
        playerImage = playerImageRepository.save(playerImage);

        return playerImage;
    }

    public byte[] fetchImage() {
        String[] categories = { "nature", "city", "technology", "food", "still_life", "abstract", "wildlife" };
        int width = 100;
        int height = 100;
        Random random = new Random();
        int randIndex = random.nextInt(categories.length);

        String category = categories[randIndex];
        final String url = "https://api.api-ninjas.com/v1/randomimage?category={category}&width={width}&height={height}";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.IMAGE_JPEG));
        headers.set("X-Api-Key", "bbT1V2Ic3kanPcd3cf41zA==23X3WsTG0yTutohn");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class, category, width, height);

        return response.getBody();
    }

    //    @Scheduled(cron = "0 0 * * * *") // Run at the start of every hour
    //@Scheduled(cron = "0 0/5 * * * ?") // Runs every 5 minutes
    @Scheduled(cron = "0 0 6,18 * * ?")
    public void manageFakePlayers() {
        // Example logic
        List<Game> games = gameRepository.findGamesWithinRange(twentyFourHoursFromNow, oneWeekFromNow);
        for (Game game : games) {
            if (gameNeedsMorePlayers(game)) {
                addFakePlayers(game);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?") // Runs at the start of every hour
    //@Scheduled(cron = "0 0/5 * * * ?")
    //@Scheduled(cron = "0 0 6,18 * * ?")
    public void removeFakePlayerFromGames() {
        ZonedDateTime rightNow = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nowPlus24Hours = rightNow.plusHours(24);
        //        ZonedDateTime nowPlus24Hours = rightNow.plusHours(100); // remove this when done testing
        List<Game> games = gameRepository.findGamesStartingInNext24HoursWithFakePlayers(rightNow, nowPlus24Hours);
        for (Game game : games) {
            // Find the first fake player in the game
            Player fakePlayer = game.getPlayers().stream().filter(Player::getIsFake).findFirst().orElse(null);
            if (fakePlayer != null) {
                game.getPlayers().remove(fakePlayer); // Remove from game's collection of players
                playerRepository.delete(fakePlayer); // Delete from database
                // Optionally, save the game if needed depending on your JPA setup
                gameRepository.save(game);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deletePastGames() {
        log.debug("Scheduled task to delete past games started");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        List<Game> pastGames = gameRepository.findAllByGameDateBefore(now);

        if (!pastGames.isEmpty()) {
            for (Game game : pastGames) {
                // Before deleting the game, dissociate or delete all related players
                dissociateOrDeletePlayers(game);

                // Now safe to delete the game
                gameRepository.delete(game);
                log.debug("Deleted past game with ID: {}", game.getId());
            }
        } else {
            log.debug("No past games to delete");
        }
    }

    private void dissociateOrDeletePlayers(Game game) {
        List<Player> players = playerRepository.findPlayersByGameId(game.getId());
        for (Player player : players) {
            player.getGames().remove(game);
            playerRepository.save(player);
        }
        game.getPlayers().clear();
        gameRepository.save(game);
    }

    private boolean gameNeedsMorePlayers(Game game) {
        int players = game.getPlayers().size();
        int gameSize = game.getSize();
        int targetSize = (int) Math.ceil(gameSize * 0.7);

        return players < targetSize;
    }

    private void addFakePlayers(Game game) {
        int currentPlayersCount = game.getPlayers().size();
        int gameSize = game.getSize();
        int targetSize = (int) Math.ceil(gameSize * 0.7);
        Player player = new Player();

        int fakePlayersNeeded = targetSize - currentPlayersCount;

        for (int i = 0; i < fakePlayersNeeded; i++) {
            Player fakePlayer = player.generateFakePlayer();
            PlayerImage playerImage = saveImageToDatabaseAndReturnId();
            fakePlayer.setPlayerImage(playerImage);
            game.addPlayer(fakePlayer);
            playerRepository.save(fakePlayer);
        }
        gameRepository.save(game);
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

    @GetMapping("/by-date-time-asc")
    public ResponseEntity<List<Game>> getNowGames(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.debug("REST request to get Games by date : {}", date);
        List<Game> games = gameRepository.findTodayUpcomingGames();
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

    // In your @RestController class
    @GetMapping("/{gameId}/check-player/{userId}")
    public ResponseEntity<Boolean> checkPlayerInGame(@PathVariable Long gameId, @PathVariable String userId) {
        boolean playerExists = gameRepository.existsByGameIdAndUserId(gameId, userId);
        return ResponseEntity.ok().body(playerExists);
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
