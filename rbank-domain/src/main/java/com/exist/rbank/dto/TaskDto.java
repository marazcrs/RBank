package com.exist.rbank.dto;

import com.exist.rbank.entity.Task;
import com.exist.rbank.reference.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TaskDto {

    private Long id;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private TaskStatus status;

    private List<TaskDto> subTasks = new ArrayList<>();
}
