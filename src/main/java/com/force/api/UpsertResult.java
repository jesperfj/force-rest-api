package com.force.api;


public class UpsertResult extends ApiResponse {

    private UpsertStatus status;

    public UpsertStatus getStatus() {
        return status;
    }

    public void setStatus(UpsertStatus status) {
        this.status = status;
    }
}
