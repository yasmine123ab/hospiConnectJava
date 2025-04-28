package org.hospiconnect.service.laboratoire;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;

public class SMSService {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private static final String FROM_PHONE_NUMBER = dotenv.get("TWILIO_FROM_PHONE_NUMBER");
    private static final String TO_PHONE_NUMBER = dotenv.get("TWILIO_TO_PHONE_NUMBER");


    private static final SMSService instance = new SMSService();

    public SMSService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static SMSService getInstance() {
        return instance;
    }

    public void sendSms(String sms) {
        Message message = Message
                .creator(
                        new PhoneNumber(TO_PHONE_NUMBER),
                        new PhoneNumber(FROM_PHONE_NUMBER),
                        sms
                )
                .create();
        System.out.println(message.getSid());
    }
}
