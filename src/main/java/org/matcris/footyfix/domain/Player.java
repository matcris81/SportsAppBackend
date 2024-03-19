package org.matcris.footyfix.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.javafaker.Faker;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.matcris.footyfix.domain.enumeration.Gender;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

/**
 * A Player.
 */
@Entity
@Table(name = "player")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    //    @GeneratedValue
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "dob")
    private LocalDate dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "is_fake")
    private boolean isFake;

    @JsonIgnoreProperties(value = { "player" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private PlayerImage playerImage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organizer")
    //    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "organizer", "players" }, allowSetters = true)
    private Set<Game> organizedGames = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "player")
    //    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "player" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "player")
    //    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "player" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "rel_player__game", joinColumns = @JoinColumn(name = "player_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
    //    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "organizer", "players" }, allowSetters = true)
    private Set<Game> games = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_player__venue",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "venue_id")
    )
    //    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "players" }, allowSetters = true)
    private Set<Venue> venues = new HashSet<>();

    public String generateFakeUsername() {
        String username;
        Faker faker = new Faker();
        SecureRandom random = new SecureRandom();
        int randomInt = random.nextInt(2) + 1;

        //        if (randomInt == 1) {
        //            String uuid = UUID.randomUUID().toString();
        //            username = "user_" + uuid.substring(0, 8);
        //        }
        //        else if (randomInt == 2) {
        //            int length = 10;
        //            String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        //
        //            StringBuilder sb = new StringBuilder();
        //            for (int i = 0; i < length; i++) {
        //                sb.append(characters.charAt(random.nextInt(characters.length())));
        //            }
        //
        //            username = sb.toString();
        //        }
        if (randomInt == 2) {
            String base = faker.superhero().name().replaceAll("\\s+", ""); // Remove spaces
            String uniqueSegment = UUID.randomUUID().toString().substring(0, 4); // Get a UUID segment

            username = base + "_" + uniqueSegment;
        } else {
            username = faker.animal().name() + "_" + faker.color().name() + faker.number().randomDigitNotZero();
        }

        return username;
    }

    //    public String getRandomCityImage() {
    //        final String url = "https://api.api-ninjas.com/v1/randomimage?category=city";
    //
    //        RestTemplate restTemplate = new RestTemplate();
    //
    //        HttpHeaders headers = new HttpHeaders();
    //        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    //        headers.set("X-Api-Key", "bbT1V2Ic3kanPcd3cf41zA==23X3WsTG0yTutohn"); // Replace with your actual API key
    //
    //        HttpEntity<String> entity = new HttpEntity<>(headers);
    //
    //        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    //
    //        return response.getBody(); // This returns the raw JSON response
    //    }

    public Player generateFakePlayer() {
        Faker faker = new Faker();
        String username = generateFakeUsername();
        String name = faker.name().fullName();
        String email = faker.internet().emailAddress();
        String userId = UUID.randomUUID().toString();

        Player player = new Player();
        player.setUsername(username);
        player.setName(name);
        player.setEmail(email);
        player.setFake(true);
        player.id(userId);

        return player;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public BigDecimal getBalance() {
        return balance;
    }

    public boolean getIsFake() {
        return isFake;
    }

    public void setFake(boolean fake) {
        isFake = fake;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @JsonIgnore
    public Game getGame() {
        if (this.games != null && !this.games.isEmpty()) {
            return this.games.iterator().next();
        }
        return null;
    }

    @JsonIgnore
    public Venue getVenue() {
        if (this.venues != null && !this.venues.isEmpty()) {
            return this.venues.iterator().next();
        }
        return null;
    }

    @JsonIgnore
    public Payment getPayment() {
        if (this.payments != null && !this.payments.isEmpty()) {
            return this.payments.iterator().next();
        }
        return null;
    }

    public String getId() {
        return this.id;
    }

    public Player id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Player name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public Player username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public Player email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public Player password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDob() {
        return this.dob;
    }

    public Player dob(LocalDate dob) {
        this.setDob(dob);
        return this;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public Gender getGender() {
        return this.gender;
    }

    public Player gender(Gender gender) {
        this.setGender(gender);
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public Player phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PlayerImage getPlayerImage() {
        return this.playerImage;
    }

    public void setPlayerImage(PlayerImage playerImage) {
        this.playerImage = playerImage;
    }

    public Player playerImage(PlayerImage playerImage) {
        this.setPlayerImage(playerImage);
        return this;
    }

    public Set<Game> getOrganizedGames() {
        return this.organizedGames;
    }

    public void setOrganizedGames(Set<Game> games) {
        if (this.organizedGames != null) {
            this.organizedGames.forEach(i -> i.setOrganizer(null));
        }
        if (games != null) {
            games.forEach(i -> i.setOrganizer(this));
        }
        this.organizedGames = games;
    }

    public Player organizedGames(Set<Game> games) {
        this.setOrganizedGames(games);
        return this;
    }

    public Player addOrganizedGame(Game game) {
        this.organizedGames.add(game);
        game.setOrganizer(this);
        return this;
    }

    public Player removeOrganizedGame(Game game) {
        this.organizedGames.remove(game);
        game.setOrganizer(null);
        return this;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setPlayer(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setPlayer(this));
        }
        this.notifications = notifications;
    }

    public Player notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public Player addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setPlayer(this);
        return this;
    }

    public Player removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setPlayer(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setPlayer(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setPlayer(this));
        }
        this.payments = payments;
    }

    public Player payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Player addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setPlayer(this);
        return this;
    }

    public Player removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setPlayer(null);
        return this;
    }

    public Set<Game> getGames() {
        return this.games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
    }

    public Player games(Set<Game> games) {
        this.setGames(games);
        return this;
    }

    public Player addGame(Game game) {
        this.games.add(game);
        return this;
    }

    public Player removeGame(Game game) {
        this.games.remove(game);
        return this;
    }

    public Set<Venue> getVenues() {
        return this.venues;
    }

    public void setVenues(Set<Venue> venues) {
        this.venues = venues;
    }

    public Player venues(Set<Venue> venues) {
        this.setVenues(venues);
        return this;
    }

    public Player addVenue(Venue venue) {
        this.venues.add(venue);
        return this;
    }

    public Player removeVenue(Venue venue) {
        this.venues.remove(venue);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Player)) {
            return false;
        }
        return getId() != null && getId().equals(((Player) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Player{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", username='" + getUsername() + "'" +
            ", email='" + getEmail() + "'" +
            ", password='" + getPassword() + "'" +
            ", dob='" + getDob() + "'" +
            ", gender='" + getGender() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", balance='" + getBalance() + "'" +
            ", isFake='" + getIsFake() + "'" +
            "}";
    }
}
