package com.exist.rbank.entity;

import com.exist.rbank.reference.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Data
@Entity(name = "TASK")
public class Task {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "START_DATE")
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToMany
    @JoinTable(name="JOIN_TASK_X_SUBTASK",
            joinColumns={@JoinColumn(name="TASK_ID")},
            inverseJoinColumns={@JoinColumn(name="SUBTASK_ID")})
    private List<Task> subTasks = new ArrayList<>();

    @ManyToMany(mappedBy="subTasks")
    private List<Task> parentTasks = new ArrayList<>();
}
