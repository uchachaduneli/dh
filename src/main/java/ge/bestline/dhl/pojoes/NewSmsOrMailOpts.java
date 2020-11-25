package ge.bestline.dhl.pojoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NewSmsOrMailOpts implements Serializable {
    private int smsOrMail;
    private int smsOrMailToSrchedOrOne;
    private String smsOrMailTo;
    private String smsOrMailSubject;
    private String smsOrMailText;
    private List<String> attachmentsPath = new ArrayList<String>();

    public int getSmsOrMail() {
        return smsOrMail;
    }

    public void setSmsOrMail(int smsOrMail) {
        this.smsOrMail = smsOrMail;
    }

    public int getSmsOrMailToSrchedOrOne() {
        return smsOrMailToSrchedOrOne;
    }

    public void setSmsOrMailToSrchedOrOne(int smsOrMailToSrchedOrOne) {
        this.smsOrMailToSrchedOrOne = smsOrMailToSrchedOrOne;
    }

    public String getSmsOrMailTo() {
        return smsOrMailTo;
    }

    public void setSmsOrMailTo(String smsOrMailTo) {
        this.smsOrMailTo = smsOrMailTo;
    }

    public String getSmsOrMailSubject() {
        return smsOrMailSubject;
    }

    public void setSmsOrMailSubject(String smsOrMailSubject) {
        this.smsOrMailSubject = smsOrMailSubject;
    }

    public String getSmsOrMailText() {
        return smsOrMailText;
    }

    public void setSmsOrMailText(String smsOrMailText) {
        this.smsOrMailText = smsOrMailText;
    }

    public List<String> getAttachmentsPath() {
        return attachmentsPath;
    }

    public void setAttachmentsPath(List<String> attachmentsPath) {
        this.attachmentsPath = attachmentsPath;
    }
}
