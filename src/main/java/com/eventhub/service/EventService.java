package com.eventhub.service;

import com.eventhub.model.Event;
import com.eventhub.model.Participant;
import com.eventhub.repository.EventRepository;
import com.eventhub.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Optional<Event> existingEvent = eventRepository.findById(id);
        if (existingEvent.isPresent()) {
            Event event = existingEvent.get();
            event.setName(updatedEvent.getName());
            event.setDate(updatedEvent.getDate());
            event.setLocation(updatedEvent.getLocation());
            return eventRepository.save(event);
        }
        return null;
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Autowired
    private JavaMailSender mailSender;

    public Event registerParticipant(Long eventId, Long participantId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Participant> participantOpt = participantRepository.findById(participantId);

        if (eventOpt.isPresent() && participantOpt.isPresent()) {
            Event event = eventOpt.get();
            Participant participant = participantOpt.get();

            event.getParticipants().add(participant);
            participant.getEvents().add(event);

            eventRepository.save(event);
            participantRepository.save(participant);

            // Simulação de notificação: Imprime no console.
            System.out.println("Notificação: O participante " + participant.getName() +
                    " foi inscrito no evento " + event.getName() + ".");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(participant.getEmail());
            message.setSubject("Convite para Evento: " + event.getName());
            message.setText("Olá " + participant.getName() + ",\n\nVocê foi inscrito no evento '" + event.getName() +
                    "' que ocorrerá em " + event.getDate() + " no local " + event.getLocation() +
                    ".\n\nEsperamos você lá!\nEquipe de Gerenciamento de Eventos");
            mailSender.send(message);

            return event;
        }
        return null;
    }

}