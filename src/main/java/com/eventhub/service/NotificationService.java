package com.eventhub.service;

import com.eventhub.model.Event;
import com.eventhub.model.Notification;
import com.eventhub.model.Participant;
import com.eventhub.repository.EventRepository;
import com.eventhub.repository.NotificationRepository;
import com.eventhub.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private EventRepository eventRepository;

    public void createNotification(Participant participant, String message) {
        Notification notification = new Notification(participant, message);
        notificationRepository.save(notification);
    }

    public void createInvite(Participant participant, Event event, String message) {
        Notification notification = new Notification(participant, event, message);
        notificationRepository.save(notification);
    }

    @Transactional
    public void acceptInvite(Long notificationId) {
        Optional<Notification> opt = notificationRepository.findById(notificationId);
        if (opt.isPresent()) {
            Notification notification = opt.get();
            if (notification.isInvite() && notification.isPending()) {

                Event event = notification.getEvent();
                Participant participant = notification.getParticipant();
                
                event.getParticipants().add(participant);
                participant.getEvents().add(event);
                
                eventRepository.save(event);
                participantRepository.save(participant);
                
                notification.setStatus("ACCEPTED");
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    public void rejectInvite(Long notificationId) {
        Optional<Notification> opt = notificationRepository.findById(notificationId);
        if (opt.isPresent()) {
            Notification notification = opt.get();
            if (notification.isInvite() && notification.isPending()) {
                notification.setStatus("REJECTED");
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }
    }

    public List<Notification> getNotificationsByEmail(String email) {
        Participant participant = participantRepository.findByEmail(email).orElse(null);
        if (participant == null) {
            return List.of();
        }
        return notificationRepository.findByParticipantOrderByCreatedAtDesc(participant);
    }

    public long countUnreadByEmail(String email) {
        Participant participant = participantRepository.findByEmail(email).orElse(null);
        if (participant == null) {
            return 0;
        }
        return notificationRepository.countByParticipantAndReadFalse(participant);
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllAsRead(String email) {
        List<Notification> notifications = getNotificationsByEmail(email);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }
}
