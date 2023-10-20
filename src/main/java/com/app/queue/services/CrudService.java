package com.app.queue.services;

import com.app.queue.entities.Reponse;

import java.util.UUID;

public interface CrudService<T> {
    public Reponse create(T obj);
    public Reponse update(T obj);
    public Reponse  getAll();
    public Reponse  getById(UUID ID);



}
