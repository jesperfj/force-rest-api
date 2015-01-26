package com.force.api;

public enum ApiVersion {
	V27 ("v27.0"),
	V26 ("v26.0"),
	V25 ("v25.0"),
	V24 ("v24.0"),
	V23 ("v23.0"),
	V22 ("v22.0"), 
	DEFAULT_VERSION ("v23.0");

	final String v;
	
	ApiVersion(String v) {
		this.v=v;
	}
	
	public String toString() { return v; }

	public static ApiVersion getVersion(String api) {
		if (api == null) {
			return DEFAULT_VERSION;
		}
		api = api.toLowerCase();
		if (!api.startsWith("v")) {
			api = "v" + api;
		}
		for (ApiVersion v : ApiVersion.values()) {
			if (v.v.equals(api)) {
				return v;
			}
		}
		return DEFAULT_VERSION;
	}
}