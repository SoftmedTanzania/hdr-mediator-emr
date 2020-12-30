package com.softmed.hdr_mediator_emr.messages;

import com.google.gson.Gson;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class HdrRequestMessageTest {

    @Test
    public void testHdrRequestMessage() {
        String payload = "{\"hdrClient\":{\"openHimClientId\":\"emr-filedrop-sync-service\",\"name\":\"emr-filedrop-sync-service\"},\"hdrEvents\":[{\"eventType\":\"save-bed-occupancy\",\"eventDate\":\"Dec 30, 2020 5:18:41 PM\",\"openHimClientId\":\"emr-filedrop-sync-service\",\"mediatorVersion\":\"0.1.0\",\"json\":{\"messageType\":\"BEDOCC\"}}]}";
        JSONObject expectedPayloadJsonObject = new JSONObject(payload);
        HdrRequestMessage hdrRequestMessage = new Gson().fromJson(payload, HdrRequestMessage.class);

        HdrRequestMessage.HdrClient payloadHdrClient = hdrRequestMessage.getHdrClient();

        assertEquals(expectedPayloadJsonObject.getJSONObject("hdrClient").getString("name"), payloadHdrClient.getName());
        assertEquals(expectedPayloadJsonObject.getJSONObject("hdrClient").getString("openHimClientId"), payloadHdrClient.getOpenHimClientId());

        payloadHdrClient.setPayload("testPayload");
        assertEquals("testPayload",payloadHdrClient.getPayload());

        HdrRequestMessage.HdrEvent payloadHdrEvent = hdrRequestMessage.getHdrEvents().get(0);
        JSONObject expectedHdrEventJsonObject = expectedPayloadJsonObject.getJSONArray("hdrEvents").getJSONObject(0);


        assertEquals(expectedHdrEventJsonObject.getString("eventType"), payloadHdrEvent.getEventType());
        assertEquals(expectedHdrEventJsonObject.getString("openHimClientId"), payloadHdrEvent.getOpenHimClientId());
        assertEquals(expectedHdrEventJsonObject.getString("mediatorVersion"), payloadHdrEvent.getMediatorVersion());
        assertEquals(expectedHdrEventJsonObject.getJSONObject("json").toString(), new Gson().toJson(payloadHdrEvent.getJson()));

        Date date = new Date();
        payloadHdrEvent.setEventDate(date);
        assertEquals(date.toString(),payloadHdrEvent.getEventDate().toString());


    }
}