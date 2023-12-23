package com.app.queue.services;

import com.app.queue.dto.request.*;
import com.app.queue.dto.response.InscrisDtoResponse;
import com.app.queue.dto.response.PatientDtoResponse;
import com.app.queue.entities.Patient;
import com.app.queue.entities.Reponse;
import com.app.queue.repositories.IDaoPatient;
import com.app.queue.repositories.IDaoQueue;
import com.app.queue.repositories.IDaoUser;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PatientService implements  CrudService<PatientDtoRequest>{
    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);
    @Autowired(required=true)
    private NotificationService notificationService;
    @Autowired
    private IDaoPatient patientRepository;
    @Autowired
    private IDaoUser userRepository;
    @Autowired
    private IDaoQueue queueRepository;
    @Autowired(required=true)
    private ModelMapper modelMapper;
    @Override
    public Reponse create(PatientDtoRequest obj)
    {
        Reponse reponse = new Reponse();
        try
        {
            Patient patient =modelMapper.map(obj, Patient.class);
            patient.setCreatedPatient(new Date().getTime());
            if(obj.getQueueID() != null)
            {
                queueRepository.findById(obj.getQueueID()).ifPresent(v->
                {

                    // RDV HEURE EST DISPONIBLE
                    var ifRdvHourIsBusy=patientRepository.findByQueueID(obj.getQueueID())
                            .stream()
                            .sorted(Comparator.comparing(Patient::getCreatedOn).reversed())
                            .filter(b->(v.getIsRdv() && obj.getRdvHour() != 0 && b.getRdvHour() == obj.getRdvHour()) || ( !v.getIsRdv() && b.getArrivalOrRegistedHours() == obj.getArrivalOrRegistedHours()))
                            .findFirst();

                    if(v.getIsRdv() &&  obj.getRdvHour()!=0 &&  obj.getRdvHour() < v.getQueueHourStart() )
                    {

                        reponse.setMessage("Cet heure de rdv n'est pas prise en compte par cette file  ");
                        logger.error("Le patient occupant cet heure de rdv  est déjà passé ");
                        reponse.setCode(201);

                    }

                    else if(ifRdvHourIsBusy.isPresent() && !ifRdvHourIsBusy.get().isStatus() &&  ifRdvHourIsBusy.get().isFinished())
                    {

                        reponse.setMessage("Le patient occupant cet heure de rdv  est déjà passé ");
                        logger.error("Le patient occupant cet heure de rdv  est déjà passé ");
                        reponse.setCode(201);

                    }
                    else if(ifRdvHourIsBusy.isPresent() && ifRdvHourIsBusy.get().isStatus()  && ifRdvHourIsBusy.get().isCanceled())
                    {

                        reponse.setMessage("Cet heure de rdv  a été annulée ");
                        logger.error("Cet heure de rdv  a été annulée");
                        reponse.setCode(201);

                    }
                    else if(ifRdvHourIsBusy.isPresent() && ifRdvHourIsBusy.get().isStatus()  && !ifRdvHourIsBusy.get().isCanceled()  && !ifRdvHourIsBusy.get().isFinished())
                    {

                        reponse.setMessage("Cette heure est occupée");
                        logger.error("Cette heure est occupée");
                        reponse.setCode(201);

                    }
                    else
                    {

                        if(!v.getIsRdv())
                        {

                            patient.setRdvHourTempon(patient.getArrivalOrRegistedHours());
                            Patient patientSave = patientRepository.save(patient);
                            var patientWaiting = patientRepository.findByQueueID(patientSave.getQueueID())
                                    .stream()
                                    .count();
                            patientSave.setOrderNumber(patientWaiting);
                            Patient patientSaveC = patientRepository.save(patient);
                            reponse.setData(modelMapper.map(patientSaveC, PatientDtoResponse.class));
                            reponse.setMessage("Le patient a été enregistré avec succès");
                            logger.error("Le patient a été enregistré avec succès ");
                            reponse.setCode(200);

                        }
                        else
                        {
                            patient.setRdvHourTempon(patient.getRdvHour());
                            if(obj.getRdvHour()  == 0 || (obj.getRdvHour()  != 0 && (obj.getArrivalOrRegistedHours() - obj.getRdvHour()) > v.getSlot()*60*1000 ))
                            {
                                patient.setDelay(true);
                                patient.setDelayMoreThanLimit(true);
                                Patient patientSave = patientRepository.save(patient);
                                reponse.setData(modelMapper.map(patientSave, PatientDtoResponse.class));
                                reponse.setMessage("Le patient a été enregistré avec succès");
                                logger.error("Le patient a été enregistré avec succès ");
                                reponse.setCode(200);
                            }
                           else if(obj.getRdvHour()  != 0 && ((obj.getArrivalOrRegistedHours() - obj.getRdvHour()) <= v.getSlot()*60*1000) && ((obj.getArrivalOrRegistedHours() - obj.getRdvHour()) >=0 ) )
                            {
                                Patient patientSave = patientRepository.save(patient);
                                reponse.setData(modelMapper.map(patientSave, PatientDtoResponse.class));
                                reponse.setMessage("Le patient a été enregistré avec succès");
                                logger.error("Le patient a été enregistré avec succès ");
                                reponse.setCode(200);
                            }
                            else if(obj.getRdvHour()  != 0 && ((obj.getArrivalOrRegistedHours() - obj.getRdvHour()) <= 0 ) )
                            {
                                Patient patientSave = patientRepository.save(patient);
                                reponse.setData(modelMapper.map(patientSave, PatientDtoResponse.class));
                                reponse.setMessage("Le patient a été enregistré avec succès");
                                logger.error("Le patient a été enregistré avec succès ");
                                reponse.setCode(200);
                            }
                            else
                            {
                                reponse.setMessage("Ce cas de rdv n'est pas prise en compte");
                                logger.error("Ce cas de rdv n'est pas prise en compte ");
                                reponse.setCode(201);
                            }
                        }


                    }

                });
            }
            else
            {
                reponse.setMessage("La file n'existe plus ");
                logger.error("La file n'existe plus ");
                reponse.setCode(201);
            }

        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }
        return reponse;
    }

    @Override
    public Reponse update(PatientDtoRequest obj) {
        Reponse reponse = new Reponse();
        try
        {

            if(obj.getID() != null)
            {
                patientRepository.findById(obj.getID()).ifPresentOrElse(
                        userById ->
                        {

                            if(obj.getLastname() != null && !obj.getLastname().equals(userById.getLastname()))
                            {
                                userById.setLastname(obj.getLastname());
                            }
                            if(obj.getFirstname() != null && !obj.getFirstname().equals(userById.getFirstname()))
                            {
                                userById.setFirstname(obj.getFirstname());
                            }
                            if(obj.getBirthDay() != 0 && obj.getBirthDay()!= (userById.getBirthDay()))
                            {
                                userById.setBirthDay(obj.getBirthDay());
                            }
                            if(obj.getPhone() != null && !obj.getPhone().equals(userById.getPhone()))
                            {
                                userById.setPhone(obj.getPhone());
                            }

                            if(obj.getArrivalOrRegistedHours() != 0 && obj.getArrivalOrRegistedHours() != (userById.getArrivalOrRegistedHours()))
                            {
                                userById.setArrivalOrRegistedHours(obj.getArrivalOrRegistedHours());
                            }


                            if(obj.getRdvHour() != 0 && obj.getRdvHour() != (userById.getRdvHour()))
                            {
                                userById.setRdvHour(obj.getRdvHour());
                            }
                            if(obj.getQueueID() != null && obj.getQueueID().compareTo(userById.getQueueID()) !=  0)
                            {
                                userById.setQueueID(obj.getQueueID());
                            }


                            userById.setStatus(true);
                                 Patient userByIdSave = patientRepository.save(userById);
                                 PatientDtoResponse userDtoResponse=modelMapper.map(userByIdSave, PatientDtoResponse.class);
                                reponse.setData(userDtoResponse);
                                reponse.setMessage("Ce  patient a été modifié avec succès");
                                reponse.setCode(200);
                                logger.error(" Ce  patient a été modifié avec succès  ");



                        },
                        () ->
                        {
                            reponse.setMessage("Ce  patient  n'existe plus ");
                            reponse.setCode(201);
                            logger.error("Ce  patient  n'existe plus " );

                        }
                );

            }
            else {
                logger.error("Ce  patient n'existe plus ");
                reponse.setMessage("Ce  patient  n'existe plus");
                reponse.setCode(201);
            }


        }
        catch (Exception e)
        {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setCode(500);
            reponse.setMessage("Une erreur interne est survenue coté serveur  !");
        }

        return reponse ;
    }

    @Override
    public Reponse getAll() {
        Reponse reponse = new Reponse();
        try
        {
            List<PatientDtoResponse> queues = patientRepository.findByStatus(true)
                    .stream()
                    .map( v->modelMapper.map(v, PatientDtoResponse.class))
                    .collect(Collectors.toList());
            reponse.setData(queues);
            reponse.setMessage("Listes des patients");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse lockPatient(UUID id)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Patient> patient = patientRepository.findById(id);

            if(patient.isPresent())
            {
                patient.get().setStatus(false);
                Patient userSave=patientRepository.save(patient.get());
                reponse.setData(modelMapper.map(userSave, PatientDtoResponse.class));
                reponse.setMessage("Ce patient  a été bloqué avec succès");
                logger.error("Ce patient  a été bloqué avec succès ");

                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Cette file n'existe pas");
                logger.error("Cette file n'existe pas ");

                reponse.setCode(201);

            }


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }



    @Override
    public Reponse getById(UUID ID) {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Patient> patient = patientRepository.findById(ID);


            if(patient.isPresent())
            {
                PatientDtoResponse patientConverted =modelMapper.map(patient.get(), PatientDtoResponse.class);
                reponse.setData(patientConverted);
                reponse.setMessage("Ce patient a été retrouvé avec succès");
                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Ce patient n'existe plus");
                reponse.setCode(201);

            }


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse canceledPatient(PatientCancelDtoRequest queue)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Patient> patient = patientRepository.findById(queue.getPatientID());

            if(patient.isPresent())
            {
                var first= patientRepository.findByQueueID(queue.getQueueID())
                        .stream()
                        .filter( y-> y.isStatus() && !y.isDelay() ||(y.isStatus() && !y.isDelay()))
                        .sorted(Comparator.comparing(Patient::getRdvHourTempon))
                        .findFirst();

                patient.get().setStatus(false);
                patient.get().setCanceled(true);
                patient.get().setCanceledMotif(queue.getCanceledMotif());
                patient.get().setWaitingTime(new Date().getTime()-patient.get().getCreatedPatient());
                Patient userSave=patientRepository.save(patient.get());
                first.ifPresent(h->{
                    if(h.getID().compareTo(queue.getPatientID()) == 0){
                        sendNotification(queue.getQueueID());
                    }
                });
                reponse.setData(modelMapper.map(userSave, PatientDtoResponse.class));
                reponse.setMessage("Ce patient  a été bien retiré avec succès");
                logger.error("Ce patient  a été bien retiré  avec succès : "+patient.get().getPhone() + "- "+new Date());
                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Ce patient n'existe pas");
                logger.error("Ce patient n'existe pas ");

                reponse.setCode(201);

            }


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse reinsertPatient(PatientReInsertDtoRequest queue)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Patient> patient = patientRepository.findById(queue.getPatientID());

            if(patient.isPresent())
            {

                if(patient.get().getQueueDelayID() != null)
                {
                    patientRepository.findById(patient.get().getQueueDelayID()).ifPresent(b->
                    {
                        if(b.isInsert() && b.isChangeStateOnce())
                        {
                            patient.get().setCanceled(false);
                            patient.get().setStatus(true);
                            patient.get().setQueueDelayID(null);
                            patient.get().setInsert(false);
                            patient.get().setCanceledMotif(null);
                            patient.get().setChangeStateOnce(false);
                            patient.get().setDelay(true);
                            patientRepository.save(patient.get());
                        }
                        else
                        {
                            b.setDelay(true);
                            b.setQueueDelayID(null);
                            b.setRdvHourTempon(b.getRdvHour());
                            b.setCanceledMotif(null);
                            b.setInsert(false);
                            b.setChangeStateOnce(false);
                            patientRepository.save(b);

                            patient.get().setRdvHourTempon(b.getRdvHourTempon());
                            patient.get().setCanceled(false);
                            patient.get().setInsert(false);
                            patient.get().setCanceledMotif(null);
                            patient.get().setChangeStateOnce(false);
                            patient.get().setQueueDelayID(null);
                            patientRepository.save(patient.get());
                        }
                    });
                    reponse.setData(modelMapper.map(patient.get(), PatientDtoResponse.class));
                    reponse.setMessage("Ce patient  a été bien re-intégré avec succès");
                    logger.error("Ce patient  a été bien re-intégré  avec succès : "+patient.get().getPhone() + "- "+new Date());
                    reponse.setCode(200);
                }
                else
                {
                    patient.get().setCanceled(false);
                    patient.get().setInsert(false);
                    patient.get().setCanceledMotif(null);
                    patient.get().setChangeStateOnce(false);
                    patient.get().setQueueDelayID(null);
                    patientRepository.save(patient.get());
                    reponse.setMessage("Ce patient inséré n'existe pas");
                    reponse.setData(modelMapper.map(patient.get(), PatientDtoResponse.class));
                    logger.error("Ce patient  inséré n'existe pas : "+patient.get().getPhone() + "- "+new Date());
                    reponse.setCode(200);
                }



            }
            else
            {
                reponse.setMessage("Ce patient n'existe pas");
                logger.error("Ce patient n'existe pas ");

                reponse.setCode(201);

            }


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse finishedPatient(PatientFinishedDtoRequest queue)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Patient> patient = patientRepository.findById(queue.getPatientID());

            if(patient.isPresent())
            {

              var first= patientRepository.findByQueueID(queue.getQueueID())
                        .stream()
                        .filter( y-> y.isStatus() && !y.isDelay() ||(y.isStatus() && !y.isDelay()))
                        .sorted(Comparator.comparing(Patient::getRdvHourTempon))
                        .findFirst();
                patient.get().setStatus(false);
                patient.get().setFinished(true);
                patient.get().setWaitingTime(new Date().getTime()-patient.get().getCreatedPatient());
                patient.get().setFinishedHour(queue.getFinishedHour());
                Patient userSave=patientRepository.save(patient.get());
                first.ifPresent(h->{
                if(h.getID().compareTo(queue.getPatientID()) == 0){
                    sendNotification(queue.getQueueID());
                }
            });
                reponse.setData(modelMapper.map(userSave, PatientDtoResponse.class));
                reponse.setMessage("Ce patient  a  bien terminé avec succès");
                logger.error("Ce patient  a  bien terminé  avec succès : "+patient.get().getPhone() + "- "+new Date());
                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Ce patient n'existe pas");
                logger.error("Ce patient n'existe pas ");

                reponse.setCode(201);

            }


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse getAll(String firstname,String lastname,String phone,String doctorID,Long createdOnStart,Long createdOnEnd,String type,Long rdvHourStart,Long rdvHourEnd, int page, int size) {
        int skipCount = (page - 1) * size;
        HashMap<String, Object> datas  = new HashMap<String, Object>();
        Reponse reponse = new Reponse();
        try
        {
          var my_size = patientRepository.findAll()
                      .stream()
                      .filter(v ->
                              {

                                if(doctorID == null || doctorID.length() == 0 )
                                {
                                    return  true;
                                }
                                else
                                {
                                  return  queueRepository.findByDoctorID(UUID.fromString(doctorID))
                                            .stream().map( b-> b.getID()).collect(Collectors.toList()).contains(v.getQueueID());


                                }})
                  .filter(v ->
                  {

                      if(createdOnStart == null || createdOnStart.longValue() == 0 )
                      {
                          return  true;
                      }
                      else
                      {

                          Calendar calendar = Calendar.getInstance();
                          calendar.setTime(v.getCreatedOn());
                          calendar.set(Calendar.HOUR_OF_DAY, 0);
                          calendar.set(Calendar.MINUTE, 0);
                          calendar.set(Calendar.SECOND, 0);
                          calendar.set(Calendar.MILLISECOND, 0);
                          return  (calendar.getTime().getTime() >= createdOnStart.longValue());

                      }})
                      .filter(v ->
                      {

                          if(type == null)
                          {
                              return  true;
                          }
                          else
                          {

                              return  queueRepository.findByIDAndTypeContainingIgnoreCase(v.getQueueID(),type).isPresent();


                          }})
                     .filter(v ->createdOnEnd == null || (createdOnEnd != null && createdOnEnd.longValue() >0 && ((createdOnEnd.longValue()+24*60*60*1000) > v.getCreatedOn().getTime())))

            /*      .filter(v ->
                  {

                      if(rdvHourStart == null)
                      {
                          return  true;
                      }
                      else if( createdOn == null)
                      {
                        if(v.getRdvHour() != 0)
                        {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date(v.getRdvHour()));
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            var newDateStart=new Date( calendar.getTime().getTime()+rdvHourStart.longValue()*1000);

                            return (v.getRdvHour() >= newDateStart.getTime()) ? true :false;
                        }
                        else
                        {
                            return  true;
                        }


                      }
                      else
                      {
                          return  true;
                      }

                  }) */




                  .filter(v ->firstname == null || (firstname != null && firstname.length() >0 && (v.getFirstname().toUpperCase().startsWith(firstname.toUpperCase()) )))
                      .filter(v ->lastname == null || (lastname != null && lastname.length() >0 && (v.getLastname().toUpperCase().startsWith(lastname.toUpperCase()) )))
                      .filter(v ->phone == null || (phone != null && phone.length() >0 && (v.getPhone().toUpperCase().startsWith(phone.toUpperCase()))))
                    .count();



            List<InscrisDtoResponse> inscrits = patientRepository.findAll()
                    .stream()
                    .filter(v ->
                    {

                        if(doctorID == null || doctorID.length() == 0 )
                        {
                            return  true;
                        }
                        else
                        {
                            return  queueRepository.findByDoctorID(UUID.fromString(doctorID))
                                    .stream().map( b-> b.getID()).collect(Collectors.toList()).contains(v.getQueueID());


                        }})
                    .filter(v ->
                    {

                        if(type == null)
                        {
                            return  true;
                        }
                        else
                        {


                            return  queueRepository.findByIDAndTypeContainingIgnoreCase(v.getQueueID(),type).isPresent();


                        }})
                    .filter(v ->
                    {

                        if(createdOnStart == null || createdOnStart.longValue() == 0 )
                        {
                            return  true;
                        }
                        else
                        {

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(v.getCreatedOn());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            return  (calendar.getTime().getTime() >= createdOnStart.longValue());

                        }})
                    .filter(v ->createdOnEnd == null || (createdOnEnd != null && createdOnEnd.longValue() >0 && ((createdOnEnd.longValue()+24*60*60*1000) > v.getCreatedOn().getTime())))


                    .filter(v ->firstname == null || (firstname != null && firstname.length() >0 && (v.getFirstname().toUpperCase().startsWith(firstname.toUpperCase()) )))
                    .filter(v ->lastname == null || (lastname != null && lastname.length() >0 && (v.getLastname().toUpperCase().startsWith(lastname.toUpperCase()) )))
                    .filter(v ->phone == null || (phone != null && phone.length() >0 && (v.getPhone().toUpperCase().startsWith(phone.toUpperCase()))))

                    .sorted(Comparator.comparing(Patient::getCreatedOn).reversed())
                    .map( v->
                    {
                        var inscrit=  modelMapper.map(v, InscrisDtoResponse.class);

                        queueRepository.findById(v.getQueueID()).ifPresent(b->{
                            userRepository.findById(b.getDoctorID()).ifPresent(c->inscrit.setDoctor( c.getLastname() + " " +c.getFirstname()));
                            inscrit.setDoctorID(b.getDoctorID());
                            inscrit.setType(b.getType());
                            inscrit.setIsRdv(b.getIsRdv());
                        });


                        return  inscrit;
                    })
                    .skip(skipCount)
                    .limit(size)
                    .collect(Collectors.toList());


            datas.put("totals",my_size);
            datas.put("data", inscrits);
            reponse.setData(datas);
            reponse.setMessage("Listes des  inscrits");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse getByDetail(UUID ID) {
        Reponse reponse = new Reponse();
        HashMap<String, Object> datas  = new HashMap<String, Object>();
        try
        {
            Optional<Patient> patient = patientRepository.findById(ID);
            if(patient.isPresent())
            {
                var queue= queueRepository.findById(patient.get().getQueueID());
                if(queue.isPresent())
                {

                    var patientWaiting = patientRepository.findByQueueID(patient.get().getQueueID())
                            .stream()
                            .filter(y->y.isStatus() && !y.isFinished() && !y.isCanceled())
                            .count();



                             userRepository.findById(queue.get().getDoctorID()).ifPresent( b-> datas.put("doctor",b.getLastname() + " "+b.getFirstname()));
                            datas.put("typeOfRdv", queue.get().getType());
                            datas.put("patientWaiting",patientWaiting - 1);
                            datas.put("orderNumber",patient.get().getOrderNumber());
                            datas.put("patientNumber", patient.get().getArrivalOrRegistedHours());
                            reponse.setData(datas);
                            reponse.setMessage("Details du patient");
                            reponse.setCode(200);

                }
                else
                {
                    reponse.setMessage("Cette file n'existe plus");
                    reponse.setCode(201);
                }

            }
            else
            {
                reponse.setMessage("Ce patient n'existe plus");
                reponse.setCode(201);

            }

                    }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }


    private boolean sendNotification(UUID queueID)
    {
        boolean result=false;
        try
        {
            var queue= queueRepository.findById(queueID);
            if(queue.isPresent())
            {

                var pa = patientRepository.findByQueueID(queueID)
                        .stream()
                        .filter( y-> y.isStatus() && !y.isDelay() ||(y.isStatus() && !y.isDelay()))
                        .sorted(Comparator.comparing(Patient::getRdvHourTempon))
                        .findFirst();

                pa.ifPresent(y->{

                    String contentMessage="Bonjour Monsieur/Madame:"+y.getFirstname() + " " +y.getLastname()+"\n" +
                            "C'est bientôt votre tour. Vous êtes le prochain à passer dans la file d'attente. Merci.";

                    this.notificationService.sendSms(new SmsDtoRequest(y.getPhone(),contentMessage));

                });
            }
        }
        catch (Exception e) {
            logger.error(" error d'envoi de message PatientService  "+e.getMessage());

        }

        return result;
    }



}
