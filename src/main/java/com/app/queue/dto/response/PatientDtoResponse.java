package com.app.queue.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@Data @AllArgsConstructor @NoArgsConstructor
public class PatientDtoResponse
{

    private UUID ID;
    private UUID queueID;
    private String lastname;
    private String firstname;
    private long birthDay;
    private long rdvHour;
    private String phone;
    private long arrivalOrRegistedHours;
    private boolean delayMoreThanLimit;
    private long rdvHourTempon;
    private boolean status;
    private boolean finished;
    private long  finishedHour;
    private long  waitingTime;
    private boolean delay;
    private boolean canceled;
    private boolean reInsertable;
    private String canceledMotif;
    private Date createdOn;
    private Date updatedOn;
    private long createdPatient;


}