package com.force.api;

import java.util.ArrayList;

/**
 * Created by jjoergensen on 2/22/17.
 */
public class SupportedVersions extends ArrayList<ExtendedApiVersion> {
    public ExtendedApiVersion latest() {
        return this.get(this.size()-1);
    }

    public ExtendedApiVersion oldest() {
        return this.get(0);
    }

    public boolean contains(String v) {
        return this.contains(new ExtendedApiVersion((v)));
    }
}
