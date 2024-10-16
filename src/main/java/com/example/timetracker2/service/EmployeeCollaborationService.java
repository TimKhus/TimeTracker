package com.example.timetracker2.service;

import com.example.timetracker2.model.EmployeePair;
import com.example.timetracker2.model.ProjectAssignment;
import com.example.timetracker2.repository.EmployeeRepository;
import com.example.timetracker2.repository.ProjectAssignmentRepository;
import com.example.timetracker2.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeCollaborationService {

    private final ProjectAssignmentRepository projectAssignmentRepository;

    private Set<EmployeePair> getEmployeePairSet() {
        List<ProjectAssignment> projectAssignments = getAllProjectAssignments();
        return createEmployeePairs(projectAssignments);
    }

    private Set<EmployeePair> createEmployeePairs(List<ProjectAssignment> projectAssignments) {
        Set<EmployeePair> employeePairs = new HashSet<>();

        for (ProjectAssignment current : projectAssignments) {
            Integer employeeId1 = current.getEmployee().getId();
            Integer projectId1 = current.getProject().getId();
            LocalDate startDate1 = current.getDateFrom();
            LocalDate endDate1 = current.getDateTo();

            for (ProjectAssignment other : projectAssignments) {
                if (other.getEmployee().getId().equals(employeeId1)) continue;

                Integer employeeId2 = other.getEmployee().getId();
                Integer projectId2 = other.getProject().getId();
                LocalDate startDate2 = other.getDateFrom();
                LocalDate endDate2 = other.getDateTo();

                // check if employees worked on the same project
                if (employeeId1 < employeeId2 && projectId1.equals(projectId2)) {
                    //find overlap dates
                    LocalDate start = startDate1.isAfter(startDate2) ? startDate1 : startDate2;
                    LocalDate end = endDate1.isBefore(endDate2) ? endDate1 : endDate2;

                    if (start.isBefore(end) || start.isEqual(end)) {
                        int daysWorkedTogether = (int) (end.toEpochDay() - start.toEpochDay() + 1);
                        System.out.println(daysWorkedTogether);
                        EmployeePair currentEmployeePair = new EmployeePair(employeeId1, employeeId2,
                                daysWorkedTogether);

                        if (employeePairs.contains(currentEmployeePair)) {
                            EmployeePair existingEmployeePair = employeePairs.stream()
                                    .filter(employeePair -> employeePair.equals(currentEmployeePair))
                                    .findFirst().orElseThrow();
                            existingEmployeePair.addDaysWorkedTogether(daysWorkedTogether);
                            if (existingEmployeePair.getProjectDays().containsKey(projectId1)) {
                                Integer currentDaysWorkedOnProject = existingEmployeePair.getProjectDays()
                                        .get(projectId1);
                                existingEmployeePair.getProjectDays().put(
                                        projectId1, currentDaysWorkedOnProject + daysWorkedTogether
                                );
                            } else {
                                existingEmployeePair.getProjectDays().put(projectId1, daysWorkedTogether);
                            }
                        } else {
                            currentEmployeePair.getProjectDays().put(projectId1, daysWorkedTogether);
                            employeePairs.add(currentEmployeePair);
                        }
                    }

                }

            }
        }

        for (EmployeePair pair : employeePairs) {
            System.out.println(pair);
        }

        return employeePairs;
    }

    private List<ProjectAssignment> getAllProjectAssignments() {
        return projectAssignmentRepository.findAll();
    }

    public EmployeePair findEmployeePairWithMostDaysWorkedTogether() {
        return getEmployeePairSet().stream()
                .max(Comparator.comparingInt(EmployeePair :: getDaysWorkedTogether)).orElseThrow();
    }
}
