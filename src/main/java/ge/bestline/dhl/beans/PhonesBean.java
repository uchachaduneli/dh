package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.PhoneNumbers;
import ge.bestline.dhl.utils.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.*;

/**
 * @author ucha
 */
@ManagedBean(name = "phoneBean")
@ViewScoped
public class PhonesBean implements Serializable {
    private PhoneNumbers slctedLeadPhone;
    private List<PhoneNumbers> leadsPhones;

    private boolean phoneEdit;
    private Integer leadId;
    private int currentUserId;

    private String company;

    public PhonesBean() {
        currentUserId = (int) Util.getSessionParameter("userId");
        if (currentUserId > 0) {
            try {
                leadId = Util.getGetParam("leadId");
                if (leadId == 0) {
                    Messages.error("კომპანიის შესახებ ინფორმაციის წამოღება ვერ ხერხდება დაბრუნდით მთავარ გვერდზე");
                    return;
                }
            } catch (Exception e) {
                Messages.error("კომპანიის შესახებ ინფორმაციის წამოღება ვერ ხერხდება დაბრუნდით მთავარ გვერდზე");
                return;
            }
            loadLeadPhones();
        } else {
            Util.logout();
        }

    }

    @PostConstruct
    public void PhoneBean() {
        slctedLeadPhone = new PhoneNumbers();
    }

    public boolean isMobile(String p) {
        return p.startsWith("+9955") || p.startsWith("5");
    }

    public void loadLeadPhones() {
        try {
            Map<String, List<PhoneNumbers>> res = DbProcessing.getLeadPhoneNums(leadId, false);
            Map.Entry<String, List<PhoneNumbers>> entr = res.entrySet().iterator().next();
            leadsPhones = entr.getValue();
            company = entr.getKey();
        } catch (NoSuchElementException e) {
            System.out.println("PhoneBean no records found for LeadId=" + leadId);
            e.printStackTrace();
        }

    }

    public void confirmPhoneManually() {
        PhoneNumbers tmp = slctedLeadPhone;
        tmp.setConfirmed(2);
        tmp.setActivationCode(null);
        if (DbProcessing.phoneAction(tmp) > 0) {
            slctedLeadPhone = new PhoneNumbers();
            Util.executeScript("confirmOfLeadphoneManualWidg.hide()");
            loadLeadPhones();
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else {
            Messages.info("ოპერაცია არ სრულდება");
        }
    }

    public void sendConfirmPhone() {
        PhoneNumbers tmp = slctedLeadPhone;
        try {
            ConfigParams confParams = ConfigurationManager.getConfiguration().getConfParams();
            tmp.setActivationCode(Util.genetareConfirmCode());
            Map<Integer, List<String>> numbers = new HashMap<>();
            numbers.put(1, Arrays.asList(tmp.getPhoneNum().startsWith("5") ? "995" + tmp.getPhoneNum() : tmp.getPhoneNum()));
            DhlSMS.sendSms(confParams.getConfirm_sms_template()
                            .replace("{ACTIVATIONCODE}", tmp.getActivationCode())
                            .replace("{URLPARAMS}", tmp.getId() + "/" + tmp.getActivationCode())
                    , numbers, currentUserId);
        } catch (ConfigurationException e) {
            Messages.warn(e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            Messages.error("SMS-ის გაგზავნა ვერ მოხერხდა");
            e.printStackTrace();
            return;
        }
        if (DbProcessing.phoneAction(tmp) > 0) {
            slctedLeadPhone = new PhoneNumbers();
            Util.executeScript("confirmOfSendConfirmphoneWidg.hide()");
            loadLeadPhones();
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else {
            Messages.error("ოპერაცია არ სრულდება");
        }
    }


    public void saveUpdatePhone() {
        if (!isValidPhone(slctedLeadPhone.getPhoneNum())) {
            Messages.error("არასწორი ფორმატის ნომერი !");
            return;
        }
        if (!(slctedLeadPhone.getLeadId() > 0)) {
            Messages.error("კომპანია ვერ იქნა იდენტიფიცირებული, დაბრუნდით მთავარ გვერდზე !");
            return;
        }
        int res = DbProcessing.phoneAction(slctedLeadPhone);
        if (res > 0) {
            slctedLeadPhone = new PhoneNumbers();
            Util.executeScript("insertphoneDLGwidg.hide();");
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else if (res == -2) {
            Messages.error("კომპანაზე უკვე დამატებულია მითითებული ნომერი");
        } else {
            Messages.error("ოპერაცია არ სრულდება");
        }
        loadLeadPhones();
    }

    public void deleteLeadPhone() {
        PhoneNumbers tmp = slctedLeadPhone;
        tmp.setId(tmp.getId() * -1);
        if (DbProcessing.phoneAction(tmp) > 0) {
            slctedLeadPhone = new PhoneNumbers();
            Util.executeScript("confirmOfLeadphoneDeletion.hide()");
            slctedLeadPhone = new PhoneNumbers();
            loadLeadPhones();
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        } else {
            Messages.error("ოპერაცია არ სრულდება");
        }
    }

    private boolean isValidPhone(String Phone) {
        String regex1 = "^(?=\\d{9}$)(5)\\d+";// mobilurebi +995 5** ** ** **
        String regex2 = "^\\+?\\d{12}$";//mobilurebi 5** ** ** **
        String regex3 = "^[0-9]{0,}$";//qalaqis nomrebi
        if (Phone.startsWith("5")) {
            return Phone.matches(regex1);
        } else if (Phone.startsWith("+")) {
            return Phone.matches(regex2);
        } else {
            return Phone.matches(regex3);
        }
    }

    public void initPhoneNumbersDialog(long id, PhoneNumbers slcted) {
        phoneEdit = id == 1;
        if (!phoneEdit) {
            slctedLeadPhone = new PhoneNumbers();
            slctedLeadPhone.setLeadId(leadId);
        } else {
            slctedLeadPhone = slcted;
        }
    }

    public PhoneNumbers getSlctedLeadPhone() {
        return slctedLeadPhone;
    }

    public void setSlctedLeadPhone(PhoneNumbers slctedLeadPhone) {
        this.slctedLeadPhone = slctedLeadPhone;
    }

    public List<PhoneNumbers> getLeadsPhones() {
        return leadsPhones;
    }

    public void setLeadsPhones(List<PhoneNumbers> leadsPhones) {
        this.leadsPhones = leadsPhones;
    }

    public boolean isPhoneEdit() {
        return phoneEdit;
    }

    public void setPhoneEdit(boolean phoneEdit) {
        this.phoneEdit = phoneEdit;
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
