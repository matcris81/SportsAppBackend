package org.matcris.footyfix.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class GameTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Game getGameSample1() {
        return new Game().id(1L).size(1).description("description1").venueId(1).sportId(1);
    }

    public static Game getGameSample2() {
        return new Game().id(2L).size(2).description("description2").venueId(2).sportId(2);
    }

    public static Game getGameRandomSampleGenerator() {
        return new Game()
            .id(longCount.incrementAndGet())
            .size(intCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .venueId(intCount.incrementAndGet())
            .sportId(intCount.incrementAndGet());
    }
}
