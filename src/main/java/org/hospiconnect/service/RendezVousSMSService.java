package org.hospiconnect.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;
import org.hospiconnect.utils.Logger;

public class RendezVousSMSService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final Logger logger = Logger.getInstance();

    // Les clés sont stockées dans le fichier .env
    private static final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private static final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private static final String FROM_PHONE_NUMBER = dotenv.get("TWILIO_PHONE_NUMBER");

    private static final RendezVousSMSService instance = new RendezVousSMSService();

    private RendezVousSMSService() {
        try {
            if (ACCOUNT_SID == null || AUTH_TOKEN == null || FROM_PHONE_NUMBER == null) {
                throw new IllegalStateException("Les variables d'environnement Twilio ne sont pas configurées");
            }
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            logger.info("Service SMS initialisé avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation du service SMS: " + e.getMessage());
            throw new RuntimeException("Impossible d'initialiser le service SMS", e);
        }
    }

    public static RendezVousSMSService getInstance() {
        return instance;
    }

    public void sendSms(String message, String phoneNumber) {
        try {
            // Vérification du format du numéro de téléphone tunisien
            if (!phoneNumber.matches("^\\+216[0-9]{8}$")) {
                throw new IllegalArgumentException("Format de numéro de téléphone invalide. Le numéro doit commencer par +216 suivi de 8 chiffres");
            }

            // Envoi du SMS
            Message twilioMessage = Message
                    .creator(
                            new PhoneNumber(phoneNumber),
                            new PhoneNumber(FROM_PHONE_NUMBER),
                            message
                    )
                    .create();

            logger.info("SMS envoyé avec succès. SID: " + twilioMessage.getSid());
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi du SMS: " + e.getMessage());
            throw new RuntimeException("Impossible d'envoyer le SMS: " + e.getMessage(), e);
        }
    }
}


