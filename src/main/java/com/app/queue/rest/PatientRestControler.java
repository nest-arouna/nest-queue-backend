package com.app.queue.rest;

import com.app.queue.dto.request.PatientCancelDtoRequest;
import com.app.queue.dto.request.PatientDtoRequest;
import com.app.queue.dto.request.PatientFinishedDtoRequest;
import com.app.queue.dto.request.PatientReInsertDtoRequest;
import com.app.queue.entities.Reponse;
import com.app.queue.services.PatientService;
import com.app.queue.utils.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class PatientRestControler {
	@Autowired
	private PatientService patientService;


	@PostMapping(Utility.PATIENTS)
	public Reponse getAddPatient(@RequestBody PatientDtoRequest queue){
		Reponse resultatCreation = patientService.create(queue);
		return resultatCreation;
    }

	@GetMapping(Utility.LOCKED_PATIENT)
	public Reponse lockPatient(@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =patientService.lockPatient(droitID);
		return userUpdate ;
	}
	@GetMapping(Utility.DETAIL_PATIENT)
	public Reponse detailPatient(@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =patientService.getByDetail(droitID);
		return userUpdate ;
	}

	@PostMapping(Utility.REINSERT_PATIENT)
	public Reponse reinsertPatient(@RequestBody PatientReInsertDtoRequest queue){
		Reponse	userUpdate =patientService.reinsertPatient(queue);
		return userUpdate ;
	}
	@PostMapping(Utility.CANCELED_PATIENT)
	public Reponse lockPatient(@RequestBody PatientCancelDtoRequest queue){
		Reponse	userUpdate =patientService.canceledPatient(queue);
		return userUpdate ;
	}
	@GetMapping(Utility.GET_SUBSCRIBER_PATIENT)
	public Reponse getAllSubscriberQueues(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String firstname,
			@RequestParam(required = false) String lastname,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String doctorID,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) Long rdvHourStart,
			@RequestParam(required = false) Long rdvHourEnd,
			@RequestParam(required = false) Long createdOnStart,
			@RequestParam(required = false) Long createdOnEnd
	){


		Reponse	userUpdate =patientService.getAll(firstname,lastname, phone,doctorID, createdOnStart,createdOnEnd,type,rdvHourStart,rdvHourEnd, page, size);

		return userUpdate ;
	}
	@PostMapping(Utility.FINISHED_PATIENT)
	public Reponse finishedPatient(@RequestBody PatientFinishedDtoRequest queue){
		Reponse	userUpdate =patientService.finishedPatient(queue);
		return userUpdate ;
	}
	@GetMapping(Utility.GET_PATIENT)
	public Reponse gePatient(@PathVariable(value = "id") UUID droitID){
		Reponse	userUpdate =patientService.getById(droitID);
		return userUpdate ;
	}

	@GetMapping(Utility.PATIENTS)
	public Reponse getPatients()
	{
		Reponse	users =patientService.getAll();
		return users ;
	}
}
