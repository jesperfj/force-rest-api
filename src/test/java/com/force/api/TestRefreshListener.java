package com.force.api;

/**
 * Created by jjoergensen on 2/19/17.
 */
public class TestRefreshListener implements SessionRefreshListener {
    public void sessionRefreshed(ApiSession session) {
        System.out.println("Session was refreshed! New access token: "+session.getAccessToken());
    }
}
