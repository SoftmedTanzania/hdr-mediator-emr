package tz.go.moh.him.hdr.mediator.emr.messages;

import java.util.Date;
import java.util.List;

public class HdrRequestMessage {
    private HdrClient hdrClient;
    private List<HdrEvent> hdrEvents;

    public HdrClient getHdrClient() {
        return hdrClient;
    }

    public void setHdrClient(HdrClient hdrClient) {
        this.hdrClient = hdrClient;
    }

    public List<HdrEvent> getHdrEvents() {
        return hdrEvents;
    }

    public void setHdrEvents(List<HdrEvent> hdrEvents) {
        this.hdrEvents = hdrEvents;
    }

    public static class HdrClient {
        private String openHimClientId;
        private String name;
        private Object payload;

        public String getOpenHimClientId() {
            return openHimClientId;
        }

        public void setOpenHimClientId(String openHimClientId) {
            this.openHimClientId = openHimClientId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getPayload() {
            return payload;
        }

        public void setPayload(Object payload) {
            this.payload = payload;
        }
    }

    public static class HdrEvent {
        private String eventType;
        private Date eventDate;
        private String openHimClientId;
        private String mediatorVersion;
        private Object payload;

        public String getEventType() {
            return eventType;
        }

        public void setEventType(String eventType) {
            this.eventType = eventType;
        }

        public Date getEventDate() {
            return eventDate;
        }

        public void setEventDate(Date eventDate) {
            this.eventDate = eventDate;
        }

        public String getOpenHimClientId() {
            return openHimClientId;
        }

        public void setOpenHimClientId(String openHimClientId) {
            this.openHimClientId = openHimClientId;
        }

        public String getMediatorVersion() {
            return mediatorVersion;
        }

        public void setMediatorVersion(String mediatorVersion) {
            this.mediatorVersion = mediatorVersion;
        }

        public Object getPayload() {
            return payload;
        }

        public void setPayload(Object payload) {
            this.payload = payload;
        }
    }
}
