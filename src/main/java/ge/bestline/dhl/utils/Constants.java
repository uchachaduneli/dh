package ge.bestline.dhl.utils;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author Ucha Chaduneli
 */
@ManagedBean(name = "constantsBean")
@SessionScoped
public class Constants implements Serializable {

    public static final String projectPath = "/dhl";
    //    public static final String uploadPath = "C:\\Users\\home\\Desktop\\apache-tomcat-7.0.94\\webapps\\ROOT\\uploads";//chemi
//    public static final String uploadPath = "D:\\uploads";//leptopi
    public static final String uploadPath = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\ROOT\\uploads";
}
