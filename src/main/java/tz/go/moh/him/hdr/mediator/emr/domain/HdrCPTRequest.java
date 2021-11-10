package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HdrCPTRequest {

    @JsonProperty("cpt_category_code_id")
    private int cptCategoryCodeId;

    @JsonProperty("cpt_code_category_description")
    private String cptCodeCategoryDescription;

    @JsonProperty("cpt_code_sub_category_id")
    private int cptCodeSubCategoryId;

    @JsonProperty("cpt_code_sub_category_description")
    private String cptCodeSubCategoryDescription;

    @JsonProperty("cpt_code_id")
    private int cptCodeId;

    @JsonProperty("cpt_code")
    private String cptCode;

    @JsonProperty("cpt_description")
    private String cptDescription;

    public int getCptCategoryCodeId() {
        return cptCategoryCodeId;
    }

    public void setCptCategoryCodeId(int cptCategoryCodeId) {
        this.cptCategoryCodeId = cptCategoryCodeId;
    }

    public String getCptCodeCategoryDescription() {
        return cptCodeCategoryDescription;
    }

    public void setCptCodeCategoryDescription(String cptCodeCategoryDescription) {
        this.cptCodeCategoryDescription = cptCodeCategoryDescription;
    }

    public int getCptCodeSubCategoryId() {
        return cptCodeSubCategoryId;
    }

    public void setCptCodeSubCategoryId(int cptCodeSubCategoryId) {
        this.cptCodeSubCategoryId = cptCodeSubCategoryId;
    }

    public String getCptCodeSubCategoryDescription() {
        return cptCodeSubCategoryDescription;
    }

    public void setCptCodeSubCategoryDescription(String cptCodeSubCategoryDescription) {
        this.cptCodeSubCategoryDescription = cptCodeSubCategoryDescription;
    }

    public int getCptCodeId() {
        return cptCodeId;
    }

    public void setCptCodeId(int cptCodeId) {
        this.cptCodeId = cptCodeId;
    }

    public String getCptCode() {
        return cptCode;
    }

    public void setCptCode(String cptCode) {
        this.cptCode = cptCode;
    }

    public String getCptDescription() {
        return cptDescription;
    }

    public void setCptDescription(String cptDescription) {
        this.cptDescription = cptDescription;
    }

}
