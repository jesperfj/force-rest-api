package com.force.api;

import java.util.List;

public class DescribeGlobal {
    String encoding;
    int maxBatchSize;
    private List<DescribeSObject> sobjects;
    
    public String getEncoding() {
        return encoding;
    }
    public int getMaxBatchSize() {
        return maxBatchSize;
    }
    public List<DescribeSObject> getSObjects() {
        return sobjects;
    }

}
