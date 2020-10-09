package ge.bestline.dhl.pojoes;

import java.io.Serializable;

/**
 * @author ninichka
 */
public class Department implements Serializable {

    private int departmentId;
    private int leadId;
    private String departmentName;
    private String accountNumber;
    private int salePercent;
    private int leadDepartmentId;

    public Department(int departmentId, int leadId) {
        this.departmentId = departmentId;
        this.leadId = leadId;
    }

    public Department() {
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(int salePercent) {
        this.salePercent = salePercent;
    }

    public int getLeadDepartmentId() {
        return leadDepartmentId;
    }

    public void setLeadDepartmentId(int leadDepartmentId) {
        this.leadDepartmentId = leadDepartmentId;
    }

}
