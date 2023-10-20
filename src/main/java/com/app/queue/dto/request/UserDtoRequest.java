package com.app.queue.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class UserDtoRequest
{
    private UUID ID;
    private String lastname;
    private String firstname;
    private String adress;
    private String email;
    private String password;
    private String phone;
    private String role;


}
