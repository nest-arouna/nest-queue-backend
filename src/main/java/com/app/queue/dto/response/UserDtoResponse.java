package com.app.queue.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.util.UUID;


@Data @AllArgsConstructor @NoArgsConstructor
public class UserDtoResponse
{

    private UUID ID;
    private String lastname;
    private String firstname;
    private String adress;
    private String email;
    private String password;
    private String phone;
    private boolean status;
    private String role;
    private Date createdOn;
    private UUID queueID;
    private Date updatedOn;

}