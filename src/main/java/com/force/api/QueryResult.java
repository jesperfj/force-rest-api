package com.force.api;

import java.util.List;

public class QueryResult<T> {

	int totalSize;
	boolean done = false;
	List<T> records;
	String nextRecordsUrl;
	
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public List<T> getRecords() {
		return records;
	}
	public void setRecords(List<T> records) {
		this.records = records;
	}
	public String getNextRecordsUrl() {
		return nextRecordsUrl;
	}
	public void setNextRecordsUrl(String nextRecordsUrl) {
		this.nextRecordsUrl = nextRecordsUrl;
	}
	
}
