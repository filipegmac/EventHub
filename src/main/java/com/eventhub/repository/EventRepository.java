package com.eventhub.repository;

import com.eventhub.model.Event;
import com.eventhub.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOwner(Participant owner);
}