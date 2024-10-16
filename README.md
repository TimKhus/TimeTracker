# Employee Collaboration Time Tracker

## Task
Create an application that identifies the pair of employees who have worked together on common projects for the longest period of time and the time for each of those projects.

## Input Data
The application accepts a CSV file with data in the following format:

EmpID, ProjectID, DateFrom, DateTo

### Sample Input Data (partial):
143, 12, 2013-11-01, 2014-01-05 
218, 10, 2012-05-16, NULL 
143, 10, 2009-01-01, 2011-04-27

## Output Data
The application outputs:
143, 218, 8 
10, 5 
12, 3

## Specific Requirements

1. **DateTo** can be **NULL**, equivalent to today's date.
2. The application should account for the number of days they worked together.
3. The input data must be loaded into the program from a CSV file.
4. More than one date format should be supported. Extra points will be given if all date formats are supported.
5. In the README.md file, summarize your understanding of the task and your algorithm.
6. Do not use external libraries for CSV parsing.
7. Follow clean code conventions.

## Bonus
1. Implement data persistence.
2. CRUD operations for Employees.

## Algorithm

1. Read data from the CSV file and save it to the database.
2. For each project assignment, check if the dates intersect with other assignments.
3. Create pairs of employees who worked on common projects.
4. Calculate the total time they worked together.
5. Output information about the employee pairs, their working time together, and the time for each project.

## Technologies Used

- Spring Boot
- Java
- Thymeleaf (for presentation)
- Hibernate (for database interaction)

## Installation and Running

1. Clone the repository:
   ```bash
   git clone https://github.com/TimKhus/TimeTracker.git
   
2. Navigate to the project directory:
   ```bash
   cd TimeTracker
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   
4. Open your browser and go to:
   http://localhost:8080/

Contributors
 - Timur Khusnutdinov