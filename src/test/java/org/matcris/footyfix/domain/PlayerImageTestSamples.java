package org.matcris.footyfix.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PlayerImageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PlayerImage getPlayerImageSample1() {
        return new PlayerImage().id(1L);
    }

    public static PlayerImage getPlayerImageSample2() {
        return new PlayerImage().id(2L);
    }

    public static PlayerImage getPlayerImageRandomSampleGenerator() {
        return new PlayerImage().id(longCount.incrementAndGet());
    }
}
