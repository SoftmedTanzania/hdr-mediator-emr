package tz.go.moh.him.hdr.mediator.emr.domain;

import java.util.List;

/**
 * Bed occupancy object for JSON payloads from EMR systems
 */
public class BedOccupancyJsonRequest extends EmrPayload {

    /**
     * Common name of submitting facility
     */
    private String orgName;

    /**
     * Unique identifier of submitting facility, as presented from HFR
     */
    private String localOrgID;

    /**
     * List of bed occupancy items
     */
    private List<Item> items;

    public String getOrgName() {
        return orgName;
    }

    public String getLocalOrgID() {
        return localOrgID;
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        /**
         * The unique ID of the ward where the patient is admitted
         */
        private String wardId;

        /**
         * The name of the ward where the patient is admitted
         */
        private String wardName;

        /**
         * Unique identifier of patient associated with the transaction
         */
        private String patID;

        /**
         * The date that the patient was admitted to the ward
         */
        private String admissionDate;

        /**
         * The date that the patient was discharged from the ward
         */
        private String dischargeDate;

        public String getWardId() {
            return wardId;
        }

        public String getWardName() {
            return wardName;
        }

        public String getPatID() {
            return patID;
        }

        public String getAdmissionDate() {
            return admissionDate;
        }

        public String getDischargeDate() {
            return dischargeDate;
        }

    }
}
