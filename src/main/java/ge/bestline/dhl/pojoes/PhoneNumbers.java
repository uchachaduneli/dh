package ge.bestline.dhl.pojoes;

import java.sql.Timestamp;

public class PhoneNumbers {
    private int id;
    private String phoneNum;
    private int leadId;
    private byte confirmed;
    private String note;
    private String activationCode;
    private Timestamp createDate;

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

    public byte getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(byte confirmed) {
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

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }
}
