package ge.bestline.dhl.utils;

public class ConfigParams {
    String smtp_host;
    String smtp_port;
    String sender_email;
    String sender_email_pass;
    String sms_url;
    String enable_multisending_sms_and_email;
    String confirm_email_template;

    public ConfigParams() {
    }

    public ConfigParams(String smtp_host, String smtp_port, String sender_email,
                        String sender_email_pass, String sms_url,
                        String enable_multisending_sms_and_email, String confirm_email_template) {
        this.smtp_host = smtp_host;
        this.smtp_port = smtp_port;
        this.sender_email = sender_email;
        this.sender_email_pass = sender_email_pass;
        this.sms_url = sms_url;
        this.enable_multisending_sms_and_email = enable_multisending_sms_and_email;
        this.confirm_email_template = confirm_email_template;
    }

    public String getConfirm_email_template() {
        return confirm_email_template;
    }

    public void setConfirm_email_template(String confirm_email_template) {
        this.confirm_email_template = confirm_email_template;
    }

    public String getEnable_multisending_sms_and_email() {
        return enable_multisending_sms_and_email;
    }

    public void setEnable_multisending_sms_and_email(String enable_multisending_sms_and_email) {
        this.enable_multisending_sms_and_email = enable_multisending_sms_and_email;
    }

    public String getSmtp_host() {
        return smtp_host;
    }

    public void setSmtp_host(String smtp_host) {
        this.smtp_host = smtp_host;
    }

    public String getSmtp_port() {
        return smtp_port;
    }

    public void setSmtp_port(String smtp_port) {
        this.smtp_port = smtp_port;
    }

    public String getSender_email() {
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public String getSender_email_pass() {
        return sender_email_pass;
    }

    public void setSender_email_pass(String sender_email_pass) {
        this.sender_email_pass = sender_email_pass;
    }

    public String getSms_url() {
        return sms_url;
    }

    public void setSms_url(String sms_url) {
        this.sms_url = sms_url;
    }
}
