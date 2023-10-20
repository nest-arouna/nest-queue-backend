package com.app.queue.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class QueueDtoRequest
{
    private UUID ID;
    private UUID doctorID;
    private String type;
    private long queueHourStart;
    private Boolean isRdv;
    private long slot;


}
