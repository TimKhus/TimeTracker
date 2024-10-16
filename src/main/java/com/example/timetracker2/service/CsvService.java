package com.example.timetracker2.service;

import com.example.timetracker2.exception.DateParseException;
import com.example.timetracker2.exception.EmptyFileException;
import com.example.timetracker2.exception.FileProcessingException;
import com.example.timetracker2.exception.InvalidFileFormatException;
import com.example.timetracker2.model.Employee;
import com.example.timetracker2.model.Project;
import com.example.timetracker2.model.ProjectAssignment;
import com.example.timetracker2.repository.EmployeeRepository;
import com.example.timetracker2.repository.ProjectAssignmentRepository;
import com.example.timetracker2.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Data
@AllArgsConstructor
public class CsvService {

    private final EmployeeRepository employeeRepository;

    private final ProjectRepository projectRepository;

    private final ProjectAssignmentRepository projectAssignmentRepository;

    public void readCsvAndSaveData(InputStream inputStream, String fileName) throws IOException {
        if (inputStream == null || inputStream.available() == 0) {
            throw new EmptyFileException("The uploaded file is empty.");
        }

        if (!fileName.endsWith(".csv")) {
            throw new InvalidFileFormatException("The uploaded file is not in CSV format.");
        }

        List<String> errorMessages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                String[] values = line.strip().split(",");
                lineNumber++;
                if (values.length == 4) {
                    try {
                        Integer employeeId = Integer.parseInt(values[0].strip());
                        Integer projectId = Integer.parseInt(values[1].strip());
                        String startDate = values[2].strip();
                        String endDate = values[3].strip();

                        if (ProjectAssignment.parseAndFormatDate(startDate).
                                isAfter(ProjectAssignment.parseAndFormatDate(endDate))) {
                            errorMessages.add("Start date cannot be after end date - line " + lineNumber);
                            continue;
                        }

                        //Searching for existing employee
                        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employeeId);
                        Employee employee = existingEmployeeOpt.orElseGet(() -> {
                            Employee newEmployee = new Employee();
                            newEmployee.setId(employeeId);
                            return employeeRepository.save(newEmployee);
                        });

                        //Searching for existing project
                        Optional<Project> existingProject = projectRepository.findById(projectId);
                        Project project = existingProject.orElseGet(() -> {
                            Project newProject = new Project();
                            newProject.setId(projectId);
                            return projectRepository.save(newProject);
                        });

                        //Searching for existing project assignment
                        List<ProjectAssignment> existingProjectAssignments = projectAssignmentRepository
                                .findByEmployeeIdAndProjectId(employeeId, projectId);

                        if (existingProjectAssignments.isEmpty()) {
                            ProjectAssignment newProjectAssignment = new ProjectAssignment(employee, project,
                                    startDate, endDate);
                            projectAssignmentRepository.save(newProjectAssignment);
                        } else {
                            boolean existingProjectAssignmentsWasUpdated = false;
                            for (ProjectAssignment existingProjectAssignment : existingProjectAssignments) {
                                if (existingProjectAssignment.hasIntersection(startDate, endDate)) {
                                    LocalDate earlierDate = existingProjectAssignment.findEarlierDate(startDate);
                                    LocalDate laterDate = existingProjectAssignment.findLaterDate(endDate);
                                    existingProjectAssignment.setDateFrom(earlierDate);
                                    existingProjectAssignment.setDateTo(laterDate);
                                    existingProjectAssignmentsWasUpdated = true;
                                    break;
                                }
                            }
                            if (existingProjectAssignmentsWasUpdated) {
                                List<ProjectAssignment> checkedAssignments =
                                        checkIfProjectAssignmentsListHasIntersections(existingProjectAssignments);
                                projectAssignmentRepository.saveAll(checkedAssignments);
                            } else {
                                ProjectAssignment newProjectAssignment = new ProjectAssignment(employee, project,
                                        startDate, endDate);
                                projectAssignmentRepository.save(newProjectAssignment);
                            }
                        }
                    } catch (DateParseException e) {
                        errorMessages.add("Error on line %d: %s".formatted(lineNumber, e.getMessage()));
                    } catch (NumberFormatException e) {
                        errorMessages.add("Error on line %d: wrong ID format".formatted(lineNumber));
                    }
                } else {
                    errorMessages.add("Error on line %d in file - wrong data".formatted(lineNumber));
                }
            }

        } catch (
                IOException e) {
            errorMessages.add("Errors with file reading: " + e.getMessage());
        }

        if (!errorMessages.isEmpty()) {
            throw new FileProcessingException(String.join("<br>", errorMessages));
        }
    }

    private List<ProjectAssignment> checkIfProjectAssignmentsListHasIntersections
            (List<ProjectAssignment> existingProjectAssignments) {
        existingProjectAssignments.sort(Comparator.comparing(ProjectAssignment::getDateFrom));
        List<ProjectAssignment> mergedProjectAssignments = new ArrayList<>();

        ProjectAssignment currentAssignment = existingProjectAssignments.get(0);

        for (int i = 1; i < existingProjectAssignments.size(); i++) {
            ProjectAssignment nextAssignment = existingProjectAssignments.get(i);

                if (currentAssignment.hasIntersection(nextAssignment)) {
                    LocalDate earlierDate = currentAssignment.findEarlierDate(nextAssignment.getDateFrom().toString());
                    LocalDate laterDate = currentAssignment.findLaterDate(nextAssignment.getDateTo().toString());
                    nextAssignment.setDateFrom(earlierDate);
                    nextAssignment.setDateFrom(laterDate);
                } else {
                    mergedProjectAssignments.add(currentAssignment);
                    currentAssignment = nextAssignment;
                }
        }

        return mergedProjectAssignments;
    }

}
