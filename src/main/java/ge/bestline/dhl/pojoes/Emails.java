package ge.bestline.dhl.pojoes;

import java.io.Serializable;

public class Emails implements Serializable {
    private int id;
    private String mail;
    private int leadId;
    private int confirmed;
    private String note;
    private String activationCode;
    private String strCreateDate;
    private int dhlDb;
    private int invoiceDb;

    public Emails() {
    }

    public Emails(int id, String mail, int leadId, int confirmed,
                  String note, String activationCode,
                  String strCreateDate, int dhlDb, int invoiceDb) {
        this.id = id;
        this.mail = mail;
        this.leadId = leadId;
        this.confirmed = confirmed;
        this.note = note;
        this.activationCode = activationCode;
        this.strCreateDate = strCreateDate;
        this.dhlDb = dhlDb;
        this.invoiceDb = invoiceDb;
    }

    public int getInvoiceDb() {
        return invoiceDb;
    }

    public void setInvoiceDb(int invoiceDb) {
        this.invoiceDb = invoiceDb;
    }

    public int getDhlDb() {
        return dhlDb;
    }

    public void setDhlDb(int dhlDb) {
        this.dhlDb = dhlDb;
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
