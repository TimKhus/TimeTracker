package com.example.timetracker2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Table(name = "project")
@Data
public class Project {
    @Id
    private Integer id;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectAssignment> projectAssignments;
}
