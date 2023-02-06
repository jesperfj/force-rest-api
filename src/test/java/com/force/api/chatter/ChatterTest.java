package com.force.api.chatter;

import com.force.api.*;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by jjoergensen on 2/25/17.
 */
public class ChatterTest {
    static final String TEST_NAME = "force-rest-api basic chatter test";

    @Test
    public void chatterCRUDTest() {

        ForceApi api = new ForceApi(new ApiConfig()
                .setUsername(Fixture.get("username"))
                .setPassword(Fixture.get("password"))
                .setClientId(Fixture.get("clientId"))
                .setClientSecret(Fixture.get("clientSecret")));

        // Test simple get
        ChatterFeed feed = api.get("/chatter/feeds/user-profile/me/feed-elements").as(ChatterFeed.class);

        Identity me = api.getIdentity();
        System.out.println(me.getUserId());

        assertEquals(feed.elements.get(0).actor.id, me.getUserId());

        // Test posting a new feed item
        ChatterFeed.FeedItemInput newItem = new ChatterFeed.FeedItemInput();
        newItem.subjectId = me.getUserId();
        newItem.feedElementType = "FeedItem";
        newItem.body = new ChatterFeed.Body();
        newItem.body.messageSegments = new ArrayList<ChatterFeed.MessageSegment>();
        ChatterFeed.MessageSegment segment = new ChatterFeed.MessageSegment();
        segment.type = "text";
        segment.text = "Hi from Chatter API";
        newItem.body.messageSegments.add(segment);

        // Normally you collapse the next two lines into a single line but we need a reference to resrep
        // for the test just down below
        ResourceRepresentation resrep = api.post("/chatter/feed-elements", newItem);
        ChatterFeed.FeedItem resp = resrep.as(ChatterFeed.FeedItem.class);
        System.out.println(resp.actor.displayName+" just posted "+resp.body.text);
        assertEquals(segment.text, resp.body.text);

        // New as of 0.0.37, response code is available in ResourceRepresentation
        assertEquals(resrep.getResponseCode(),201);

        // Test updating the feed item just posted
        ChatterFeed.FeedItemInput updatedItem = new ChatterFeed.FeedItemInput();
        updatedItem.body = new ChatterFeed.Body();
        updatedItem.body.messageSegments = new ArrayList<ChatterFeed.MessageSegment>();
        ChatterFeed.MessageSegment updatedSegment = new ChatterFeed.MessageSegment();
        updatedSegment.type = "text";
        updatedSegment.text = "I changed my mind";
        updatedItem.body.messageSegments.add(updatedSegment);

        ChatterFeed.FeedItem updatedresp = api.patch("/chatter/feed-elements/" + resp.id, updatedItem).as(ChatterFeed.FeedItem.class);
        System.out.println(resp.actor.displayName+" just updated to "+updatedresp.body.text);
        assertEquals(updatedSegment.text, updatedresp.body.text);

        // Test deleting the feed item just posted
        try {
            api.delete("/chatter/feed-elements/" + resp.id);
        } catch(ResourceException e){
                fail("Delete failed unexpectedly: "+e);
        }
    }
}