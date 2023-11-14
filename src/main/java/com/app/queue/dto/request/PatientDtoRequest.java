package com.app.queue.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class PatientDtoRequest
{
    private UUID ID;
    private UUID queueID;
    private String lastname;
    private String firstname;
    private long birthDay;
    private long orderNumber;
    private long rdvHour;
    private String phone;
    private long arrivalOrRegistedHours;
    private String canceledMotif;


}
