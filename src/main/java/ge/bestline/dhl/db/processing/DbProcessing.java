package ge.bestline.dhl.db.processing;

import com.mysql.jdbc.CallableStatement;
import ge.bestline.dhl.db.connection.DBConnection;
import ge.bestline.dhl.db.connection.DhlCon;
import ge.bestline.dhl.db.connection.InvoiceCon;
import ge.bestline.dhl.pojoes.*;
import ge.bestline.dhl.utils.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 * @author Ucha Chaduneli
 */
public class DbProcessing implements Serializable {
    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private static final Logger logger = LogManager.getLogger(DbProcessing.class);

    /*
     * tu userId -1 gadavcem pirvel parametrad amowmebs usernami - passwordit
     * tu 0 yvela moaqvs
     * tu rame useris aidis gadavmcem am aidis mqone users wamoigebs danarchen parametrebs ikidebs
     */
    public static List<User> getUsers(int userId, String username, String password) {
        List<User> users = new ArrayList<User>();
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_user(?,?,?)}");
            cs.setString(1, username);
            cs.setString(2, password != null ? Util.MD5(password) : null);
            cs.setInt(3, userId);
            ResultSet res = cs.executeQuery();
            User user;
            while (res.next()) {
                user = new User();
                user.setId(res.getInt("user_id"));
                user.setUserName(res.getString("user_name"));
                user.setDescription(res.getString("user_description"));
                user.setPassword(res.getString("user_password"));
                user.setStatusId(res.getInt("status_id"));
                user.setTypeId(res.getInt("type_id"));
                user.setTypeName(res.getString("user_type_name"));
                user.setInvTypeId(res.getInt("inv_type_id"));
                user.setInvTypeName(res.getString("inv_user_type_name"));
                user.setInventarAccess(res.getInt("inv_access") == 1 ? true : false);
                users.add(user);
            }
            return users;
        } catch (Exception ex) {
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static List<SelectItem> getUserTypes(int typeId) {
        List<SelectItem> types = new ArrayList<SelectItem>();
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_user_types(?)}");
            cs.setInt(1, typeId);
            ResultSet res = cs.executeQuery();
            SelectItem type;
            while (res.next()) {
                type = new SelectItem(res.getInt("user_type_id"), res.getString("user_type_name"));
                types.add(type);
            }
            return types;
        } catch (SQLException ex) {
            logger.error("Can't retrieve user types", ex);
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static int checkUserNameAvailability(String userName) {
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            ResultSet res = cs.executeQuery("select user_id from users where user_name='" + userName.trim() + "'");
            int yes = 0;
            while (res.next()) {
                yes = 1;
            }
            return yes;
        } catch (SQLException exception) {
            return 0;
        }
    }

    public static Map<String, List<Emails>> getLeadEmails(int leadId) {
        Map<String, List<Emails>> map = new HashMap<String, List<Emails>>();
        List<Emails> list = new ArrayList<Emails>();
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            ResultSet res = cs.executeQuery("select * from emails where lead_id='" + leadId + "'");
            while (res.next()) {
                list.add(new Emails(res.getInt("id"),
                                res.getString("mail"),
                                res.getInt("lead_id"),
                                res.getInt("confirmed"),
                                res.getString("note"),
                                res.getString("activation_code"),
                                dateFormat.format(res.getTimestamp("create_date")),
                                res.getInt("dhl_db"),
                                res.getInt("invoice_db")
                        )
                );
            }
            res = cs.executeQuery("select concat(company_name,'(#', company_id_number, ')') from leads where lead_id='" + leadId + "'");
            while (res.next()) {
                map.put(res.getString(1), list);
            }
            return map;
        } catch (Exception exception) {
            logger.error(" getting Lead Emails", exception);
            return null;
        }
    }

    public static Map<String, List<PhoneNumbers>> getLeadPhoneNums(int leadId, boolean mobilesOnly) {
        Map<String, List<PhoneNumbers>> map = new HashMap<String, List<PhoneNumbers>>();
        List<PhoneNumbers> list = new ArrayList<PhoneNumbers>();
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            String query = "select * from phone_numbers where lead_id='" + leadId + "' ";
            if (mobilesOnly) query += "and mobile_or_not = '1'";
            ResultSet res = cs.executeQuery(query);
            while (res.next()) {
                list.add(new PhoneNumbers(res.getInt("id"),
                                res.getString("phone_num"),
                                res.getInt("lead_id"),
                                res.getInt("confirmed"),
                                res.getString("note"),
                                res.getString("activation_code"),
                                dateFormat.format(res.getTimestamp("create_date")),
                                res.getInt("mobile_or_not")
                        )
                );
            }
            res = cs.executeQuery("select concat(company_name,'(#', company_id_number, ')') from leads where lead_id='" + leadId + "'");
            while (res.next()) {
                map.put(res.getString(1), list);
            }
            return map;
        } catch (Exception exception) {
            logger.error(" getting Lead Phones", exception);
            return new HashMap<String, List<PhoneNumbers>>();
        }
    }

    public static int userAction(User user, int authorId) {
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_users(?,?,?,?,?,?,?,?,?,?,?)}");
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, user.getId());
            cs.setString(3, user.getUserName());
            cs.setString(4, user.getDescription());
            cs.setString(5, Util.MD5(user.getPassword()));
            cs.setInt(6, 1);
            cs.setInt(7, authorId);
            cs.setInt(8, user.getTypeId());
            cs.setInt(9, user.isEditPass() ? 1 : 0);
            cs.setInt(10, user.isInventarAccess() ? 1 : 0);
            cs.setInt(11, user.getInvTypeId());
            cs.execute();
            return cs.getInt("p_result_id");
        } catch (SQLException exception) {
            logger.error(" users save-update", exception);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }


    public static List<String> syncWithDhlOrInvoices(Emails email) {
        List<String> result = new ArrayList<>();
        String companyIdenNum = null;
        boolean success = true;
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            ResultSet res = cs.executeQuery("select company_id_number from leads where lead_id='" + email.getLeadId() + "'");
            while (res.next()) {
                companyIdenNum = res.getString(1);
            }
        } catch (Exception e) {
            result.add("ხელშეკრულებების ბაზაში ინფორმაციის დამუშავება ვერ მოხერხდა");
            logger.error(" sync With Dhl Or Invoices DB", e);
            return result;
        }

        // მაიესქუელიდან დატა მოგროვებულია და იწყება რემოუთ ბაზებში ცვლილებების გაშვება
        if (email != null && companyIdenNum != null) {
            //update or insert into DHL database
            if (email.getDhlDb() == 2) {
                RemoteDbObj dhlObj = null;
                try {
                    DhlCon.openDbConn();
                    Statement cs = DhlCon.getDbConn().createStatement();
//                    ResultSet res = cs.executeQuery("select ID from Contacts where Name = 'accountant'");
                    ResultSet res = cs.executeQuery("select c.ID from Contacts c where c.Name = 'accountant' " +
                            "and c.ClientID = (select cl.ID from Clients cl where cl.IdentificationNumber='"
                            + companyIdenNum.trim() + "')");
                    while (res.next()) {
                        dhlObj = new RemoteDbObj();
                        dhlObj.setContactId(res.getInt(1));
                    }
                    if (dhlObj == null) {// tu bugaltris contacti ar arsebobs insert new
                        createDhlContact(companyIdenNum, email.getMail());
                    } else {// tu arsebobs update
                        updateDhlContact(dhlObj.getContactId(), email.getMail());
                    }
                } catch (Exception e) {
                    result.add("დომესტიკის ბაზაში ინფორმაციის განახლება ვერ მოხერხდა");
                    if (e.getMessage().contains("---"))
                        result.add(e.getMessage());
                    logger.error(" during update or insert into DHL database: ", e);
                    success = false;
                } finally {
                    DhlCon.closeDbConn();
                }
            } else {// ubralod gamourtia remote bazastan sinqi
                try {
                    Statement cs = DBConnection.getDbConn().createStatement();
                    cs.executeUpdate("update emails set dhl_db = 1  where id='" + email.getId() + "'");
                } catch (SQLException throwables) {
                    logger.error(" disabling sync With Dhl DB", throwables);
                }
            }
            //update or insert into Invoices database
            if (email.getInvoiceDb() == 2) {
                RemoteDbObj invoicesObj = null;
                try {
                    InvoiceCon.openDbConn();
                    Statement cs = InvoiceCon.getDbConn().createStatement();
//                    ResultSet res = cs.executeQuery("select ID from Contacts where FullName = 'accountant'");
                    ResultSet res = cs.executeQuery("select c.ID from Contacts c where c.FullName = 'accountant' " +
                            "and c.ClientID = (select cl.ID from Clients cl where cl.IdentificationNumber='" +
                            companyIdenNum.trim() + "')");
                    while (res.next()) {
                        invoicesObj = new RemoteDbObj();
                        invoicesObj.setContactId(res.getInt(1));
                    }
                    if (invoicesObj == null) {// tu bugaltris contacti ar arsebobs insert new
                        createInvoicesDbContact(companyIdenNum, email.getMail());
                    } else {// tu arsebobs update
                        updateInvoicesDbContact(invoicesObj.getContactId(), email.getMail());
                    }

                } catch (Exception e) {
                    result.add("ინვოისის ბაზაში ინფორმაციის განახლება ვერ მოხერხდა");
                    if (e.getMessage().contains("---"))
                        result.add(e.getMessage());
                    logger.error(" during update or insert into Invoices database: ", e);
                    success = false;
                } finally {
                    InvoiceCon.closeDbConn();
                }
            } else {// ubralod gamourtia remote bazastan sinqi
                try {
                    Statement cs = DBConnection.getDbConn().createStatement();
                    cs.executeUpdate("update emails set invoice_db = 1  where id='" + email.getId() + "'");
                } catch (SQLException throwables) {
                    logger.error(" during disabling sync With Invoice DB in Mysql", throwables);
                }
            }

        } else {
            result.add("ხელშეკრულებების ბაზაში მოცემულ კომპანიაზე საიდენტიფიკაციო კოდი არ ფიქსირედება");
            success = false;
        }
        if (success) {
            result.add("ოპერაცია დასრულდა წარმატებით.");
            updateContactDbStatuses(email);
        }
        return result;
    }

    public static void updateContactDbStatuses(Emails email) {
        try {
            String columns = null;
            String columns2 = null;
            if (email.getInvoiceDb() == 2 && email.getDhlDb() == 2) {
                columns = "invoice_db = '1', dhl_db='1'";
                columns2 = "invoice_db = '2', dhl_db='2'";
            } else if (email.getInvoiceDb() == 2 && email.getDhlDb() == 1) {
                columns = "invoice_db = '1'";
                columns2 = "invoice_db = '2'";
            } else if (email.getInvoiceDb() == 1 && email.getDhlDb() == 2) {
                columns = "dhl_db='1'";
                columns2 = "dhl_db = '2'";
            }
            if (columns != null && columns2 != null) {
                DBConnection.openDbConn();
                Statement cs = DBConnection.getDbConn().createStatement();
                cs.executeUpdate("update emails set " + columns + "  where lead_id='" + email.getLeadId() + "'");
                cs.executeUpdate("update emails set " + columns2 + "  where id='" + email.getId() + "'");
            }
        } catch (Exception e) {
            logger.error(" update Contact Db Statuses in Mysql", e);
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static void createInvoicesDbContact(String companyIdenNum, String email) throws Exception {
        InvoiceCon.openDbConn();
        Statement cs = InvoiceCon.getDbConn().createStatement();
        ResultSet res = cs.executeQuery("select ID from Clients where RTRIM(LTRIM(IdentificationNumber))='" + companyIdenNum.trim() + "'");
        RemoteDbObj dhlObj = null;
        while (res.next()) {
            dhlObj = new RemoteDbObj();
            dhlObj.setClientId(res.getInt(1));
        }
        if (dhlObj != null) {
            Statement cs2 = InvoiceCon.getDbConn().createStatement();
            if (cs2.executeUpdate("insert into Contacts(ClientID, FullName, Phone, Email, RecordDate) " +
                    "values (" + dhlObj.getClientId() + ",'accountant','-', '" + email + "', CURRENT_TIMESTAMP)") < 1) {
                logger.error(" inserting Contact in Invoices Db");
                throw new SQLException("--- ინვოისის ბაზაში კონტაქტი ვერ დაემატა" + companyIdenNum);
            } else {
                logger.info("Successfylly created Inoice DB Contact for IdentNumber: " + companyIdenNum + " Email: " + email);
            }
        } else {
            logger.error(" Can't find Clients record in Invoices Db with IdenNumber: " + companyIdenNum);
            throw new SQLException("--- მოცემული საიდენტიფიკაციო ნომრით ინვოისის ბაზაში კლიენტი ვე მოიძებნა" + companyIdenNum);
        }
    }

    public static void updateInvoicesDbContact(Integer Id, String email) throws SQLException {
        InvoiceCon.openDbConn();
        Statement cs2 = InvoiceCon.getDbConn().createStatement();
        if (cs2.executeUpdate("update Contacts set Email = '" + email + "' where ID = '" + Id + "'") < 1) {
            throw new SQLException("--- ინვოისის ბაზაში კონტაქტი ვერ განახლდა: " + email);
        } else {
            logger.info("Successfylly updated Invoice Db Contact with ID: " + Id + " Email: " + email);
        }
    }

    public static void createDhlContact(String companyIdenNum, String email) throws Exception {
        DhlCon.openDbConn();
        Statement cs = DhlCon.getDbConn().createStatement();
        ResultSet res = cs.executeQuery("select ID from Clients where RTRIM(LTRIM(IdentificationNumber))='" + companyIdenNum.trim() + "'");
        RemoteDbObj dhlObj = null;
        while (res.next()) {
            dhlObj = new RemoteDbObj();
            dhlObj.setClientId(res.getInt(1));
        }
        if (dhlObj != null) {
            System.out.println("Client with identNum" + companyIdenNum + " Found DHl DB ID=" + dhlObj.getClientId());
            Statement cs2 = DhlCon.getDbConn().createStatement();
            int ins = cs2.executeUpdate("insert into Contacts(ClientID, Name, Email, RecordDate) " +
                    "values (" + dhlObj.getClientId() + ",'accountant', '" + email + "', CURRENT_TIMESTAMP)");
            if (ins < 1) {
                logger.error(" Can't find Contact in DHL db with IdentNum: " + companyIdenNum);
                throw new SQLException("--- დომესტიკის ბაზაში კონტაქტი ვერ დაემატა" + companyIdenNum);
            } else {
                logger.info("Successfylly created DHL DB Contact for IdentNumber: " + companyIdenNum + " Email: " + email);
            }
        } else {
            logger.error(" Can't find Clients record in DHL Db with IdenNumber: " + companyIdenNum);
            throw new SQLException("--- მოცემული საიდენტიფიკაციო ნომრით დომესტიკის ბაზაში კლიენტი ვერ მოიძებნა" + companyIdenNum);
        }
    }

    public static void updateDhlContact(Integer Id, String email) throws SQLException {
        DhlCon.openDbConn();
        Statement cs2 = DhlCon.getDbConn().createStatement();
        if (cs2.executeUpdate("update Contacts set Email = '" + email + "' where ID = '" + Id + "'") < 1) {
            logger.error(" Can't update Contact in DHL DB with email: " + email + " Id: " + Id);
            throw new SQLException("--- დომესტიკის ბაზაში კონტაქტი ვერ განახლდა: " + email);
        } else {
            logger.info("Successfylly updated Dhl Db Contact with ID: " + Id + " Email: " + email);
        }
    }

    public static int emailAction(Emails email) {
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_email(?,?,?,?,?,?,?,?,?)}");
            cs.registerOutParameter(9, java.sql.Types.INTEGER);
            cs.setInt(1, email.getId());
            cs.setString(2, email.getMail());
            cs.setInt(3, email.getLeadId());
            cs.setInt(4, email.getConfirmed());
            cs.setString(5, email.getNote());
            cs.setString(6, email.getActivationCode());
            cs.setInt(7, email.getDhlDb());
            cs.setInt(8, email.getInvoiceDb());
            cs.execute();
            return cs.getInt("p_result_id");
        } catch (SQLIntegrityConstraintViolationException ex) {
            logger.error(" Company Already have such email", ex);
            return -2;
        } catch (Exception exception) {
            logger.error(" during Email Action", exception);
            return 0;
        }
    }

    public static int phoneAction(PhoneNumbers phone) {
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_phone_nums(?,?,?,?,?,?,?,?)}");
            cs.registerOutParameter(8, java.sql.Types.INTEGER);
            cs.setInt(1, phone.getId());
            cs.setString(2, phone.getPhoneNum());
            cs.setInt(3, phone.getLeadId());
            cs.setInt(4, phone.getConfirmed());
            cs.setString(5, phone.getNote());
            cs.setString(6, phone.getActivationCode());
            cs.setInt(7, phone.getMobileOrNot());
            cs.execute();
            return cs.getInt("p_result_id");
        } catch (SQLIntegrityConstraintViolationException ex) {
            logger.error(" Company Already have such Phone Number", ex);
            return -2;
        } catch (Exception exception) {
            logger.error(" during Phone Number Action", exception);
            return 0;
        }
    }

    public static int getSentEmailsCount(SentEmails srchObj) {
        java.sql.Date fromDateSql = null;
        java.sql.Date toDateSql = null;
        if (srchObj.getSendDateStart() != null) {
            fromDateSql = new java.sql.Date(srchObj.getSendDateStart().getTime());
        }
        if (srchObj.getSendDateEnd() != null) {
            toDateSql = new java.sql.Date(srchObj.getSendDateEnd().getTime());
        }
        int count = 0;
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_sent_emails_count(?,?,?,?,?,?,?,?)}");

            cs.setString(1, srchObj.getMail());
            cs.setInt(2, srchObj.getStatus());
            cs.setString(3, srchObj.getIdentNumber());
            cs.setInt(4, srchObj.getLeadId());
            cs.setInt(5, srchObj.getConfirmed());
            cs.setDate(6, fromDateSql);
            cs.setDate(7, toDateSql);
            cs.setString(8, srchObj.getSubject());

            ResultSet res = cs.executeQuery();
            while (res.next()) {
                count = res.getInt(1);
            }
            return count;
        } catch (SQLException ex) {
            logger.error(" Getting Sent Emails Count", ex);
            return 0;
        }

    }

    public static List<SentEmails> getSentEmails(SentEmails srchObj, int start, int rowLimit) {
        List<SentEmails> list = new ArrayList<SentEmails>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        java.sql.Date fromDateSql = null;
        java.sql.Date toDateSql = null;
        if (srchObj.getSendDateStart() != null) {
            fromDateSql = new java.sql.Date(srchObj.getSendDateStart().getTime());
        }
        if (srchObj.getSendDateEnd() != null) {
            toDateSql = new java.sql.Date(srchObj.getSendDateEnd().getTime());
        }

        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_sent_emails(?,?,?,?,?,?,?,?,?,?)}");
            cs.setInt(1, start);
            cs.setInt(2, rowLimit);
            cs.setString(3, srchObj.getMail());
            cs.setInt(4, srchObj.getStatus());
            cs.setString(5, srchObj.getIdentNumber());
            cs.setInt(6, srchObj.getLeadId());
            cs.setDate(7, fromDateSql);
            cs.setDate(8, toDateSql);
            cs.setInt(9, srchObj.getConfirmed());
            cs.setString(10, srchObj.getSubject());

            ResultSet res = cs.executeQuery();
            SentEmails obj;
            while (res.next()) {
                obj = new SentEmails();
                obj.setId(res.getInt("id"));
                obj.setMail(res.getString("mail"));
                obj.setNote(res.getString("note"));
                obj.setConfirmed(res.getInt("confirmed"));
                obj.setStatus(res.getInt("status"));
                obj.setCompany(res.getString("company"));
                obj.setIdentNumber(res.getString("ident_number"));
                obj.setSubject(res.getString("subject"));
                obj.setBodyText(res.getString("body_text"));
                obj.setStrSentDate(dateFormat.format(res.getTimestamp("create_date")));
                list.add(obj);
            }
            return list;
        } catch (Exception ex) {
            logger.error(" during Getting sent emails", ex);
            return null;
        }

    }

    public static int departAction(Department depart) {
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_depart(?,?,?)}");
            cs.registerOutParameter(3, java.sql.Types.INTEGER);
            cs.setInt(1, depart.getDepartmentId());
            cs.setString(2, depart.getDepartmentName());
            cs.execute();
            return cs.getInt("p_result_id");
        } catch (SQLException exception) {
            logger.error(exception);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static int getAllReportInDates(Date startDate, Date endDate, int hasContract) {
        try {
            int sum = 0;
            if (startDate != null && endDate != null) {
                DBConnection.openDbConn();
                Statement cs = DBConnection.getDbConn().createStatement();
                ResultSet res = cs.executeQuery(
                        "select count(*) from leads where status_id=1 and has_contract='" + hasContract + "' and create_date between '"
                                + new java.sql.Date(startDate.getTime()) + "' and '" + new java.sql.Date(endDate.getTime()) + "' ");
                while (res.next()) {
                    sum = res.getInt(1);
                }
            }
            return sum;
        } catch (SQLException exception) {
            logger.error(exception);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static int getreportByUser(Date startDate, Date endDate, int hasContract, boolean operator, int userId) {
        try {
            int sum = 0;
            if (startDate != null && endDate != null) {
                DBConnection.openDbConn();
                Statement cs = DBConnection.getDbConn().createStatement();
                ResultSet res = null;
                if (operator) {
                    res = cs.executeQuery("select count(lead_id) from leads where status_id=1 and has_contract='" + hasContract
                            + "' and create_date between '" + new java.sql.Date(startDate.getTime()) + "' and '" + new java.sql.Date(
                            endDate.getTime()) + "' and operator_id='" + userId + "' ");
                } else {

                    res = cs.executeQuery("select count(lead_id) from leads where status_id=1 and has_contract='" + hasContract
                            + "' and create_date between '" + new java.sql.Date(startDate.getTime()) + "' and '" + new java.sql.Date(
                            endDate.getTime()) + "' and sail_or_iurist_id='" + userId + "' ");

                    //                    res = cs.executeQuery("select count(id) from updates_log where has_contract='" + hasContract
                    //                            + "' and oper_date between '" + new java.sql.Date(startDate.getTime()) + "' and '"
                    //                            + new java.sql.Date(endDate.getTime()) + "' and user_id='" + userId + "' ");
                }
                while (res.next()) {
                    sum = res.getInt(1);
                }
            }
            return sum;
        } catch (SQLException exception) {
            logger.error(exception);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static int getDisagrees(Date startDate, Date endDate) {
        try {
            int count = 0;
            if (startDate != null && endDate != null) {
                DBConnection.openDbConn();
                Statement cs = DBConnection.getDbConn().createStatement();
                ResultSet res = cs.executeQuery(
                        "select count(lead_id) from leads where status_id=1 and create_date between '" + new java.sql.Date(
                                startDate.getTime()) + "' and '" + new java.sql.Date(endDate.getTime()) + "' and answer_id='2' ");

                while (res.next()) {
                    count = res.getInt(1);
                }
            }
            return count;
        } catch (SQLException exception) {
            logger.error(exception);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static Lead getExistingLead(String leadIdIdentNumber) {
        try {
            Lead lead = null;
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            ResultSet res = cs.executeQuery(
                    "select company_name, address, contact_person, phone, mail, note from vi_leads where company_id_number='"
                            + leadIdIdentNumber + "'");
            while (res.next()) {
                lead = new Lead();
                lead.setCompanyName(res.getString("company_name"));
                lead.setCompanyIdentCode(leadIdIdentNumber);
                lead.setAddress(res.getString("address"));
                lead.setContactPerson(res.getString("contact_person"));
                lead.setPhone(res.getString("phone"));
                lead.setEmail(res.getString("mail"));
                lead.setNote(res.getString("note"));
                break;
            }
            return lead;
        } catch (Exception exception) {
            logger.error(exception);
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static void insertSmsHistory(List<SentSMSes> list, int userId) throws SQLException {
        String sql = "INSERT INTO `dhl`.`sent_sms` (`lead_id`, `to`, `text`, `response`, `status_id`, `user_id`) VALUES (?, ?, ?, ?, ?, ?)";
        DBConnection.openDbConn();
        PreparedStatement ps = DBConnection.getDbConn().prepareStatement(sql);
        for (SentSMSes sms : list) {
            if (sms.getLeadId() == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, sms.getLeadId().intValue());
            }
            ps.setString(2, sms.getTo());
            ps.setString(3, sms.getText());
            ps.setString(4, sms.getResponse());
            ps.setInt(5, sms.getStatus());
            ps.setInt(6, userId);
            ps.executeUpdate();
        }
    }

    public static void insertEmailHistory(List<SentEmails> list, int userId) throws SQLException {
        String sql = "INSERT INTO `sent_emails` (`email_id`, `subject`, `body_text`, `status`, `user_id`) VALUES (?, ?, ?, ?, ?)";
        DBConnection.openDbConn();
        PreparedStatement ps = DBConnection.getDbConn().prepareStatement(sql);
        for (SentEmails email : list) {
            ps.setInt(1, email.getId());
            ps.setString(2, email.getSubject());
            ps.setString(3, email.getBodyText());
            ps.setInt(4, email.getStatus());
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
    }

    public static int getSmsesCount(SentSMSes srchSms) {
        java.sql.Date fromDateSql = null;
        java.sql.Date toDateSql = null;
        if (srchSms.getSendDateStart() != null) {
            fromDateSql = new java.sql.Date(srchSms.getSendDateStart().getTime());
        }
        if (srchSms.getSendDateEnd() != null) {
            toDateSql = new java.sql.Date(srchSms.getSendDateEnd().getTime());
        }
        int count = 0;
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_sent_sms_count(?,?,?,?,?,?)}");
            cs.setDate(1, fromDateSql);
            cs.setDate(2, toDateSql);
            cs.setString(3, srchSms.getTo());
            cs.setInt(4, srchSms.getStatus());
            cs.setString(5, srchSms.getText());
            cs.setInt(6, srchSms.getSentUserId());
            ResultSet res = cs.executeQuery();
            while (res.next()) {
                count = res.getInt(1);
            }
            return count;
        } catch (SQLException ex) {
            logger.error(ex);
            return 0;
        }

    }

    public static String confirmSMSOrEmail(String code, boolean numberConfirm) {
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            ResultSet res = cs.executeQuery("select * from " + (numberConfirm ? "phone_numbers" : "emails") + " where activation_code='" + code + "'");
            PhoneNumbers number = null;
            Emails email = null;
            String result = null;
            if (numberConfirm) {
                while (res.next()) {
                    number = new PhoneNumbers(res.getInt("id"),
                            res.getString("phone_num"),
                            res.getInt("lead_id"),
                            res.getInt("confirmed"),
                            res.getString("note"),
                            res.getString("activation_code"),
                            dateFormat.format(res.getTimestamp("create_date")),
                            res.getInt("mobile_or_not")
                    );
                }
                if (number != null) {
                    number.setConfirmed(2);
                    number.setActivationCode(null);
                    phoneAction(number);
                    res = cs.executeQuery("select concat(company_name,'(#', company_id_number, ')') from leads where lead_id='"
                            + number.getLeadId() + "'");
                    while (res.next()) {
                        result = res.getString(1) + "  --  " + number.getPhoneNum();
                    }
                }
            } else {
                email = buildEmail(res);
                if (email != null) {
                    email.setConfirmed(2);
                    email.setActivationCode(null);
                    emailAction(email);
                    res = cs.executeQuery("select concat(company_name,'(#', company_id_number, ')') from leads where lead_id='"
                            + email.getLeadId() + "'");
                    while (res.next()) {
                        result = res.getString(1) + "  --  " + email.getMail();
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            logger.error(ex);
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    private static Emails buildEmail(ResultSet res) throws SQLException {
        Emails email = null;
        while (res.next()) {
            email = new Emails(res.getInt("id"),
                    res.getString("mail"),
                    res.getInt("lead_id"),
                    res.getInt("confirmed"),
                    res.getString("note"),
                    res.getString("activation_code"),
                    dateFormat.format(res.getTimestamp("create_date")),
                    res.getInt("dhl_db"),
                    res.getInt("invoice_db")
            );
        }
        return email;
    }

    public static List<SentSMSes> getSmses(SentSMSes srchSms, int start, int rowLimit) {
        List<SentSMSes> list = new ArrayList<SentSMSes>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        java.sql.Date fromDateSql = null;
        java.sql.Date toDateSql = null;
        if (srchSms.getSendDateStart() != null) {
            fromDateSql = new java.sql.Date(srchSms.getSendDateStart().getTime());
        }
        if (srchSms.getSendDateEnd() != null) {
            toDateSql = new java.sql.Date(srchSms.getSendDateEnd().getTime());
        }

        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_sent_sms(?,?,?,?,?,?,?,?,?)}");
            cs.setDate(1, fromDateSql);
            cs.setDate(2, toDateSql);
            cs.setString(3, srchSms.getTo());
            cs.setInt(4, srchSms.getStatus());
            cs.setString(5, srchSms.getText());
            cs.setInt(6, start);
            cs.setInt(7, rowLimit);
            cs.setString(8, srchSms.getLeadData());
            cs.setInt(9, srchSms.getSentUserId());
            ResultSet res = cs.executeQuery();
            SentSMSes sms;
            while (res.next()) {
                sms = new SentSMSes();
                sms.setId(res.getInt("id"));
                sms.setLeadId(res.getInt("lead_id"));
                sms.setTo(res.getString("to"));
                sms.setText(res.getString("text"));
                sms.setLeadData(res.getString("lead_data"));
                sms.setStatus(res.getInt("status_id"));
                sms.setResponse(res.getString("response"));
                sms.setSentUser(res.getString("user_description"));
                sms.setStrSendTime(dateFormat.format(res.getTimestamp("create_date")));
                list.add(sms);
            }
            return list;
        } catch (Exception ex) {
            logger.error(ex);
            return null;
        }

    }

    public static List<Lead> getLeads(Lead srchLead, int leadId, int userId, int statusId, int start, int rowLimit, int expiredAlert,
                                      int limitedAlert, boolean calAlert, boolean payAlert, boolean emptyIdent) {

        List<Lead> leads = new ArrayList<Lead>();
        List<Document> docs = new ArrayList<Document>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        java.sql.Date fromDateSql = null;
        java.sql.Date toDateSql = null;
        Date currDate = new Date();
        if (srchLead.getContractStart() != null) {
            fromDateSql = new java.sql.Date(srchLead.getContractStart().getTime());
        }
        if (srchLead.getContractEnd() != null) {
            toDateSql = new java.sql.Date(srchLead.getContractEnd().getTime());
        }
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn()
                    .prepareCall("{CALL prc_get_lead(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setInt(1, leadId);
            cs.setInt(2, userId);
            cs.setDate(3, fromDateSql);
            cs.setDate(4, toDateSql);
            cs.setInt(5, statusId);
            cs.setInt(6, start);
            cs.setInt(7, rowLimit);
            cs.setString(8, srchLead.getCompanyName());
            cs.setString(9, srchLead.getCompanyIdentCode());
            cs.setString(10, srchLead.getDepartment().getAccountNumber());
            cs.setInt(11, srchLead.getDepartment().getSalePercent());
            cs.setInt(12, srchLead.getDavalianeba());
            cs.setInt(13, srchLead.getDepartment().getDepartmentId());
            cs.setInt(14, srchLead.getOperatorId());
            cs.setInt(15, srchLead.getSaleOrAdminId());
            cs.setString(16, srchLead.getContractCode());
            cs.setInt(17, expiredAlert);
            cs.setInt(18, limitedAlert);
            cs.setString(19, srchLead.getServiceType());
            if (calAlert) {
                cs.setInt(20, 1);
            } else {
                cs.setInt(20, 0);
            }
            if (payAlert) {
                cs.setInt(21, 1);
            } else {
                cs.setInt(21, 0);
            }
            if (emptyIdent) {
                cs.setInt(22, 1);
            } else {
                cs.setInt(22, 0);
            }
            cs.setString(23, srchLead.getEmail());
            cs.setString(24, srchLead.getPhone());
            ResultSet res = cs.executeQuery();
            Lead lead;
            while (res.next()) {
                lead = new Lead();
                lead.setLeadId(res.getInt("lead_id"));
                lead.setCompanyName(res.getString("company_name"));
                lead.setCompanyIdentCode(res.getString("company_id_number"));
                lead.setAddress(res.getString("address"));
                lead.setContactPerson(res.getString("contact_person"));
                lead.setPhone(res.getString("phone"));
                lead.setEmail(res.getString("mail"));
                lead.setContractStart(res.getDate("contraction_date"));
                lead.setContractEnd(res.getDate("expiration_date"));
                lead.setHasContract(res.getInt("has_contract"));
                lead.setOperatorId(res.getInt("operator_id"));
                lead.setOperatorName(res.getString("operator_name"));
                lead.setSaleOrAdminId(res.getInt("sail_or_iurist_id"));
                lead.setSaleOrAdminName(res.getString("admin_name"));
                lead.setDateofInterest(dateFormat.format(res.getTimestamp("create_date")));
                lead.setStrContractEnd(lead.getContractEnd() != null ? dateFormat.format(lead.getContractEnd().getTime()) : "");
                lead.setStrContractStart(lead.getContractStart() != null ? dateFormat.format(lead.getContractStart().getTime()) : "");
                lead.setIntrstedIndepartments(res.getString("interested_departs"));
                lead.getDepartment().setDepartmentId(res.getInt("contr_depart_id"));
                lead.getDepartment().setDepartmentName(res.getString("department_name"));
                lead.getDepartment().setSalePercent(res.getInt("sale_percents"));
                lead.getDepartment().setAccountNumber(res.getString("account_number"));
                lead.setNote(res.getString("note"));
                lead.setIsActive(res.getInt("status_id"));
                lead.setActive(res.getInt("status_id") == 1 ? Util.ka("aqtiuri") : Util.ka("pasiuri"));
                lead.setHasContr(lead.getHasContract() == 1 ? Util.ka("ki") : Util.ka("ara"));
                lead.setCheckLead(res.getInt("check_lead"));
                lead.setTermType(res.getInt("term_type"));
                lead.setAnswerId(res.getInt("answer_id"));
                lead.setLimited(res.getInt("limited"));
                lead.setDavalianeba(res.getInt("davalianeba"));
                lead.setServiceType(res.getString("service_type"));
                lead.setContractCode(res.getString("contract_code"));
                lead.setNextCall(res.getDate("next_call"));
                lead.setCallAlert(res.getInt("call_alert"));
                lead.setPayAlert(res.getInt("payment_alert"));
                lead.setPermissionId(res.getInt("type_id"));
                lead.setMaxLimitAmount(res.getDouble("max_limit"));
                //                if (currDate.getDate() > 14 && currDate.getDate() < 21 && lead.getLimited() == 1) {
                //                    lead.setLimitedAlarm(1);
                //                }
                if (lead.getMaxLimitAmount() != null && lead.getMaxLimitAmount() < 30 && lead.getLimited() == 1) {
                    lead.setLimitedAlarm(1);
                }
                cs = (CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_lead_docs(?)}");
                cs.setInt(1, lead.getLeadId());
                ResultSet result = cs.executeQuery();
                Document document;
                while (result.next()) {
                    document = new Document();
                    document.setDocumentId(result.getInt("doc_id"));
                    document.setLeadId(result.getInt("lead_id"));
                    document.setDocumentName(result.getString("doc_name"));
                    document.setCreateDate(result.getDate("create_date"));
                    lead.getDocuments().add(document);
                }
                leads.add(lead);
            }
            return leads;
        } catch (Exception ex) {
            logger.error("Can't load leads list from DB ", ex);
            return null;
        }

    }

    public static int getLeadsCount(Lead srchLead, int leadId, int userId, int statusId, int expiredAlert, int limitedAlert,
                                    boolean calAlert, boolean payAlert, boolean emptyIdent) {
        java.sql.Date fromDateSql = null;
        java.sql.Date toDateSql = null;
        Date currDate = new Date();
        if (srchLead.getContractStart() != null) {
            fromDateSql = new java.sql.Date(srchLead.getContractStart().getTime());
        }
        if (srchLead.getContractEnd() != null) {
            toDateSql = new java.sql.Date(srchLead.getContractEnd().getTime());
        }
        int count = 0;
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (CallableStatement) DBConnection.getDbConn()
                    .prepareCall("{CALL prc_get_leads_count(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cs.setInt(1, leadId);
            cs.setInt(2, userId);
            cs.setDate(3, fromDateSql);
            cs.setDate(4, toDateSql);
            cs.setInt(5, statusId);
            cs.setString(6, srchLead.getCompanyName());
            cs.setString(7, srchLead.getCompanyIdentCode());
            cs.setString(8, srchLead.getDepartment().getAccountNumber());
            cs.setInt(9, srchLead.getDepartment().getSalePercent());
            cs.setInt(10, srchLead.getDavalianeba());
            cs.setInt(11, srchLead.getDepartment().getDepartmentId());
            cs.setInt(12, srchLead.getOperatorId());
            cs.setInt(13, srchLead.getSaleOrAdminId());
            cs.setString(14, srchLead.getContractCode());
            cs.setInt(15, expiredAlert);
            cs.setInt(16, limitedAlert);
            cs.setString(17, srchLead.getServiceType());
            if (calAlert) {
                cs.setInt(18, 1);
            } else {
                cs.setInt(18, 0);
            }
            if (payAlert) {
                cs.setInt(19, 1);
            } else {
                cs.setInt(19, 0);
            }
            if (emptyIdent) {
                cs.setInt(20, 1);
            } else {
                cs.setInt(20, 0);
            }
            cs.setString(21, srchLead.getEmail());
            cs.setString(22, srchLead.getPhone());
            ResultSet res = cs.executeQuery();
            while (res.next()) {
                count = res.getInt(1);
            }
            return count;
        } catch (Exception ex) {
            logger.error("Can't load leads count from DB ", ex);
            return 0;
        }

    }

    public static List<Department> getLeadDepartments(int leadId) {
        List<Department> departments = new ArrayList<Department>();
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_lead_depts(?)}");
            cs.setInt(1, leadId);
            ResultSet res = cs.executeQuery();
            Department dep;
            while (res.next()) {
                dep = new Department();
                dep.setLeadDepartmentId(res.getInt("lead_dept_id"));
                dep.setDepartmentId(res.getInt("dept_id"));
                dep.setDepartmentName(res.getString("department_name"));
                departments.add(dep);
            }
            return departments;
        } catch (SQLException ex) {
            logger.error("Can't load leads departments from DB ", ex);
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static List<LimitHistory> getLeadsLimitHistory(int leadId) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        List<LimitHistory> history = new ArrayList<LimitHistory>();
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            ResultSet res = cs
                    .executeQuery("select * from limits_history where deleted=0 and lead_id='" + leadId + "' order by create_date asc");
            LimitHistory obj;
            while (res.next()) {
                obj = new LimitHistory();
                obj.setId(res.getInt("id"));
                obj.setLeadId(res.getInt("lead_id"));
                obj.setDecreaseAmount(res.getDouble("decrease_amount"));
                obj.setLeftAmount(res.getDouble("left_amount"));
                obj.setStartAmount(res.getDouble("start_amount"));
                obj.setStrCreateDate(format.format(res.getTimestamp("create_date")));
                obj.setDeleted(res.getInt("deleted"));
                history.add(obj);
            }
            return history;
        } catch (SQLException ex) {
            logger.error("Can't load leads limits history from DB ", ex);
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static int actionLeadsLimitHistory(LimitHistory obj) {
        try {
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_limit_history(?,?,?,?,?)}");
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, obj.getId());
            cs.setInt(3, obj.getLeadId());
            cs.setDouble(4, obj.getStartAmount());
            cs.setDouble(5, obj.getDecreaseAmount());
            cs.execute();
            return cs.getInt("p_result_id");
        } catch (SQLException ex) {
            logger.error(ex);
            return 0;
        }
    }

    public static int actionLeadDeparts(Department depart) {
        try {
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_lead_depts(?,?,?,?)}");
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, depart.getLeadDepartmentId());
            cs.setInt(3, depart.getLeadId());
            cs.setInt(4, depart.getDepartmentId());
            cs.execute();
            return cs.getInt("p_result_id");
        } catch (SQLException ex) {
            logger.error(ex);
            return 0;
        }
    }

    public static int changeLeadStatus(int leadId, int statusId) {
        try {
            DBConnection.openDbConn();
            Statement cs = DBConnection.getDbConn().createStatement();
            cs.executeUpdate("update leads set status_id='" + statusId + "' where lead_id='" + leadId + "'");
            return 1;
        } catch (SQLException exception) {
            logger.error(exception);
            return 0;
        }
    }

    public static void rewriteExpireDates() {
        try {
            DBConnection.openDbConn();
            int resultId;
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL expired()}");
            cs.execute();
        } catch (SQLException exception) {
            logger.error(exception);
        }
    }

    public static int actionLeads(Lead lead) {
        try {
            DBConnection.openDbConn();
            int resultId;
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn()
                    .prepareCall("{CALL prc_leads(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            cs.registerOutParameter(1, java.sql.Types.INTEGER);
            cs.setInt(2, lead.getLeadId());
            cs.setString(3, lead.getCompanyName());
            cs.setString(4, lead.getCompanyIdentCode());
            cs.setString(5, lead.getAddress());
            cs.setString(6, lead.getPhone());
            cs.setString(7, lead.getContactPerson());
            cs.setDate(8, lead.getContractStart() != null ? new java.sql.Date(lead.getContractStart().getTime()) : null);
            cs.setDate(9, lead.getContractEnd() != null ? new java.sql.Date(lead.getContractEnd().getTime()) : null);
            cs.setInt(10, lead.getOperatorId());
            cs.setString(11, lead.getEmail());
            cs.setString(12, lead.getIntrstedIndepartments());
            cs.setString(13, lead.getNote());
            cs.setInt(14, lead.getDepartment().getDepartmentId());
            cs.setInt(15, lead.getDepartment().getSalePercent());
            cs.setString(16, lead.getDepartment().getAccountNumber());
            cs.setInt(17, lead.getIsActive());
            cs.setInt(18, lead.getTermType());
            cs.setInt(19, lead.getHasContract());
            cs.setInt(20, lead.getSaleOrAdminId());
            cs.setInt(21, lead.getAnswerId());
            cs.setString(22, lead.getServiceType());
            cs.setInt(23, lead.getLimited());
            cs.setInt(24, lead.getDavalianeba());
            cs.setString(25, lead.getContractCode());
            cs.setDate(26, lead.getNextCall() != null ? new java.sql.Date(lead.getNextCall().getTime()) : null);
            cs.setInt(27, lead.getPermissionId());
            cs.setDouble(28, lead.getMaxLimitAmount() != null ? lead.getMaxLimitAmount() : 0.0);
            cs.execute();
            resultId = cs.getInt("p_result_id");
            return resultId;

        } catch (Exception ex) {
            logger.error("Can't Save leads data into DB ", ex);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static List<Department> getDepartments(int departmentId) {
        List<Department> depts = new ArrayList<Department>();
        try {
            DBConnection.openDbConn();
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_get_departments(?)}");
            cs.setInt(1, departmentId);
            ResultSet res = cs.executeQuery();
            Department department;
            while (res.next()) {
                department = new Department();
                department.setDepartmentId(res.getInt("department_id"));
                department.setDepartmentName(res.getString("department_name"));
                depts.add(department);
            }
            return depts;
        } catch (Exception ex) {
            logger.error("Can't load departments from DB ", ex);
            return null;
        } finally {
            DBConnection.closeDbConn();
        }
    }

    public static int actionLeadDocs(Document doc, int operId) {
        try {
            DBConnection.openDbConn();
            int resultId;
            CallableStatement cs;
            cs = (com.mysql.jdbc.CallableStatement) DBConnection.getDbConn().prepareCall("{CALL prc_lead_docs(?,?,?,?,?)}");
            cs.registerOutParameter(4, java.sql.Types.INTEGER);
            cs.setInt(1, doc.getDocumentId());
            cs.setInt(2, doc.getLeadId());
            cs.setInt(5, operId);
            cs.setString(3, doc.getDocumentName());
            cs.execute();
            resultId = cs.getInt("p_result_id");
            return resultId;
        } catch (SQLException ex) {
            logger.error("Can't save leads docs into DB ", ex);
            return 0;
        } finally {
            DBConnection.closeDbConn();
        }
    }

}
