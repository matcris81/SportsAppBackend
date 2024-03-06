package org.matcris.footyfix.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.matcris.footyfix.domain.enumeration.Gender;

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

    @Column(name = "username", unique = true)
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
    private BigDecimal balance;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public BigDecimal getBalance() {
        return balance;
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
            "}";
    }
}
