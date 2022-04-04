package com.exist.rbank.service.impl;

import com.exist.rbank.dto.TaskDto;
import com.exist.rbank.entity.Task;
import com.exist.rbank.exception.EntityAlreadyExistsException;
import com.exist.rbank.exception.EntityDoesNotExistException;
import com.exist.rbank.exception.InvalidDataException;
import com.exist.rbank.mapper.TaskMapper;
import com.exist.rbank.reference.TaskStatus;
import com.exist.rbank.repository.TaskRepository;
import com.exist.rbank.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Override
    @Transactional(readOnly = true)
    public TaskDto getByTaskId(Long id) throws EntityDoesNotExistException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Task with id=" + id + " does not exist!"));

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskDto create(TaskDto taskDto) throws EntityAlreadyExistsException, InvalidDataException, EntityDoesNotExistException {
        Task task;

        if (taskDto.getId() != null) {
            task = taskRepository.findById(taskDto.getId())
                    .orElseThrow(() -> new EntityDoesNotExistException("Task with id=" + taskDto.getId() + " does not exist!"));

            if (!task.getStatus().equals(TaskStatus.NEW)) {
                throw new InvalidDataException("Unable to update task already started/completed.");
            }
            taskMapper.updateEntity(taskDto, task);
        } else {
            validateTask(taskDto);
            task = taskMapper.toEntity(taskDto);
            task.setStatus(TaskStatus.NEW);
        }

        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, TaskStatus taskStatus) throws EntityDoesNotExistException {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Task with id=" + id + " does not exist!"));

        task.setStatus(taskStatus);
        taskRepository.save(task);
    }

    private void validateTask(TaskDto taskDto) throws EntityAlreadyExistsException, InvalidDataException {
        if (!StringUtils.isNotEmpty(taskDto.getDescription()) || taskDto.getStartDate() == null
                || taskDto.getEndDate() == null) {
            throw new InvalidDataException("Mandatory fields cannot be null.");
        }

        if (taskRepository.existsByDescription(taskDto.getDescription())) {
            throw new EntityAlreadyExistsException("Task with the same description already exists!");
        }

        if (taskDto.getEndDate().isBefore(taskDto.getStartDate())) {
            throw new InvalidDataException("Invalid End Date.");
        }
    }
}
