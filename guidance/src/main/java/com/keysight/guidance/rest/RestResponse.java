package com.keysight.guidance.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestResponse {

    //@XmlAttribute
    //private String messageBody;

    @XmlElement(name = "message-body")
    private String messageBody;

    public RestResponse() {
    }

    public RestResponse(String messageBody) {
        this.messageBody = messageBody;
    }
 
    public String getMessageBody() {
        return messageBody;
    }
 
    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
