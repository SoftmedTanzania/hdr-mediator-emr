package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BedOccupancy {
    /**
     * Defines the Message type - will contain value of BEDOCC for this object
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
     * The unique ID of the ward where the patient is admitted
     */
    @JsonProperty("Ward ID")
    private String wardId;

    /**
     * The name of the ward where the patient is admitted
     */
    @JsonProperty("Ward Name")
    private String wardName;

    /**
     * Unique identifier of patient associated with the transaction
     */
    @JsonProperty("Pat ID")
    private String patID;

    /**
     * The date that the patient was admitted to the ward
     */
    @JsonProperty("Admission Date")
    private String admissionDate;

    /**
     * The date that the patient was discharged from the ward
     */
    @JsonProperty("Discharge Date")
    private String dischargeDate;

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

    public String getPatID() {
        return patID;
    }

    public void setPatID(String patID) {
        this.patID = patID;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
}
