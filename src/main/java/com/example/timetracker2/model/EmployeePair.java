package com.example.timetracker2.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class EmployeePair {

    private Integer employeeId1;
    private Integer employeeId2;
    Map<Integer, Integer> projectDays;
    private Integer daysWorkedTogether;


    public EmployeePair(Integer employeeId1, Integer employeeId2, Integer daysWorkedTogether) {
        if (employeeId1 < employeeId2) {
            this.employeeId1 = employeeId1;
            this.employeeId2 = employeeId2;
        } else {
            this.employeeId1 = employeeId2;
            this.employeeId2 = employeeId1;
        }
        this.daysWorkedTogether = daysWorkedTogether;
        this.projectDays = new HashMap<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePair that = (EmployeePair) o;
        return Objects.equals(employeeId1, that.employeeId1) && Objects.equals(employeeId2, that.employeeId2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId1, employeeId2);
    }

    public void addDaysWorkedTogether(int daysWorkedTogether) {
        this.daysWorkedTogether += daysWorkedTogether;
    }

    @Override
    public String toString() {
        return "EmployeePair{" +
                "employeeId1=" + employeeId1 +
                ", employeeId2=" + employeeId2 +
                ", projectDays=" + projectDays +
                ", daysWorkedTogether=" + daysWorkedTogether +
                '}';
    }
}
