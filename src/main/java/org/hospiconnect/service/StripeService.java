package org.hospiconnect.service;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class StripeService {

    public StripeService() {
        Stripe.apiKey = "sk_test_51Qw6Jj2HJALCS9kukoEah12re5Nl72TRcfcl6zfjAk0TfPysHnjGqaIh2SBSIAwFx983OujqQlAc3jiexsfo2nUV00DAooSw34"; // Remplace par ta clé secrète
    }

    public String createCheckoutSession(double amount) throws Exception {
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("https://example.com/success") // à remplacer
                        .setCancelUrl("https://example.com/cancel")   // à remplacer
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("eur")
                                                        .setUnitAmount((long) (amount * 100)) // en centimes
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("Don")
                                                                        .build()
                                                        )
                                                        .build()
                                        )
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}

