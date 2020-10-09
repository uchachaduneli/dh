package ge.bestline.dhl.pojoes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ucha
 */
public class Lead implements Serializable {

    private int leadId;
    private String companyName;
    private String companyIdentCode;
    private String address;
    private String contactPerson;
    private String phone;
    private int operatorId;
    private int saleOrAdminId;
    private String operatorName;
    private String saleOrAdminName;
    private Date contractStart;
    private Date contractEnd;
    private String strContractStart;
    private String strContractEnd;
    private int hasContract; // 1 tu dadebulia 2 tu araris dadebuli
    private String email;
    private String note;
    private Department department = new Department();
    private String dateofInterest;
    private String intrstedIndepartments;
    private List<Document> documents = new ArrayList<Document>();
    private List<Department> departments = new ArrayList<Department>();
    private int termType;
    private int isActive;//1 actiuria 2 ara
    private String active;//aqtiuri ki/ara
    private String hasContr;
    private String serviceType;
    private int checkLead;//alerti tuki 1 -ia
    private int answerId;//undat 1 ar undat 2
    private int limited;// 1-ki; 2-ara
    private int davalianeba;//1-ki; 2-ara
    private int limitedAlarm;//1-ki; 2-ara
    private String contractCode;
    private Date nextCall;
    private int callAlert;
    private int payAlert;
    private int permissionId;
    private Double maxLimitAmount;

    public Lead() {
        documents = new ArrayList<Document>();
        department = new Department();
    }

    public Double getMaxLimitAmount() {
        return maxLimitAmount;
    }

    public void setMaxLimitAmount(Double maxLimitAmount) {
        this.maxLimitAmount = maxLimitAmount;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public int getPayAlert() {
        return payAlert;
    }

    public void setPayAlert(int payAlert) {
        this.payAlert = payAlert;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public int getCallAlert() {
        return callAlert;
    }

    public void setCallAlert(int callAlert) {
        this.callAlert = callAlert;
    }

    public Date getNextCall() {
        return nextCall;
    }

    public void setNextCall(Date nextCall) {
        this.nextCall = nextCall;
    }

    public String getContractCode() {
        return contractCode;
    }

    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
    }

    public int getLimitedAlarm() {
        return limitedAlarm;
    }

    public void setLimitedAlarm(int limitedAlarm) {
        this.limitedAlarm = limitedAlarm;
    }

    public int getLimited() {
        return limited;
    }

    public void setLimited(int limited) {
        this.limited = limited;
    }

    public int getDavalianeba() {
        return davalianeba;
    }

    public void setDavalianeba(int davalianeba) {
        this.davalianeba = davalianeba;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getCheckLead() {
        return checkLead;
    }

    public void setCheckLead(int checkLead) {
        this.checkLead = checkLead;
    }

    public String getHasContr() {
        return hasContr;
    }

    public void setHasContr(String hasContr) {
        this.hasContr = hasContr;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getStrContractStart() {
        return strContractStart;
    }

    public void setStrContractStart(String strContractStart) {
        this.strContractStart = strContractStart;
    }

    public String getStrContractEnd() {
        return strContractEnd;
    }

    public void setStrContractEnd(String strContractEnd) {
        this.strContractEnd = strContractEnd;
    }

    public String getDateofInterest() {
        return dateofInterest;
    }

    public void setDateofInterest(String dateofInterest) {
        this.dateofInterest = dateofInterest;
    }

    public String getIntrstedIndepartments() {
        return intrstedIndepartments;
    }

    public void setIntrstedIndepartments(String intrstedIndepartments) {
        this.intrstedIndepartments = intrstedIndepartments;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHasContract() {
        return hasContract;
    }

    public void setHasContract(int hasContract) {
        this.hasContract = hasContract;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyIdentCode() {
        return companyIdentCode;
    }

    public void setCompanyIdentCode(String companyIdentCode) {
        this.companyIdentCode = companyIdentCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }

    public int getSaleOrAdminId() {
        return saleOrAdminId;
    }

    public void setSaleOrAdminId(int saleOrAdminId) {
        this.saleOrAdminId = saleOrAdminId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getSaleOrAdminName() {
        return saleOrAdminName;
    }

    public void setSaleOrAdminName(String saleOrAdminName) {
        this.saleOrAdminName = saleOrAdminName;
    }

    public Date getContractStart() {
        return contractStart;
    }

    public void setContractStart(Date contractStart) {
        this.contractStart = contractStart;
    }

    public Date getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(Date contractEnd) {
        this.contractEnd = contractEnd;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public int getTermType() {
        return termType;
    }

    public void setTermType(int termType) {
        this.termType = termType;
    }

}
