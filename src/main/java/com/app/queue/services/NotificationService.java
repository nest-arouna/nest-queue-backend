package com.app.queue.services;
import com.app.queue.dto.request.SmsDtoRequest;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService  implements  INotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    static final String  subject="Ux8BPAk4wnXN0leW4a92iVHXeDl9ZXAf";
    static final String key="X0ikbHQaZQProhhB";
    static final String signature="https://api.orange.com/";
    static final String token="c5f51a8b487e9eaa757b1328c83d5fdd";
    @Autowired
    RestTemplate restTemplate;
    private boolean result ;
    public boolean sendSms(SmsDtoRequest sms)
    {
        String link= "https://api.orangesmspro.sn:8443/api";
        String key_private="ee6428bd9ded0736a7b87323b841c257";
        String token="ae5807302a4e95fd4c74ec7e8aed78e5";
        String login = "nest";
        String subject="Notification file d'attente";
        String signature="NEST";
        String recipient=sms.getPhone();
        String content=sms.getMessage();
        String password = token;
        String authString = login + ":" + password;
        String authStringEnc =Base64.getEncoder().encodeToString(authString.getBytes());
        try
        {
            content=URLEncoder.encode(content, "UTF8");
            subject=URLEncoder.encode(subject, "UTF8");
            signature=URLEncoder.encode(signature, "UTF8");
            long timestamp = System.currentTimeMillis()/1000;
            String msgToEncrypt=token+subject+signature+recipient+content+timestamp;
            String key=hmacSha(key_private, msgToEncrypt);
            System.setProperty("javax.net.ssl.trustStore", "clienttrust");
            //download, extract and paste clienttrust file in the project
            //link https://www.orangesmspro.sn/app/alertsms/user/clienttrust.php
            String url= link+"?token="+token+"&subject="+subject+"&signature="+signature+"&recipient="+recipient+"&content="
                    +content+"&timestamp="+timestamp+"&key="+key;
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestProperty("Authorization", "Basic " + authStringEnc);
            int responseCode = con.getResponseCode();
            if (responseCode == 200)
            {
               result=true;

            }

        }

        catch(Exception e)
        {
            logger.error("sms non envoyé au numéro: "+ sms.getPhone());

        }

        return result;
    }



    public static String hmacSha(String SECRETKEY, String VALUE)
    {

        try {
            SecretKeySpec signingKey = new SecretKeySpec(SECRETKEY.getBytes("UTF-8"), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(VALUE.getBytes("UTF-8"));
            byte[] hexArray = {
                    (byte)'0', (byte)'1', (byte)'2', (byte)'3',
                    (byte)'4', (byte)'5', (byte)'6', (byte)'7',
                    (byte)'8', (byte)'9', (byte)'a', (byte)'b',
                    (byte)'c', (byte)'d', (byte)'e', (byte)'f'
            };
            byte[] hexChars = new byte[rawHmac.length * 2];
            for ( int j = 0; j < rawHmac.length; j++ ) {
                int v = rawHmac[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}