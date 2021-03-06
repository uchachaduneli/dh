package ge.bestline.dhl.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public class DhlMail implements Serializable {

    static Properties props = new Properties();

    private static void addAttachment(Multipart multipart, String filePath) throws MessagingException {
        DataSource source = new FileDataSource(filePath);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(source.getName());
        multipart.addBodyPart(messageBodyPart);
    }

    public static void sendEmail(String Bcc, String subject, String text, List<String> attachments) throws MessagingException, IOException, ConfigurationException {

        ConfigParams confParams = ConfigurationManager.getConfiguration().getConfParams();

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.eu.mailgun.org");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        InternetAddress[] myBccList = InternetAddress.parse(Bcc);

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(confParams.getSender_email(), confParams.getSender_email_pass());
                    }
                });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(confParams.getSender_email()));
        message.setRecipients(Message.RecipientType.TO, myBccList); // BCC -ში ყრია კლიენტების მეილები

        message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
        message.setHeader("Content-Type", "text/plain; charset=UTF-8");


        Multipart multipart = new MimeMultipart();

        BodyPart textBodyPart = new MimeBodyPart();
//        textBodyPart.setText(text);
        textBodyPart.setContent(text, "text/plain; charset=UTF-8");
        multipart.addBodyPart(textBodyPart);
        if (attachments != null) {
            for (String fileName : attachments) {
                addAttachment(multipart, Constants.uploadPath + "\\" + fileName);
            }
        }
        message.setContent(multipart);

        Transport.send(message);
    }

//    public static void main(String[] args) {
//
//        List<String> attachments = new ArrayList<String>();
//        attachments.add("C:\\Users\\home\\Desktop\\File N1.pdf");
//        attachments.add("C:\\Users\\home\\Desktop\\File N2.docx");
//        try {
//            DhlMail.sendEmail("uchachaduneli@gmail.com, chaduneliucha@gmail.com",
//                    "asdasda ასდას ",
//                    "ასდასდ ama da am kompaniis xelSekrulebis vada gadaiwia amdeniT da amdeniT",
//                    attachments);
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
