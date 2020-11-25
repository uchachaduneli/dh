package ge.bestline.dhl.pojoes;

import java.io.Serializable;

public class Emails implements Serializable {
    private Integer id;
    private String mail;
    private Integer leadId;
    private Integer confirmed;
    private String note;
    private String activationCode;
    private String strCreateDate;

    public Emails() {
    }

    public Emails(Integer id, String mail, Integer leadId, Integer confirmed, String note, String activationCode, String strCreateDate) {
        this.id = id;
        this.mail = mail;
        this.leadId = leadId;
        this.confirmed = confirmed;
        this.note = note;
        this.activationCode = activationCode;
        this.strCreateDate = strCreateDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    public Integer getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Integer confirmed) {
        this.confirmed = confirmed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getStrCreateDate() {
        return strCreateDate;
    }

    public void setStrCreateDate(String strCreateDate) {
        this.strCreateDate = strCreateDate;
    }
}
