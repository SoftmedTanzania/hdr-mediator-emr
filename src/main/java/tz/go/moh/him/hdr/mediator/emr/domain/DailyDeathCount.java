package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DailyDeathCount extends EmrPayload {

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

    public String getLocalOrgID() {
        return localOrgID;
    }

    public void setLocalOrgID(String localOrgID) {
        this.localOrgID = localOrgID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
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
