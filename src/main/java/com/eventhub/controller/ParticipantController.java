package com.eventhub.controller;

import com.eventhub.model.Participant;
import com.eventhub.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller REST para gerenciar Participantes.
 * Endpoints como /api/participants para CRUD.
 */
@RestController
@RequestMapping("/api/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;  // Injeção do serviço.

    /**
     * Endpoint para criar um novo participante (POST).
     *
     * @param participant Dados do participante.
     * @return O participante criado.
     */
    @PostMapping
    public Participant createParticipant(@RequestBody Participant participant) {
        return participantService.createParticipant(participant);
    }

    /**
     * Endpoint para listar todos os participantes (GET).
     *
     * @return Lista de participantes.
     */
    @GetMapping
    public List<Participant> getAllParticipants() {
        return participantService.getAllParticipants();
    }

    /**
     * Endpoint para buscar um participante por ID (GET).
     *
     * @param id ID do participante.
     * @return ResponseEntity com o participante ou 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
        Optional<Participant> participant = participantService.getParticipantById(id);
        return participant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para atualizar um participante (PUT).
     *
     * @param id ID do participante.
     * @param participant Dados atualizados.
     * @return ResponseEntity com o participante atualizado ou 404.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Participant> updateParticipant(@PathVariable Long id, @RequestBody Participant participant) {
        Participant updated = participantService.updateParticipant(id, participant);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Endpoint para deletar um participante (DELETE).
     *
     * @param id ID do participante.
     * @return ResponseEntity indicando sucesso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long id) {
        participantService.deleteParticipant(id);
        return ResponseEntity.noContent().build();
    }
}