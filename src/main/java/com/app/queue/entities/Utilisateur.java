package com.app.queue.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class Utilisateur
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;
    private String lastname;
    private String firstname;
    private String adress;
    private String email;
    private String password;
    private String phone;
    private boolean status;
    private String role;
    @CreationTimestamp
    private Date createdOn;
    @UpdateTimestamp
    private Date updatedOn;
}
