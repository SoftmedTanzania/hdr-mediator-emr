package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServiceReceivedRequest extends EmrPayload {
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
     * List of service received items
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
         * Point of care facility or other ancillary facility (e.g. clinic, lab, MRI center) within the hospital
         */
        @JsonProperty("deptName")
        private String deptName;

        /**
         * Unique identifier of the department/section
         */
        @JsonProperty("deptId")
        private String deptId;

        /**
         * Unique identifier of patient associated with the transaction
         */
        @JsonProperty("patId")
        private String patId;

        /**
         * Gender of the patient
         */
        @JsonProperty("gender")
        private String gender;

        /**
         * Patient date of birth
         */
        @JsonProperty("dob")
        private String dob;

        /**
         * The unique identifier of the medical services provided. They should be comma separated (e.g. 002923, 00277, 002772)
         */
        @JsonProperty("medSvcCode")
        private List<String> medSvcCode;

        /**
         * The unique identifier of the confirmed diagnosed diseases.
         */
        @JsonProperty("confirmedDiagnosis")
        private List<String> confirmedDiagnosis;


        /**
         * The unique identifier of the differential diagnosed diseases.
         */
        @JsonProperty("differentialDiagnosis")
        private List<String> differentialDiagnosis;


        /**
         * The unique identifier of the provisional diagnosed diseases.
         */
        @JsonProperty("provisionalDiagnosis")
        private List<String> provisionalDiagnosis;

        /**
         * For in-patient records, the date the service was provided. For out-patient records, this should be the date of the visit.
         */
        @JsonProperty("serviceDate")
        private String serviceDate;

        /**
         * The rank of the service provider.
         */
        @JsonProperty("serviceProviderRankingId")
        private String serviceProviderRankingId;

        /**
         * Visit type whether IPD or OPD visit.
         */
        @JsonProperty("visitType")
        private String visitType;

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getPatId() {
            return patId;
        }

        public void setPatId(String patId) {
            this.patId = patId;
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

        public List<String> getMedSvcCode() {
            return medSvcCode;
        }

        public void setMedSvcCode(List<String> medSvcCode) {
            this.medSvcCode = medSvcCode;
        }

        public List<String> getConfirmedDiagnosis() {
            return confirmedDiagnosis;
        }

        public void setConfirmedDiagnosis(List<String> confirmedDiagnosis) {
            this.confirmedDiagnosis = confirmedDiagnosis;
        }

        public List<String> getDifferentialDiagnosis() {
            return differentialDiagnosis;
        }

        public void setDifferentialDiagnosis(List<String> differentialDiagnosis) {
            this.differentialDiagnosis = differentialDiagnosis;
        }

        public List<String> getProvisionalDiagnosis() {
            return provisionalDiagnosis;
        }

        public void setProvisionalDiagnosis(List<String> provisionalDiagnosis) {
            this.provisionalDiagnosis = provisionalDiagnosis;
        }

        public String getServiceDate() {
            return serviceDate;
        }

        public void setServiceDate(String serviceDate) {
            this.serviceDate = serviceDate;
        }

        public String getServiceProviderRankingId() {
            return serviceProviderRankingId;
        }

        public void setServiceProviderRankingId(String serviceProviderRankingId) {
            this.serviceProviderRankingId = serviceProviderRankingId;
        }

        public String getVisitType() {
            return visitType;
        }

        public void setVisitType(String visitType) {
            this.visitType = visitType;
        }
    }
}
