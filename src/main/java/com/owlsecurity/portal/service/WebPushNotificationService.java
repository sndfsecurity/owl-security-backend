package com.owlsecurity.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.owlsecurity.portal.entity.PushSubscription;
import com.owlsecurity.portal.repository.PushSubscriptionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushAsyncService;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.asynchttpclient.Response;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.owlsecurity.portal.entity.Report;


@Service
public class WebPushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;

    private final String vapidPublicKey;
    private final String vapidPrivateKey;
    private final String vapidSubject;

    public WebPushNotificationService(
            PushSubscriptionRepository pushSubscriptionRepository,
            ObjectMapper objectMapper,
            @Value("${webpush.vapid.public-key}")
            String vapidPublicKey,
            @Value("${webpush.vapid.private-key}")
            String vapidPrivateKey,
            @Value("${webpush.vapid.subject}")
            String vapidSubject
    ) {

        this.pushSubscriptionRepository =
                pushSubscriptionRepository;

        this.objectMapper = objectMapper;

        this.vapidPublicKey = vapidPublicKey;
        this.vapidPrivateKey = vapidPrivateKey;
        this.vapidSubject = vapidSubject;

        if (Security.getProvider(
                BouncyCastleProvider.PROVIDER_NAME
        ) == null) {

            Security.addProvider(
                    new BouncyCastleProvider()
            );
        }
    }

    
    
    
    public int sendReportNotification(Report report)
            throws Exception {

        Long clientId = report.getClientId();

        if (clientId == null) {
            return 0;
        }

        List<PushSubscription> subscriptions =
                pushSubscriptionRepository
                        .findByClientIdAndActiveTrue(clientId);

        if (subscriptions.isEmpty()) {
            return 0;
        }

        PushAsyncService pushService =
                new PushAsyncService(
                        vapidPublicKey,
                        vapidPrivateKey,
                        vapidSubject
                );

        Map<String, Object> payload =
                new HashMap<>();

        payload.put(
                "title",
                "New Security Report"
        );

        payload.put(
                "body",
                "A new security report has been submitted."
        );

        payload.put(
                "url",
                "/client/reports"
        );

        payload.put(
                "tag",
                "report-" + report.getId()
        );

        String payloadJson =
                objectMapper.writeValueAsString(
                        payload
                );

        int sentCount = 0;

        for (PushSubscription savedSubscription
                : subscriptions) {

            try {

                Notification notification =
                        new Notification(
                                savedSubscription.getEndpoint(),
                                savedSubscription.getP256dh(),
                                savedSubscription.getAuth(),
                                payloadJson
                        );

                Response response =
                        pushService
                                .send(notification)
                                .get(
                                        15,
                                        TimeUnit.SECONDS
                                );

                int statusCode =
                        response.getStatusCode();

                System.out.println(
                        "Report notification response for client "
                                + clientId
                                + ": "
                                + statusCode
                );

                if (statusCode >= 200
                        && statusCode < 300) {

                    sentCount++;

                } else if (
                        statusCode == 404
                                || statusCode == 410
                ) {

                    savedSubscription.setActive(false);

                    pushSubscriptionRepository.save(
                            savedSubscription
                    );
                }

            } catch (Exception e) {

                System.err.println(
                        "Failed to send report notification to endpoint: "
                                + savedSubscription.getEndpoint()
                );

                e.printStackTrace();
            }
        }

        return sentCount;
    }
}