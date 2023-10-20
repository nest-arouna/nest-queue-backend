package com.app.queue.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data @AllArgsConstructor @NoArgsConstructor
public class InscrisDtoResponse
{

    private UUID ID;
    private UUID queueID;
    private String lastname;
    private String firstname;
    private Long birthDay;
    private Long rdvHour;
    private String phone;
    private Long arrivalOrRegistedHours;
    private boolean status;
    private Boolean finished;
    private boolean delay;
    private Long  finishedHour;
    private Boolean canceled;
    private String canceledMotif;
    private Date createdOn;
    private Date updatedOn;
    private UUID doctorID;
    private String type;
    private String doctor;
    private Boolean isRdv;
}