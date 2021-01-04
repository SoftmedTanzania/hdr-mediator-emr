package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceReceived {
    @JsonProperty("Message Type")
    private String messageType;

    @JsonProperty("Org Name")
    private String orgName;

    @JsonProperty("Local Org ID")
    private String localOrgID;

    @JsonProperty("Dept Name")
    private String deptName;

    @JsonProperty("Dept ID")
    private String deptID;

    @JsonProperty("Pat ID")
    private String patID;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("Med SVC Code")
    private String medSvcCode;

    @JsonProperty("ICD10 Code")
    private String icd10Code;

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
