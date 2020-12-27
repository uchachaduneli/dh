package ge.bestline.dhl.pojoes;

import java.io.Serializable;

public class SentEmailsDocs implements Serializable {
    private int id;
    private int sentEmailsId;
    private String docName;

    public SentEmailsDocs(int id, int sentEmailsId, String docName) {
        this.id = id;
        this.sentEmailsId = sentEmailsId;
        this.docName = docName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSentEmailsId() {
        return sentEmailsId;
    }

    public void setSentEmailsId(int sentEmailsId) {
        this.sentEmailsId = sentEmailsId;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }
}
