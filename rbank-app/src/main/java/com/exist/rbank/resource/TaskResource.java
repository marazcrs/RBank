package com.exist.rbank.resource;

import com.exist.rbank.dto.TaskDto;
import com.exist.rbank.exception.EntityAlreadyExistsException;
import com.exist.rbank.exception.EntityDoesNotExistException;
import com.exist.rbank.exception.InvalidDataException;
import com.exist.rbank.reference.TaskStatus;
import com.exist.rbank.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskResource {

    @Autowired
    private TaskService taskService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<TaskDto> get(@PathVariable Long id) throws EntityDoesNotExistException {
        log.debug("Fetching task by id={}", id);
        return new ResponseEntity<>(taskService.getByTaskId(id), HttpStatus.OK);
    }

    @PostMapping
    public TaskDto save(@RequestBody TaskDto taskDto) throws EntityAlreadyExistsException, EntityDoesNotExistException, InvalidDataException {
        log.debug("Saving task with details={}", taskDto);
        return taskService.create(taskDto);
    }

    @GetMapping(value = "/list")
    public List<TaskDto> list() {
        log.debug("Fetching all tasks.");
        return taskService.list();
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<TaskDto> updateStatus(@PathVariable("id") Long id, @RequestParam("status") TaskStatus status) throws EntityDoesNotExistException, InvalidDataException {
        log.debug("Updating Task with id={} and status={}", id, status);
        return new ResponseEntity<>(taskService.updateStatus(id, status), HttpStatus.OK);
    }
}