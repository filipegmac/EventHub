package com.eventhub.service;

import com.eventhub.exception.ResourceNotFoundException;
import com.eventhub.model.Event;
import com.eventhub.model.Participant;
import com.eventhub.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Participant createParticipant(Participant participant) {

        participant.setPassword(passwordEncoder.encode(participant.getPassword()));
        return participantRepository.save(participant);
    }

    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    public Participant getParticipantById(Long id) {
        return participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participante não encontrado com ID: " + id));
    }

    public Participant updateParticipant(Long id, Participant updatedParticipant) {
        Participant participant = getParticipantById(id);

        participant.setName(updatedParticipant.getName());
        participant.setEmail(updatedParticipant.getEmail());

        return participantRepository.save(participant);
    }


    public void deleteParticipant(Long id) {
        if (!participantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Participante não encontrado com ID: " + id);
        }
        participantRepository.deleteById(id);
    }


    @Transactional(readOnly = true)
    public Participant getByEmail(String email) {
        return participantRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Participante não encontrado com email: " + email));
    }

    @Transactional(readOnly = true)
    public List<Event> getEventsByEmail(String email) {
        Participant participant = participantRepository.findByEmail(email).orElse(null);
        if (participant == null) {
            return List.of();
        }
        return participant.getEvents();
    }
}