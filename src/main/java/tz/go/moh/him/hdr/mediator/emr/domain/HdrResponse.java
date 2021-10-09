package tz.go.moh.him.hdr.mediator.emr.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

public class HdrResponse {

    /**
     * The Status Code.
     */
    @SerializedName("Status")
    @JsonProperty("Status")
    private int status;

    /**
     * The HDR ICD10 Category ID.
     */
    @SerializedName("CategoryId")
    @JsonProperty("CategoryId")
    private int categoryId;


    /**
     * The Response Message.
     */
    @SerializedName("Message")
    @JsonProperty("Message")
    private String message;

    /**
     * The Response Message.
     */
    @SerializedName("MessageType")
    @JsonProperty("MessageType")
    private String messageType;

    public HdrResponse(int status, int categoryId, String message, String messaType) {
        this.status = status;
        this.categoryId = categoryId;
        this.message = message;
        this.messageType = messaType;
    }

    public HdrResponse() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
