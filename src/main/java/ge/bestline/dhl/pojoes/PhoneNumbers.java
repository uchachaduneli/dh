package ge.bestline.dhl.pojoes;

import java.io.Serializable;

public class PhoneNumbers implements Serializable {
    private int id;
    private String phoneNum;
    private int leadId;
    private int confirmed;
    private String note;
    private String activationCode;
    private String strCreateDate;

    public PhoneNumbers() {
    }

    public PhoneNumbers(int id, String phoneNum, int leadId, int confirmed, String note, String activationCode, String strCreateDate) {
        this.id = id;
        this.phoneNum = phoneNum;
        this.leadId = leadId;
        this.confirmed = confirmed;
        this.note = note;
        this.activationCode = activationCode;
        this.strCreateDate = strCreateDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
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
