# Course Registration – AI-Assisted Engineering Assessment

AI-Assisted Course Enrollment, Drop, and FIFO Waitlist Promotion using Java and Spring Boot.

## 1. Project Overview

This project implements a bounded course-registration workflow supporting:

- Student enrollment.
- Course capacity validation.
- Prerequisite validation.
- Duplicate enrollment prevention.
- FIFO waitlist management.
- Enrollment cancellation.
- Automatic promotion of eligible waitlisted students when a seat becomes available.

The implementation is based on the supplied Spring Boot starter project and focuses on one course offering.

## 2. Technology Stack

| Technology | Version / Details |
|---|---|
| Java | 21 |
| Spring Boot | 4.2.0 |
| Spring Web MVC | REST APIs |
| Spring Data JPA | Persistence |
| Hibernate | ORM |
| H2 Database | In-memory database |
| Maven | Build tool |
| JUnit / Mockito | Automated testing |

## 3. Application Architecture

The application follows a layered architecture:

```text
Client
  |
  v
REST Controllers
  |
  v
Service Layer
  |
  +----------------------+
  |                      |
  v                      v
CourseRepository   EnrollmentRepository
  |                      |
  +----------+-----------+
             |
             v
         H2 Database
```

### Main Components

- **Controller:** Exposes course and enrollment REST APIs.
- **Service:** Implements enrollment, dropping, capacity validation, and waitlist promotion.
- **Repository:** Handles persistence and enrollment queries.
- **Entity:** Represents Course and Enrollment data.
- **Exception Handler:** Provides consistent error responses.

## 4. Key Business Rules

### Enrollment

- A student can enroll only once in a course.
- A student must satisfy the prerequisite requirement.
- If seats are available, the enrollment status becomes `ENROLLED`.
- If the course is full, an eligible student is added to the waitlist as `WAITLISTED`.
- Students who do not satisfy prerequisites are marked `INELIGIBLE`.

### Waitlist Promotion

- Waitlisted students are processed in FIFO order using their creation timestamp.
- When an enrolled student drops the course, a seat becomes available.
- The earliest eligible waitlisted student is promoted to `ENROLLED`.
- An ineligible waitlisted student is marked `INELIGIBLE`, and processing continues with the next eligible student.

### Enrollment Statuses

| Status | Description |
|---|---|
| ENROLLED | Student successfully enrolled |
| WAITLISTED | Eligible student waiting for a seat |
| DROPPED | Enrollment cancelled |
| INELIGIBLE | Student does not satisfy prerequisites |

## 5. Prerequisites

Install the following before running the application:

- Java Development Kit (JDK) 21.
- Maven (optional if using the included Maven Wrapper).
- Git.

Verify Java installation:

```bash
java -version
```

## 6. Setup and Execution

### Clone the repository

```bash
git clone https://github.com/bhaskar-architect/course-registration-ai-assessment.git
```

### Navigate to the project

```bash
cd course-registration-ai-assessment
```

### Build the application

Windows:

```bash
mvnw.cmd clean install
```

Linux / macOS:

```bash
./mvnw clean install
```

### Run automated tests

Windows:

```bash
mvnw.cmd clean test
```

Linux / macOS:

```bash
./mvnw clean test
```

### Run the application

Windows:

```bash
mvnw.cmd spring-boot:run
```

Linux / macOS:

```bash
./mvnw spring-boot:run
```

The application uses Spring Boot's default HTTP port unless configured otherwise.

## 7. Database Configuration

The application uses an H2 in-memory database.

Configuration is maintained in:

`src/main/resources/application.yaml`

```yaml
spring:
  application:
    name: course-registration

  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: update

  h2:
    console:
      enabled: true
      path: /h2-console
```

H2 Console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:testdb
```

## 8. REST API Overview

The application exposes REST APIs for course and enrollment operations.

Base paths:

```text
/api/courses
/api/enrollments
```

Enrollment operations include:

- Creating an enrollment.
- Retrieving enrollment details.
- Retrieving enrollment records.
- Dropping an enrollment and triggering waitlist promotion.

Refer to the controller classes for the exact HTTP methods, endpoint paths, request formats, and response structures.

## 9. Automated Testing

The project includes unit and controller/application tests.

Test coverage includes:

- Enrollment when seats are available.
- Waitlisting when the course is full.
- Duplicate enrollment prevention.
- Prerequisite validation.
- Invalid course handling.
- FIFO waitlist promotion.
- Skipping ineligible waitlisted students.
- Invalid enrollment handling.
- Repeated drop prevention.
- Controller and application behavior.

### Verification Evidence

Reported Maven test execution:

| Test Class | Tests |
|---|---:|
| CourseControllerTest | 4 |
| EnrollmentControllerTest | 4 |
| CourseRegistrationApplicationTests | 2 |
| EnrollmentServiceImplTest | 13 |
| **Total** | **23** |

Result:

- Tests run: 23
- Failures: 0
- Errors: 0
- Skipped: 0
- Build: BUILD SUCCESS

## 10. AI-Assisted Engineering

AI was used as an engineering assistant for:

- Assessment requirement analysis.
- Existing implementation review.
- Test coverage review.
- Design specification preparation.
- Technical risk identification.
- Documentation preparation.

AI suggestions were reviewed against the supplied implementation and assessment acceptance criteria.

No AI-generated application code changes are claimed in the AI usage record.

Refer to `AI-USAGE-RECORD.md` for details.

## 11. Known Limitations and Risks

- The application currently uses an H2 in-memory database.
- Explicit locking is not implemented for enrollment capacity validation.
- A database-level unique constraint for the student-course pair is not present in the supplied entity.
- Concurrent enrollment requests could potentially exceed course capacity.
- Production database behavior and concurrency guarantees have not been demonstrated.

These limitations are documented as follow-up risks and are not represented as implemented features.

## 12. Project Documentation

The repository includes:

| Document | Purpose |
|---|---|
| `README.md` | Project overview and setup instructions |
| `DESIGN-SPECIFICATION.md` | Architecture, workflow, data model, and design decisions |
| `AI-USAGE-RECORD.md` | AI prompts, assistance, decisions, and verification evidence |
| `LIMITATIONS-AND-RISKS.md` | Known limitations and follow-up risks |
| `Course-Registration-Rubric-Score.md` | Independent evidence-based assessment scorecard; not the official assessment-owner rubric |

## 13. Assessment Scope

This implementation focuses only on the requested one-course-offering enrollment, drop, and FIFO waitlist-promotion workflow.

The project intentionally excludes frontend development, payments, identity integrations, multi-term registration, and additional infrastructure.

---

**Project:** Course Registration – AI-Assisted Engineering Assessment

**Repository:** https://github.com/bhaskar-architect/course-registration-ai-assessment

**Technology:** Java 21 | Spring Boot | Spring Data JPA | H2 | Maven