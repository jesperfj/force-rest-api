package com.force.api;

import java.util.Map;

public class DescribeSObject {
    String name;
    String label;
    boolean custom;
    String keyPrefix;
    String labelPlural;
    boolean layoutable;
    boolean activateable;
    boolean updateable;
    Map<String, String> urls;
    boolean createable;
    boolean deletable;
    boolean feedEnabled;
    boolean queryable;
    boolean replicateable;
    boolean retrieveable;
    boolean undeletable;
    boolean triggerable;
    boolean mergeable;
    boolean deprecatedAndHidden;
    boolean customSetting;
    boolean searchable;
	public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public boolean isCustom() {
		return custom;
	}
	public String getKeyPrefix() {
		return keyPrefix;
	}
	public String getLabelPlural() {
		return labelPlural;
	}
	public boolean isLayoutable() {
		return layoutable;
	}
	public boolean isActivateable() {
		return activateable;
	}
	public boolean isUpdateable() {
		return updateable;
	}
	public Map<String, String> getUrls() {
		return urls;
	}
	public boolean isCreateable() {
		return createable;
	}
	public boolean isDeletable() {
		return deletable;
	}
	public boolean isFeedEnabled() {
		return feedEnabled;
	}
	public boolean isQueryable() {
		return queryable;
	}
	public boolean isReplicateable() {
		return replicateable;
	}
	public boolean isRetrieveable() {
		return retrieveable;
	}
	public boolean isUndeletable() {
		return undeletable;
	}
	public boolean isTriggerable() {
		return triggerable;
	}
	public boolean isMergeable() {
		return mergeable;
	}
	public boolean isDeprecatedAndHidden() {
		return deprecatedAndHidden;
	}
	public boolean isCustomSetting() {
		return customSetting;
	}
	public boolean isSearchable() {
		return searchable;
	}

    
}
