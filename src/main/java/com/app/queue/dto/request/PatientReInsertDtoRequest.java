package com.app.queue.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class PatientReInsertDtoRequest
{
    private UUID patientID;
    private UUID queueID;

}
