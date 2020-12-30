package com.softmed.hdr_mediator_emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DailyDeathCount {
    @JsonProperty("Message Type")
    private String messageType;

    @JsonProperty("Org Name")
    private String orgName;

    @JsonProperty("Local Org ID")
    private String localOrgID;

    @JsonProperty("Ward ID")
    private String wardId;

    @JsonProperty("Ward Name")
    private String wardName;

    @JsonProperty("Pat ID")
    private String patID;

    @JsonProperty("Disease Code")
    private String diseaseCode;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("DOB")
    private String dob;

    @JsonProperty("Date Death Occurred")
    private String dateDeathOccurred;

    public String getMessageType() {
        return messageType;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getLocalOrgID() {
        return localOrgID;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setLocalOrgID(String localOrgID) {
        this.localOrgID = localOrgID;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getPatID() {
        return patID;
    }

    public void setPatID(String patID) {
        this.patID = patID;
    }

    public String getDiseaseCode() {
        return diseaseCode;
    }

    public void setDiseaseCode(String diseaseCode) {
        this.diseaseCode = diseaseCode;
    }

    public String getWardName() {
        return wardName;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
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
