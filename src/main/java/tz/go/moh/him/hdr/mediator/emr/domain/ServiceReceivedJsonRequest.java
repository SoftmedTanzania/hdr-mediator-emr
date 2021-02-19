package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ServiceReceivedJsonRequest extends EmrPayload {
    /**
     * Common name of submitting facility
     */
    @JsonProperty("Org Name")
    @JsonAlias({"ORG NAME", "org name"})
    private String orgName;

    /**
     * Unique identifier of submitting facility, as presented from HFR
     */
    @JsonProperty("Local Org ID")
    @JsonAlias({"LOCAL ORG ID", "local org id"})
    private String localOrgID;

    /**
     * List of service received items
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
         * Point of care facility or other ancillary facility (e.g. clinic, lab, MRI center) within the hospital
         */
        @JsonProperty("Dept Name")
        @JsonAlias({"DEPT NAME", "dept name"})
        private String deptName;

        /**
         * Unique identifier of the department/section
         */
        @JsonProperty("Dept ID")
        @JsonAlias({"DEPT ID", "dept id"})
        private String deptID;

        /**
         * Unique identifier of patient associated with the transaction
         */
        @JsonProperty("Pat ID")
        @JsonAlias({"PAT ID", "pat id"})
        private String patID;

        /**
         * Gender of the patient
         */
        @JsonProperty("Gender")
        @JsonAlias({"GENDER", "gender"})
        private String gender;

        /**
         * Patient date of birth
         */
        @JsonProperty("DOB")
        @JsonAlias({"dob"})
        private String dob;

        /**
         * The unique identifier of the medical services provided. They should be comma separated (e.g. 002923, 00277, 002772)
         */
        @JsonProperty("Med SVC Code")
        @JsonAlias({"SERVICE CODE", "service code"})
        private String medSvcCode;

        /**
         * The unique identifier of the diagnosed diseases. Should be comma separated (e.g. A17.8, M60.1, B29)
         */
        @JsonProperty("ICD10 Code")
        @JsonAlias({"DIAGNOSES CODE", "diagnoses code"})
        private String icd10Code;

        /**
         * For in-patient records, the date the service was provided. For out-patient records, this should be the date of the visit.
         */
        @JsonProperty("Service Date")
        @JsonAlias("SERVICE DATE")
        private String serviceDate;

        public String getDeptName() {
            return deptName;
        }

        public String getDeptID() {
            return deptID;
        }

        public String getPatID() {
            return patID;
        }

        public String getGender() {
            return gender;
        }

        public String getDob() {
            return dob;
        }

        public String getMedSvcCode() {
            return medSvcCode;
        }

        public String getIcd10Code() {
            return icd10Code;
        }

        public String getServiceDate() {
            return serviceDate;
        }
    }
}
