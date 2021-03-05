package ge.bestline.dhl.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationManager implements Serializable {
    private static final Logger lgg = Logger.getLogger(ConfigurationManager.class);
    public static final String CONFIG_FILE_LOCATION = "/dhl-conf.properties";
    public static final String CONF_PATTERN = "(smtp_host|smtp_port|sender_email|sender_email_pass" +
            "|sms_url|enable_multisending_sms_and_email|confirm_email_template|confirm_sms_template|email_titul|sms_titul)";

    protected long lastModified;
    private static ConfigurationManager instance = new ConfigurationManager();

    private Map<String, String> confMap;

    protected ConfigurationManager() {
    }

    public ConfigurationManager(URL url) throws IOException {
        this(url.openConnection());
    }

    public ConfigurationManager(URLConnection conn) throws IOException {
        this.lastModified = conn.getLastModified();
        Properties props = new Properties();
        props.load(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
        buildSettingMap(props);
    }

    public static synchronized ConfigurationManager getConfiguration() throws ConfigurationException {
        URL url = ConfigurationManager.class.getResource(CONFIG_FILE_LOCATION);
        if (url == null) {
            throw new ConfigurationException("Configuration file " + CONFIG_FILE_LOCATION + " not found");
        }

        URLConnection conn = null;
        try {
            conn = url.openConnection();

            long lastModified = conn.getLastModified();
            if (lastModified > instance.lastModified) {
                instance = new ConfigurationManager(conn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    private void buildSettingMap(Properties props) {
        this.confMap = new HashMap<String, String>();

        Pattern confPattern = Pattern.compile(CONF_PATTERN);

        Enumeration<Object> propNames = props.keys();
        while (propNames.hasMoreElements()) {
            String propName = (String) propNames.nextElement();
            Matcher matcher = confPattern.matcher(propName);

            if (matcher.matches()) {
                String key = matcher.group(1);
                String value = props.getProperty(propName);

                if (value != null) {
                    this.confMap.put(key, value);
                }
            } else {
                continue;
            }

        }
    }

    public ConfigParams getConfParams() throws ConfigurationException {
        if (confMap != null)
            return new ConfigParams(confMap.get("smtp_host"), confMap.get("smtp_port"),
                    confMap.get("sender_email"), confMap.get("sender_email_pass"),
                    confMap.get("sms_url"), confMap.get("enable_multisending_sms_and_email")
                    , confMap.get("confirm_email_template")
                    , confMap.get("confirm_sms_template"), confMap.get("email_titul")
                    ,confMap.get("sms_titul"));
        else
            throw new ConfigurationException("Can not get configuration");
    }
}
