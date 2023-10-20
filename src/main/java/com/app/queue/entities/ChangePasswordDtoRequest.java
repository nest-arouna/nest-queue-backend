package com.app.queue.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @AllArgsConstructor @NoArgsConstructor
public class ChangePasswordDtoRequest
{
	private UUID ID;
	private int isAdmin;
	private String oldPassword;
	private String newPassword;
}
