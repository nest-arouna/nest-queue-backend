package com.app.queue.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.UUID;


@Data @AllArgsConstructor @NoArgsConstructor
public class QueueDtoResponse
{

    private UUID ID;
    private UUID doctorID;
    private String type;
    private String doctor;
    private Boolean status;
    private long slot;
    private Date createdOn;
    private Date updatedOn;
    private Boolean isRdv;


}