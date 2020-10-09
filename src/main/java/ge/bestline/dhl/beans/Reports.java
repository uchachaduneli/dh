/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.User;
import ge.bestline.dhl.utils.Messages;
import ge.bestline.dhl.utils.Util;
import org.primefaces.model.chart.PieChartModel;

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
@ManagedBean(name = "reportsBean")
@ViewScoped
public class Reports implements Serializable {

    private PieChartModel contrPieModel;
    private PieChartModel byUserPieModel;
    private Date startDate;
    private Date endDate;
    private int selectedUserId;
    private int pieYes = 0;
    private int pieNo = 10;
    private int pieByUserYes = 0;
    private int pieByUserNo = 10;
    private List<SelectItem> users = new ArrayList<SelectItem>();
    private List<User> tempUsers;
    private Integer disagrees;

    public Reports() {
        if (Util.hasAdminOrSalePerms()) {
            tempUsers = DbProcessing.getUsers(0, "", "");
            for (User usr : tempUsers) {
                users.add(new SelectItem(usr.getId(), usr.getDescription()));
            }
            initPie();
        } else {
            Util.redirect("/home.jsf");
        }
    }

    public void loadHitory() {
        if (startDate == null) {
            Messages.warn(Util.ka("gTxovT miuTiToT sawyisi TariRi"));
            return;
        }

        if (endDate == null) {
            Messages.warn(Util.ka("gTxovT miuTiToT saboloo TariRi"));
            return;
        }

        if (endDate.before(startDate)) {
            Messages.warn(Util.ka("gTxovT sworad miuTiToT droiTi Sualedi"));
            return;
        }

        if (selectedUserId == 0) {
            Messages.warn(Util.ka("TanamSromlis miuTiTeblad ixilavT mxolod saerTo angariSs miTiTebul droiT SualedSi"));
        }
        contrPieModel.clear();
        pieYes = DbProcessing.getAllReportInDates(startDate, endDate, 1);
        pieNo = DbProcessing.getAllReportInDates(startDate, endDate, 2);
        contrPieModel.set(Util.ka("xelSekrulebiT (") + pieYes + ")", pieYes);
        contrPieModel.set(Util.ka("uxelSekrulebo (") + pieNo + ")", pieNo);

        if (DbProcessing.getUsers(selectedUserId, "", "").get(0).getTypeId() == 1) {
            pieByUserYes = DbProcessing.getreportByUser(startDate, endDate, 1, true, selectedUserId);
            pieByUserNo = DbProcessing.getreportByUser(startDate, endDate, 2, true, selectedUserId);
        } else {
            pieByUserYes = DbProcessing.getreportByUser(startDate, endDate, 1, false, selectedUserId);
            pieByUserNo = DbProcessing.getreportByUser(startDate, endDate, 2, false, selectedUserId);
        }
        byUserPieModel.clear();
        byUserPieModel.set(Util.ka("xelSekrulebiT (") + pieByUserYes + ")", pieByUserYes);
        byUserPieModel.set(Util.ka("uxelSekrulebo (") + pieByUserNo + ")", pieByUserNo);
        disagrees = DbProcessing.getDisagrees(startDate, endDate);
    }

    public Integer getDisagrees() {
        return disagrees;
    }

    public void setDisagrees(Integer disagrees) {
        this.disagrees = disagrees;
    }

    public PieChartModel getByUserPieModel() {
        return byUserPieModel;
    }

    public void setByUserPieModel(PieChartModel byUserPieModel) {
        this.byUserPieModel = byUserPieModel;
    }

    public List<SelectItem> getUsers() {
        return users;
    }

    public void setUsers(List<SelectItem> users) {
        this.users = users;
    }

    public int getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(int selectedUserId) {
        this.selectedUserId = selectedUserId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    private void initPie() {
        contrPieModel = new PieChartModel();
        contrPieModel.set(Util.ka("xelSekrulebiT"), pieYes);
        contrPieModel.set(Util.ka("uxelSekrulebo"), pieNo);

        byUserPieModel = new PieChartModel();
        byUserPieModel.set(Util.ka("xelSekrulebiT"), pieByUserYes);
        byUserPieModel.set(Util.ka("uxelSekrulebo"), pieByUserNo);
    }

    public PieChartModel getContrPieModel() {
        return contrPieModel;
    }

    public void setContrPieModel(PieChartModel contrPieModel) {
        this.contrPieModel = contrPieModel;
    }
}
