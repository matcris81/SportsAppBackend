package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.NotificationTestSamples.*;
import static org.matcris.footyfix.domain.PlayerTestSamples.*;

import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void playerTest() throws Exception {
        Notification notification = getNotificationRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        notification.setPlayer(playerBack);
        assertThat(notification.getPlayer()).isEqualTo(playerBack);

        notification.player(null);
        assertThat(notification.getPlayer()).isNull();
    }
}
