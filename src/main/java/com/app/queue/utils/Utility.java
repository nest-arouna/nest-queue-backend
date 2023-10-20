package com.app.queue.utils;


import java.util.Calendar;
import java.util.Date;

public final class Utility
{

	// CONSTANT POUR LA SECURITE
//	public static final long EXPIRATION_TIME = 1*24* 60 * 60 ; // expire dans un 1 jour

	public static final long EXPIRATION_TIME = 1*2*60*60 ; // expire dans un 2h

	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SECRET = "javainuse";


	public static final String QUEUES = "/queues";
	public static final String QUEUES_PATIENTS = "/queues/patients/{id}";
	public static final String QUEUES_NEXT_PATIENTS = "/queues/next";
	public static final String QUEUES_CANCELED_PATIENT = "/queues/patients/canceled/{id}";
	public static final String QUEUES_DELAY_PATIENT = "/queues/patients/delay/{id}";

	public static final String QUEUES_FINISHED_PATIENT = "/queues/patients/finished/{id}";
	public static final String GET_ACTIVE_QUEUE = "/queues/active";

	public static final String GET_QUEUE = "/queues/{id}";
	public static final String LOCKED_QUEUE = "/queues/lock/{id}";

	public static final String PATIENTS = "/patients";
	public static final String GET_PATIENT = "/patients/{id}";
	public static final String LOCKED_PATIENT = "/patients/lock/{id}";
	public static final String DETAIL_PATIENT = "/patients/detail/{id}";
	public static final String GET_SUBSCRIBER_PATIENT = "/patients/subscrivers";
	public static final String REINSERT_PATIENT = "/patients/reinsert";

	public static final String CANCELED_PATIENT = "/patients/canceled";
	public static final String FINISHED_PATIENT = "/patients/finished";

	public static final String PERSONNALS = "/personnals";
	public static final String DOCTORS = "/doctors";
	public static final String DOCTORS_DISPLAY = "/doctors/display";

	public static final String USERS = "/users";


	public static final String UPDATE_USER = "/users/update";

	public static final String GET_USER_BY_ID = "/users/{id}";
	public static final String DELETE_USER_BY_ID = "/users/{id}";

	public static final String DO_CONTACTED = "/acceuil/user/contacter";
	public static final String DO_REGISTER = "/user/register";
	public static final String DO_REGISTER_BY_ADMIN = "/user/register/admin";
	public static final String DO_LOGIN = "/login";
	public static final String DO_ACTIVATION = "/activation";

	public static final String DO_FORGOT_PASSWORD = "/forgot";
	public static final String UPDATE_PASSWORD = "/modifierpwd/{id}";
	
	
	// GENERER TOKEN

	public static boolean isCompareField(String fieldOfDb,String fieldOfEntity )
	{

		if(fieldOfEntity != null && fieldOfEntity.length() != 0
		   && fieldOfDb != null && fieldOfDb.length() != 0
		   && (fieldOfDb.toLowerCase().trim().contains(fieldOfEntity.toLowerCase().trim()) || fieldOfDb.toUpperCase().trim().contains(fieldOfEntity.toUpperCase().trim()) )

		)
		{
         return  true;
		}
		else if(fieldOfEntity == null)
		{
			return  true;
		}
		else {
			return  false;
		}
	}

	public static long currentSlot(long queueHourStart,long slot,int index)
	{

		    Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(queueHourStart + (slot*60000*index)));
			calendar.getTime().getTime();
			return calendar.getTime().getTime();

	}

}
