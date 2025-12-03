package com.eventhub.controller;

import com.eventhub.model.Event;
import com.eventhub.model.Participant;
import com.eventhub.service.EventService;
import com.eventhub.service.NotificationService;
import com.eventhub.service.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class WebController {

    @Autowired
    private EventService eventService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/")
    public String home(Model model, java.security.Principal principal) {
        List<Event> events;
        if (principal != null) {
            Participant owner = participantService.getByEmail(principal.getName());
            events = eventService.getEventsByOwner(owner);
        } else {
            events = List.of();
        }

        List<Participant> participants = participantService.getAllParticipants();

        model.addAttribute("totalEvents", events.size());
        model.addAttribute("totalParticipants", participants.size());
        model.addAttribute("recentEvents", events.stream().limit(5).toList());
        model.addAttribute("currentPage", "home");
        
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("participant", new Participant());
        return "register";
    }

    @PostMapping("/register")
    public String registerParticipant(@ModelAttribute Participant participant, 
                                      RedirectAttributes redirectAttributes) {
        try {
            participantService.createParticipant(participant);
            redirectAttributes.addFlashAttribute("success", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao cadastrar: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/events")
    public String listEvents(Model model, java.security.Principal principal) {
        Participant owner = participantService.getByEmail(principal.getName());
        model.addAttribute("events", eventService.getEventsByOwner(owner));
        model.addAttribute("currentPage", "events");
        return "events/list";
    }

    @GetMapping("/events/{id}")
    public String viewEvent(@PathVariable Long id,
                            Model model,
                            java.security.Principal principal,
                            RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);

        Participant current = participantService.getByEmail(principal.getName());
        boolean isOwner = event.getOwner() != null && event.getOwner().getId().equals(current.getId());
        boolean isParticipant = event.getParticipants().contains(current);

        if (!isOwner && !isParticipant) {
            redirectAttributes.addFlashAttribute("error", "Você não tem acesso a este evento.");
            return "redirect:/events";
        }

        model.addAttribute("event", event);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("canLeave", !isOwner && isParticipant);
        model.addAttribute("currentPage", "events");
        
        return "events/view";
    }

    @GetMapping("/events/new")
    public String newEventForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("currentPage", "events");
        return "events/form";
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable Long id,
                                Model model,
                                java.security.Principal principal,
                                RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);
        Participant current = participantService.getByEmail(principal.getName());
        if (event.getOwner() == null || !event.getOwner().getId().equals(current.getId())) {
            redirectAttributes.addFlashAttribute("error", "Você não pode editar este evento.");
            return "redirect:/events";
        }

        model.addAttribute("event", event);
        model.addAttribute("currentPage", "events");
        return "events/form";
    }

    @PostMapping("/events/save")
    public String saveEvent(@ModelAttribute Event event,
                            java.security.Principal principal,
                            RedirectAttributes redirectAttributes) {
        Participant current = participantService.getByEmail(principal.getName());

        if (event.getId() == null) {
            event.setOwner(current);
            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("message", "Evento criado com sucesso!");
        } else {
            Event existing = eventService.getEventById(event.getId());
            if (existing.getOwner() == null || !existing.getOwner().getId().equals(current.getId())) {
                redirectAttributes.addFlashAttribute("error", "Você não pode editar este evento.");
                return "redirect:/events";
            }
            eventService.updateEvent(event.getId(), event);
            redirectAttributes.addFlashAttribute("message", "Evento atualizado com sucesso!");
        }
        return "redirect:/events";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id,
                              java.security.Principal principal,
                              RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(id);
        Participant current = participantService.getByEmail(principal.getName());
        if (event.getOwner() == null || !event.getOwner().getId().equals(current.getId())) {
            redirectAttributes.addFlashAttribute("error", "Você não pode excluir este evento.");
            return "redirect:/events";
        }

        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("message", "Evento excluído com sucesso!");
        return "redirect:/events";
    }

    @PostMapping("/events/{eventId}/invite")
    public String inviteParticipant(@PathVariable Long eventId,
                                    @RequestParam String email,
                                    java.security.Principal principal,
                                    RedirectAttributes redirectAttributes) {
        Event event = eventService.getEventById(eventId);
        Participant current = participantService.getByEmail(principal.getName());
        if (event.getOwner() == null || !event.getOwner().getId().equals(current.getId())) {
            redirectAttributes.addFlashAttribute("error", "Você não pode convidar participantes para este evento.");
            return "redirect:/events/" + eventId;
        }

        try {
            eventService.inviteParticipantByEmail(eventId, email);
            redirectAttributes.addFlashAttribute("message", "Participante convidado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao convidar participante: " + e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }

    @PostMapping("/events/{eventId}/leave")
    public String leaveEvent(@PathVariable Long eventId,
                             java.security.Principal principal,
                             RedirectAttributes redirectAttributes) {
        Participant current = participantService.getByEmail(principal.getName());
        eventService.leaveEvent(eventId, current);
        redirectAttributes.addFlashAttribute("message", "Você saiu do evento.");
        return "redirect:/my-events";
    }

    @GetMapping("/participants")
    public String listParticipants(Model model) {
        model.addAttribute("participants", participantService.getAllParticipants());
        model.addAttribute("currentPage", "participants");
        return "participants/list";
    }

    @GetMapping("/participants/{id}")
    public String viewParticipant(@PathVariable Long id, Model model) {
        model.addAttribute("participant", participantService.getParticipantById(id));
        model.addAttribute("currentPage", "participants");
        return "participants/view";
    }

    @GetMapping("/participants/new")
    public String newParticipantForm(Model model) {
        model.addAttribute("participant", new Participant());
        model.addAttribute("currentPage", "participants");
        return "participants/form";
    }

    @GetMapping("/participants/edit/{id}")
    public String editParticipantForm(@PathVariable Long id, Model model) {
        model.addAttribute("participant", participantService.getParticipantById(id));
        model.addAttribute("currentPage", "participants");
        return "participants/form";
    }

    @PostMapping("/participants/save")
    public String saveParticipant(@ModelAttribute Participant participant, RedirectAttributes redirectAttributes) {
        if (participant.getId() == null) {
            participantService.createParticipant(participant);
            redirectAttributes.addFlashAttribute("message", "Participante criado com sucesso!");
        } else {
            participantService.updateParticipant(participant.getId(), participant);
            redirectAttributes.addFlashAttribute("message", "Participante atualizado com sucesso!");
        }
        return "redirect:/participants";
    }

    @GetMapping("/participants/delete/{id}")
    public String deleteParticipant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        participantService.deleteParticipant(id);
        redirectAttributes.addFlashAttribute("message", "Participante excluído com sucesso!");
        return "redirect:/participants";
    }

    @GetMapping("/notifications")
    public String listNotifications(Model model, java.security.Principal principal) {
        String email = principal.getName();
        model.addAttribute("notifications", notificationService.getNotificationsByEmail(email));
        model.addAttribute("currentPage", "notifications");
        return "notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllAsRead(java.security.Principal principal) {
        notificationService.markAllAsRead(principal.getName());
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/{id}/accept")
    public String acceptInvite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.acceptInvite(id);
        redirectAttributes.addFlashAttribute("message", "Convite aceito! Você foi adicionado ao evento.");
        return "redirect:/notifications";
    }

    @PostMapping("/notifications/{id}/reject")
    public String rejectInvite(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.rejectInvite(id);
        redirectAttributes.addFlashAttribute("message", "Convite recusado.");
        return "redirect:/notifications";
    }

    @GetMapping("/my-events")
    public String myEvents(Model model, java.security.Principal principal) {
        String email = principal.getName();
        model.addAttribute("events", participantService.getEventsByEmail(email));
        model.addAttribute("currentPage", "my-events");
        return "my-events";
    }
}
