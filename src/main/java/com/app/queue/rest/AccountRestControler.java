package com.app.queue.rest;

import com.app.queue.dto.request.UserDtoRequest;
import com.app.queue.entities.ChangePasswordDtoRequest;
import com.app.queue.entities.Login;
import com.app.queue.entities.Reponse;
import com.app.queue.services.UserService;
import com.app.queue.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
public class AccountRestControler {
	@Autowired
	private UserService accountService;


	@PostMapping(Utility.USERS)
	public Reponse getAddUser(@RequestBody UserDtoRequest user){
		Reponse resultatCreation = accountService.create(user);
		return resultatCreation;
    }

	@PostMapping(Utility.DO_LOGIN)
	public Reponse Login(@RequestBody Login login){

		Reponse resultatCreation = accountService.login_in(login);

		return resultatCreation;
	}
	@PostMapping(Utility.UPDATE_PASSWORD)
	public Reponse updatePwd(@PathVariable(value = "id") UUID userID ,@RequestBody ChangePasswordDtoRequest changePasswordDtoRequest){
		changePasswordDtoRequest.setID(userID);
		Reponse resultatCreation = accountService.changePassword(changePasswordDtoRequest);

		return resultatCreation;
	}
	@PostMapping(Utility.UPDATE_USER)
	public Reponse updateUser( @RequestBody UserDtoRequest user){
		Reponse updateDroit = accountService.update(user);
		return updateDroit;
    }



	@GetMapping(Utility.DELETE_USER_BY_ID)
	public Reponse lockUser(@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =accountService.lockUser(droitID);
		return userUpdate ;
	}

	@GetMapping(Utility.PERSONNALS)
	public Reponse getUsers(
			@RequestParam(required = false) String lastname,
			@RequestParam(required = false) String firstname,
			@RequestParam(required = false) String adress,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String role,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size

	)
	{
		Reponse	users =accountService.getAllProfesionnals( lastname,firstname,adress,email,phone, role,size,page);
		return users ;
	}
	@GetMapping(Utility.DOCTORS)
	public Reponse getUsers(
			@RequestParam(required = false) String lastname,
			@RequestParam(required = false) String firstname,
			@RequestParam(required = false) String adress,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size

	)
	{
		Reponse	users =accountService.getAllDoctors( lastname,firstname,adress,email,phone,size,page);
		return users ;
	}
	@GetMapping(Utility.DOCTORS_DISPLAY)
	public Reponse getUsers()
	{
		Reponse	users =accountService.getAllDoctors();
		return users ;
	}
}
