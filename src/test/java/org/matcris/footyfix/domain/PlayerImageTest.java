package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.PlayerImageTestSamples.*;
import static org.matcris.footyfix.domain.PlayerTestSamples.*;

import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class PlayerImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlayerImage.class);
        PlayerImage playerImage1 = getPlayerImageSample1();
        PlayerImage playerImage2 = new PlayerImage();
        assertThat(playerImage1).isNotEqualTo(playerImage2);

        playerImage2.setId(playerImage1.getId());
        assertThat(playerImage1).isEqualTo(playerImage2);

        playerImage2 = getPlayerImageSample2();
        assertThat(playerImage1).isNotEqualTo(playerImage2);
    }

    @Test
    void playerTest() throws Exception {
        PlayerImage playerImage = getPlayerImageRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        playerImage.setPlayer(playerBack);
        assertThat(playerImage.getPlayer()).isEqualTo(playerBack);
        assertThat(playerBack.getPlayerImage()).isEqualTo(playerImage);

        playerImage.player(null);
        assertThat(playerImage.getPlayer()).isNull();
        assertThat(playerBack.getPlayerImage()).isNull();
    }
}
