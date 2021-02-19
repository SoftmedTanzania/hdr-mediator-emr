package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DeathByDiseaseCasesJsonRequest extends EmrPayload {

    /**
     * Common name of submitting facility
     */
    @JsonProperty("Org Name")
    private String orgName;

    /**
     * Unique identifier of submitting facility, as presented from HFR
     */
    @JsonProperty("Local Org ID")
    private String localOrgID;

    /**
     * List of death by disease cases items
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
         * Unique identifier of ward
         */
        @JsonProperty("Ward ID")
        private String wardId;

        /**
         * Point of care facility or other ancillary facility (e.g. clinic, lab, MRI center)
         */
        @JsonProperty("Ward Name")
        private String wardName;

        /**
         * Unique identifier f patient
         */
        @JsonProperty("Pat ID")
        private String patID;

        /**
         * ICD 10 preferred but other codes will be accepted as long as they are mapped to ICD10.Codes will be comma separated
         */
        @JsonProperty("Disease Code")
        private String diseaseCode;

        /**
         * Gender of the patient
         */
        @JsonProperty("Gender")
        private String gender;

        /**
         * Date of Birth of the patient
         */
        @JsonProperty("DOB")
        private String dob;

        /**
         * The date that the patient died
         */
        @JsonProperty("Date Death Occured")
        private String dateDeathOccurred;

        public String getWardId() {
            return wardId;
        }

        public String getWardName() {
            return wardName;
        }

        public String getPatID() {
            return patID;
        }

        public String getDiseaseCode() {
            return diseaseCode;
        }

        public String getGender() {
            return gender;
        }

        public String getDob() {
            return dob;
        }

        public String getDateDeathOccurred() {
            return dateDeathOccurred;
        }
    }
}
