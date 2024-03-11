package org.matcris.footyfix.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Venue.
 */
@Entity
@Table(name = "venue")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Venue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "address")
    private String address;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "creator_id")
    private String creatorId;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "venues")
    @JsonIgnoreProperties(value = { "playerImage", "organizedGames", "notifications", "payments", "games", "venues" }, allowSetters = true)
    private Set<Player> players = new HashSet<>();

    // Getter and setter for imageId

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return this.id;
    }

    public Venue id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVenueName() {
        return this.venueName;
    }

    public Venue venueName(String venueName) {
        this.setVenueName(venueName);
        return this;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddress() {
        return this.address;
    }

    public Venue address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(Set<Player> players) {
        if (this.players != null) {
            this.players.forEach(i -> i.removeVenue(this));
        }
        if (players != null) {
            players.forEach(i -> i.addVenue(this));
        }
        this.players = players;
    }

    public Venue players(Set<Player> players) {
        this.setPlayers(players);
        return this;
    }

    public Venue addPlayer(Player player) {
        this.players.add(player);
        player.getVenues().add(this);
        return this;
    }

    public Venue removePlayer(Player player) {
        this.players.remove(player);
        player.getVenues().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Venue)) {
            return false;
        }
        return getId() != null && getId().equals(((Venue) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Venue{" +
            "id=" + getId() +
            ", venueName='" + getVenueName() + "'" +
            ", address='" + getAddress() + "'" +
            ", creatorId='" + getCreatorId() + "'" +
            "}";
    }
}
