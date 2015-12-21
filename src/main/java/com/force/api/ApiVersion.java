package com.force.api;

public enum ApiVersion {
	V24 ("v24.0"),
	V23 ("v23.0"),
	V22 ("v22.0"),
	V30 ("v30.0"),
	DEFAULT_VERSION ("v23.0");

	final String v;
	
	ApiVersion(String v) {
		this.v=v;
	}
	
	public String toString() { return v; }
	
}