Employee Leave Management System

A RESTful backend application built using Java and Spring Boot to manage employees and their leave applications. The system provides employee management, leave requests, approval workflows, leave balance tracking, and API documentation.

Technologies Used:

Java
Spring Boot
Spring Data JPA
MySQL
Maven
REST APIs
Git and GitHub

Features

Employee Management:  

Add and view employees.
Find employees by employee ID.
Prevent duplicate employee IDs.
Delete employees when they have no existing leave applications.

Leave Management:  

Apply for leave using start date, end date, leave type, and reason.
View all leave applications and individual leave details.
Retrieve leave history for a specific employee.
Approve or reject pending leave applications.
Cancel approved leave applications.
Delete pending leave applications.
Leave Balance and Validation
Maintain a leave allowance of 20 days per employee.
Calculate used, pending, and available leave days.
Validate leave dates and available balance.
Prevent overlapping pending or approved leave applications.
Release reserved leave balance when a pending application is rejected or deleted.

Error Handling: 

Centralized exception handling for common application errors.
Request validation for invalid input.

Prerequisites: 

Install the following before running the application:
Java 21
MySQL Server
Git (optional, for cloning the repository)

Database Configuration:

Start MySQL

Create the database:

CREATE DATABASE employeemanagementsystem;

Configure your database credentials using the environment variables DB_USERNAME and DB_PASSWORD.

The application uses the following configuration in application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/employeemanagementsystem
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update

Replace the example values with your local MySQL credentials. Run the application from the same terminal session.

Run the Application

Clone the repository:

git clone YOUR_GITHUB_REPOSITORY_URL

cd employee-leave-management

Main Leave API Endpoints:


POST          /leaves                                    Apply for leave

GET           /leaves                              Retrieve all leave applications

GET        /leaves/{leaveId}                       Retrieve a leave application by ID

GET      /employees/{employeeId}/leaves            View an employee's leave history

GET    /employees/{employeeId}/leaves/available      Check available leave balance

POST      /leaves/{leaveId}/approve                 Approve a pending application

POST      /leaves/{leaveId}/reject                  Reject a pending application

POST      /leaves/{leaveId}/cancel                  Cancel an approved application

DELETE      /leaves/{leaveId}                        Delete a pending application

Future Enhancements:

Authentication and role-based authorization.
Frontend interface for employees and administrators.
Email notifications for leave status changes.

Author

Developed as a Java and Spring Boot backend project to practice REST API development, database integration, layered architecture, exception handling, and unit testing.
