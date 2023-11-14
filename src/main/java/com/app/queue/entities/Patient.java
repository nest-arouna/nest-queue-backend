package com.app.queue.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class Patient
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID ID;
    private UUID queueID;
    private String lastname;
    private String firstname;
    private String phone;
    private long birthDay;
    private boolean finished;
    private boolean canceled;
    private UUID queueDelayID;
    private boolean delay;
    private boolean delayMoreThanLimit;
    private boolean isInsert;
    private boolean isChangeStateOnce;
    private String canceledMotif;
    private long  finishedHour;
    private boolean status=true;
    private long arrivalOrRegistedHours;
    private long orderNumber;
    private long  waitingTime;
    private long rdvHour;
    private long rdvHourTempon;
    private long createdPatient;
    @CreationTimestamp
    private Date createdOn;
    @UpdateTimestamp
    private Date updatedOn;
}
