package com.eventhub.config;

import com.eventhub.model.Participant;
import com.eventhub.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (participantRepository.findByEmail("admin@eventhub.com").isEmpty()) {
            Participant admin = new Participant();
            admin.setName("Administrador");
            admin.setEmail("admin@eventhub.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            participantRepository.save(admin);
            System.out.println("✓ Usuário admin criado: admin@eventhub.com / admin");
        }
    }
}
