package ge.bestline.dhl.pojoes;

import java.io.Serializable;
import java.util.Date;

public class SentSMSes implements Serializable {
    private int id;
    private Integer leadId;
    private String to;
    private String text;
    private String response;
    private String strSendTime;
    private String leadData;//company name , ident code
    private Date sendDateStart;
    private Date sendDateEnd;
    private int status;

    public SentSMSes() {
    }

    public SentSMSes(int id, Integer leadId, String to, String text, String response, String strSendTime) {
        this.id = id;
        this.leadId = leadId;
        this.to = to;
        this.text = text;
        this.response = response;
        this.strSendTime = strSendTime;
    }

    public String getLeadData() {
        return leadData;
    }

    public void setLeadData(String leadData) {
        this.leadData = leadData;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSendDateStart() {
        return sendDateStart;
    }

    public void setSendDateStart(Date sendDateStart) {
        this.sendDateStart = sendDateStart;
    }

    public Date getSendDateEnd() {
        return sendDateEnd;
    }

    public void setSendDateEnd(Date sendDateEnd) {
        this.sendDateEnd = sendDateEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getStrSendTime() {
        return strSendTime;
    }

    public void setStrSendTime(String strSendTime) {
        this.strSendTime = strSendTime;
    }
}
