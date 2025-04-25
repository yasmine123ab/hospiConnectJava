package org.hospiconnect.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {

    private final String ACCOUNT_SID = "AC7884e7eba29839acaaede244d5ca7c76";
    private final String AUTH_TOKEN ="40f8c167acbdd84c643d39afc18536b0";
    private final String FROM_NUMBER ="+19142667899";

    public TwilioService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendThankYouSms(String toPhoneNumber, String donorName) {
        String messageBody = "Merci " + donorName + " pour votre généreux don à HospiConnect 💙";

        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(FROM_NUMBER),
                messageBody
        ).create();

        System.out.println("SMS envoyé à " + toPhoneNumber + " avec l'ID : " + message.getSid());
    }
}
