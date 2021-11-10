package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HdrCPTRequest {

    @JsonProperty("cpt_category_description")
    private String cptCategoryDescription;

    @JsonProperty("cpt_sub_category_description")
    private String cptSubCategoryDescription;

    @JsonProperty("cpt_code")
    private String cptCode;

    @JsonProperty("cpt_code_description")
    private String cptCodeDescription;

    public String getCptCategoryDescription() {
        return cptCategoryDescription;
    }

    public void setCptCategoryDescription(String cptCategoryDescription) {
        this.cptCategoryDescription = cptCategoryDescription;
    }

    public String getCptSubCategoryDescription() {
        return cptSubCategoryDescription;
    }

    public void setCptSubCategoryDescription(String cptSubCategoryDescription) {
        this.cptSubCategoryDescription = cptSubCategoryDescription;
    }

    public String getCptCode() {
        return cptCode;
    }

    public void setCptCode(String cptCode) {
        this.cptCode = cptCode;
    }

    public String getCptCodeDescription() {
        return cptCodeDescription;
    }

    public void setCptCodeDescription(String cptCodeDescription) {
        this.cptCodeDescription = cptCodeDescription;
    }
}
