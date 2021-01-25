package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceReceived {
    /**
     * Defines the Message type - will contain value of SVCREC for this object
     */
    @JsonProperty("Message Type")
    private String messageType;

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
     * Point of care facility or other ancillary facility (e.g. clinic, lab, MRI center) within the hospital
     */
    @JsonProperty("Dept Name")
    private String deptName;

    /**
     * Unique identifier of the department/section
     */
    @JsonProperty("Dept ID")
    private String deptID;

    /**
     * Unique identifier of patient associated with the transaction
     */
    @JsonProperty("Pat ID")
    private String patID;

    /**
     * Gender of the patient
     */
    @JsonProperty("Gender")
    private String gender;

    /**
     * Patient date of birth
     */
    @JsonProperty("DOB")
    private String dob;

    /**
     * The unique identifier of the medical services provided. They should be comma separated (e.g. 002923, 00277, 002772)
     */
    @JsonProperty("Med SVC Code")
    private String medSvcCode;

    /**
     * The unique identifier of the diagnosed diseases. Should be comma separated (e.g. A17.8, M60.1, B29)
     */
    @JsonProperty("ICD10 Code")
    private String icd10Code;

    /**
     * For in-patient records, the date the service was provided. For out-patient records, this should be the date of the visit.
     */
    @JsonProperty("Service Date")
    private String serviceDate;

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLocalOrgID() {
        return localOrgID;
    }

    public void setLocalOrgID(String localOrgID) {
        this.localOrgID = localOrgID;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptID() {
        return deptID;
    }

    public void setDeptID(String deptID) {
        this.deptID = deptID;
    }

    public String getPatID() {
        return patID;
    }

    public void setPatID(String patID) {
        this.patID = patID;
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

    public String getMedSvcCode() {
        return medSvcCode;
    }

    public void setMedSvcCode(String medSvcCode) {
        this.medSvcCode = medSvcCode;
    }

    public String getIcd10Code() {
        return icd10Code;
    }

    public void setIcd10Code(String icd10Code) {
        this.icd10Code = icd10Code;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }
}
