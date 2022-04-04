package com.exist.rbank.repository;

import com.exist.rbank.entity.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    Boolean existsByDescriptionIgnoreCase(String description);

    Boolean existsByIdNotAndDescriptionIgnoreCase(Long id, String description);
}
