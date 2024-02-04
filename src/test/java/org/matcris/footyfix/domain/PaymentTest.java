package org.matcris.footyfix.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.matcris.footyfix.domain.PaymentTestSamples.*;
import static org.matcris.footyfix.domain.PlayerTestSamples.*;

import org.junit.jupiter.api.Test;
import org.matcris.footyfix.web.rest.TestUtil;

class PaymentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Payment.class);
        Payment payment1 = getPaymentSample1();
        Payment payment2 = new Payment();
        assertThat(payment1).isNotEqualTo(payment2);

        payment2.setId(payment1.getId());
        assertThat(payment1).isEqualTo(payment2);

        payment2 = getPaymentSample2();
        assertThat(payment1).isNotEqualTo(payment2);
    }

    @Test
    void playerTest() throws Exception {
        Payment payment = getPaymentRandomSampleGenerator();
        Player playerBack = getPlayerRandomSampleGenerator();

        payment.setPlayer(playerBack);
        assertThat(payment.getPlayer()).isEqualTo(playerBack);

        payment.player(null);
        assertThat(payment.getPlayer()).isNull();
    }
}
