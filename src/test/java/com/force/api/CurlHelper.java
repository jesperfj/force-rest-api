package com.force.api;

import org.junit.Test;

/**
 * Created by jjoergensen on 2/26/17.
 */

public class CurlHelper {

    @Test
    public void curlHelper() {
        ForceApi api = new ForceApi(new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setClientId(Fixture.get("clientId"))
                .setClientSecret(Fixture.get("clientSecret")));

        System.out.println(api.curlHelper());
    }
}
