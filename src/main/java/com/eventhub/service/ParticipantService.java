package com.eventhub.service;

import com.eventhub.model.Participant;
import com.eventhub.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;  // Injeção do repositório.


    public Participant createParticipant(Participant participant) {
        return participantRepository.save(participant);
    }


    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }


    public Optional<Participant> getParticipantById(Long id) {
        return participantRepository.findById(id);
    }


    public Participant updateParticipant(Long id, Participant updatedParticipant) {
        Optional<Participant> existingParticipant = participantRepository.findById(id);
        if (existingParticipant.isPresent()) {
            Participant participant = existingParticipant.get();
            participant.setName(updatedParticipant.getName());
            participant.setEmail(updatedParticipant.getEmail());
            // Nota: Eventos não são atualizados aqui; use inscrição no EventService.
            return participantRepository.save(participant);
        }
        return null;
    }


    public void deleteParticipant(Long id) {
        participantRepository.deleteById(id);
    }
}