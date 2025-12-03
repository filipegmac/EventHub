package com.eventhub.controller;

import com.eventhub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @ModelAttribute("unreadCount")
    public long unreadNotificationsCount(Principal principal) {
        if (principal == null) {
            return 0;
        }
        return notificationService.countUnreadByEmail(principal.getName());
    }
}
