package com.app.queue.services;

import com.app.queue.dto.request.SmsDtoRequest;
import com.app.queue.dto.request.UserDtoRequest;
import com.app.queue.dto.response.UserDtoResponse;
import com.app.queue.entities.*;
import com.app.queue.repositories.IDaoUser;
import com.app.queue.security.JwtTokenUtil;
import com.app.queue.utils.Utility;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Service
public class UserService implements  CrudService<UserDtoRequest>
{
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private IDaoUser userRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired(required=true)
    private ModelMapper modelMapper;
    @Override
    public Reponse create(UserDtoRequest user) {
        Reponse reponse = new Reponse();

        try
        {

            if(user.getEmail() != null && user.getEmail().length() > 0)
            {
                userRepository.findByEmail(user.getEmail()).ifPresentOrElse(
                        utilisateur ->
                        {
                            reponse.setMessage( utilisateur.isStatus() ?  "Cet email est déjà utilisé svp !" :
                                    "Cet email est déjà utilisé par compte bloqué svp !");
                            logger.error( utilisateur.isStatus() ?  "Cet email est déjà utilisé svp !" :
                                    "Cet email est déjà utilisé par compte bloqué svp !");
                            reponse.setCode(201);
                        },
                        () ->
                        {
                            Utilisateur userConverted =modelMapper.map(user, Utilisateur.class);
                            userConverted.setStatus(true);
                            userConverted.setPassword(bCryptPasswordEncoder.encode(user.getEmail()));
                            Utilisateur userSave = userRepository.save(userConverted);
                            reponse.setData(modelMapper.map(userSave, UserDtoResponse.class));
                            reponse.setMessage("Ce compte a été enregistré avec succès");
                            logger.error("Ce compte a été enregistré avec succès ");
                            reponse.setCode(200);
                        }
                );
            }
            else
            {

                reponse.setMessage("Veuillez renseigner l'email svp !");
                logger.error("Veuillez renseigner l'email svp ");
                reponse.setCode(201);

            }


        }
        catch (Exception e)
        {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setCode(500);
            reponse.setMessage("Un problème de serveur  !");
        }

        return reponse ;
    }

    @Override
    public Reponse update(UserDtoRequest user) {
        Reponse reponse = new Reponse();
        try
        {

            if(user.getID() != null)
            {
                Optional<Utilisateur>  userById = userRepository.findById(user.getID());
                userRepository.findByEmail(user.getEmail()).ifPresentOrElse(
                        utilisateur ->
                        {
                            if(userById.get().getID().compareTo(utilisateur.getID()) != 0)
                            {
                                logger.error(" cet email est déjà utilisé   svp ");
                                reponse.setMessage("cet email est déjà utilisé   svp !");
                                reponse.setCode(201);
                            }
                            else
                            {
                                if(user.getLastname() != null && !user.getLastname().equals(userById.get().getLastname()))
                                {
                                    userById.get().setLastname(user.getLastname());
                                }
                                if(user.getFirstname() != null && !user.getFirstname().equals(userById.get().getFirstname()))
                                {
                                    userById.get().setFirstname(user.getFirstname());
                                }
                                if(user.getEmail() != null && !user.getEmail().equals(userById.get().getEmail()))
                                {
                                    userById.get().setEmail(user.getEmail());
                                }
                                if(user.getPhone() != null && !user.getPhone().equals(userById.get().getPhone()))
                                {
                                    userById.get().setPhone(user.getPhone());
                                }

                                if(user.getAdress() != null && !user.getAdress().equals(userById.get().getAdress()))
                                {
                                    userById.get().setAdress(user.getAdress());
                                }


                                if(user.getRole() != null && !user.getRole().equals(userById.get().getRole()))
                                {
                                    userById.get().setRole(user.getRole());
                                }

                                userById.get().setStatus(true);
                                Utilisateur userSave = userRepository.save(userById.get());
                                UserDtoResponse userDtoResponse=modelMapper.map(userSave, UserDtoResponse.class);
                                reponse.setData(userDtoResponse);
                                reponse.setMessage("Ce compte a été modifié avec succès");
                                reponse.setCode(200);
                                logger.error(" Ce compte a été modifié avec succès  " +userById.get().getEmail());


                            }
                        },
                        () ->
                        {

                            if(user.getLastname() != null && !user.getLastname().equals(userById.get().getLastname()))
                            {
                                userById.get().setLastname(user.getLastname());
                            }
                            if(user.getFirstname() != null && !user.getFirstname().equals(userById.get().getFirstname()))
                            {
                                userById.get().setFirstname(user.getFirstname());
                            }
                            if(user.getEmail() != null && !user.getEmail().equals(userById.get().getEmail()))
                            {
                                userById.get().setEmail(user.getEmail());
                            }
                            if(user.getPhone() != null && !user.getPhone().equals(userById.get().getPhone()))
                            {
                                userById.get().setPhone(user.getPhone());
                            }

                            if(user.getAdress() != null && !user.getAdress().equals(userById.get().getAdress()))
                            {
                                userById.get().setAdress(user.getAdress());
                            }


                            if(user.getRole() != null && !user.getRole().equals(userById.get().getRole()))
                            {
                                userById.get().setRole(user.getRole());
                            }

                            userById.get().setStatus(true);
                            Utilisateur userSave = userRepository.save(userById.get());
                            UserDtoResponse userDtoResponse=modelMapper.map(userSave, UserDtoResponse.class);
                            reponse.setData(userDtoResponse);
                            reponse.setMessage("Ce compte a été modifié avec succès");
                            reponse.setCode(200);
                            logger.error(" Ce compte a été modifié avec succès  " +userById.get().getEmail());



                        }
                );

            }
            else {
                logger.error(" Ce  personnels n'existe pas  ");
                reponse.setMessage(" Ce  personnels n'existe pas");
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
    public Reponse getById(UUID ID)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Utilisateur> user = userRepository.findById(ID);


            if(user.isPresent())
            {
                UserDtoResponse userConverted =modelMapper.map(user.get(), UserDtoResponse.class);
                reponse.setData(userConverted);
                reponse.setMessage("Ce compte a été retrouvé avec succès");
                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Ce compte n'existe pas");
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
    public Reponse getAll() {
        Reponse reponse = new Reponse();
        try
        {
            List<UserDtoResponse> users = userRepository.findAll()
                    .stream()
                    .map( v->modelMapper.map(v, UserDtoResponse.class))
                    .collect(Collectors.toList());
            reponse.setData(users);
            reponse.setMessage("Listes des personnels");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue "+e.getMessage());
            reponse.setMessage("Une erreur interne est survenue");
            reponse.setCode(500);

        }

        return reponse;
    }

    public Reponse getAllProfesionnals(String lastname, String firstname, String adress, String email,String phone,String role,int size,int page)
    {
        int skipCount = (page - 1) * size;
        Reponse reponse = new Reponse();
        AtomicInteger my_size= new AtomicInteger(0);
        try
        {
            HashMap<String, Object> datas  = new HashMap<String, Object>();

            List<UserDtoResponse> users = userRepository.findByStatus(true)
                    .stream()
                    .filter(h-> !h.getRole().equals(String.valueOf(EnumUser.DOCTOR)))
                    .filter(h-> Utility.isCompareField(h.getAdress(),adress))
                    .filter(h-> Utility.isCompareField(h.getEmail(),email))
                    .filter(h-> Utility.isCompareField(h.getLastname(),lastname))
                    .filter(h-> Utility.isCompareField(h.getFirstname(),firstname))
                    .filter(h-> Utility.isCompareField(h.getPhone(),phone))
                    .filter(h-> Utility.isCompareField(h.getRole(),role))
                    .peek(o-> my_size.getAndIncrement())
                    .sorted(Comparator.comparing(Utilisateur::getCreatedOn).reversed())
                    .skip(skipCount)
                    .limit(size)
                    .map(p->modelMapper.map(p, UserDtoResponse.class) )
                    .collect(Collectors.toList());

            datas.put("totalUsers",my_size);
            datas.put("data", users);

            reponse.setData(datas);
            reponse.setMessage("Liste des professionnels");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue " +e.getMessage());
            reponse.setMessage("Une erreur interne est survenue" +e.getMessage());
            reponse.setCode(500);

        }

        return reponse;



    }
    public Reponse getAllDoctors(String lastname, String firstname, String adress, String email,String phone,int size,int page)
    {
        int skipCount = (page - 1) * size;
        Reponse reponse = new Reponse();
        AtomicInteger my_size= new AtomicInteger(0);
        try
        {
            HashMap<String, Object> datas  = new HashMap<String, Object>();

            List<UserDtoResponse> users = userRepository.findByStatus(true)
                    .stream()
                    .filter(h-> h.getRole().equals(String.valueOf(EnumUser.DOCTOR)))
                    .filter(h-> Utility.isCompareField(h.getAdress(),adress))
                    .filter(h-> Utility.isCompareField(h.getEmail(),email))
                    .filter(h-> Utility.isCompareField(h.getLastname(),lastname))
                    .filter(h-> Utility.isCompareField(h.getFirstname(),firstname))
                    .filter(h-> Utility.isCompareField(h.getPhone(),phone))
                    .peek(o-> my_size.getAndIncrement())
                    .sorted(Comparator.comparing(Utilisateur::getCreatedOn).reversed())
                    .skip(skipCount)
                    .limit(size)
                    .map(p->modelMapper.map(p, UserDtoResponse.class) )
                    .collect(Collectors.toList());

            datas.put("totalUsers",my_size);
            datas.put("data", users);

            reponse.setData(datas);
            reponse.setMessage("Liste des médécins");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue " +e.getMessage());
            reponse.setMessage("Une erreur interne est survenue" +e.getMessage());
            reponse.setCode(500);

        }

        return reponse;



    }
    public Reponse getAllDoctors()
    {
        Reponse reponse = new Reponse();
        try
        {
            List<UserDtoResponse> users = userRepository.findByStatus(true)
                    .stream()
                    .filter(h-> h.getRole().equals(String.valueOf(EnumUser.DOCTOR)))
                    .sorted(Comparator.comparing(Utilisateur::getCreatedOn).reversed())
                    .map(p->modelMapper.map(p, UserDtoResponse.class) )
                    .collect(Collectors.toList());

            reponse.setData(users);
            reponse.setMessage("Liste des médécins");
            reponse.setCode(200);


        }
        catch (Exception e) {
            logger.error(" une exception est survenue " +e.getMessage());
            reponse.setMessage("Une erreur interne est survenue" +e.getMessage());
            reponse.setCode(500);

        }

        return reponse;



    }
    public Reponse login_in(Login login)
    {

        Reponse response = new Reponse();
        try
        {
          userRepository.findByEmail(login.getEmail()).ifPresentOrElse(
                  userC ->
                  {
                      if((bCryptPasswordEncoder.matches(login.getPassword(), userC.getPassword())))
                      {
                          HashMap<String, String> credentials = new HashMap<String, String>();
                          String token=this.getToken(login.getEmail(), login.getPassword());
                          credentials.put("id", userC.getID().toString());
                          credentials.put("token", token);
                          credentials.put("role", userC.getRole());
                          credentials.put("email", userC.getEmail());
                          credentials.put("lastname", userC.getLastname());
                          credentials.put("firstname", userC.getFirstname());
                          credentials.put("phone", userC.getPhone());
                          response.setCode(200);
                          response.setMessage("La connexion a reussi !");
                          logger.error("USER WITH EMAIL :"+login.getEmail() + " connected");
                          response.setData(credentials);

                      }
                      else
                      {
                          response.setCode(201);
                          logger.error("Mot de passe incorrect PASSWORD :"+login.getPassword());
                          response.setMessage("Mot de passe incorrect !");
                      }

                  },
                  () -> {
                      response.setCode(201);
                      logger.error(" Cet compte n'existe pas  EMAIL :  "+login.getEmail() + "PASSWORD :"+login.getPassword());
                      response.setMessage("Cet compte n'existe pas !");
                  });


        }
        catch (Exception e)
        {
            logger.error(" une exception est survenue "+e.getMessage());
            response.setCode(500);
            response.setMessage("Un problème de serveur !");
        }


        return response ;
    }
    public Reponse lockUser(UUID id)
    {
        Reponse reponse = new Reponse();
        try
        {
            Optional<Utilisateur> user = userRepository.findById(id);

            if(user.isPresent())
            {
                user.get().setStatus(false);
                Utilisateur userSave=userRepository.save(user.get());
                reponse.setData(modelMapper.map(userSave, UserDtoResponse.class));
                reponse.setMessage("Ce compte a été bloqué avec succès");
                logger.error("Ce compte a été bloqué avec succès EMAIL :  "+user.get().getEmail());

                reponse.setCode(200);

            }
            else
            {
                reponse.setMessage("Ce compte n'existe pas");
                logger.error("Ce compte n'existe pas ");

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

    public Reponse changePassword(ChangePasswordDtoRequest changePasswordDtoRequest)
    {
        Reponse response = new Reponse();
        try
        {
            Optional<Utilisateur>  user = userRepository.findById(changePasswordDtoRequest.getID());

            if(user.isPresent())
            {

                System.out.println(changePasswordDtoRequest.toString());

                if((bCryptPasswordEncoder.matches(changePasswordDtoRequest.getOldPassword(), user.get().getPassword())) || changePasswordDtoRequest.getIsAdmin() == 1 )
                {
                    String pwdCryp = bCryptPasswordEncoder.encode(changePasswordDtoRequest.getNewPassword());
                    user.get().setPassword(pwdCryp);
                    userRepository.save(user.get());
                    response.setCode(200);
                    response.setMessage("Le mot de passe a été modifié avec succès : !");
                    logger.error("Le mot de passe a été modifié avec succès ");

                    response.setData(changePasswordDtoRequest.getNewPassword());

                }
                else
                {
                    response.setCode(201);
                    response.setMessage("L'ancien mot de passe est incorrect !");
                    logger.error("Cet email est déjà utilisé svp ");
                    logger.error("L'ancien mot de passe est incorrect  ");

                }
            }
            else
            {
                response.setCode(201);
                response.setMessage("Ce compte n'existe pas  !");
                logger.error("Ce compte n'existe pas ");


            }


        }
        catch (Exception e)
        {
            logger.error(" une exception est survenue ");
            response.setCode(500);
            response.setMessage("Une erreur serveur est survenue !");
        }


        return response ;

    }
    public String getToken(String phone , String password)
    {
        try {
            authenticate(phone,  password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
        final String token = jwtTokenUtil.generateToken(userDetails);
        return token;

    }
    public  void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }


    public void initAccount() {
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        userDtoRequest.setRole(String.valueOf(EnumUser.ADMIN));
        userDtoRequest.setEmail("arouna.sanou@nest.sn");
        userDtoRequest.setPhone("775073511");
        userDtoRequest.setFirstname("Admin");
        userDtoRequest.setLastname("Nest");
        Reponse reponse=this.create(userDtoRequest);
        logger.info(" Le compte par defaut a été crée avec un code : "+reponse.getCode() + " :"+reponse.getMessage());
    }

}
