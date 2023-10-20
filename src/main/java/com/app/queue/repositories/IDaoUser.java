package com.app.queue.repositories;
import com.app.queue.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDaoUser extends JpaRepository<Utilisateur, UUID> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByPhone(String phone);
    List<Utilisateur> findByStatus(Boolean status);


}
