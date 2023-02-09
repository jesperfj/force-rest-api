package com.force.api;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DescribeGlobal {

    private String encoding;
    private int maxBatchSize;
    private List<DescribeSObjectBasic> sobjects;

    public String getEncoding() {
        return encoding;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public List<DescribeSObjectBasic> getSObjects() {
        return sobjects;
    }
}
