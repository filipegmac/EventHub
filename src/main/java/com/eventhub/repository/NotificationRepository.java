package com.eventhub.repository;

import com.eventhub.model.Event;
import com.eventhub.model.Notification;
import com.eventhub.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByParticipantOrderByCreatedAtDesc(Participant participant);
    
    long countByParticipantAndReadFalse(Participant participant);

    List<Notification> findByEvent(Event event);
}