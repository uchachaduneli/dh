package ge.bestline.dhl.db.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Ucha Chaduneli
 */
public class InvoiceCon implements Serializable {

    private static final String dbUrl = "jdbc:sqlserver://2.240.0.13:1433;"
            + "databaseName=Invoices;user=mailuser;password=1234!@#$;";
//        private static final String dbUrl = "jdbc:sqlserver://192.168.0.6:1433;"
//            + "databaseName=Invoices;user=sa;password=Anacuabanacua123!@#;";
    private static final String dbDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final Logger logger = LogManager.getLogger(InvoiceCon.class);
    private static Connection dbConn;

    public static Connection getDbConn() {
        try {
            if (dbConn == null || dbConn.isClosed()) {
                Class.forName(dbDriver).newInstance();
                dbConn = DriverManager.getConnection(dbUrl);
            }
        } catch (Exception ex) {
            logger.error("Can't Connect To Invoices DB ", ex);
            dbConn = null;
        }
        return dbConn;
    }

    public static boolean IsClosedDbConn() {
        try {
            if (dbConn == null) {
                return true;
            } else {
                if (dbConn.isClosed()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception ex) {
            logger.error("Can't Check Invoices dbConn.isClosed()", ex);
            return false;
        }
    }

    public static void openDbConn() {
        if (IsClosedDbConn()) {
            try {
                Class.forName(dbDriver).newInstance();
                dbConn = DriverManager.getConnection(dbUrl);
            } catch (Exception ex) {
                logger.error("Can't Open Invoices DB Connection", ex);
            }
        }
    }

    public static void closeDbConn() {
        try {
            if (dbConn != null) {
                if (!dbConn.isClosed()) {
                    dbConn.close();
                    dbConn = null;
                }
            }
        } catch (SQLException ex) {
            logger.error("Can't Close Invoices DB Connection", ex);
        }
    }
}
