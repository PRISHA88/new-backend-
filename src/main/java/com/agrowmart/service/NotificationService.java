
package com.agrowmart.service;

import com.agrowmart.entity.Notification;
import com.agrowmart.entity.User;
import com.agrowmart.repository.NotificationRepository;
import com.agrowmart.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public NotificationService(UserRepository userRepository, NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public void sendNotification(Long userId, String title, String body, Map<String, String> data) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || user.getFcmToken() == null || user.getFcmToken().trim().isEmpty()) {
            System.out.println("Skipped notification → User ID: " + userId + " (No FCM token)");
            return;
        }

        String token = user.getFcmToken();

        Notification log = new Notification(user, token, title, body);
        log.setSuccess(false);
        notificationRepository.save(log);

        try {
            Message.Builder msg = Message.builder()
                    .setToken(token)
                    .putData("title", title)
                    .putData("body", body)
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK");

            if (data != null) {
                data.forEach(msg::putData);
            }

            String messageId = FirebaseMessaging.getInstance().send(msg.build());

            log.setSuccess(true);
            log.setMessageId(messageId);
            notificationRepository.save(log);

            System.out.println("Sent → " + user.getName() + " | " + title + " | ID: " + messageId);

        } catch (Exception e) {
            log.setMessageId("FAILED: " + e.getMessage());
            notificationRepository.save(log);
            System.err.println("FCM Failed for " + user.getName() + ": " + e.getMessage());
        }
    }
}



