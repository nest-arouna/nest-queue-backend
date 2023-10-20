package com.app.queue.repositories;
import com.app.queue.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDaoPatient extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByPhone(String phone);
    List<Patient> findByStatus(Boolean status);
    List<Patient> findByQueueID(UUID queueID);


}
