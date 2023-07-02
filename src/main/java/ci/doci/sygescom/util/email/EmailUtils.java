package ci.doci.sygescom.util.email;

import ci.doci.sygescom.service.DomainUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class EmailUtils {

    private final DomainUserDetailsService userService;

    public EmailUtils(DomainUserDetailsService userService) {
        this.userService = userService;
    }

    public Mailer mailerDiffusion(String intitule) throws Exception {
        String[] base64String = new String[1];
        String[] filenameString = new String[1];
        String path = "";
        String base64Binary = encodeFileToBase64Binary(path);
        String fileName = "test.png";
        base64String[0] = base64Binary;
        filenameString[0] = fileName;

        Mailer mailer = new Mailer();
        mailer.setCc("");
        mailer.setRecipientEmail(userService.formatMailPersonnel());
        mailer.setMessageObject("Une nouvelle exigence reglementaire a été transmise. " +
                "\nConnectez vous à l'applicatif de veille reglementaire " +
                "pour plus d'informations.");
        mailer.setSubject("Nouvelle exigence reglementaire - " + intitule);
        mailer.setMessageType("AA");
        mailer.setApplicationId("VEILLE_REGLEMENTAIRE");
        mailer.setBaseString(base64String);
        mailer.setFilename(filenameString);

        return mailer;
    }

    private String encodeFileToBase64Binary(String fileName)
            throws IOException {

        File file = new File(fileName);
        byte[] bytes = loadFile(file);
        byte[] encoded = Base64.encodeBase64(bytes);
        String encodedString = new String(encoded);

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }
}
