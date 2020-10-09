package ge.bestline.dhl.utils;

import ge.bestline.dhl.db.processing.DbProcessing;
import ge.bestline.dhl.pojoes.SentSMSes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DhlSMS {

    public static void sendSms(String text, Map<Integer, List<String>> numbers) throws SQLException, IOException {
        final ConfigParams confParams = ConfigurationManager.getConfiguration().getConfParams();
        List<SentSMSes> smsforDb = new ArrayList<SentSMSes>();
        SentSMSes sms;
        for (Map.Entry<Integer, List<String>> entry : numbers.entrySet()) {
            for (String num : entry.getValue()) {
                sms = new SentSMSes();

                URLConnection connection = new URL(confParams.getSms_url() + num
                        + "&text=" + URLEncoder.encode(text, "UTF-8") + "&coding=2").openConnection();
                connection.setRequestProperty("Accept-Charset", "UTF-8");

                InputStream response = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(response));

                String line = "";
                String message = "";

                while ((line = reader.readLine()) != null) {
                    message += line;
                }
                sms.setLeadId(entry.getKey());
                sms.setTo(num);
                sms.setText(text);
                if (message.contains("0000")) {
                    sms.setStatus(1);
                } else {
                    sms.setStatus(2);
                }
                sms.setResponse(message);
                smsforDb.add(sms);
            }
        }

        if (!smsforDb.isEmpty()) {
            DbProcessing.insertSmsHistory(smsforDb);
        }

    }
}
