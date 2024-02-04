package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.GameTestSamples.*;
import static org.matcris.footyfix.domain.PlayerTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class GameTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Game.class);
        Game game1 = getGameSample1();
        Game game2 = new Game();
        assertThat(game1).isNotEqualTo(game2);

        game2.setId(game1.getId());
        assertThat(game1).isEqualTo(game2);

        game2 = getGameSample2();
        assertThat(game1).isNotEqualTo(game2);
    }

    @Test
    void organizerTest() throws Exception {
        Game game = getGameRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        game.setOrganizer(playerBack);
        assertThat(game.getOrganizer()).isEqualTo(playerBack);

        game.organizer(null);
        assertThat(game.getOrganizer()).isNull();
    }

    @Test
    void playerTest() throws Exception {
        Game game = getGameRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        game.addPlayer(playerBack);
        assertThat(game.getPlayers()).containsOnly(playerBack);
        assertThat(playerBack.getGames()).containsOnly(game);

        game.removePlayer(playerBack);
        assertThat(game.getPlayers()).doesNotContain(playerBack);
        assertThat(playerBack.getGames()).doesNotContain(game);

        game.players(new HashSet<>(Set.of(playerBack)));
        assertThat(game.getPlayers()).containsOnly(playerBack);
        assertThat(playerBack.getGames()).containsOnly(game);

        game.setPlayers(new HashSet<>());
        assertThat(game.getPlayers()).doesNotContain(playerBack);
        assertThat(playerBack.getGames()).doesNotContain(game);
    }
}
