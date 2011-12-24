package com.force.api;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DescribeGlobal {
    private List<DescribeSObject> sobjects;
    
    public List<DescribeSObject> getSObjects() {
        return sobjects;
    }

}
