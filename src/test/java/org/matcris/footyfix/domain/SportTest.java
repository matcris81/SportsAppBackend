package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.SportTestSamples.*;

import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class SportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sport.class);
        Sport sport1 = getSportSample1();
        Sport sport2 = new Sport();
        assertThat(sport1).isNotEqualTo(sport2);

        sport2.setId(sport1.getId());
        assertThat(sport1).isEqualTo(sport2);

        sport2 = getSportSample2();
        assertThat(sport1).isNotEqualTo(sport2);
    }
}
