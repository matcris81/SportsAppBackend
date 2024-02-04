package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.GameTestSamples.*;
import static org.matcris.footyfix.domain.NotificationTestSamples.*;
import static org.matcris.footyfix.domain.PaymentTestSamples.*;
import static org.matcris.footyfix.domain.PlayerImageTestSamples.*;
import static org.matcris.footyfix.domain.PlayerTestSamples.*;
import static org.matcris.footyfix.domain.VenueTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class PlayerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Player.class);
        Player player1 = getPlayerSample1();
        Player player2 = new Player();
        assertThat(player1).isNotEqualTo(player2);

        player2.setId(player1.getId());
        assertThat(player1).isEqualTo(player2);

        player2 = getPlayerSample2();
        assertThat(player1).isNotEqualTo(player2);
    }

    @Test
    void playerImageTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        PlayerImage playerImageBack = getPlayerImageRandomSampleGenerator();

        player.setPlayerImage(playerImageBack);
        assertThat(player.getPlayerImage()).isEqualTo(playerImageBack);

        player.playerImage(null);
        assertThat(player.getPlayerImage()).isNull();
    }

    @Test
    void organizedGameTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        player.addOrganizedGame(gameBack);
        assertThat(player.getOrganizedGames()).containsOnly(gameBack);
        assertThat(gameBack.getOrganizer()).isEqualTo(player);

        player.removeOrganizedGame(gameBack);
        assertThat(player.getOrganizedGames()).doesNotContain(gameBack);
        assertThat(gameBack.getOrganizer()).isNull();

        player.organizedGames(new HashSet<>(Set.of(gameBack)));
        assertThat(player.getOrganizedGames()).containsOnly(gameBack);
        assertThat(gameBack.getOrganizer()).isEqualTo(player);

        player.setOrganizedGames(new HashSet<>());
        assertThat(player.getOrganizedGames()).doesNotContain(gameBack);
        assertThat(gameBack.getOrganizer()).isNull();
    }

    @Test
    void notificationTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        Notification notificationBack = getNotificationRandomSampleGenerator();

        player.addNotification(notificationBack);
        assertThat(player.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getPlayer()).isEqualTo(player);

        player.removeNotification(notificationBack);
        assertThat(player.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getPlayer()).isNull();

        player.notifications(new HashSet<>(Set.of(notificationBack)));
        assertThat(player.getNotifications()).containsOnly(notificationBack);
        assertThat(notificationBack.getPlayer()).isEqualTo(player);

        player.setNotifications(new HashSet<>());
        assertThat(player.getNotifications()).doesNotContain(notificationBack);
        assertThat(notificationBack.getPlayer()).isNull();
    }

    @Test
    void paymentTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        player.addPayment(paymentBack);
        assertThat(player.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getPlayer()).isEqualTo(player);

        player.removePayment(paymentBack);
        assertThat(player.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getPlayer()).isNull();

        player.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(player.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getPlayer()).isEqualTo(player);

        player.setPayments(new HashSet<>());
        assertThat(player.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getPlayer()).isNull();
    }

    @Test
    void gameTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        Game gameBack = getGameRandomSampleGenerator();

        player.addGame(gameBack);
        assertThat(player.getGames()).containsOnly(gameBack);

        player.removeGame(gameBack);
        assertThat(player.getGames()).doesNotContain(gameBack);

        player.games(new HashSet<>(Set.of(gameBack)));
        assertThat(player.getGames()).containsOnly(gameBack);

        player.setGames(new HashSet<>());
        assertThat(player.getGames()).doesNotContain(gameBack);
    }

    @Test
    void venueTest() throws Exception {
        Player player = getPlayerRandomSampleGenerator();
        Venue venueBack = getVenueRandomSampleGenerator();

        player.addVenue(venueBack);
        assertThat(player.getVenues()).containsOnly(venueBack);

        player.removeVenue(venueBack);
        assertThat(player.getVenues()).doesNotContain(venueBack);

        player.venues(new HashSet<>(Set.of(venueBack)));
        assertThat(player.getVenues()).containsOnly(venueBack);

        player.setVenues(new HashSet<>());
        assertThat(player.getVenues()).doesNotContain(venueBack);
    }
}
