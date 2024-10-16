package com.example.timetracker2.model;

import com.example.timetracker2.exception.DateParseException;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "project_assignments")
@Data
@NoArgsConstructor
public class ProjectAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private LocalDate dateFrom;

    private LocalDate dateTo;

    private static final List<DateTimeFormatter> dateFormatters = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyy.M.d"),
            DateTimeFormatter.ofPattern("yyyy-M-dd"),
            DateTimeFormatter.ofPattern("yyyy/M/dd"),
            DateTimeFormatter.ofPattern("yyyy.M.dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-d"),
            DateTimeFormatter.ofPattern("yyyy/MM/d"),
            DateTimeFormatter.ofPattern("yyyy.MM.d"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("d.M.yyyy"),
            DateTimeFormatter.ofPattern("dd-M-yyyy"),
            DateTimeFormatter.ofPattern("dd/M/yyyy"),
            DateTimeFormatter.ofPattern("dd.M.yyyy"),
            DateTimeFormatter.ofPattern("d-MM-yyyy"),
            DateTimeFormatter.ofPattern("d/MM/yyyy"),
            DateTimeFormatter.ofPattern("d.MM.yyyy"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.US),
            DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.US),
            DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US),
            DateTimeFormatter.ofPattern("d.M.yyyy г.", Locale.getDefault())
    );

    public ProjectAssignment(Employee employee, Project project, String startDate, String endDate) {
        this.employee = employee;
        this.project = project;
        this.dateFrom = parseAndFormatDate(startDate);
        this.dateTo = parseAndFormatDate(endDate);
    }

    public static LocalDate parseAndFormatDate(String dateString) {
        if (dateString.equals("NULL")) {
            return LocalDate.now();
        }
        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                LocalDate parsedDate =LocalDate.parse(dateString, formatter);
                return LocalDate.parse(parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            } catch (Exception ignored) {
                //Next, if current doesn't work
            }
        }
        throw new DateParseException("Impossible to recognise date format: " + dateString);
    }

    public boolean hasIntersection(String startDateStr, String endDateStr) {
        LocalDate startDate = parseAndFormatDate(startDateStr);
        LocalDate endDate = parseAndFormatDate(endDateStr);

        if (this.getDateFrom().isEqual(startDate)
                || (this.getDateFrom().isAfter(startDate)
                && this.getDateFrom().isBefore(endDate))
        || (this.getDateFrom().isBefore(startDate)) && this.getDateTo().isAfter(endDate)) {
            return true;
        }
        if (this.getDateTo().isEqual(endDate)
                || (this.getDateTo().isBefore(endDate)
                && this.getDateTo().isAfter(startDate))) {
            return true;
        }
        if (this.getDateTo().plusDays(1).isEqual(startDate) ||
        this.dateFrom.isEqual(endDate.plusDays(1))) {
            return true;
        }
        if (this.getDateFrom().isEqual(endDate) ||
                this.getDateTo().isEqual(startDate)) {
            return true;
        }
        return false;
    }

    public LocalDate findEarlierDate(String startDate) {
        return this.getDateFrom()
                .isBefore(parseAndFormatDate(startDate)) ?
                this.getDateFrom() : parseAndFormatDate(startDate);
    }


    public LocalDate findLaterDate(String endDate) {
        return this.getDateTo()
                .isAfter(parseAndFormatDate(endDate)) ?
                this.getDateTo() : parseAndFormatDate(endDate);
    }

    public boolean hasIntersection(ProjectAssignment assignment) {
        LocalDate startDate = assignment.getDateFrom();
        LocalDate endDate = assignment.getDateTo();

        if (this.getDateFrom().isEqual(startDate)
                || (this.getDateFrom().isAfter(startDate)
                && this.getDateFrom().isBefore(endDate))
                || (this.getDateFrom().isBefore(startDate)) && this.getDateTo().isAfter(endDate)) {
            return true;
        }
        if (this.getDateTo().isEqual(endDate)
                || (this.getDateTo().isBefore(endDate)
                && this.getDateTo().isAfter(startDate))) {
            return true;
        }
        if (this.getDateTo().plusDays(1).isEqual(startDate) ||
                this.dateFrom.isEqual(endDate.plusDays(1))) {
            return true;
        }
        if (this.getDateFrom().isEqual(endDate) ||
                this.getDateTo().isEqual(startDate)) {
            return true;
        }
        return false;
    }
}
