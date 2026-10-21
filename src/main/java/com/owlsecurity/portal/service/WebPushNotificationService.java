package com.owlsecurity.portal.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.owlsecurity.portal.entity.PushSubscription;
import com.owlsecurity.portal.repository.PushSubscriptionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushAsyncService;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.owlsecurity.portal.repository.ReportMessageRepository;

import org.asynchttpclient.Response;

import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.owlsecurity.portal.entity.Report;

import com.owlsecurity.portal.entity.Client;
import com.owlsecurity.portal.repository.ClientRepository;

@Service
public class WebPushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final ClientRepository clientRepository;
    private final ReportMessageRepository reportMessageRepository;

    private final String vapidPublicKey;
    private final String vapidPrivateKey;
    private final String vapidSubject;

    public WebPushNotificationService(
            PushSubscriptionRepository pushSubscriptionRepository,
            ObjectMapper objectMapper,
            ClientRepository clientRepository,
            ReportMessageRepository reportMessageRepository,
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
        this.clientRepository = clientRepository;
        this.reportMessageRepository = reportMessageRepository;

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
    

		Client client = clientRepository.findById(clientId)
		        .orElse(null);
		
		String companyName = client != null
		        && client.getCompanyName() != null
		        && !client.getCompanyName().isBlank()
		        ? client.getCompanyName().trim()
		        : "Client";
		
		String notificationMessage =
		        getNotificationPreview(report.getNotes());
		
		if (notificationMessage.isBlank()) {
		    notificationMessage = "A new security report has been submitted.";
		}
		
		Map<String, Object> payload = new HashMap<>();
		
		payload.put(
		        "title",
		        "New Report: " + companyName
		);
		
		payload.put(
		        "body",
		        notificationMessage
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
    

		public int sendClientMessageNotification(Report report)
		        throws Exception {
		
		    Long clientId = report.getClientId();
		
		    if (clientId == null) {
		        return 0;
		    }
		
		    Client client = clientRepository.findById(clientId)
		            .orElse(null);
		
		    String companyName = client != null
		            && client.getCompanyName() != null
		            && !client.getCompanyName().isBlank()
		            ? client.getCompanyName().trim()
		            : "A client";
		
		    List<com.owlsecurity.portal.entity.ReportMessage> messages =
		            reportMessageRepository
		                    .findByReportIdOrderByCreatedAtAsc(report.getId());
		
		    String messagePreview = messages.stream()
		            .filter(message ->
		                    "CLIENT".equalsIgnoreCase(message.getSenderRole()))
		            .max(java.util.Comparator.comparing(
		                    com.owlsecurity.portal.entity.ReportMessage::getCreatedAt))
		            .map(message -> getMessagePreview(message.getMessage()))
		            .orElse("A new message has been received.");
		
		    return sendMessagePush(
		            pushSubscriptionRepository.findByRoleAndActiveTrue("ADMIN"),
		            "New Message — " + companyName,
		            messagePreview + "\nReport: " + buildReportDetails(report),
		            "/clients/" + clientId + "/reports?reportId="
		                    + report.getId() + "&openConversation=true",
		            "report-message-" + report.getId()
		    );
		}



		public int sendAdminReplyNotification(Report report)
		        throws Exception {
		
		    Long clientId = report.getClientId();
		
		    if (clientId == null) {
		        return 0;
		    }
		
		    List<com.owlsecurity.portal.entity.ReportMessage> messages =
		            reportMessageRepository
		                    .findByReportIdOrderByCreatedAtAsc(report.getId());
		
		    String messagePreview = messages.stream()
		            .filter(message ->
		                    "ADMIN".equalsIgnoreCase(message.getSenderRole()))
		            .max(java.util.Comparator.comparing(
		                    com.owlsecurity.portal.entity.ReportMessage::getCreatedAt))
		            .map(message -> getMessagePreview(message.getMessage()))
		            .orElse("A new admin reply has been received.");
		
		    return sendMessagePush(
		            pushSubscriptionRepository.findByClientIdAndActiveTrue(clientId),
		            "Admin Reply — OWL Security",
		            messagePreview + "\nReport: " + buildReportDetails(report),
		            "/client/reports?reportId="
		                    + report.getId() + "&openConversation=true",
		            "report-reply-" + report.getId()
		    );
		}

    
    
    //helper method..........
   

			private String buildReportDetails(Report report) {
			    String reportDate = report.getReportDate();
			    String reportTime = report.getReportTime();
			
			    if (reportDate == null || reportDate.isBlank()) {
			        reportDate = "Date unavailable";
			    }
			
			    if (reportTime == null || reportTime.isBlank()) {
			        return reportDate;
			    }
			
			    return reportDate + " · " + reportTime;
			}
			

			private int sendMessagePush(
			        List<PushSubscription> subscriptions,
			        String title,
			        String body,
			        String url,
			        String tag
			) throws Exception {
			
			    if (subscriptions.isEmpty()) {
			        return 0;
			    }
			
			    PushAsyncService pushService = new PushAsyncService(
			            vapidPublicKey,
			            vapidPrivateKey,
			            vapidSubject
			    );
			
			    Map<String, Object> payload = new HashMap<>();
			    payload.put("title", title);
			    payload.put("body", body);
			    payload.put("url", url);
			    payload.put("tag", tag);
			
			    String payloadJson = objectMapper.writeValueAsString(payload);
			    int sentCount = 0;
			
			    for (PushSubscription subscription : subscriptions) {
			        try {
			            Notification notification = new Notification(
			                    subscription.getEndpoint(),
			                    subscription.getP256dh(),
			                    subscription.getAuth(),
			                    payloadJson
			            );
			
			            Response response = pushService.send(notification)
			                    .get(15, TimeUnit.SECONDS);
			
			            int statusCode = response.getStatusCode();
			
			            if (statusCode >= 200 && statusCode < 300) {
			                sentCount++;
			            } else if (statusCode == 404 || statusCode == 410) {
			                subscription.setActive(false);
			                pushSubscriptionRepository.save(subscription);
			            }
			
			        } catch (Exception e) {
			            System.err.println(
			                    "Failed to send message notification: "
			                            + e.getMessage()
			            );
			        }
			    }
			
			    return sentCount;
			}
			
			

			private String getNotificationPreview(String notes) {
			    if (notes == null || notes.isBlank()) {
			        return "A new security report has been submitted.";
			    }
			
			    // Convert HTML line breaks into newline characters
			    String plainText = notes
			            .replaceAll("(?i)<br\\s*/?>", "\n")
			            .replaceAll("(?i)</p\\s*>", "\n")
			            .replaceAll("(?i)</div\\s*>", "\n")
			            .replaceAll("(?i)</li\\s*>", "\n");
			
			    // Remove remaining HTML tags
			    plainText = plainText.replaceAll("<[^>]*>", "");
			
			    // Decode common HTML entities
			    plainText = plainText
			            .replace("&nbsp;", " ")
			            .replace("&amp;", "&")
			            .replace("&lt;", "<")
			            .replace("&gt;", ">")
			            .replace("&quot;", "\"")
			            .replace("&#39;", "'")
			            .replace("&apos;", "'");
			
			    return plainText.lines()
			            .map(String::trim)
			            .filter(line -> !line.isEmpty())
			            .findFirst()
			            .orElse("A new security report has been submitted.");
			}
			
			

		private String getMessagePreview(String message) {
		    if (message == null || message.isBlank()) {
		        return "A new message has been received.";
		    }
		
		    String plainText = message
		            .replaceAll("(?i)<br\\s*/?>", "\n")
		            .replaceAll("(?i)</p\\s*>", "\n")
		            .replaceAll("(?i)</div\\s*>", "\n")
		            .replaceAll("(?i)</li\\s*>", "\n")
		            .replaceAll("<[^>]*>", "")
		            .replace("&nbsp;", " ")
		            .replace("&amp;", "&")
		            .replace("&lt;", "<")
		            .replace("&gt;", ">")
		            .replace("&quot;", "\"")
		            .replace("&#39;", "'")
		            .replace("&apos;", "'");
		
		    return plainText.lines()
		            .map(String::trim)
		            .filter(line -> !line.isEmpty())
		            .findFirst()
		            .orElse("A new message has been received.");
		}
		
}