package com.force.api;

import java.util.List;

/**
 * @author Ryan Brainard
 */
public class DiscoverSObject<T> {

    private DescribeSObjectBasic describeSObjectBasic;
    private List<T> recentItems;

    DiscoverSObject(DescribeSObjectBasic describeSObjectBasic, List<T> recentItems) {
        this.describeSObjectBasic = describeSObjectBasic;
        this.recentItems = recentItems;
    }

    public DescribeSObjectBasic getObjectDescribe() {
        return describeSObjectBasic;
    }

    public List<T> getRecentItems() {
        return recentItems;
    }
}
