# Course Registration and FIFO Waitlist API

A Spring Boot REST API for course creation, student enrollment, capacity management, prerequisite validation, and FIFO waitlist promotion.

## 1. Technology Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- H2 in-memory database
- Maven
- JUnit 5 and Mockito

## 2. Project Features

- Create, retrieve, update, and delete courses.
- Enroll students in courses.
- Check course capacity during enrollment.
- Prevent duplicate enrollment.
- Validate prerequisite completion.
- Place students on a waitlist when a course is full.
- Promote eligible waitlisted students in FIFO order when a seat becomes available.
- Mark ineligible waitlisted students as `INELIGIBLE` and continue checking the queue.
- Retrieve enrollment details.
- Drop an existing enrollment.

## 3. Prerequisites

Install the following:

- Java 21 or later.
- Maven, if you are not using the Maven Wrapper.
- Git.

Verify Java installation:

```bash
java -version
```

## 4. Clone the Repository

```bash
git clone https://github.com/bhaskar-architect/course-registration-ai-assessment.git
cd course-registration-ai-assessment
```

## 5. Build and Run

### Windows

Run tests:

```bash
mvnw.cmd clean test
```

Start the application:

```bash
mvnw.cmd spring-boot:run
```

### Linux / macOS

Run tests:

```bash
./mvnw clean test
```

Start the application:

```bash
./mvnw spring-boot:run
```

The examples below assume the application is running locally on port `8080`.

Base URL:

```text
http://localhost:8080
```

## 6. REST API Reference

All request and response bodies use JSON.

### 6.1 Course APIs

#### Create a Course

**POST** `/api/courses`

Request:

```json
{
  "courseName": "Java Programming",
  "capacity": 2
}
```

Successful response — `201 Created`:

```json
{
  "id": 1,
  "courseName": "Java Programming",
  "capacity": 2
}
```

#### Get All Courses

**GET** `/api/courses`

Successful response — `200 OK`:

```json
[
  {
    "id": 1,
    "courseName": "Java Programming",
    "capacity": 2
  }
]
```

#### Update a Course

**PUT** `/api/courses/{id}`

Example:

**PUT** `/api/courses/1`

Request:

```json
{
  "courseName": "Advanced Java",
  "capacity": 3
}
```

Successful response — `200 OK`:

```json
{
  "id": 1,
  "courseName": "Advanced Java",
  "capacity": 3
}
```

#### Delete a Course

**DELETE** `/api/courses/{id}`

Example:

```text
DELETE http://localhost:8080/api/courses/1
```

Successful response — `204 No Content`.

### 6.2 Enrollment APIs

#### Enroll a Student

**POST** `/api/enrollments`

Request:

```json
{
  "studentId": 101,
  "courseId": 1,
  "prerequisiteCompleted": true
}
```

Successful response — `201 Created`:

```json
{
  "id": 1,
  "studentId": 101,
  "courseId": 1,
  "courseName": "Java Programming",
  "status": "ENROLLED",
  "createdAt": "2026-11-25T10:30:00",
  "prerequisiteCompleted": true
}
```

The `status` value depends on the enrollment outcome. For example, a student may be enrolled or placed on the waitlist, depending on course capacity and eligibility.

#### Get All Enrollments

**GET** `/api/enrollments`

Successful response — `200 OK`:

```json
[
  {
    "id": 1,
    "studentId": 101,
    "courseId": 1,
    "courseName": "Java Programming",
    "status": "ENROLLED",
    "createdAt": "2026-11-25T10:30:00",
    "prerequisiteCompleted": true
  }
]
```

#### Get Enrollment by ID

**GET** `/api/enrollments/{enrollmentId}`

Example:

```text
GET http://localhost:8080/api/enrollments/1
```

Successful response — `200 OK`:

```json
{
  "id": 1,
  "studentId": 101,
  "courseId": 1,
  "courseName": "Java Programming",
  "status": "ENROLLED",
  "createdAt": "2026-11-25T10:30:00",
  "prerequisiteCompleted": true
}
```

#### Drop an Enrollment

**DELETE** `/api/enrollments/{enrollmentId}`

Example:

```text
DELETE http://localhost:8080/api/enrollments/1
```

Successful response — `204 No Content`.

Dropping an enrollment can trigger FIFO waitlist promotion when a seat becomes available.

## 7. Error Response Structure

The application includes an `ErrorResponse` DTO with the following fields:

```json
{
  "timestamp": "2026-11-25T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Example error message",
  "path": "/api/enrollments"
}
```

This illustrates the response structure. Actual error messages and HTTP status codes depend on the error-handling implementation and the particular failure.

## 8. Testing with Postman

Use the following sequence to exercise the main API workflow.

1. Start the application.
2. Create a course using `POST /api/courses`.
3. Create another course if needed for additional scenarios.
4. Enroll a student using `POST /api/enrollments`.
5. Enroll another student in the same course.
6. Fill the course capacity.
7. Submit another enrollment to exercise the waitlist workflow.
8. Retrieve enrollments using `GET /api/enrollments`.
9. Retrieve an individual enrollment using `GET /api/enrollments/{enrollmentId}`.
10. Drop an enrolled student using `DELETE /api/enrollments/{enrollmentId}`.
11. Check enrollment records to examine the resulting waitlist promotion.

Use the IDs returned by the API responses in subsequent requests.

## 9. Automated Test Evidence

The AI Usage Record documents the following reported test execution:

Command:

```bash
mvn clean test
```

| Test Class | Tests |
|---|---:|
| CourseControllerTest | 4 |
| EnrollmentControllerTest | 4 |
| CourseRegistrationApplicationTests | 2 |
| EnrollmentServiceImplTest | 13 |
| **Total** | **23** |

Reported result:

- Tests run: 23
- Failures: 0
- Errors: 0
- Skipped: 0
- Build result: `BUILD SUCCESS`

Run the tests locally to verify the current repository state.

## 10. Design and Implementation Notes

The application separates responsibilities across controllers, services, repositories, entities, and DTOs.

- Controllers expose REST endpoints.
- Services implement course and enrollment business rules.
- Repositories handle persistence operations.
- DTOs define API request and response structures.
- JPA and Hibernate provide persistence integration.

The enrollment workflow checks course capacity and prerequisite completion. When capacity is unavailable, eligible students can be waitlisted. When a seat becomes available, the earliest eligible waitlisted student is considered for promotion.

## 11. Known Limitations and Risks

- Explicit locking for enrollment capacity validation is not implemented.
- Concurrent enrollment requests may create a race condition and could exceed the configured course capacity.
- No dedicated concurrent enrollment test is included in the reported test results.
- The H2 in-memory database does not provide persistent data across application restarts.
- The reported test results do not establish production-level concurrency guarantees.

These limitations are documented rather than claiming production-grade concurrency behavior.

## 12. Additional Documentation

Refer to the following project documents for more information:

- `DESIGN-SPECIFICATION.md` — architecture, workflow, and design decisions.
- `LIMITATIONS-AND-RISKS.md` — known limitations and technical risks.
- `AI-USAGE-RECORD.md` — AI-assisted engineering activities and verification evidence.
- `Course-Registration-Rubric-Score.md` — assessment-oriented implementation review.

## 13. Repository

GitHub:

https://github.com/bhaskar-architect/course-registration-ai-assessment

---

**End of README**