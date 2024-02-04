package org.matcris.footyfix.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.matcris.footyfix.domain.enumeration.PaymentStatus;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Column(name = "date_time")
    private Instant dateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "playerImage", "organizedGames", "notifications", "payments", "games", "venues" }, allowSetters = true)
    private Player player;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Payment id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Payment amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getDateTime() {
        return this.dateTime;
    }

    public Payment dateTime(Instant dateTime) {
        this.setDateTime(dateTime);
        return this;
    }

    public void setDateTime(Instant dateTime) {
        this.dateTime = dateTime;
    }

    public PaymentStatus getStatus() {
        return this.status;
    }

    public Payment status(PaymentStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Payment player(Player player) {
        this.setPlayer(player);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return getId() != null && getId().equals(((Payment) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", dateTime='" + getDateTime() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
