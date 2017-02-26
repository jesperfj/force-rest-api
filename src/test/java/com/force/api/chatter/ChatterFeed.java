package com.force.api.chatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by jjoergensen on 2/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatterFeed {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MessageSegment {
        public String type;
        public String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        public List<MessageSegment> messageSegments;
        public String text;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Actor {

        public String displayName;
        public String id;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeedItem {

        public String id;
        public Actor actor;
        public Body body;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeedItemInput {

        public String feedElementType;
        public String subjectId;
        public Body body;
    }

    public List<FeedItem> elements;

}
