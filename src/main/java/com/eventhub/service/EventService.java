package com.eventhub.service;

import com.eventhub.exception.ResourceNotFoundException;
import com.eventhub.model.Event;
import com.eventhub.model.Participant;
import com.eventhub.repository.EventRepository;
import com.eventhub.repository.ParticipantRepository;
import com.eventhub.repository.NotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }


    public List<Event> getEventsByOwner(Participant owner) {
        return eventRepository.findByOwner(owner);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event event = getEventById(id);
        
        event.setName(updatedEvent.getName());
        event.setDate(updatedEvent.getDate());
        event.setLocation(updatedEvent.getLocation());
        
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));

        for (Participant p : event.getParticipants()) {
            p.getEvents().remove(event);
        }
        event.getParticipants().clear();

        notificationRepository.deleteAll(notificationRepository.findByEvent(event));

        eventRepository.delete(event);
    }

    @Transactional
    public void leaveEvent(Long eventId, Participant participant) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + eventId));

        event.getParticipants().remove(participant);
        participant.getEvents().remove(event);

        eventRepository.save(event);
        participantRepository.save(participant);
    }

    public void inviteParticipantByEmail(Long eventId, String email) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + eventId));
        
        Participant participant = participantRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Participante não encontrado com email: " + email));

        String message = "Você foi convidado para o evento '" + event.getName() + 
                "' em " + event.getDate() + " - " + event.getLocation();
        notificationService.createInvite(participant, event, message);
    }

}