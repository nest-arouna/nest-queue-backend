package com.app.queue.repositories;
import com.app.queue.entities.Queue;
import com.app.queue.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDaoQueue extends JpaRepository<Queue, UUID> {
    List<Queue> findByStatus(Boolean status);
    List<Queue> findByDoctorID(UUID ID);
    Optional<Queue> findByIDAndTypeContainingIgnoreCase(UUID ID,String type);

}
