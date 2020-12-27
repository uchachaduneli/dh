/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.SentEmails;
import ge.bestline.dhl.pojoes.SentEmailsDocs;
import ge.bestline.dhl.pojoes.User;
import ge.bestline.dhl.utils.Constants;
import ge.bestline.dhl.utils.Messages;
import ge.bestline.dhl.utils.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author uchachaduneli
 */
@ManagedBean(name = "emailsBean")
@ViewScoped
public class EmailsBean implements Serializable {

    private List<SentEmails> emailsList;
    private SentEmails slctedEmail = new SentEmails();
    private SentEmails searchObj = new SentEmails();
    private int rowLimit = 15;
    private String paginator;
    private List<SelectItem> rowCountList;
    private List<SelectItem> usersSelection = new ArrayList<SelectItem>();
    private List<SentEmailsDocs> documents;

    public EmailsBean() {
        if (Util.getSessionParameter("userId") != null) {
            if (Util.getSessionParameter("to") != null) {
                searchObj.setMail((String) Util.getSessionParameter("to"));
            }
            if (Util.getSessionParameter("ident_number") != null) {
                searchObj.setIdentNumber((String) Util.getSessionParameter("ident_number"));
            }
            if (Util.getSessionParameter("subject") != null) {
                searchObj.setSubject((String) Util.getSessionParameter("subject"));
            }
            if (Util.getSessionParameter("status") != null) {
                searchObj.setStatus((Integer) Util.getSessionParameter("status"));
            }
            if (Util.getSessionParameter("confirmed") != null) {
                searchObj.setConfirmed((Integer) Util.getSessionParameter("confirmed"));
            }
            if (Util.getSessionParameter("sent_em_user_id") != null) {
                searchObj.setUserId((Integer) Util.getSessionParameter("sent_em_user_id"));
            }
            if (Util.getSessionParameter("date_filter_start") != null) {
                searchObj.setSendDateStart((Date) Util.getSessionParameter("date_filter_start"));
            }
            if (Util.getSessionParameter("date_filter_end") != null) {
                searchObj.setSendDateEnd((Date) Util.getSessionParameter("date_filter_end"));
            }
            loadRowCountList();
            if (usersSelection.isEmpty()) {
                List<User> workers = DbProcessing.getUsers(0, "", "");
                for (User user : workers) {
                    usersSelection.add(new SelectItem(user.getId(), user.getDescription()));
                }
            }
            init();
        } else {
            Util.logout();
        }
    }

    public void loadSentEmailsAttachments() {
        documents = DbProcessing.getSentEmailsAttachments(slctedEmail.getId());
    }

    public void init() {
        Integer p = Util.getParam("p");
        p = p != null && p > 0 ? p : 0;
        int start = 0;
        if (p > 0) {
            start = (p - 1) * rowLimit;
        }
        int fullCount = 0;
        try {
            fullCount = DbProcessing.getSentEmailsCount(searchObj);
        } catch (Exception e) {
        }

        emailsList = DbProcessing.getSentEmails(searchObj, start, rowLimit);

        paginator = Util.paginator(fullCount, p, rowLimit, Util.getURLFullPath() + "&ns=1", 3);
        if (fullCount == 0) {
            paginator = Util.paginator(fullCount, p, rowLimit, Constants.projectPath + "/sent_emails.jsf?", 3);
            if (fullCount == 0) {
                emailsList = new ArrayList<SentEmails>();
            }
        }
    }

    public void loadRowCountList() {
        if (rowCountList == null || rowCountList.isEmpty()) {
            rowLimit = ((Integer) Util.getSessionParameter("row_limit")) != null ? ((Integer) Util.getSessionParameter("row_limit")) : 15;
            rowCountList = new ArrayList<SelectItem>();
            rowCountList.add(new SelectItem(15, "15"));
            rowCountList.add(new SelectItem(50, "50"));
            rowCountList.add(new SelectItem(100, "100"));
            rowCountList.add(new SelectItem(200, "200"));
            rowCountList.add(new SelectItem(500, "500"));
        }
    }

    public void resetDateFilter() {
        Util.removeSessionAttribute("date_filter_start");
        Util.removeSessionAttribute("date_filter_end");
        searchObj.setSendDateStart(null);
        searchObj.setSendDateEnd(null);
    }

    public void filterSaleOrAdmin() {
        try {
            Util.setSessionParameter("sent_em_user_id", searchObj.getUserId());
        } catch (Exception e) {
        }
    }

    public void filterStatus() {
        try {
            Util.setSessionParameter("status", searchObj.getStatus());
        } catch (Exception e) {
        }
    }

    public void filterByTo() {
        try {
            Util.setSessionParameter("to", searchObj.getMail());
        } catch (Exception e) {
        }
    }

    public void filterBySubject() {
        try {
            Util.setSessionParameter("subject", searchObj.getSubject());
        } catch (Exception e) {
        }
    }

    public void filterByConfirmed() {
        try {
            Util.setSessionParameter("confirmed", searchObj.getConfirmed());
        } catch (Exception e) {
        }
    }

    public void filterByIdentNumber() {
        try {
            Util.setSessionParameter("ident_number", searchObj.getIdentNumber());
        } catch (Exception e) {
        }
    }

    public void validateFromDateFilter() {
        if (searchObj != null) {
            if (searchObj.getSendDateEnd() == null) {
                searchObj.setSendDateEnd(searchObj.getSendDateStart());
            } else {
                if (searchObj.getSendDateEnd() != null && !searchObj.getSendDateStart().before(searchObj.getSendDateEnd())) {
                    Messages.warn("გთხოვთ სწორად მიუთითოთ დროითი შუალედი");
                } else if (searchObj.getSendDateEnd() != null) {
                    validateToDateFilter();
                }
            }
            try {
                if (searchObj.getSendDateStart() != null && searchObj.getSendDateEnd() != null) {
                    Util.setSessionParameter("date_filter_start", searchObj.getSendDateStart());
                    Util.setSessionParameter("date_filter_end", searchObj.getSendDateEnd());
                }
            } catch (Exception ex) {
            }
        }
    }

    public void validateToDateFilter() {
        if (searchObj != null) {
            if (searchObj.getSendDateStart() != null && searchObj.getSendDateEnd() != null) {
                if (!searchObj.getSendDateStart().before(searchObj.getSendDateEnd())) {
                    Messages.warn("გთხოვთ სწორად მიუთითოთ დროითი შუალედი");
                    return;
                }
            } else {
                Messages.warn("გთხოვთ სწორად მიუთითოთ საწყისი თარიღი");
                return;
            }
            try {
                if (searchObj.getSendDateStart() != null && searchObj.getSendDateEnd() != null) {
                    Util.setSessionParameter("date_filter_start", searchObj.getSendDateStart());
                    Util.setSessionParameter("date_filter_end", searchObj.getSendDateEnd());
                } else if (searchObj.getSendDateStart() == null) {
                    Util.removeSessionAttribute("date_filter_start");
                } else if (searchObj.getSendDateEnd() == null) {
                    Util.removeSessionAttribute("date_filter_end");
                }
            } catch (Exception ex) {
            }
        }
    }

    public void rowsCountChangeListener() {
        init();
        try {
            Util.setSessionParameter("row_limit", rowLimit);
        } catch (Exception e) {
        }
    }

    public List<SentEmails> getEmailsList() {
        return emailsList;
    }

    public void setEmailsList(List<SentEmails> emailsList) {
        this.emailsList = emailsList;
    }

    public SentEmails getSlctedEmail() {
        return slctedEmail;
    }

    public void setSlctedEmail(SentEmails slctedEmail) {
        this.slctedEmail = slctedEmail;
    }

    public SentEmails getSearchObj() {
        return searchObj;
    }

    public void setSearchObj(SentEmails searchObj) {
        this.searchObj = searchObj;
    }

    public int getRowLimit() {
        return rowLimit;
    }

    public void setRowLimit(int rowLimit) {
        this.rowLimit = rowLimit;
    }

    public String getPaginator() {
        return paginator;
    }

    public void setPaginator(String paginator) {
        this.paginator = paginator;
    }

    public List<SelectItem> getRowCountList() {
        return rowCountList;
    }

    public void setRowCountList(List<SelectItem> rowCountList) {
        this.rowCountList = rowCountList;
    }

    public List<SelectItem> getUsersSelection() {
        return usersSelection;
    }

    public void setUsersSelection(List<SelectItem> usersSelection) {
        this.usersSelection = usersSelection;
    }

    public List<SentEmailsDocs> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SentEmailsDocs> documents) {
        this.documents = documents;
    }
}
