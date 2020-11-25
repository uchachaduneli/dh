package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.Emails;
import ge.bestline.dhl.utils.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author ucha
 */
@ManagedBean(name = "emailBean")
@ViewScoped
public class EmailBean implements Serializable {
    private Emails slctedLeadEmail;
    private List<Emails> leadsEmailsList;

    /*private List<PhoneNumbers> leadsPhoneNumsList;
    private boolean phoneNumEdit;
    private PhoneNumbers slctedLeadPhoneNumber;*/

    private boolean emailEdit;
    private Integer leadId;
    private String company;

    public EmailBean() {
//        if (Util.getSessionParameter("userId") != null) {
        leadId = Util.getGetParam("leadId");
        loadLeadEmails();
//        } else {
//            Util.logout();
//        }

    }

    @PostConstruct
    public void EmailBean() {
        slctedLeadEmail = new Emails();
    }

    public void loadLeadEmails() {
        Map<String, List<Emails>> res = DbProcessing.getLeadEmails(leadId);
        Map.Entry<String, List<Emails>> entr = res.entrySet().iterator().next();
        leadsEmailsList = entr.getValue();
        company = entr.getKey();
    }

    public void confirmEmailManually() {
        Emails tmp = slctedLeadEmail;
        tmp.setConfirmed(2);
        if (DbProcessing.emailAction(tmp) > 0) {
            slctedLeadEmail = new Emails();
            Util.executeScript("confirmOfLeadEmailManualWidg.hide()");
            loadLeadEmails();
            Messages.info(Util.ka("operacia dasrulda warmatebiT"));
        } else {
            Messages.info(Util.ka("operacia ar sruldeba"));
        }
    }

    public void sendConfirmEmail() {
        Emails tmp = slctedLeadEmail;
        try {
            ConfigParams confParams = ConfigurationManager.getConfiguration().getConfParams();
            tmp.setActivationCode(Util.genetareConfirmCode());
            DhlMail.sendEmail(tmp.getMail(), "Email Confirmation - Georgian Express",
                    confParams.getConfirm_email_template().
                            replace("{ACTIVATIONCODE}", tmp.getActivationCode()).
                            replace("{URLPARAMS}", tmp.getId() + "/" + tmp.getActivationCode()),
                    null);

        } catch (ConfigurationException e) {
            Messages.warn(e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            Messages.warn("მეილის გაგზავნა ვერ მოხერხდა");
            e.printStackTrace();
            return;
        }
        if (DbProcessing.emailAction(tmp) > 0) {
            slctedLeadEmail = new Emails();
            Util.executeScript("confirmOfSendConfirmEmailWidg.hide()");
            loadLeadEmails();
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else {
            Messages.info("ოპერაცია არ სრულდება");
        }
    }


    public void saveUpdateEmail() {
        if (!isValidEmail(slctedLeadEmail.getMail())) {
            Messages.error("არასწორი ელ.ფოსტა !");
            return;
        }
        if (DbProcessing.emailAction(slctedLeadEmail) > 0) {
            slctedLeadEmail = new Emails();
            Util.executeScript("insertEmailDLGwidg.hide();");
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else {
            Messages.info("ოპერაცია არ სრულდება");
        }
        loadLeadEmails();
    }

    public void deleteLeadEmail() {
        Emails tmp = slctedLeadEmail;
        tmp.setId(tmp.getId() * -1);
        if (DbProcessing.emailAction(tmp) > 0) {
            slctedLeadEmail = new Emails();
            Util.executeScript("confirmOfLeadEmailDeletion.hide()");
            slctedLeadEmail = new Emails();
            loadLeadEmails();
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else {
            Messages.info("ოპერაცია არ სრულდება");
        }
    }

//
//    public void loadLeadPhoneNums() {
//        leadsPhoneNumsList = DbProcessing.getLeadPhoneNums(leadId);
//    }

//    public void confirmPhoneNumManually() {
//        PhoneNumbers tmp = slctedLeadPhoneNumber;
//        tmp.setConfirmed(2);
//        if (DbProcessing.phoneAction(tmp) > 0) {
//            slctedLeadPhoneNumber = new PhoneNumbers();
//            Util.executeScript("editEmailDLGwidg.hide()");
//            loadLeadEmails();
//            Messages.info(Util.ka("operacia dasrulda warmatebiT"));
//        } else {
//            Messages.info(Util.ka("operacia ar sruldeba"));
//        }
//    }
//
//    public void saveUpdateLeadPhoneNum() {
//        if (DbProcessing.phoneAction(slctedLeadPhoneNumber) > 0) {
//            slctedLeadPhoneNumber = new PhoneNumbers();
//            Util.executeScript("insertEmailDLGwidg.hide()");
//            loadLeadPhoneNums();
//            Messages.info("ოპერაცია დასრულდა წარმატებით");
//        } else {
//            Messages.info("ოპერაცია არ სრულდება");
//        }
//    }
//
//    public void deleteLeadPhoneNumber() {
//        PhoneNumbers tmp = slctedLeadPhoneNumber;
//        tmp.setId(tmp.getId() * -1);
//        if (DbProcessing.phoneAction(tmp) > 0) {
//            slctedLeadPhoneNumber = new PhoneNumbers();
//            Util.executeScript("confirmOfLeadPhoneDeletion.hide()");
//            slctedLeadPhoneNumber = new PhoneNumbers();
//            loadLeadPhoneNums();
//            Messages.info("ოპერაცია დასრულდა წარმატებით");
//        } else {
//            Messages.info("ოპერაცია არ სრულდება");
//        }
//    }
//
//    public void initphoneNumsDialog(long id) {
//        phoneNumEdit = id == 1;
//        if (!phoneNumEdit) {
//            slctedLeadPhoneNumber = new PhoneNumbers();
//        }
//    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public void initEmailsDialog(long id, Emails slcted) {

        emailEdit = id == 1;
        if (!emailEdit) {
            slctedLeadEmail = new Emails();
        } else {
            slctedLeadEmail = slcted;
        }
    }

    public Emails getSlctedLeadEmail() {
        return slctedLeadEmail;
    }

    public void setSlctedLeadEmail(Emails slctedLeadEmail) {
        this.slctedLeadEmail = slctedLeadEmail;
    }

    public List<Emails> getLeadsEmailsList() {
        return leadsEmailsList;
    }

    public void setLeadsEmailsList(List<Emails> leadsEmailsList) {
        this.leadsEmailsList = leadsEmailsList;
    }

    public boolean isEmailEdit() {
        return emailEdit;
    }

    public void setEmailEdit(boolean emailEdit) {
        this.emailEdit = emailEdit;
    }

    public Integer getLeadId() {
        return leadId;
    }

    public void setLeadId(Integer leadId) {
        this.leadId = leadId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
