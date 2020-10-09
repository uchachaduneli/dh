/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.SentSMSes;
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
@ManagedBean(name = "smsBean")
@ViewScoped
public class SmsBean implements Serializable {

    private List<SentSMSes> smsList;
    private SentSMSes slctedSms = new SentSMSes();
    private SentSMSes searchObj = new SentSMSes();
    private int rowLimit = 15;
    private String paginator;
    private List<SelectItem> rowCountList;

    public SmsBean() {
        if (Util.getSessionParameter("userId") != null) {
            if (Util.getSessionParameter("to") != null) {
                searchObj.setTo((String) Util.getSessionParameter("to"));
            }
            if (Util.getSessionParameter("text") != null) {
                searchObj.setText((String) Util.getSessionParameter("text"));
            }
            if (Util.getSessionParameter("status") != null) {
                searchObj.setStatus((Integer) Util.getSessionParameter("status"));
            }
            if (Util.getSessionParameter("date_filter_start") != null) {
                searchObj.setSendDateStart((Date) Util.getSessionParameter("date_filter_start"));
            }
            if (Util.getSessionParameter("date_filter_end") != null) {
                searchObj.setSendDateEnd((Date) Util.getSessionParameter("date_filter_end"));
            }
            if (Util.getSessionParameter("company") != null) {
                searchObj.setLeadData((String) Util.getSessionParameter("company"));
            }
            loadRowCountList();
            init();
        } else {
            Util.logout();
        }
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
            fullCount = DbProcessing.getSmsesCount(searchObj);
        } catch (Exception e) {
        }

        smsList = DbProcessing.getSmses(searchObj, start, rowLimit);

        paginator = Util.paginator(fullCount, p, rowLimit, Util.getURLFullPath() + "&ns=1", 3);
        if (fullCount == 0) {
            paginator = Util.paginator(fullCount, p, rowLimit, Constants.projectPath + "/sms.jsf?", 3);
            if (fullCount == 0) {
                smsList = new ArrayList<SentSMSes>();
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

    public void filterStatus() {
        try {
            Util.setSessionParameter("status", searchObj.getStatus());
        } catch (Exception e) {
        }
    }

    public void filterByTo() {
        try {
            Util.setSessionParameter("to", searchObj.getTo());
        } catch (Exception e) {
        }
    }

    public void filterByCompany() {
        try {
            Util.setSessionParameter("company", searchObj.getLeadData());
        } catch (Exception e) {
        }
    }

    public void filterByText() {
        try {
            Util.setSessionParameter("text", searchObj.getText());
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

    public List<SentSMSes> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<SentSMSes> smsList) {
        this.smsList = smsList;
    }

    public SentSMSes getSlctedSms() {
        return slctedSms;
    }

    public void setSlctedSms(SentSMSes slctedSms) {
        this.slctedSms = slctedSms;
    }

    public SentSMSes getSearchObj() {
        return searchObj;
    }

    public void setSearchObj(SentSMSes searchObj) {
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
}
