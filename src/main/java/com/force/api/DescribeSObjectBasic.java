package com.force.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * @author Ryan Brainard
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class DescribeSObjectBasic {

    private String name;
    private String label;
    private Boolean custom;
    private String keyPrefix;
    private String labelPlural;
    private Boolean layoutable;
    private Boolean activateable;
    private Boolean updateable;
    private Map<String, String> urls;
    private Boolean createable;
    private Boolean deletable;
    private Boolean feedEnabled;
    private Boolean queryable;
    private Boolean replicateable;
    private Boolean retrieveable;
    private Boolean undeletable;
    private Boolean triggerable;
    private Boolean mergeable;
    private Boolean deprecatedAndHidden;
    private Boolean customSetting;
    private Boolean searchable;

    /**
     * @return Name of the sobject.
     */
    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public Boolean isCustom() {
        return custom;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public String getLabelPlural() {
        return labelPlural;
    }

    public Boolean isLayoutable() {
        return layoutable;
    }

    public Boolean isActivateable() {
        return activateable;
    }

    public Boolean isUpdateable() {
        return updateable;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public Boolean isCreateable() {
        return createable;
    }

    public Boolean isDeletable() {
        return deletable;
    }

    public Boolean isFeedEnabled() {
        return feedEnabled;
    }

    public Boolean isQueryable() {
        return queryable;
    }

    public Boolean isReplicateable() {
        return replicateable;
    }

    public Boolean isRetrieveable() {
        return retrieveable;
    }

    public Boolean isUndeletable() {
        return undeletable;
    }

    public Boolean isTriggerable() {
        return triggerable;
    }

    public Boolean isMergeable() {
        return mergeable;
    }

    public Boolean isDeprecatedAndHidden() {
        return deprecatedAndHidden;
    }

    public Boolean isCustomSetting() {
        return customSetting;
    }

    public Boolean isSearchable() {
        return searchable;
    }
}
