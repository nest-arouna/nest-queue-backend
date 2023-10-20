package com.app.queue.rest;

import com.app.queue.dto.request.QueueDtoRequest;
import com.app.queue.dto.request.UserDtoRequest;
import com.app.queue.entities.ChangePasswordDtoRequest;
import com.app.queue.entities.Login;
import com.app.queue.entities.Reponse;
import com.app.queue.services.QueueService;
import com.app.queue.services.UserService;
import com.app.queue.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class QueueRestControler {
	@Autowired
	private QueueService queueService;


	@PostMapping(Utility.QUEUES)
	public Reponse getAddQueue(@RequestBody QueueDtoRequest queue){
		Reponse resultatCreation = queueService.create(queue);
		return resultatCreation;
    }

	@GetMapping(Utility.LOCKED_QUEUE)
	public Reponse lockQueue(@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =queueService.lockQueue(droitID);
		return userUpdate ;
	}
	@GetMapping(Utility.GET_QUEUE)
	public Reponse getQueue(@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =queueService.getById(droitID);
		return userUpdate ;
	}
	@GetMapping(Utility.QUEUES_NEXT_PATIENTS)
	public Reponse getNextPatient(){
		Reponse	userUpdate =queueService.getAllNextPatient();
		return userUpdate ;
	}
	@GetMapping(Utility.QUEUES_PATIENTS)
	public Reponse getQueuePatient(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =queueService.getAll(droitID,page,size);
		return userUpdate ;
	}
	@GetMapping(Utility.QUEUES)
	public Reponse getAllQueues(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String doctorID,
	        @RequestParam(required = false) Long createdOn
	){
		Reponse	userUpdate =queueService.getAll(type, doctorID, createdOn, page, size);
		return userUpdate ;
	}

	@GetMapping(Utility.QUEUES_FINISHED_PATIENT)
	public Reponse getQueuePatientFinished(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =queueService.getAllFinnishedPatient(droitID,page,size);
		return userUpdate ;
	}
	@GetMapping(Utility.QUEUES_DELAY_PATIENT)
	public Reponse getQueuePatientDelay(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =queueService.getAllDelayPatient(droitID,page,size);
		return userUpdate ;
	}
	@GetMapping(Utility.GET_ACTIVE_QUEUE)
	public Reponse getQueues()
	{
		Reponse	users =queueService.getAll();
		return users ;
	}
	@GetMapping(Utility.QUEUES_CANCELED_PATIENT)
	public Reponse getAllCanceledPatient(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =queueService.getAllCanceledPatient(droitID,page,size);
		return userUpdate ;
	}
}
