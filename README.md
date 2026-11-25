# Course Registration – Course Waitlist

## 1. Project Overview

This project implements a course registration and waitlist management feature using Spring Boot.

The application supports the following functionalities:

- Student enrollment.
- Course capacity validation.
- Waitlist management.
- Duplicate enrollment prevention.
- Prerequisite validation.
- Enrollment cancellation.
- FIFO (First-In, First-Out) waitlist promotion.

The implementation focuses on one course offering and follows the requirements defined in the AI-Assisted Engineering Assessment.

## 2. Technology Stack

| Technology | Version / Purpose |
|---|---|
| Java | 21 |
| Spring Boot | 4.2.0 |
| Spring Data JPA | Persistence |
| Hibernate | ORM |
| H2 Database | In-memory database |
| Maven | Build tool |
| JUnit 5 | Unit testing |
| Mockito | Mocking framework |

## 3. Prerequisites

Ensure the following software is installed:

- JDK 21
- Apache Maven
- IntelliJ IDEA or any Java IDE

Verify the installation:

```bash
java -version
mvn -version
```

## 4. Project Setup

1. Clone or download the project.
2. Open the project in IntelliJ IDEA.
3. Navigate to the project root directory.
4. Ensure Maven dependencies are downloaded.
5. Build the project.

```bash
mvn clean compile
```

## 5. Run the Application

Execute the following command from the project root directory:

```bash
mvn spring-boot:run
```

The application uses port 8080 by default.

## 6. Database Configuration

The application uses an H2 in-memory database.

Configuration:

- Database: H2
- JDBC URL: jdbc:h2:mem:testdb
- Hibernate DDL: update
- H2 Console: Enabled
- Console Path: /h2-console

Access the H2 console:

```text
http://localhost:8080/h2-console
```

The database is in-memory, so data is not retained after application shutdown.

## 7. Enrollment Business Rules

The application follows these business rules:

1. A student can enroll in a course only once.
2. Students must satisfy prerequisite requirements.
3. Students are enrolled when course capacity is available.
4. Eligible students are added to the waitlist when capacity is full.
5. Dropping an enrolled student releases a seat.
6. The earliest eligible waitlisted student is promoted.
7. Ineligible waitlisted students are marked as INELIGIBLE and skipped during promotion.
8. Duplicate and invalid drop requests are rejected.

## 8. Waitlist Promotion Strategy

The application follows the FIFO (First-In, First-Out) approach.

When an enrolled student drops:

1. The enrollment status changes to DROPPED.
2. Waitlisted students are retrieved in ascending creation-time order.
3. The earliest eligible student is identified.
4. That student is promoted to ENROLLED.
5. Ineligible students are skipped and marked INELIGIBLE.

## 9. Running Automated Tests

Execute the following command:

```bash
mvn clean test
```

### Latest Test Execution Results

| Test Class | Tests |
|---|---:|
| CourseControllerTest | 4 |
| EnrollmentControllerTest | 4 |
| CourseRegistrationApplicationTests | 2 |
| EnrollmentServiceImplTest | 13 |
| **Total** | **23** |

Test execution summary:

- Total Tests: 23
- Failures: 0
- Errors: 0
- Skipped: 0
- Build Status: BUILD SUCCESS

## 10. Known Limitations

- H2 is an in-memory database.
- Explicit locking for concurrent enrollment requests is not implemented.
- Database-level unique constraints for student-course enrollment pairs are not identified in the current entity.
- FIFO ordering does not define a secondary ordering for identical timestamps.
- Dedicated concurrent enrollment testing has not been performed.

## 11. Project Documentation

The following documents are included as part of the assessment:

- DESIGN-SPECIFICATION.md
- AI-USAGE-RECORD.md
- README.md

## 12. Assessment Scope

This implementation focuses exclusively on:

- Course enrollment.
- Enrollment cancellation.
- Prerequisite validation.
- Capacity management.
- FIFO waitlist promotion.

The implementation does not include frontend development, payments, identity integrations, or a complete course-registration platform.

