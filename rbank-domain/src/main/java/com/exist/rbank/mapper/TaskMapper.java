package com.exist.rbank.mapper;

import com.exist.rbank.dto.TaskDto;
import com.exist.rbank.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface TaskMapper {

    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    @Mapping(target = "id", ignore = true)
    void updateEntity(TaskDto taskDto, @MappingTarget Task task);
}
