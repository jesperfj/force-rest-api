package com.force.api;

public enum ApiVersion {
	V39 ("v39.0"),
	V38 ("v38.0"),
	V37 ("v37.0"),
	V36 ("v36.0"),
	V35 ("v35.0"),
	V34 ("v34.0"),
	V33 ("v33.0"),
	V32 ("v32.0"),
	V31 ("v31.0"),
	V30 ("v30.0"),
	V29 ("v29.0"),
	V28 ("v28.0"),
	V27 ("v27.0"),
	V26 ("v26.0"),
	V25 ("v25.0"),
	V24 ("v24.0"),
	V23 ("v23.0"),
	V22 ("v22.0"), 
	DEFAULT_VERSION ("v39.0");

	final String v;
	
	ApiVersion(String v) {
		this.v=v;
	}

    /**
     * For a particular year, Salesforce releases 3 versions in the following order - Winter, Spring, then Summer
     * For example, for 2016, SF released Winter '16, Spring '16, and Summer '16. The first version was Summer '04.
     * Version number is calculated with the following formula:
     * Winter: 3(YEAR - 2004) - 1
     * Spring: 3(YEAR - 2004)
     * Summer: 3(YEAR - 2004) + 1
     *
     * @param year Fully qualified year, such as 2017
     * @return String of version in format of v34.0
     * @throws IllegalArgumentException if resulting version would be less than version 1
     */
    public static String resolveVersionString(ApiVersionSeason season, int year) {

	    if (year < 2004 || (year == 2004 && season != ApiVersionSeason.SUMMER)) {
	        throw new IllegalArgumentException("Salesforce Versions are only valid from Summer 2004 and on.");
        }

	    String version = "v" + (3 * (year - 2004) + season.offset) + ".0";

        return version;
    }
	
	public String toString() { return v; }

}

