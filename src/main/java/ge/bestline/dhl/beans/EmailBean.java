package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.Emails;
import ge.bestline.dhl.pojoes.SentEmails;
import ge.bestline.dhl.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
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

    private boolean emailEdit;
    private Integer leadId;
    private String company;
    private static final Logger logger = LogManager.getLogger(EmailBean.class);
    private int currentUserId;

    public EmailBean() {
        currentUserId = (int) Util.getSessionParameter("userId");
        if (currentUserId > 0) {
            try {
                leadId = Util.getGetParam("leadId");
                if (leadId == 0) {
                    Messages.error("კომპანიის შესახებ ინფორმაციის წამოღება ვერ ხერხდება დაბრუნდით მთავარ გვერდზე");
                    return;
                }
            } catch (Exception e) {
                logger.error("Can't Get userId From Session", e);
                Messages.error("კომპანიის შესახებ ინფორმაციის წამოღება ვერ ხერხდება დაბრუნდით მთავარ გვერდზე");
                return;
            }
            loadLeadEmails();
        } else {
            Util.logout();
        }

    }

    @PostConstruct
    public void EmailBean() {
        slctedLeadEmail = new Emails();
    }

    public void syncWithDhlOrInvoices() {
        DbProcessing.syncWithDhlOrInvoices(slctedLeadEmail).stream().forEach(s -> {
            if (s.contains(".")) {
                Messages.info(s);
                loadLeadEmails();
            } else {
                Messages.error(s);
            }
        });
        Util.executeScript("dhlOrInvoiceDbSyncWidgDlg.hide()");
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
        tmp.setActivationCode(null);
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
        List<SentEmails> sentEmails = new ArrayList<>();
        Emails tmp = slctedLeadEmail;
        try {
            ConfigParams confParams = ConfigurationManager.getConfiguration().getConfParams();
            tmp.setActivationCode(Util.genetareConfirmCode());
            DhlMail.sendEmail(tmp.getMail(), "Email Confirmation - Georgian Express",
                    confParams.getConfirm_email_template().
                            replace("{ACTIVATIONCODE}", tmp.getActivationCode()).
                            replace("{URLPARAMS}", tmp.getId() + "/" + tmp.getActivationCode()),
                    null);
            sentEmails.add(new SentEmails(tmp.getId(), "Email Confirmation", confParams.getConfirm_email_template(), 1));
        } catch (ConfigurationException e) {
            sentEmails.add(new SentEmails(tmp.getId(), "Email Confirmation", e.getLocalizedMessage(), 2));
            Messages.warn(e.getMessage());
            logger.error(e);
            return;
        } catch (Exception e) {
            sentEmails.add(new SentEmails(tmp.getId(), "Email Confirmation", e.getLocalizedMessage(), 2));
            Messages.warn("მეილის გაგზავნა ვერ მოხერხდა");
            logger.error("Can't send email", e);
            return;
        }
        if (!sentEmails.isEmpty()) {
            try {
                DbProcessing.insertEmailHistory(sentEmails, currentUserId);
            } catch (SQLException e) {
                logger.error("Can't insert sent emails into Database", e);
            }
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
        if (!(slctedLeadEmail.getLeadId() > 0)) {
            Messages.error("კომპანია ვერ იქნა იდენტიფიცირებული, დაბრუნდით მთავარ გვერდზე !");
            return;
        }
        int res = DbProcessing.emailAction(slctedLeadEmail);
        if (res > 0) {
            slctedLeadEmail = new Emails();
            Util.executeScript("insertEmailDLGwidg.hide();");
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else if (res == -2) {
            Messages.info("კომპანიაზე უკვე დამატებულია მითითებული მეილი");
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

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public void initEmailsDialog(long id, Emails slcted) {
        emailEdit = id == 1;
        if (!emailEdit) {
            slctedLeadEmail = new Emails();
            slctedLeadEmail.setLeadId(leadId);
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
