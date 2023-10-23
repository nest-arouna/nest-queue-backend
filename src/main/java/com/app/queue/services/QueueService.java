package com.app.queue.services;

import com.app.queue.dto.request.QueueDtoRequest;
import com.app.queue.dto.response.NextPatientDtoResponse;
import com.app.queue.dto.response.PatientDtoResponse;
import com.app.queue.dto.response.QueueDtoResponse;
import com.app.queue.dto.response.UserDtoResponse;
import com.app.queue.entities.Patient;
import com.app.queue.entities.Queue;
import com.app.queue.entities.Reponse;
import com.app.queue.entities.Utilisateur;
import com.app.queue.repositories.IDaoPatient;
import com.app.queue.repositories.IDaoQueue;
import com.app.queue.repositories.IDaoUser;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class QueueService implements  CrudService<QueueDtoRequest>{
    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);
    @Autowired
    private IDaoUser userRepository;
    @Autowired
    private IDaoPatient patientRepository;
    @Autowired
    private IDaoQueue queueRepository;
    @Autowired(required=true)
    private ModelMapper modelMapper;
    @Override
    public Reponse create(QueueDtoRequest obj)
    {
        Reponse reponse = new Reponse();
        try
        {

            Queue queue =modelMapper.map(obj, Queue.class);
            queue.setStatus(true);
            Queue queueSave = queueRepository.save(queue);
            reponse.setData(modelMapper.map(queueSave, QueueDtoResponse.class));
            reponse.setMessage("La file a été enregistrée avec succès");
            logger.error("La file a été enregistrée avec succès ");
            reponse.setCode(200);

        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }

    @Override
    public Reponse update(QueueDtoRequest obj) {
        Reponse reponse = new Reponse();
        try
        {

            if(obj.getID() != null)
            {
                      queueRepository.findById(obj.getID()).ifPresentOrElse(
                        queue ->
                        {


                                if(obj.getType() != null && !queue.getType().equals(obj.getType()))
                                {
                                    queue.setType(obj.getType());
                                }
                                if(obj.getDoctorID() != null && obj.getDoctorID().compareTo(queue.getDoctorID())!=0)
                                {
                                    queue.setDoctorID(obj.getDoctorID());
                                }
                                queue.setStatus(true);
                                Queue queueSave = queueRepository.save(queue);
                                QueueDtoResponse userDtoResponse=modelMapper.map(queueSave, QueueDtoResponse.class);
                                reponse.setData(userDtoResponse);
                                reponse.setMessage("Cette file a été modifiée avec succès");
                                reponse.setCode(200);
                                logger.error(" Cette file a été modifiée avec succès  ");



                        },
                        () ->
                        {
                            reponse.setMessage("Cette file n'existe plus ");
                            reponse.setCode(201);
                            logger.error(" Cette file n'existe plus " );

                        }
                );

            }
            else {
                logger.error("Cette file n'existe plus ");
                reponse.setMessage("Cette file n'existe plus");
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
            List<QueueDtoResponse> queues = queueRepository.findByStatus(true)
                    .stream()
                    .sorted(Comparator.comparing(Queue::getCreatedOn).reversed())
                    .map( v->
                            {
                             var queue=  modelMapper.map(v, QueueDtoResponse.class);
                             userRepository.findById(v.getDoctorID()).ifPresent(c->queue.setDoctor( c.getLastname() + " " +c.getFirstname()));
                             return  queue;
                            })
                    .collect(Collectors.toList());
            reponse.setData(queues);
            reponse.setMessage("Listes des files");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse getAll(UUID id,int page,int size) {
        int skipCount = (page - 1) * size;
        AtomicInteger my_size= new AtomicInteger(0);
        HashMap<String, Object> datas  = new HashMap<String, Object>();
        List<PatientDtoResponse> queues = new ArrayList<>();
        Reponse reponse = new Reponse();
        try
        {
           var queue= queueRepository.findById(id);
           if(queue.isPresent())
           {


               List<PatientDtoResponse> list_queues = patientRepository.findByQueueID(id)
                           .stream()
                       .filter( y-> y.isStatus() && !y.isFinished() && !y.isCanceled())
                       .filter( y-> {

                           boolean a = y.isStatus() && !y.isFinished() && !y.isCanceled() && !y.isDelay() ;
                           boolean b = y.isStatus() && !y.isFinished() && !y.isCanceled() && y.isDelay() && y.isDelay() && y.isDelayMoreThanLimit() ;

                           boolean d =  (new Date().getTime()-y.getCreatedOn().getTime()) > 0;
                           boolean c =   (new Date().getTime()-y.getCreatedOn().getTime()) -3600000 <=0 ;

                           return a || (b && d && c) ;

                       })
                          .peek(o-> my_size.getAndIncrement())
                           .sorted(Comparator.comparing(Patient::getRdvHour))
                           .map( v->
                                   {
                                       var patient=modelMapper.map(v, PatientDtoResponse.class);
                                       patient.setWaitingTime(new Date().getTime()-v.getCreatedOn().getTime());
                                       return  patient;
                                   })
                           .collect(Collectors.toList());
                  queues= list_queues.stream().skip(skipCount)
                           .limit(size)
                           .collect(Collectors.toList());

           }
           else
           {
               reponse.setMessage("Cette file n'existe plus");
               reponse.setCode(201);
           }



            datas.put("totals",my_size);
            datas.put("data", queues);
            reponse.setData(datas);
            reponse.setMessage("Listes des patients pour cette file");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse getAll(String type,String doctorID,Long createdOn,int page,int size) {
        int skipCount = (page - 1) * size;
        AtomicInteger my_size= new AtomicInteger(0);
        HashMap<String, Object> datas  = new HashMap<String, Object>();
        Reponse reponse = new Reponse();
        try
        {

            List<QueueDtoResponse> queues = queueRepository.findAll()
                    .stream()
                    .peek(o-> my_size.getAndIncrement())
                    .filter(v ->doctorID == null || (doctorID != null && v.getDoctorID().compareTo(UUID.fromString(doctorID)) == 0))
                    .filter(v ->createdOn == null || (createdOn != null && createdOn.longValue() >0 && ((createdOn.longValue()+24*60*60*1000) > v.getCreatedOn().getTime()) && (v.getCreatedOn().getTime() >= createdOn.longValue())))
                    .filter(v ->type == null || (type != null && type.length() >0 && (v.getType().toUpperCase().startsWith(type) || v.getType().toLowerCase().startsWith(type))))

                    .sorted(Comparator.comparing(Queue::getCreatedOn).reversed())
                    .map( v->
                    {
                        var queue=  modelMapper.map(v, QueueDtoResponse.class);
                        userRepository.findById(v.getDoctorID()).ifPresent(c->queue.setDoctor( c.getLastname() + " " +c.getFirstname()));
                        return  queue;
                    })
                    .skip(skipCount)
                    .limit(size)
                    .collect(Collectors.toList());

            datas.put("totals",my_size);
            datas.put("data", queues);
            reponse.setData(datas);
            reponse.setMessage("Listes des  file");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }

    public Reponse lockQueue(UUID id)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Queue> queue = queueRepository.findById(id);

            if(queue.isPresent())
            {
                queue.get().setStatus(false);
                Queue userSave=queueRepository.save(queue.get());
                reponse.setData(modelMapper.map(userSave, QueueDtoResponse.class));
                reponse.setMessage("Cette file  a été bloquée avec succès");
                logger.error("Cette file  a été bloquée avec succès ");

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

    public Reponse getAllNextPatient() {
        Reponse reponse = new Reponse();
        try
        {

            List<NextPatientDtoResponse> queues = queueRepository.findByStatus(true)
                    .stream()
                    .map( v->
                    {
                       var nextPatient = patientRepository.findByQueueID(v.getID())
                                .stream()
                                .filter(y->y.isStatus() && !y.isFinished() && !y.isDelay() && !y.isCanceled() || y.isStatus() && !y.isFinished() && y.isDelay() && !y.isDelayMoreThanLimit()  )
                                .sorted(Comparator.comparing(Patient::getRdvHourTempon))
                                .map( p->{
                                    var patient=  modelMapper.map(p, NextPatientDtoResponse.class);
                                    userRepository.findById(v.getDoctorID()).ifPresent(c->patient.setDoctor( c.getLastname() + " " +c.getFirstname()));
                                    return patient;
                                })
                                .findFirst();
                       return  nextPatient.isPresent() ?nextPatient.get() :null;
                    })
                    .filter(l->l!=null)
                    .collect(Collectors.toList());
            reponse.setData(queues);
            reponse.setMessage("Listes des prochains patients");
            reponse.setCode(200);


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
            Optional<Queue> queue = queueRepository.findById(ID);


            if(queue.isPresent())
            {
                QueueDtoResponse queueConverted =modelMapper.map(queue.get(), QueueDtoResponse.class);
                reponse.setData(queueConverted);
                reponse.setMessage("Cette file a été retrouvée avec succès");
                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Cette file n'existe plus");
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
    public Reponse getAllCanceledPatient(UUID id,int page,int size) {
        int skipCount = (page - 1) * size;
        AtomicInteger my_size= new AtomicInteger(0);
        HashMap<String, Object> datas  = new HashMap<String, Object>();
        Reponse reponse = new Reponse();
        try
        {
            // RDV LIST WITHOUT CANCELED AND FINISHED QUEUE
            List<PatientDtoResponse> queues = patientRepository.findByQueueID(id)
                    .stream()
                    .filter(y->y.isStatus() &&(y.isCanceled() || y.isDelay() ))
                    .filter( y->
                    {
                        boolean d =  (new Date().getTime()-y.getCreatedOn().getTime()) > 0;
                        boolean c =   (new Date().getTime()-y.getCreatedOn().getTime()) - 3600000 > 0 ;
                        boolean e =   !y.isFinished() && y.isCanceled() && !y.isDelay() ;

                        return  e || ( y.isDelay() && d  && c && y.isDelayMoreThanLimit());

                    })
                    .peek(o-> my_size.getAndIncrement())
                    .sorted(Comparator.comparing(Patient::getCreatedOn))
                    .map( v->
                            {
                                var patient=modelMapper.map(v, PatientDtoResponse.class);
                                var timeout=new Date().getTime()-patient.getArrivalOrRegistedHours();
                                if( timeout >= 0 && timeout <=15*60*1000 )
                                {
                                    if(v.getQueueDelayID() != null)
                                    {
                                       patientRepository.findById(v.getQueueDelayID()).ifPresent(g->
                                               {
                                                  if(!g.isFinished())
                                                  {
                                                      patient.setReInsertable(true);

                                                  }

                                               });

                                    }
                                    else
                                    {
                                        patient.setReInsertable(true);

                                    }

                                }
                                return  patient;
                            })
                    .skip(skipCount)
                    .limit(size)
                    .collect(Collectors.toList());

            datas.put("totals",my_size);
            datas.put("data", queues);
            reponse.setData(datas);
            reponse.setMessage("Listes des patients annulés pour cette file");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse getAllFinnishedPatient(UUID id,int page,int size) {
        int skipCount = (page - 1) * size;
        AtomicInteger my_size= new AtomicInteger(0);
        HashMap<String, Object> datas  = new HashMap<String, Object>();

        Reponse reponse = new Reponse();
        try
        {
            // RDV LIST WITHOUT CANCELED AND FINISHED QUEUE
            List<PatientDtoResponse> queues = patientRepository.findByQueueID(id)
                    .stream()
                    .filter(y->!y.isStatus()  && y.isFinished())
                    .peek(o-> my_size.getAndIncrement())
                    .sorted(Comparator.comparing(Patient::getFinishedHour).reversed())
                    .map( v->modelMapper.map(v, PatientDtoResponse.class))
                    .skip(skipCount)
                    .limit(size)
                    .collect(Collectors.toList());

            datas.put("totals",my_size);
            datas.put("data", queues);
            reponse.setData(datas);
            reponse.setMessage("Listes des patients terminés pour cette file");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
    public Reponse getAllDelayPatient(UUID id,int page,int size) {
        int skipCount = (page - 1) * size;
        AtomicInteger my_size= new AtomicInteger(0);
        HashMap<String, Object> datas  = new HashMap<String, Object>();

        Reponse reponse = new Reponse();
        try
        {
            // RDV LIST WITHOUT CANCELED AND FINISHED QUEUE
            List<PatientDtoResponse> queues = patientRepository.findByQueueID(id)
                    .stream()
                    .filter( y-> {

                        boolean d =  (new Date().getTime()-y.getCreatedOn().getTime()) > 0;
                        boolean c =   (new Date().getTime()-y.getCreatedOn().getTime()) -3600000 <=0 ;
                        boolean e =   y.isStatus() && !y.isFinished() && !y.isCanceled() && y.isDelay() && y.isDelayMoreThanLimit() ;
                        return e && d && c  ;

                    })
                    .filter(y->y.isStatus()  && y.isDelay())
                    .peek(o-> my_size.getAndIncrement())
                    .sorted(Comparator.comparing(Patient::getArrivalOrRegistedHours))
                    .map( v->
                    {
                        var patient=modelMapper.map(v, PatientDtoResponse.class);
                        patient.setWaitingTime(new Date().getTime()-v.getCreatedOn().getTime());
                        return  patient;
                    })
                    .skip(skipCount)
                    .limit(size)
                    .collect(Collectors.toList());

            datas.put("totals",my_size);
            datas.put("data", queues);
            reponse.setData(datas);
            reponse.setMessage("Listes des patients en retard pour cette file");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }
}
