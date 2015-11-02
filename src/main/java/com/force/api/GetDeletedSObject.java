package com.force.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;

/**
 * This class represents a JSON response from /services/data/v{version}/sobjects/{sobjectName}/deleted. It contains all
 * the deleted sobjects IDs for given timeframe, and some additional info.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetDeletedSObject {

    private List<DeletedRecord> deletedRecords;
    private Date earliestDateAvailable;
    private Date latestDateCovered;

    public List<DeletedRecord> getDeletedRecords() {
        return deletedRecords;
    }

    public void setDeletedRecords(List<DeletedRecord> deletedRecords) {
        this.deletedRecords = deletedRecords;
    }

    public Date getEarliestDateAvailable() {
        return earliestDateAvailable;
    }

    public void setEarliestDateAvailable(Date earliestDateAvailable) {
        this.earliestDateAvailable = earliestDateAvailable;
    }

    public Date getLatestDateCovered() {
        return latestDateCovered;
    }

    public void setLatestDateCovered(Date latestDateCovered) {
        this.latestDateCovered = latestDateCovered;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeletedRecord {
        private String id;
        private Date deletedDate;

        public DeletedRecord() {
            // for Jackson
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Date getDeletedDate() {
            return deletedDate;
        }

        public void setDeletedDate(Date deletedDate) {
            this.deletedDate = deletedDate;
        }
    }

}
