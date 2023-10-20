package com.app.queue.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data @AllArgsConstructor @NoArgsConstructor
public class NextPatientDtoResponse
{

    private UUID ID;
    private UUID queueID;
    private String doctor;
    private String lastname;
    private String firstname;
    private long birthDay;
    private long rdvHour;
    private String phone;
    private long arrivalOrRegistedHours;
    private boolean delayMoreThanLimit;
    private boolean status;
    private boolean finished;
    private long  waitingTime;
    private long rdvHourTempon;
    private boolean delay;
    private boolean canceled;
    private String canceledMotif;
    private Date createdOn;
    private Date updatedOn;

}