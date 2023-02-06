package com.force.api;


/**
 * represents a single supported API version as reported by the <code>/services/data</code> resource
 * for the instance endpoint returned on authentication.
 *
 * com.frejo.api.ApiVersion was originally used to set the current API version and manage the complete list
 * of versions as an enum. It will likely be phased out in favor of using plain version strings along with this class
 * for more extended version information.
 *
 * This is a tweak to an original proposal by @cswendrowski.
 *
 */
public class ExtendedApiVersion implements Comparable<ExtendedApiVersion> {

    public enum Season {
        WINTER(),
        SPRING(),
        SUMMER();
    }

    public ExtendedApiVersion() {}

    public ExtendedApiVersion(float version) {
        this.version = version;
    }

    public ExtendedApiVersion(Season s, int y) {
        // Thank you @cswendrowski for this neat algorithm
        this.version = (float) (3*(y - 2004) + s.ordinal() - 1);
        if(version < 20) {
            throw new IllegalArgumentException("Only Winter 2011 (v20.0) and later versions are supported");
        }
    }

    /**
     *
     * @param version formatted as a string: "vX.Y", e.g. "v39.0"
     */
    public ExtendedApiVersion(String version) {
        this.version = Float.parseFloat(version.substring(1));
    }

    private String label;

    private String url;

    float version;

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

    public float getVersion() {
        return version;
    }

    public String getVersionString() { return "v"+version; }

    /**
     * sets version number as a float. This would more correct have been called <code>setNumber</code> but
     * the field name in REST API is <code>version</code>.
     * @param version version number as float, e.g. 25.0
     */
    public void setVersion(float version) {
        this.version = version;
    }

    public boolean equals(Object other) {
        return other instanceof ExtendedApiVersion && ((ExtendedApiVersion) other).version == version;
    }

    @Override
    public String toString() {
        return getVersionString();
    }

    @Override
    public int compareTo(ExtendedApiVersion o) {
        return Float.compare(version, o.version);
    }
}
