package org.matcris.footyfix.domain;

import java.util.UUID;

public class PlayerTestSamples {

    public static Player getPlayerSample1() {
        return new Player().id("id1").name("name1").username("username1").email("email1").password("password1").phoneNumber("phoneNumber1");
    }

    public static Player getPlayerSample2() {
        return new Player().id("id2").name("name2").username("username2").email("email2").password("password2").phoneNumber("phoneNumber2");
    }

    public static Player getPlayerRandomSampleGenerator() {
        return new Player()
            .id(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
