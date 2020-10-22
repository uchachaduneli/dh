package ge.bestline.dhl.pojoes;

import java.sql.Timestamp;

public class Emails {
    private int id;
    private String mail;
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


    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Emails emails = (Emails) o;

        if (id != emails.id) return false;
        if (leadId != emails.leadId) return false;
        if (confirmed != emails.confirmed) return false;
        if (mail != null ? !mail.equals(emails.mail) : emails.mail != null) return false;
        if (note != null ? !note.equals(emails.note) : emails.note != null) return false;
        if (activationCode != null ? !activationCode.equals(emails.activationCode) : emails.activationCode != null)
            return false;
        if (createDate != null ? !createDate.equals(emails.createDate) : emails.createDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (mail != null ? mail.hashCode() : 0);
        result = 31 * result + leadId;
        result = 31 * result + (int) confirmed;
        result = 31 * result + (note != null ? note.hashCode() : 0);
        result = 31 * result + (activationCode != null ? activationCode.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        return result;
    }
}
