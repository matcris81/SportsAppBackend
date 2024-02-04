package org.matcris.footyfix.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A PlayerImage.
 */
@Entity
@Table(name = "player_image")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PlayerImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "image_data")
    private String imageData;

    @JsonIgnoreProperties(value = { "playerImage", "organizedGames", "notifications", "payments", "games", "venues" }, allowSetters = true)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "playerImage")
    private Player player;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PlayerImage id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageData() {
        return this.imageData;
    }

    public PlayerImage imageData(String imageData) {
        this.setImageData(imageData);
        return this;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        if (this.player != null) {
            this.player.setPlayerImage(null);
        }
        if (player != null) {
            player.setPlayerImage(this);
        }
        this.player = player;
    }

    public PlayerImage player(Player player) {
        this.setPlayer(player);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerImage)) {
            return false;
        }
        return getId() != null && getId().equals(((PlayerImage) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PlayerImage{" +
            "id=" + getId() +
            ", imageData='" + getImageData() + "'" +
            "}";
    }
}
