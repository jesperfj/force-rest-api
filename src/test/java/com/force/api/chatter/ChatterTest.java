package com.force.api.chatter;

import com.force.api.ApiConfig;
import com.force.api.Fixture;
import com.force.api.ForceApi;
import com.force.api.Identity;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by jjoergensen on 2/25/17.
 */
public class ChatterTest {
    static final String TEST_NAME = "force-rest-api basic chatter test";

    @Test
    public void rawRestTest() {

        ForceApi api = new ForceApi(new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setClientId(Fixture.get("clientId"))
                .setClientSecret(Fixture.get("clientSecret")));

        ChatterFeed feed = api.get("/chatter/feeds/user-profile/me/feed-elements").as(ChatterFeed.class);

        Identity me = api.getIdentity();
        System.out.println(me.getUserId());

        assertEquals(feed.elements.get(0).actor.id, me.getUserId());

        ChatterFeed.FeedItemInput newItem = new ChatterFeed.FeedItemInput();
        newItem.subjectId = me.getUserId();
        newItem.feedElementType = "FeedItem";
        newItem.body = new ChatterFeed.Body();
        newItem.body.messageSegments = new ArrayList<ChatterFeed.MessageSegment>();
        ChatterFeed.MessageSegment segment = new ChatterFeed.MessageSegment();
        segment.type = "text";
        segment.text = "Hi from Chatter API";
        newItem.body.messageSegments.add(segment);

        ChatterFeed.FeedItem resp = api.post("/chatter/feed-elements", newItem, 201).as(ChatterFeed.FeedItem.class);
        System.out.println(resp.actor.displayName+" just posted "+resp.body.text);
        assertEquals(segment.text, resp.body.text);
    }
}