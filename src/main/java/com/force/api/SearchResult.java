package com.force.api;

import java.util.List;

public class SearchResult<T> {

	List<T> searchRecords;

	public List<T> getSearchRecords() {
		return searchRecords;
	}
	public void setSearchRecords(List<T> records) {
		this.searchRecords = records;
	}

}
