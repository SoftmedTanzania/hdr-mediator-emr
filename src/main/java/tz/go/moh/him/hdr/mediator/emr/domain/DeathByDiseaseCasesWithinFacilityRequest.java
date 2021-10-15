package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DeathByDiseaseCasesWithinFacilityRequest extends EmrPayload {

    /**
     * Common name of submitting facility
     */
    @JsonProperty("orgName")
    private String orgName;

    /**
     * Unique identifier of submitting facility, as presented from HFR
     */
    @JsonProperty("facilityHfrCode")
    private String facilityHfrCode;

    /**
     * List of death by disease cases items
     */
    private List<Item> items;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFacilityHfrCode() {
        return facilityHfrCode;
    }

    public void setFacilityHfrCode(String facilityHfrCode) {
        this.facilityHfrCode = facilityHfrCode;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {

        /**
         * Unique identifier of ward
         */
        @JsonProperty("wardId")
        private String wardId;

        /**
         * Point of care facility or other ancillary facility (e.g. clinic, lab, MRI center)
         */
        @JsonProperty("wardName")
        private String wardName;

        /**
         * Unique identifier f patient
         */
        @JsonProperty("patId")
        private String patId;

        /**
         * First name of the patient
         */
        @JsonProperty("firstName")
        private String firstName;

        /**
         * Middle name of the patient
         */
        @JsonProperty("middleName")
        private String middleName;

        /**
         * Last name of the patient
         */
        @JsonProperty("lastName")
        private String lastName;

        /**
         * ICD 10 preferred but other codes will be accepted as long as they are mapped to ICD10.Codes will be comma separated
         */
        @JsonProperty("icd10Code")
        private String icd10Code;

        /**
         * Gender of the patient
         */
        @JsonProperty("gender")
        private String gender;

        /**
         * Date of Birth of the patient
         */
        @JsonProperty("dob")
        private String dob;

        /**
         * The date that the patient died
         */
        @JsonProperty("dateDeathOccurred")
        private String dateDeathOccurred;


        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getWardId() {
            return wardId;
        }

        public void setWardId(String wardId) {
            this.wardId = wardId;
        }

        public String getWardName() {
            return wardName;
        }

        public void setWardName(String wardName) {
            this.wardName = wardName;
        }

        public String getPatId() {
            return patId;
        }

        public void setPatId(String patId) {
            this.patId = patId;
        }

        public String getIcd10Code() {
            return icd10Code;
        }

        public void setIcd10Code(String icd10Code) {
            this.icd10Code = icd10Code;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getDateDeathOccurred() {
            return dateDeathOccurred;
        }

        public void setDateDeathOccurred(String dateDeathOccurred) {
            this.dateDeathOccurred = dateDeathOccurred;
        }
    }
}
