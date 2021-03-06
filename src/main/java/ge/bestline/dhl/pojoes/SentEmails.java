package ge.bestline.dhl.pojoes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class SentEmails implements Serializable {
    private int id;
    private String mail;
    private String note;
    private int confirmed;
    private int status;
    private String strSentDate;
    private String identNumber;
    private int leadId;
    private int userId;
    private String userDesc;
    private String company;
    private String subject;
    private String bodyText;
    private Date sendDateStart;
    private Date sendDateEnd;
    List<String> attachments;

    public SentEmails(String email, int id, String subject, String bodyText, int status, List<String> attachments) {
        this.subject = subject;
        this.bodyText = bodyText;
        this.id = id;
        this.status = status;
        this.mail = email;
        this.attachments = attachments;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public SentEmails() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStrSentDate() {
        return strSentDate;
    }

    public void setStrSentDate(String strSentDate) {
        this.strSentDate = strSentDate;
    }

    public String getIdentNumber() {
        return identNumber;
    }

    public void setIdentNumber(String identNumber) {
        this.identNumber = identNumber;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
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

    @Override
    public String toString() {
        return "{" +
                "email_id=" + id +
                ", mail='" + mail + '\'' +
                '}';
    }
}
