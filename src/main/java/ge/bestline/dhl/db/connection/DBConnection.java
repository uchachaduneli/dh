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
public class DBConnection implements Serializable {

    private static final String dbUrl = "jdbc:mysql://localhost:3306/";
    private static final String dbName = "dhl?characterEncoding=utf8";
    private static final String dbDriver = "com.mysql.jdbc.Driver";
    private static final String dbUserName = "root";
    private static final String dbPassword = "root";
    private static Connection dbConn;
    private static final Logger logger = LogManager.getLogger(DBConnection.class);

    public static Connection getDbConn() {
        try {
            if (dbConn == null || dbConn.isClosed()) {
                Class.forName(dbDriver).getDeclaredConstructor().newInstance();
                dbConn = DriverManager.getConnection(dbUrl + dbName, dbUserName, dbPassword);
            }
        } catch (Exception ex) {
            logger.error("Can't Connect To Mysql DB ", ex);
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
            logger.error("Can't Check Mysql dbConn.isClosed()", ex);
            return false;
        }
    }

    public static void openDbConn() {
        if (IsClosedDbConn()) {
            try {
                Class.forName(dbDriver).newInstance();
                dbConn = DriverManager.getConnection(dbUrl + dbName, dbUserName, dbPassword);
            } catch (Exception ex) {
                logger.error("Can't Open Mysql DB Connection", ex);
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
            logger.error("Can't Close Mysql DB Connection", ex);
        }
    }
}
