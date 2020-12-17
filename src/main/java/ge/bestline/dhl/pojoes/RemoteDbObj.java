package ge.bestline.dhl.pojoes;

import java.io.Serializable;

public class RemoteDbObj implements Serializable {
    private String identNumber;
    private int clientId;
    private int contactId;
    private String Email;
    private String name = "accountant";

    public RemoteDbObj(String identNumber, int clientId, int contactId, String email, String name) {
        this.identNumber = identNumber;
        this.clientId = clientId;
        this.contactId = contactId;
        Email = email;
        this.name = name;
    }

    public RemoteDbObj() {
    }

    public String getIdentNumber() {
        return identNumber;
    }

    public void setIdentNumber(String identNumber) {
        this.identNumber = identNumber;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
