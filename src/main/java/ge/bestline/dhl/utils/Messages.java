package ge.bestline.dhl.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * @author Ucha Chaduneli
 */
public class Messages {

    public static void info(String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", text));
    }

    public static void warn(String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "", text));
    }

    public static void error(String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", text));
    }

    public static void fatal(String text) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "", text));
    }

    public static void showMsg(String message, FacesMessage.Severity svr) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(svr, "", message));
    }

    public static void showBaseError() {
        Messages.error(Util.ka("Secdoma, daukavSirdiT administrators"));
    }

    public static void showAndKeepMessage(String message, FacesMessage.Severity svr) {
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().getFlash().setKeepMessages(true);
        fc.addMessage(null, new FacesMessage(svr, "", message));
    }

    public static abstract class GrowlMessage {

        public static void showSuccessMessage(String text) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage("", text));
        }
    }
}
