package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.User;
import ge.bestline.dhl.utils.Messages;
import ge.bestline.dhl.utils.Util;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * @author Ucha Chaduneli
 */
@ManagedBean(name = "authBean")
@RequestScoped
public class Auth implements Serializable {

    private String userName;
    private String password;

    public Auth() {
        if (Util.getSessionParameter("userId") != null && new Integer(Util.getSessionParameter("userId").toString()) > 0) {
            Util.redirect("/home.jsf");
        }
    }

    public void login() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            if (userName != null && userName.length() > 0 && password != null && password.length() > 0) {
                User user = DbProcessing.getUsers(-1, userName, password).get(0);
                if (user != null && user.getId() > 0) {
                    session.setAttribute("userId", user.getId());
                    session.setAttribute("userDesc", user.getDescription());
                    session.setAttribute("user_type_id", user.getTypeId());
                    DbProcessing.rewriteExpireDates();
                    if (user.getTypeId() == 3) {
                        Util.redirect("/home.jsf?active=0");
                    } else {
                        Util.redirect("/home.jsf");
                    }
                } else {
                    Messages.showMsg(Util.ka("Seyvanili monacemebi arasworia"), FacesMessage.SEVERITY_ERROR);
                }
            }
        } catch (Exception ex) {
            Messages.showMsg(Util.ka("Seyvanili monacemebi arasworia"), FacesMessage.SEVERITY_ERROR);
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
