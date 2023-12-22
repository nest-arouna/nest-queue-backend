package com.app.queue.services;

import com.app.queue.dto.request.SmsDtoRequest;

public interface INotificationService {

    public boolean sendSms(SmsDtoRequest sms );

}
