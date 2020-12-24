package com.softmed.hdr_mediator_emr.domain;

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

}
