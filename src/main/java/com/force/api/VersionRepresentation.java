package com.force.api;

public class VersionRepresentation {

    private String label;

    private String url;

    private String version;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return "v" + version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
