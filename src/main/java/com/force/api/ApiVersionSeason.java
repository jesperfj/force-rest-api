package com.force.api;

/**
 * For a particular year, Salesforce releases 3 versions in the following order - Winter, Spring, then Summer
 * For example, for 2016, SF released Winter '16, Spring '16, and Summer '16
 * The offset value helps to calculate the version number based on this pattern, with Summer '04 being version 1.
 */
public enum ApiVersionSeason {
	WINTER(-1),
	SPRING(0),
	SUMMER(1);

	final int offset;

	ApiVersionSeason(int offset) {
		this.offset = offset;
	}
}
