package com.force.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DescribeSObject {
    String name;
    String label;
    Field[] fields;

    public String getName() {
		return name;
	}
	public String getLabel() {
		return label;
	}
	public Field[] getFields() {
		return fields;
	}

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class Field {
    	String name;
    	String type;
    	
    	public Field() {}
    	
		public String getName() {
			return name;
		}
		public String getType() {
			return type;
		}

	}
}
