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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        Task task = getTask(id);
        if (CollectionUtils.isNotEmpty(task.getSubTasks())) {
            task.getSubTasks().sort(Comparator.comparing(Task::getStartDate));
        }
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskDto create(TaskDto taskDto) throws EntityAlreadyExistsException, InvalidDataException, EntityDoesNotExistException {
        Task task;
        validateTask(taskDto);

        if (taskDto.getId() != null) {
            task = getTask(taskDto.getId());

            if (!task.getStatus().equals(TaskStatus.NEW)) {
                throw new InvalidDataException("Unable to update task already started/completed.");
            }

            validateStatus(taskDto.getStatus(), task);
            taskMapper.updateEntity(taskDto, task);
        } else {
            task = taskMapper.toEntity(taskDto);
            task.setStatus(TaskStatus.NEW);
        }

        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskDto updateStatus(Long id, TaskStatus taskStatus) throws EntityDoesNotExistException, InvalidDataException {
        Task task = getTask(id);

        validateStatus(taskStatus, task);
        task.setStatus(taskStatus);
        task = taskRepository.save(task);

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> list() {
        return Lists.newArrayList(taskRepository.findAll())
                .stream()
                .sorted(Comparator.comparing(task -> task.getStartDate()))
                .map(task -> {
                    TaskDto taskDto = taskMapper.toDto(task);
                    if (CollectionUtils.isNotEmpty(taskDto.getSubTasks())) {
                        taskDto.getSubTasks().sort(Comparator.comparing(TaskDto::getStartDate));
                    }
                    return taskDto;
                })
                .collect(Collectors.toList());

    }

    private void validateStatus(TaskStatus status, Task task) throws InvalidDataException {
        List<TaskStatus> statusList = List.of(TaskStatus.STARTED, TaskStatus.COMPLETED);

        if (statusList.contains(status) && CollectionUtils.isNotEmpty(task.getSubTasks())) {
            if (task.getSubTasks().stream().filter(task1 -> !task1.getStatus().equals(TaskStatus.COMPLETED)).findFirst().isPresent()) {
                throw new InvalidDataException("Cannot start/complete task with ongoing dependencies");
            }
        }
    }

    private void validateTask(TaskDto taskDto) throws EntityAlreadyExistsException, InvalidDataException, EntityDoesNotExistException {
        if (!StringUtils.isNotEmpty(taskDto.getDescription()) || taskDto.getStartDate() == null
                || taskDto.getEndDate() == null) {
            throw new InvalidDataException("Mandatory fields cannot be null.");
        }

        if ((taskDto.getId() == null &&  taskRepository.existsByDescriptionIgnoreCase(taskDto.getDescription()))
                || (taskDto.getId() != null && taskRepository.existsByIdNotAndDescriptionIgnoreCase(taskDto.getId(), taskDto.getDescription()))) {
            throw new EntityAlreadyExistsException("Task with the same description already exists!");
        }

        if (taskDto.getEndDate().isBefore(taskDto.getStartDate())) {
            throw new InvalidDataException("Invalid End Date.");
        }

        if (CollectionUtils.isNotEmpty(taskDto.getSubTasks())) {
            for (TaskDto subTaskDto : taskDto.getSubTasks()) {
                getTask(subTaskDto.getId());
            }

        }
    }

    private Task getTask(Long id) throws EntityDoesNotExistException {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityDoesNotExistException("Task with id=" + id + " does not exist!"));
    }
}
