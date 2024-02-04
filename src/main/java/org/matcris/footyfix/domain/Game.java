package org.matcris.footyfix.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Game.
 */
@Entity
@Table(name = "game")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "game_date")
    private ZonedDateTime gameDate;

    @Column(name = "price", precision = 21, scale = 2)
    private BigDecimal price;

    @Column(name = "size")
    private Integer size;

    @Column(name = "description")
    private String description;

    @Column(name = "venue_id")
    private Integer venueId;

    @Column(name = "sport_id")
    private Integer sportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "playerImage", "organizedGames", "notifications", "payments", "games", "venues" }, allowSetters = true)
    private Player organizer;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "games")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "playerImage", "organizedGames", "notifications", "payments", "games", "venues" }, allowSetters = true)
    private Set<Player> players = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Game id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getGameDate() {
        return this.gameDate;
    }

    public Game gameDate(ZonedDateTime gameDate) {
        this.setGameDate(gameDate);
        return this;
    }

    public void setGameDate(ZonedDateTime gameDate) {
        this.gameDate = gameDate;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Game price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getSize() {
        return this.size;
    }

    public Game size(Integer size) {
        this.setSize(size);
        return this;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDescription() {
        return this.description;
    }

    public Game description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVenueId() {
        return this.venueId;
    }

    public Game venueId(Integer venueId) {
        this.setVenueId(venueId);
        return this;
    }

    public void setVenueId(Integer venueId) {
        this.venueId = venueId;
    }

    public Integer getSportId() {
        return this.sportId;
    }

    public Game sportId(Integer sportId) {
        this.setSportId(sportId);
        return this;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public Player getOrganizer() {
        return this.organizer;
    }

    public void setOrganizer(Player player) {
        this.organizer = player;
    }

    public Game organizer(Player player) {
        this.setOrganizer(player);
        return this;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<Player> players) {
        if (this.players != null) {
            this.players.forEach(i -> i.removeGame(this));
        }
        if (players != null) {
            players.forEach(i -> i.addGame(this));
        }
        this.players = players;
    }

    public Game players(Set<Player> players) {
        this.setPlayers(players);
        return this;
    }

    public Game addPlayer(Player player) {
        this.players.add(player);
        player.getGames().add(this);
        return this;
    }

    public Game removePlayer(Player player) {
        this.players.remove(player);
        player.getGames().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Game)) {
            return false;
        }
        return getId() != null && getId().equals(((Game) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Game{" +
            "id=" + getId() +
            ", gameDate='" + getGameDate() + "'" +
            ", price=" + getPrice() +
            ", size=" + getSize() +
            ", description='" + getDescription() + "'" +
            ", venueId=" + getVenueId() +
            ", sportId=" + getSportId() +
            "}";
    }
}
