package com.force.api;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class NestedRecord {

    @JsonProperty(value = "attributes")
    protected Attribute attribute;

//    @JsonSerialize(keyUsing = KeyInnerNestedSerializer.class, contentUsing = InnerNestedSerializer.class)
    private Map<String, NestedRecordRequest> innerNestedRecordsByType;

    @JsonAnyGetter
    public Map<String, NestedRecordRequest> getInnerNestedRecordsByType() {
        return innerNestedRecordsByType;
    }

    @JsonAnySetter
    public void setInnerNestedRecordsByType(Map<String, NestedRecordRequest> innerNestedRecordsByType) {
        this.innerNestedRecordsByType = innerNestedRecordsByType;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public class Attribute{

        private String type;
        private String referenceId;

        public Attribute() { }

        public Attribute(String type, String referenceId){
            this.type = type;
            this.referenceId = referenceId;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
