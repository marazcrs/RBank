package com.exist.rbank.service;

import com.exist.rbank.dto.TaskDto;
import com.exist.rbank.exception.EntityAlreadyExistsException;
import com.exist.rbank.exception.EntityDoesNotExistException;
import com.exist.rbank.exception.InvalidDataException;
import com.exist.rbank.reference.TaskStatus;

public interface TaskService {

    TaskDto getByTaskId(Long id) throws EntityDoesNotExistException;

    TaskDto create(TaskDto taskDto) throws EntityAlreadyExistsException, InvalidDataException, EntityDoesNotExistException;

    void updateStatus(Long id, TaskStatus taskStatus) throws EntityDoesNotExistException;
}
