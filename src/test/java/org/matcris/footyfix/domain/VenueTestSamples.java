package org.matcris.footyfix.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VenueTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Venue getVenueSample1() {
        return new Venue().id(1L).venueName("venueName1").address("address1");
    }

    public static Venue getVenueSample2() {
        return new Venue().id(2L).venueName("venueName2").address("address2");
    }

    public static Venue getVenueRandomSampleGenerator() {
        return new Venue().id(longCount.incrementAndGet()).venueName(UUID.randomUUID().toString()).address(UUID.randomUUID().toString());
    }
}
