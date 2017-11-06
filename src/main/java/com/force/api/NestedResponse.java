package com.force.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NestedResponse {

    public NestedResponse(){}

    @JsonCreator
    public NestedResponse(@JsonProperty("hasErrors") boolean hasErrors, @JsonProperty("results") List<Result> results){
        this.hasErrors = hasErrors;
        this.results = results;
    }

    @JsonProperty("hasErrors") private boolean hasErrors;
    @JsonProperty("results") private List<Result> results;

    public boolean getHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    static class Result{

        public Result(){ }

        @JsonCreator
        public Result(@JsonProperty("referenceId") String referenceId, @JsonProperty("id") String id){

            this.referenceId = referenceId;
            this.id = id;
        }

        @JsonProperty("referenceId") private String referenceId;
        @JsonProperty("id") private String id;

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
