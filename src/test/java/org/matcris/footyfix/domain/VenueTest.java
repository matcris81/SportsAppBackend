package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.PlayerTestSamples.*;
import static org.matcris.footyfix.domain.VenueTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class VenueTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Venue.class);
        Venue venue1 = getVenueSample1();
        Venue venue2 = new Venue();
        assertThat(venue1).isNotEqualTo(venue2);

        venue2.setId(venue1.getId());
        assertThat(venue1).isEqualTo(venue2);

        venue2 = getVenueSample2();
        assertThat(venue1).isNotEqualTo(venue2);
    }

    @Test
    void playerTest() throws Exception {
        Venue venue = getVenueRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        venue.addPlayer(playerBack);
        assertThat(venue.getPlayers()).containsOnly(playerBack);
        assertThat(playerBack.getVenues()).containsOnly(venue);

        venue.removePlayer(playerBack);
        assertThat(venue.getPlayers()).doesNotContain(playerBack);
        assertThat(playerBack.getVenues()).doesNotContain(venue);

        venue.players(new HashSet<>(Set.of(playerBack)));
        assertThat(venue.getPlayers()).containsOnly(playerBack);
        assertThat(playerBack.getVenues()).containsOnly(venue);

        venue.setPlayers(new HashSet<>());
        assertThat(venue.getPlayers()).doesNotContain(playerBack);
        assertThat(playerBack.getVenues()).doesNotContain(venue);
    }
}
