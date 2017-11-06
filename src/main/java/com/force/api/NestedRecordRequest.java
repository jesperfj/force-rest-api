package com.force.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class NestedRecordRequest<T extends NestedRecord> {

    @JsonProperty(value = "records")
    private List<T> records;

    @JsonIgnore
    private String objectApiName;

    /**
     *
     * @param objectApiName    Object API Name ex: Account, CustomObject__c
     */
    public NestedRecordRequest(String objectApiName){
        this.records = new ArrayList<T>();
        this.objectApiName = objectApiName;
    }

    public NestedRecordRequest(String objectApiName, List<T> records){
        this.records = records;
        this.objectApiName = objectApiName;
    }

    public NestedRecordRequest(String objectApiName, T t){
        this.records = new ArrayList<T>();
        this.records.add(t);
        this.objectApiName = objectApiName;
    }

    public String getObjectApiName() {
        return objectApiName;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
