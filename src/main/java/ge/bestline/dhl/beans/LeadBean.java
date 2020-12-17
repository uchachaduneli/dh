package ge.bestline.dhl.beans;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.*;
import ge.bestline.dhl.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author ucha
 */
@ManagedBean(name = "leadBean")
@ViewScoped
public class LeadBean implements Serializable {

    private List<Lead> leads;
    private Lead slctedLead;
    private User loginedUser;
    private List<SelectItem> departmentList;
    private List<SelectItem> interstDepartmentList;
    private List<Department> departments;
    private Department selectedDepartment;
    private boolean edit;
    private boolean callAlert;
    private boolean payAlert;
    private boolean emptyIdent;
    private int leadStatusId;
    private List<User> workers;
    private List<String> interestedDepartments;
    private Document slcedDoc;
    private boolean activeStatus;
    private boolean hasPermissionChooser;
    private List<SelectItem> contrStatus = new ArrayList<SelectItem>();
    private List<SelectItem> alertFilter = new ArrayList<SelectItem>();
    private List<SelectItem> limitedFilter = new ArrayList<SelectItem>();
    private List<SelectItem> usersSelection = new ArrayList<SelectItem>();
    private String existingLeadIdentCode;
    private Lead searchLead = new Lead();
    private int rowLimit = 15;
    private int expiredAlert;
    private int limitedAlert;
    private String paginator;
    private List<SelectItem> rowCountList;
    private Integer salePercent;
    private Double limitDecreaseAmount;
    private LimitHistory slctedLeadsLimit;
    private List<LimitHistory> leadsLimitHitoryList;
    private NewSmsOrMailOpts contaction;
    private String slctedAttachName;
    private static final Logger logger = LogManager.getLogger(LeadBean.class);
    private int currentUserId;
    private int checkSmsOrMail;
    private String smsOrEmailConrifmCode;

    public LeadBean() {
        currentUserId = (int) Util.getSessionParameter("userId");
        if (currentUserId > 0) {
            if (Util.getSessionParameter("davalianeba") != null) {
                searchLead.setDavalianeba((Integer) Util.getSessionParameter("davalianeba"));
            }
            if (Util.getSessionParameter("department_id") != null) {
                searchLead.getDepartment().setDepartmentId((Integer) Util.getSessionParameter("department_id"));
            }
            if (Util.getSessionParameter("saleOrAdmin_id") != null) {
                searchLead.setSaleOrAdminId((Integer) Util.getSessionParameter("saleOrAdmin_id"));
            }
            if (Util.getSessionParameter("operator_id") != null) {
                searchLead.setOperatorId((Integer) Util.getSessionParameter("operator_id"));
            }
            if (Util.getSessionParameter("expired_alert") != null) {
                expiredAlert = (Integer) Util.getSessionParameter("expired_alert");
            }
            if (Util.getSessionParameter("limited_alert") != null) {
                limitedAlert = (Integer) Util.getSessionParameter("limited_alert");
            }
            if (Util.getSessionParameter("date_filter_start") != null) {
                searchLead.setContractStart((Date) Util.getSessionParameter("date_filter_start"));
            }
            if (Util.getSessionParameter("date_filter_end") != null) {
                searchLead.setContractEnd((Date) Util.getSessionParameter("date_filter_end"));
            }
            try {
                loginedUser = DbProcessing.getUsers(((Integer) Util.getSessionParameter("userId")), "a", "a").get(0);
                if (loginedUser.getId() == 20) {
                    hasPermissionChooser = true;
                }
            } catch (Exception e) {
                logger.error("Can't get Logined User's data from DB", e);
                Util.logout();
                return;
            }
            if (Util.getSessionParameter("call_alert") != null) {
                if (((Integer) Util.getSessionParameter("call_alert")) == 1) {
                    callAlert = true;
                } else {
                    callAlert = false;
                }
            }
            if (Util.getSessionParameter("pay_alert") != null) {
                if (((Integer) Util.getSessionParameter("pay_alert")) == 1) {
                    payAlert = true;
                } else {
                    payAlert = false;
                }
            }
            if (Util.getSessionParameter("empty_ident") != null) {
                if (((Integer) Util.getSessionParameter("empty_ident")) == 1) {
                    emptyIdent = true;
                } else {
                    emptyIdent = false;
                }
            }

            slctedLead = new Lead();
            selectedDepartment = new Department();
            departmentList = new ArrayList<SelectItem>();
            interstDepartmentList = new ArrayList<SelectItem>();
            leads = new ArrayList<Lead>();
            if (((Integer) Util.getSessionParameter("user_type_id")) == 1) {
                leadStatusId = 1;
            } else {
                leadStatusId = Util.getParam("active");
            }
            loadRowCountList();
            loadDeparts();
            init();
        } else {
            Util.logout();
        }

    }

    public void sendConfirmSMSOrMail() {
        if (smsOrEmailConrifmCode == null || smsOrEmailConrifmCode.length() == 0 || checkSmsOrMail == 0) {
            Messages.warn("მიუთითეთ აქტივაციის ტიპი და კოდი");
            return;
        }
        String res = DbProcessing.confirmSMSOrEmail(smsOrEmailConrifmCode, checkSmsOrMail == 1);
        if (res == null) {
            Messages.warn("მითითებული მონაცემებით ჩანაწერი ვერ მოიძებნა");
        } else {
            Messages.info("Confirmed " + res);

        }
        Util.executeScript("checkDLGwidg.hide()");
        smsOrEmailConrifmCode = null;
        checkSmsOrMail = 0;
    }

    public void redirectToEmails() {
        RequestContext.getCurrentInstance().execute("window.open('emails.jsf?leadId=" + slctedLead.getLeadId() + "', '_newtab')");
    }

    public void redirectToPhones() {
        RequestContext.getCurrentInstance().execute("window.open('phones.jsf?leadId=" + slctedLead.getLeadId() + "', '_newtab')");
    }

    private List<Emails> getLeadEmails(Integer leadId) {
        Map<String, List<Emails>> res = DbProcessing.getLeadEmails(leadId);
        Map.Entry<String, List<Emails>> entr = res.entrySet().iterator().next();
        return entr.getValue();
    }

    public void sendSmsOrMail() {
        if (contaction.getSmsOrMail() == 0) {
            Messages.warn("მიუთითეთ გაგზავნის მეთოდი");
            return;
        }
        if (contaction.getSmsOrMailToSrchedOrOne() == 0) {
            Messages.warn("მიუთითეთ ადრესატის ტიპი");
            return;
        }
        if (contaction.getSmsOrMail() == 2) {//Sending Using Email
            List<SentEmails> sentEmails = new ArrayList<>();
            try {
                if (contaction.getSmsOrMailToSrchedOrOne() == 1) {// ხელით უთითებს ვისაც მიუვიდეს MAIL
                    DhlMail.sendEmail(contaction.getSmsOrMailTo().trim().
                                    replaceAll(";", ",").
                                    replaceAll("\\s", ""),
                            contaction.getSmsOrMailSubject(), contaction.getSmsOrMailText(), contaction.getAttachmentsPath());
                } else {
                    if (contaction.getSmsOrMailToSrchedOrOne() == 2) {// დასერჩილ ქეისებს უგზავნის MAIL
                        init();
                        for (Lead lead : leads) {
                            getLeadEmails(lead.getLeadId()).stream().forEach(email -> {
                                try {
                                    DhlMail.sendEmail(email.getMail(),
                                            contaction.getSmsOrMailSubject(), contaction.getSmsOrMailText(), contaction.getAttachmentsPath());
                                    sentEmails.add(new SentEmails(email.getId(), contaction.getSmsOrMailSubject(), contaction.getSmsOrMailText(), 1));
                                } catch (Exception e) {
                                    sentEmails.add(new SentEmails(email.getId(), contaction.getSmsOrMailSubject(), contaction.getSmsOrMailText(), 2));
                                    logger.error("Can't send email To: " + email.getMail(), e);
                                }
                            });

                        }
                        if (!sentEmails.isEmpty()) {
                            DbProcessing.insertEmailHistory(sentEmails, currentUserId);
                        }
                    }
                }
                Util.executeScript("smsDLGwidg.hide();");
                Messages.info("შეტყობინება გაგზავნილია");


            } catch (Exception e) {
                logger.error("Can't send email", e);
                Messages.error("მეილის გაგზავნა ვერ მოხერხდა");
            }
        } else {// Sending Using SMS
            Map<Integer, List<String>> numbers = new HashMap<Integer, List<String>>();
            int sentsCount = 0;
            int failedSentsCount = 0;
            if (contaction.getSmsOrMailToSrchedOrOne() == 1) {// ხელით უთითებს ვისაც მიუვიდეს SMS
                String[] tmpNums = contaction.getSmsOrMailTo().trim().
                        replaceAll(";", ",").
                        replaceAll("\\s", "").
                        split(",");
                List<String> nuList = new ArrayList<String>();
                for (String num : tmpNums) {
                    nuList.add(num.startsWith("5") ? "995" + num : num);
                }
                numbers.put(null, nuList);
                try {
                    DhlSMS.sendSms(contaction.getSmsOrMailText(), numbers, currentUserId);
                } catch (Exception e) {
                    failedSentsCount++;
                    logger.error("Sending SMSes Failed", e);
                }
            } else {
                if (contaction.getSmsOrMailToSrchedOrOne() == 2) {// დასერჩილ ქეისებს უგზავნის SMS
                    init();
                    for (Lead l : leads) {
                        numbers = new HashMap<>();
                        List<String> numsList = new ArrayList<>();
                        List<PhoneNumbers> tmpLeadNumsList = DbProcessing.getLeadPhoneNums(l.getLeadId(), true)
                                .entrySet().stream()
                                .findAny().get().getValue();
                        tmpLeadNumsList.stream().forEach(numObj -> {
                            if (numObj.getPhoneNum().startsWith("5"))
                                numObj.setPhoneNum("995" + numObj.getPhoneNum());
                            numsList.add(numObj.getPhoneNum());
                        });
                        numbers.put(l.getLeadId(), numsList);
                        try {
                            DhlSMS.sendSms(contaction.getSmsOrMailText(), numbers, currentUserId);
                            sentsCount++;
                        } catch (Exception e) {
                            failedSentsCount++;
                            logger.error("Error During Sending SMS for LeadId: " + l.getLeadId(), e);
                        }
                    }
                }
            }
            Util.executeScript("smsDLGwidg.hide();");
            if (failedSentsCount == 0) {
                Messages.info("შეტყობინება გაგზავნილია კომპანიის(ების) მობილურ ნომრებზე");
            } else {
                Messages.error(failedSentsCount + " კომპანიისთვის შეტყობინების გაგზავნა ვერ მოხერხდა, გთხოვთ გადაამოწმოთ მობილური ნომრების ვალიდურობა");
            }
        }
    }


    public void loadLeadDepartList() {
        slctedLead.setDepartments(DbProcessing.getLeadDepartments(slctedLead.getLeadId()));
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

    private void loadDeparts() {
        if (departmentList.isEmpty()) {
            departments = DbProcessing.getDepartments(0);
            departmentList.add(new SelectItem("", "დეპარტ."));
            for (Department d : departments) {
                departmentList.add(new SelectItem(d.getDepartmentId(), d.getDepartmentName()));
                interstDepartmentList.add(new SelectItem(d.getDepartmentId(), d.getDepartmentName()));
            }
        }
        if (contrStatus.isEmpty()) {
            contrStatus.add(new SelectItem("", "დავალიანება"));
            contrStatus.add(new SelectItem("1", Util.ka("ki")));
            contrStatus.add(new SelectItem("2", Util.ka("ara")));
        }
        if (alertFilter.isEmpty()) {
            alertFilter.add(new SelectItem("", "ვადაგას."));
            alertFilter.add(new SelectItem("1", Util.ka("ki")));
            alertFilter.add(new SelectItem("2", Util.ka("ara")));
        }

        if (limitedFilter.isEmpty()) {
            limitedFilter.add(new SelectItem("", "ლიმიტ."));
            limitedFilter.add(new SelectItem("1", Util.ka("ki")));
            limitedFilter.add(new SelectItem("2", Util.ka("ara")));
        }

        if (usersSelection.isEmpty()) {
            loadWorkers();
            usersSelection.add(new SelectItem("", "თანამშრ."));
            for (User user : workers) {
                usersSelection.add(new SelectItem(user.getId(), user.getDescription()));
            }
        }
    }

    public void loadWorkers() {
        if (workers != null && !workers.isEmpty()) {
            workers.clear();
        } else {
            workers = new ArrayList<User>();
        }
        workers = DbProcessing.getUsers(0, "", "");
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(cellStyle);
        }
        slctedLead = new Lead();
    }

    public void handleActivesFilter() {
        //        if (activeStatus) {
        //            leadStatusId = 1;
        //        } else {
        //            leadStatusId = 2;
        //        }
        Util.redirect("/home.jsf?active=" + leadStatusId + "");
        //        slctedLead = new Lead();
        //        init();
        //        RequestContext.getCurrentInstance().update("@(#operatorLeadsTableId)");
    }

    public void deleteLead() {
        try {
            slctedLead.setLeadId(-slctedLead.getLeadId());
            if (DbProcessing.actionLeads(slctedLead) > 0) {
                //            leads.remove(slctedLead);
                init();
                Messages.info("ოპერაცია დასრულდა წარმატებით");
                RequestContext.getCurrentInstance().update("@(#operHomeForm)");
                Util.executeScript("confirmOfLeadDeletion.hide();");
            } else {
                Messages.error("მონაცემის წაშლა ვერ ხერხდება");
            }
        } catch (Exception e) {
            Messages.warn("ჩანაწერი მონიშნული არაა");
        }

    }

    public void validateFromDateFilter() {
        if (searchLead != null) {
            if (searchLead.getContractEnd() == null) {
                searchLead.setContractEnd(searchLead.getContractStart());
            } else {
                if (searchLead.getContractEnd() != null && !searchLead.getContractStart().before(searchLead.getContractEnd())) {
                    Messages.warn(Util.ka("gTxovT sworad miuTiToT droiTi Sualedi"));
                } else if (searchLead.getContractEnd() != null) {
                    validateToDateFilter();
                }
            }
            try {
                if (searchLead.getContractStart() != null && searchLead.getContractEnd() != null) {
                    Util.setSessionParameter("date_filter_start", searchLead.getContractStart());
                    Util.setSessionParameter("date_filter_end", searchLead.getContractEnd());
                }
            } catch (Exception ex) {
            }
        }
    }

    public void resetDateFilter() {
        Util.removeSessionAttribute("date_filter_start");
        Util.removeSessionAttribute("date_filter_end");
        searchLead.setContractStart(null);
        searchLead.setContractEnd(null);
    }

    public void validateToDateFilter() {
        if (searchLead != null) {
            if (searchLead.getContractStart() != null && searchLead.getContractEnd() != null) {
                if (!searchLead.getContractStart().before(searchLead.getContractEnd())) {
                    Messages.warn(Util.ka("gTxovT sworad miuTiToT droiTi Sualedi"));
                    return;
                }
            } else {
                Messages.warn(Util.ka("gTxovT miuTiToT vadis sawyisi TariRi"));
                return;
            }
            try {
                if (searchLead.getContractStart() != null && searchLead.getContractEnd() != null) {
                    Util.setSessionParameter("date_filter_start", searchLead.getContractStart());
                    Util.setSessionParameter("date_filter_end", searchLead.getContractEnd());
                } else if (searchLead.getContractStart() == null) {
                    Util.removeSessionAttribute("date_filter_start");
                } else if (searchLead.getContractEnd() == null) {
                    Util.removeSessionAttribute("date_filter_end");
                }
            } catch (Exception ex) {
            }
        }
    }

    public void validateInsertFromDate() {
        if (slctedLead != null) {
            if (slctedLead.getContractEnd() != null && !slctedLead.getContractStart().before(slctedLead.getContractEnd())) {
                Messages.warn(Util.ka("gTxovT sworad miuTiToT droiTi Sualedi"));
            } else if (slctedLead.getContractEnd() != null) {
                validateContrDaies();
            }
        }
    }

    public void validateContrDaies() {
        if (slctedLead != null) {
            if (slctedLead.getContractStart() != null && slctedLead.getContractEnd() != null) {
                SimpleDateFormat simpleDateformat = new SimpleDateFormat("yyyy");
                if (!slctedLead.getContractStart().before(slctedLead.getContractEnd())) {
                    Messages.warn(Util.ka("gTxovT sworad miuTiToT droiTi Sualedi"));
                    return;
                }
            } else {
                Messages.warn(Util.ka("gTxovT miuTiToT kontraqtis vadis sawyisi TariRi"));
            }
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
        fullCount = DbProcessing
                .getLeadsCount(searchLead, 0, loginedUser.getId(), leadStatusId, expiredAlert, limitedAlert, callAlert, payAlert,
                        emptyIdent);

        leads = DbProcessing
                .getLeads(searchLead, 0, loginedUser.getId(), leadStatusId, start, rowLimit, expiredAlert, limitedAlert, callAlert,
                        payAlert, emptyIdent);

        paginator = Util.paginator(fullCount, p, rowLimit, Util.getURLFullPath() + "&ns=1", 3);
        if (fullCount == 0) {
            paginator = Util.paginator(fullCount, p, rowLimit, Constants.projectPath + "/home.jsf?", 3);
            if (fullCount == 0) {
                leads = new ArrayList<Lead>();
            }
        }
    }

    public void selectCallAlerts() {
        try {
            Util.setSessionParameter("call_alert", callAlert ? 1 : 0);
        } catch (Exception e) {
            Messages.warn(e.getMessage());
            return;
        }
        if (!leads.isEmpty()) {
            leads.clear();
        }
        init();
    }

    public void selectPayAlerts() {
        try {
            Util.setSessionParameter("pay_alert", payAlert ? 1 : 0);
        } catch (Exception e) {
            Messages.warn(e.getMessage());
            return;
        }
        if (!leads.isEmpty()) {
            leads.clear();
        }
        init();
    }

    public void selectEmptyIdents() {
        try {
            Util.setSessionParameter("empty_ident", emptyIdent ? 1 : 0);
        } catch (Exception e) {
            Messages.warn(e.getMessage());
            return;
        }
        if (!leads.isEmpty()) {
            leads.clear();
        }
        init();
    }

    public void initNewContaction() {
        contaction = new NewSmsOrMailOpts();
    }

    public void warnForFilteredAdressats() {
        if (contaction.getSmsOrMailToSrchedOrOne() == 2)
            Messages.warn("გაგზავნის დაწყებამდე დაფილტრეთ სასურველი ჩანაწერები !!!");
    }

    public void initDialog(long id) {
        edit = id == 1;
        if (!edit) {
            slctedLead = new Lead();
            slctedLead.setAnswerId(1);
            slctedLead.setTermType(2);
            slctedLead.setLimited(2);
            slctedLead.setDavalianeba(2);
        }
    }

    private String upload(UploadedFile file, String dirName) throws Exception {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName =
                file.getFileName().substring(0, file.getFileName().lastIndexOf('.') - 1) + "_" + fmt.format(new Date()) + file.getFileName()
                        .substring(file.getFileName().lastIndexOf('.'));
        File dir = new File(dirName);
        if (dir.isDirectory() == false) {
            dir.mkdirs();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new Exception(Util.ka("direqtoria ver Seiqmna"));
                }
            }
        }
        File files = new File(dirName + "/" + fileName);
        OutputStream out;
        InputStream is = file.getInputstream();
        out = new FileOutputStream(files);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        is.close();
        out.close();
        return fileName;
    }

    public void uploadAttachments(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            String dirName = Constants.uploadPath;
            String fileName = this.upload(file, dirName);
            if (fileName == null || fileName.equals("")) {
                throw new Exception("ფაილის მიმაგრება ვერ ხერხდება");
            }
            contaction.getAttachmentsPath().add(fileName);
            Messages.info("ფაილის მიმაგრება დასრულდა წარმატებით");
        } catch (Exception e) {
            Messages.error(e.getMessage());
        }

    }

    public void uploadContract(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            String dirName = Constants.uploadPath;
            String fileName = this.upload(file, dirName);
            if (fileName == null || fileName.equals("")) {
                throw new Exception(Util.ka("xelSekrulebis atvirTva ver xerxdeba!"));
            }
            Document doc = new Document();
            doc.setDocumentName(fileName);
            slctedLead.getDocuments().add(doc);
            Messages.info(Util.ka("xelSekrulebis atvirTva dasrulda warmatebiT"));
            RequestContext.getCurrentInstance().update("@(#newSealgrowlid)");
        } catch (Exception e) {
            Messages.error(e.getMessage());
        }

    }

    public void saveLimitHistory() {
        LimitHistory obj = new LimitHistory(slctedLead.getLeadId(), limitDecreaseAmount, slctedLead.getMaxLimitAmount());
        if (DbProcessing.actionLeadsLimitHistory(obj) == 0) {
            Messages.warn("ოპერაცია არ სრულდება");
        } else {
            Messages.info("ოპერაცია დასრულდა წარმატებით");
            slctedLead.setMaxLimitAmount(slctedLead.getMaxLimitAmount() - limitDecreaseAmount);
        }
        limitDecreaseAmount = 0.0;
    }

    public void saveUpdate() {
        if (edit) {
            slctedLead.setSaleOrAdminId(loginedUser.getId());
        } else {
            slctedLead.setOperatorId(loginedUser.getId());
            if (loginedUser.getTypeId() == 1 || slctedLead.getDocuments().isEmpty()) {
                slctedLead.setSaleOrAdminId(7);
            } else {
                slctedLead.setSaleOrAdminId(loginedUser.getId());
            }
        }
        if (loginedUser.getTypeId() != 1) {
            try {
                if (slctedLead.getCompanyIdentCode().equals("") || slctedLead.getCompanyName().equals("") || slctedLead.getAddress()
                        .equals("")) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Messages.warn(Util.ka("momxmareblis informaciaSi yvela savaldlebulo veli araa Sevsebuli"));
                return;
            }

        }

        if (loginedUser.getTypeId() == 2 && slctedLead.getAnswerId() == 1) {
            try {
                if (slctedLead.getDepartment().getDepartmentId() == 0 || slctedLead.getContractStart() == null || slctedLead.getDocuments()
                        .isEmpty() || slctedLead.getContractEnd() == null) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Messages.warn(Util.ka("gadaamowmeT servisis blokSi savaldlebulo yvela informaciis da dokumentebis arseboba"));
                return;
            }
        }

        if (!edit && interestedDepartments != null && !interestedDepartments.isEmpty() && loginedUser.getTypeId() == 1) {
            if (slctedLead.getIntrstedIndepartments() == null) {
                slctedLead.setIntrstedIndepartments("");
            }

            for (int i = 0; i < interestedDepartments.size(); i++) {
                for (Department d : departments) {
                    if (d.getDepartmentId() == Integer.parseInt(interestedDepartments.get(i))) {
                        slctedLead.setIntrstedIndepartments(slctedLead.getIntrstedIndepartments() + d.getDepartmentName() + " ");
                    }
                }
            }
            slctedLead.setIntrstedIndepartments(slctedLead.getIntrstedIndepartments().trim());
        }

        if (!slctedLead.getDocuments().isEmpty()) {
            slctedLead.setHasContract(1);
        } else {
            slctedLead.setHasContract(2);
        }
        slctedLead.setLeadId(DbProcessing.actionLeads(slctedLead));
        if (slctedLead.getLeadId() == 0) {
            Messages.warn(Util.ka("damateba ver moxerxda gadaamowmeT velebis marTebuloba"));
            return;
        } else {
            //
            if (loginedUser.getTypeId() != 1) {//roca operatori araa dainteresebulebs misabmelad viyeneb
                for (String d : interestedDepartments) {
                    if (DbProcessing.actionLeadDeparts(new Department(Integer.parseInt(d), slctedLead.getLeadId())) == 0) {
                        Messages.warn(Util.ka("departamentis damateba ver moxerxda"));
                        return;
                    }
                }
            }
            //
            for (Document doc : slctedLead.getDocuments()) {
                doc.setLeadId(slctedLead.getLeadId());
                if (DbProcessing.actionLeadDocs(doc, loginedUser.getId()) == 0) {
                    Messages.error(Util.ka("dafiqsirda Secdoma dokumentebis Senaxvis dros"));
                    return;
                }
            }
        }
        if (interestedDepartments != null && !interestedDepartments.isEmpty()) {
            interestedDepartments.clear();
        }
        slctedLead = new Lead();
        Util.executeScript("insertDLGwidg.hide()");
        init();
        Messages.info(Util.ka("operacia dasrulda warmatebiT"));
    }

    public void delWrongAttachment() {
        contaction.getAttachmentsPath().remove(slctedAttachName);
    }

    public void delWrongDoc() {
        slctedLead.getDocuments().remove(slcedDoc);
        slcedDoc.setDocumentId(-slcedDoc.getDocumentId());
        DbProcessing.actionLeadDocs(slcedDoc, loginedUser.getId());
    }

    public void delSailDepart() {
        slctedLead.getDepartments().remove(selectedDepartment);
        selectedDepartment.setLeadDepartmentId(-selectedDepartment.getLeadDepartmentId());
        DbProcessing.actionLeadDeparts(selectedDepartment);
    }

    public void delLimitHistory() {
        leadsLimitHitoryList.remove(slctedLeadsLimit);
        slctedLeadsLimit.setId(-slctedLeadsLimit.getId());
        if (DbProcessing.actionLeadsLimitHistory(slctedLeadsLimit) == 0) {
            Messages.warn("ოპერაცია ვერ სრულდება");
        } else {
            slctedLead.setMaxLimitAmount(slctedLead.getMaxLimitAmount() + slctedLeadsLimit.getDecreaseAmount());
            Messages.info("ოპერაცია დასრულდა წარმატებით");
        }
    }

    public void disableLead() {
        if (DbProcessing.changeLeadStatus(slctedLead.getLeadId(), 2) == 1) {
            leads.remove(slctedLead);
            Messages.info(Util.ka("statusis Secvla dasrulda warmatebiT"));
        } else {
            Messages.warn(Util.ka("statusis Secvla ver moxerxda"));
        }
    }

    public void enableLead() {
        if (DbProcessing.changeLeadStatus(slctedLead.getLeadId(), 1) == 1) {
            leads.remove(slctedLead);
            Messages.info(Util.ka("statusis Secvla dasrulda warmatebiT"));
        } else {
            Messages.warn(Util.ka("statusis Secvla ver moxerxda"));
        }
    }

    public void loadExistingLeadData() {
        slctedLead = DbProcessing.getExistingLead(existingLeadIdentCode);
        if (slctedLead == null) {
            slctedLead = new Lead();
            Messages.warn(Util.ka("aseTi Canaweri ver moiZebna"));
        }
    }

    public void loadLeadLimitHistory() {
        leadsLimitHitoryList = DbProcessing.getLeadsLimitHistory(slctedLead.getLeadId());
        if (slctedLead == null) {
            slctedLead = new Lead();
            Messages.warn(Util.ka("aseTi Canaweri ver moiZebna"));
        }
    }

    public void rowsCountChangeListener() {
        init();
        try {
            Util.setSessionParameter("row_limit", rowLimit);
        } catch (Exception e) {
        }
    }

    public void filterDavalianeba() {
        try {
            Util.setSessionParameter("davalianeba", searchLead.getDavalianeba());
        } catch (Exception e) {
        }
    }

    public void filterDepartment() {
        try {
            Util.setSessionParameter("department_id", searchLead.getDepartment().getDepartmentId());
        } catch (Exception e) {
        }
    }

    public void filterSaleOrAdmin() {
        try {
            Util.setSessionParameter("saleOrAdmin_id", searchLead.getSaleOrAdminId());
        } catch (Exception e) {
        }
    }

    public void filterOperator() {
        try {
            Util.setSessionParameter("operator_id", searchLead.getOperatorId());
        } catch (Exception e) {
        }
    }

    public void filterAlert() {
        try {
            Util.setSessionParameter("expired_alert", expiredAlert);
        } catch (Exception e) {
        }
    }

    public void filterLimitedAlert() {
        try {
            Util.setSessionParameter("limited_alert", limitedAlert);
        } catch (Exception e) {
        }
    }

    public boolean isHasPermissionChooser() {
        return hasPermissionChooser;
    }

    public void setHasPermissionChooser(boolean hasPermissionChooser) {
        this.hasPermissionChooser = hasPermissionChooser;
    }

    public boolean isCallAlert() {
        return callAlert;
    }

    public void setCallAlert(boolean callAlert) {
        this.callAlert = callAlert;
    }

    public List<SelectItem> getLimitedFilter() {
        return limitedFilter;
    }

    public void setLimitedFilter(List<SelectItem> limitedFilter) {
        this.limitedFilter = limitedFilter;
    }

    public int getLimitedAlert() {
        return limitedAlert;
    }

    public void setLimitedAlert(int limitedAlert) {
        this.limitedAlert = limitedAlert;
    }

    public Integer getSalePercent() {
        return salePercent;
    }

    public void setSalePercent(Integer salePercent) {
        searchLead.getDepartment().setSalePercent(salePercent);
        this.salePercent = salePercent;
    }

    public Lead getSearchLead() {
        return searchLead;
    }

    public void setSearchLead(Lead searchLead) {
        this.searchLead = searchLead;
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

    public int getExpiredAlert() {
        return expiredAlert;
    }

    public void setExpiredAlert(int expiredAlert) {
        this.expiredAlert = expiredAlert;
    }

    public List<SelectItem> getAlertFilter() {
        return alertFilter;
    }

    public void setAlertFilter(List<SelectItem> alertFilter) {
        this.alertFilter = alertFilter;
    }

    public String getExistingLeadIdentCode() {
        return existingLeadIdentCode;
    }

    public void setExistingLeadIdentCode(String existingLeadIdentCode) {
        this.existingLeadIdentCode = existingLeadIdentCode;
    }

    public List<SelectItem> getUsersSelection() {
        return usersSelection;
    }

    public void setUsersSelection(List<SelectItem> usersSelection) {
        this.usersSelection = usersSelection;
    }

    public List<SelectItem> getContrStatus() {
        return contrStatus;
    }

    public void setContrStatus(List<SelectItem> contrStatus) {
        this.contrStatus = contrStatus;
    }

    public boolean isActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(boolean activeStatus) {
        this.activeStatus = activeStatus;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    public Document getSlcedDoc() {
        return slcedDoc;
    }

    public void setSlcedDoc(Document slcedDoc) {
        this.slcedDoc = slcedDoc;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public int getLeadStatusId() {
        return leadStatusId;
    }

    public void setLeadStatusId(int leadStatusId) {
        this.leadStatusId = leadStatusId;
    }

    public List<String> getInterestedDepartments() {
        return interestedDepartments;
    }

    public void setInterestedDepartments(List<String> interestedDepartments) {
        this.interestedDepartments = interestedDepartments;
    }

    public List<User> getWorkers() {
        return workers;
    }

    public void setWorkers(List<User> workers) {
        this.workers = workers;
    }

    public User getLoginedUser() {
        return loginedUser;
    }

    public void setLoginedUser(User loginedUser) {
        this.loginedUser = loginedUser;
    }

    public Lead getSlctedLead() {
        if (slctedLead == null) {
            slctedLead = new Lead();
        }
        return slctedLead;
    }

    public void setSlctedLead(Lead slctedLead) {
        this.slctedLead = slctedLead;
    }

    public List<Lead> getLeads() {
        return leads;
    }

    public void setLeads(List<Lead> leads) {
        this.leads = leads;
    }

    public List<SelectItem> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<SelectItem> departmentList) {
        this.departmentList = departmentList;
    }

    public Department getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public List<SelectItem> getInterstDepartmentList() {
        return interstDepartmentList;
    }

    public void setInterstDepartmentList(List<SelectItem> interstDepartmentList) {
        this.interstDepartmentList = interstDepartmentList;
    }

    public boolean isPayAlert() {
        return payAlert;
    }

    public void setPayAlert(boolean payAlert) {
        this.payAlert = payAlert;
    }

    public boolean isEmptyIdent() {
        return emptyIdent;
    }

    public void setEmptyIdent(boolean emptyIdent) {
        this.emptyIdent = emptyIdent;
    }

    public List<LimitHistory> getLeadsLimitHitoryList() {
        return leadsLimitHitoryList;
    }

    public void setLeadsLimitHitoryList(List<LimitHistory> leadsLimitHitoryList) {
        this.leadsLimitHitoryList = leadsLimitHitoryList;
    }

    public LimitHistory getSlctedLeadsLimit() {
        return slctedLeadsLimit;
    }

    public void setSlctedLeadsLimit(LimitHistory slctedLeadsLimit) {
        this.slctedLeadsLimit = slctedLeadsLimit;
    }

    public Double getLimitDecreaseAmount() {
        return limitDecreaseAmount;
    }

    public void setLimitDecreaseAmount(Double limitDecreaseAmount) {
        this.limitDecreaseAmount = limitDecreaseAmount;
    }

    public NewSmsOrMailOpts getContaction() {
        return contaction;
    }

    public void setContaction(NewSmsOrMailOpts contaction) {
        this.contaction = contaction;
    }

    public String getSlctedAttachName() {
        return slctedAttachName;
    }

    public void setSlctedAttachName(String slctedAttachName) {
        this.slctedAttachName = slctedAttachName;
    }

    public int getCheckSmsOrMail() {
        return checkSmsOrMail;
    }

    public void setCheckSmsOrMail(int checkSmsOrMail) {
        this.checkSmsOrMail = checkSmsOrMail;
    }

    public String getSmsOrEmailConrifmCode() {
        return smsOrEmailConrifmCode;
    }

    public void setSmsOrEmailConrifmCode(String smsOrEmailConrifmCode) {
        this.smsOrEmailConrifmCode = smsOrEmailConrifmCode;
    }
}
