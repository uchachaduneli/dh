package ge.bestline.dhl.utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationManager {
    private static final Logger lgg = Logger.getLogger(ConfigurationManager.class);
    public static final String CONFIG_FILE_LOCATION = "/dhl-conf.properties";
    public static final String CONF_PATTERN = "(smtp_host|smtp_port|sender_email|sender_email_pass|sms_url)";

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
        props.load(conn.getInputStream());
        buildSettingMap(props);
    }

    public static synchronized ConfigurationManager getConfiguration() throws ConfigurationException {
        lgg.debug(" methodName=getConfiguration" + " {{{method has been started}}}");
        URL url = ConfigurationManager.class.getResource(CONFIG_FILE_LOCATION);
        if (url == null) {
            lgg.fatal(" methodName=getConfiguration" + " {{{Configuration file not found}}}");
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
            throw new ConfigurationException("Error while reading configuration file", e);
        } finally {
            if (conn != null) {
                lgg.debug(" methodName=getConfiguration" + " {{{Closing connection input stream}}}");
                try {
                    conn.getInputStream().close();
                } catch (IOException e) {
                    lgg.warn(" methodName=getConfiguration" + " {{{Can't close connection input stream}}}");
                }
            }
        }
        lgg.trace(" methodName=getConfiguration" + " {{{method has been finished}}}");
        return instance;
    }

    public static Properties readPropertiesFile(String fileName) throws ConfigurationException {
        lgg.trace(" methodName=readPropertiesFile" + " {{{method has been started}}}");
        InputStream configStream = null;
        Properties configurationProperties = new Properties();

        configStream = ConfigurationManager.class.getResourceAsStream(fileName);
        if (configStream == null) {
            throw new ConfigurationException("Configuration file " + fileName + " not found");
        }

        try {
            configurationProperties.load(configStream);
        } catch (IOException e) {
            throw new ConfigurationException("Exception while loading property file: " + fileName, e);
        } finally {
            try {
                configStream.close();
            } catch (IOException e) {
                lgg.trace(" methodName=readPropertiesFile" + " {{{Can't close configuration file " + e.getMessage() + "}}}", e);
            }
        }
        lgg.trace(" methodName=readPropertiesFile" + " {{{method has been finished}}}");
        return configurationProperties;
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
            return new ConfigParams(confMap.get("smtp_host"), confMap.get("smtp_port"), confMap.get("sender_email"), confMap.get("sender_email_pass"), confMap.get("sms_url"));
        else
            throw new ConfigurationException("Can not get configuration");
    }
}
