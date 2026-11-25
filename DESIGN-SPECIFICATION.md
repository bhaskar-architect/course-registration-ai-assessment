# Course Registration – Design Specification

**Project:** Course Waitlist  
**Technology:** Java 21, Spring Boot 4.2.0, Spring Data JPA, Hibernate, H2  
**Build Tool:** Maven  
**Document Type:** Technical Design Specification

---

## 1. Project Overview

The objective of this application is to implement a bounded course-registration feature supporting:

- Student enrollment.
- Course capacity validation.
- Waitlist management.
- Enrollment cancellation.
- FIFO waitlist promotion.
- Prerequisite validation.
- Duplicate enrollment prevention.

The application uses the existing Spring Boot starter project and its JPA persistence model.

The implementation is restricted to one course offering's enrollment and waitlist workflow.

## 2. Architecture and Service Boundaries

The application follows a layered Spring Boot architecture.

### Architecture flow

```text
Client
  |
  v
REST Controllers
  |
  v
Service Layer
  |
  +--------------------+
  |                    |
  v                    v
CourseRepository   EnrollmentRepository
  |                    |
  +----------+---------+
             |
             v
        H2 Database
```

### Component responsibilities

**Controller Layer**
- Receives HTTP requests.
- Invokes the appropriate service operations.
- Returns HTTP responses.

**Service Layer**

CourseServiceImpl:
- Create courses.
- Retrieve courses.
- Update courses.
- Delete courses.

EnrollmentServiceImpl:
- Enroll students.
- Validate prerequisites.
- Check available capacity.
- Add eligible students to the waitlist.
- Drop enrollments.
- Promote eligible waitlisted students.

**Repository Layer**

CourseRepository:
- Course persistence.
- Course-name duplicate checking.

EnrollmentRepository:
- Enrollment persistence.
- Duplicate enrollment checking.
- Counting enrolled students.
- Retrieving waitlisted students in FIFO order.

## 3. Database and Entity Design

The application uses Spring Data JPA with an H2 in-memory database.

### Course Entity

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| courseName | String | Course name |
| capacity | Integer | Maximum enrollment capacity |

The course name has a unique database constraint.

### Enrollment Entity

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| studentId | Long | Student identifier |
| course | Course | Many-to-one relationship |
| status | EnrollmentStatus | Enrollment state |
| createdAt | LocalDateTime | Enrollment creation timestamp |
| prerequisiteCompleted | boolean | Prerequisite completion status |

The Course relationship uses lazy fetching.

Enrollment status is stored using the enum's string representation.

### Enrollment statuses

- ENROLLED
- WAITLISTED
- DROPPED
- INELIGIBLE

## 4. API and Service Design

The service layer exposes the following operations.

| Operation | Responsibility |
|---|---|
| getAllCourses() | Retrieve all courses |
| saveCourse() | Create a course |
| updateCourse() | Update a course |
| deleteCourse() | Delete a course |
| enrollStudent() | Enroll or waitlist a student |
| dropEnrollment() | Drop an enrolled student and process promotion |
| getAllEnrollments() | Retrieve all enrollments |
| getEnrollmentById() | Retrieve an enrollment by ID |

The exact REST endpoint paths and HTTP status mappings should be confirmed against the controller classes before final submission.

## 5. Enrollment Workflow

When a student requests enrollment:

1. Retrieve the requested course.
2. If the course does not exist, throw CourseNotFoundException.
3. Check whether the student already has an enrollment for the course.
4. If a duplicate exists, throw DuplicateEnrollmentException.
5. Create a new enrollment record.
6. Validate prerequisite completion.
7. If the prerequisite is incomplete, assign INELIGIBLE status.
8. Otherwise, count currently enrolled students.
9. If capacity is available, assign ENROLLED status.
10. If capacity is full, assign WAITLISTED status.
11. Save the enrollment record.

### Enrollment decision

```text
Enrollment Request
       |
       v
  Course Exists?
       |
   No  |  Yes
       | 
  Exception
          |
          v
    Duplicate?
          |
      Yes | No
          |
      Exception
              |
              v
      Prerequisite Complete?
          /          \
        No            Yes
        |              |
   INELIGIBLE      Check Capacity
                       |
                 +-----+-----+
                 |           |
              Available     Full
                 |           |
              ENROLLED    WAITLISTED
```

## 6. Capacity Invariants

The following business rules apply:

1. A student must not have duplicate enrollment records for the same course.
2. Only students who satisfy the prerequisite rule can be enrolled.
3. The number of ENROLLED students must not exceed course capacity.
4. Waitlisted students do not occupy enrolled seats.
5. A dropped enrollment must not continue occupying an enrolled seat.
6. Promotion must occur only for an eligible waitlisted student.

The capacity condition is:

`Enrolled Students <= Course Capacity`

These rules are implemented in the enrollment service. Concurrent request protection is a known limitation discussed in Section 9.

## 7. FIFO Waitlist Promotion

The application uses First-In, First-Out (FIFO) promotion.

When an enrolled student drops:

1. Retrieve the enrollment using its ID.
2. Verify that its status is ENROLLED.
3. Change its status to DROPPED.
4. Save the updated enrollment.
5. Retrieve waitlisted students ordered by createdAt ascending.
6. Examine the waitlisted students in that order.
7. If a student has completed the prerequisite, promote that student to ENROLLED.
8. If a student has not completed the prerequisite, change the status to INELIGIBLE.
9. Stop after promoting the first eligible student.

### Example

Course capacity: 2

| Student | Initial status | After Student 101 drops |
|---|---|---|
| 101 | ENROLLED | DROPPED |
| 102 | ENROLLED | ENROLLED |
| 103 | WAITLISTED | ENROLLED |
| 104 | WAITLISTED | WAITLISTED |

Student 103 receives the available seat because they are first in the eligible waitlist.

The implementation also skips and marks an ineligible waitlisted student as INELIGIBLE before promoting the next eligible student.

## 8. Prerequisite and Waitlist Assumptions

The following assumptions are applied:

- Prerequisite completion is represented by a boolean field.
- A student who has not completed the prerequisite receives INELIGIBLE status.
- An ineligible student cannot occupy an enrolled seat.
- Eligible students are placed on the waitlist when the course is full.
- An ineligible student encountered during promotion is marked INELIGIBLE.
- Promotion continues until the first eligible student is found.
- Only one eligible student is promoted for each released seat.

This policy is consistent with the current service implementation and unit tests.

## 9. Transaction and Concurrency Approach

### Persistence configuration

- Database: H2 in-memory database.
- ORM: Hibernate.
- Persistence: Spring Data JPA.
- Hibernate schema configuration: update.
- Default transaction isolation reported in the test execution: READ_COMMITTED.

### Drop operation

The dropEnrollment() method uses @Transactional.

This ensures that the drop and subsequent promotion operations participate in one transaction.

### Enrollment operation

The current enrollStudent() method does not declare @Transactional or explicit database locking.

The capacity check and subsequent save are separate operations without explicit concurrency protection.

If simultaneous enrollment requests occur, multiple requests could observe the same available capacity and potentially exceed the course capacity.

No explicit locking pattern was identified in the supplied configuration and repository definitions.

**Known limitation:** Concurrent enrollment protection is not currently established by the supplied implementation.

For this bounded assessment, this limitation should be documented rather than claiming that concurrent enrollment is guaranteed to be safe.

A production implementation should consider course-level pessimistic locking or another appropriate concurrency-control mechanism, together with database constraints and focused concurrency tests.

## 10. Testing Strategy

The application currently has 23 passing automated tests.

| Test class | Tests |
|---|---:|
| CourseControllerTest | 4 |
| EnrollmentControllerTest | 4 |
| CourseRegistrationApplicationTests | 2 |
| EnrollmentServiceImplTest | 13 |
| **Total** | **23** |

### Important scenarios covered

- Available capacity enrollment.
- Full capacity and waitlisting.
- Duplicate enrollment prevention.
- Prerequisite failure.
- Invalid course ID.
- FIFO promotion.
- Skipping ineligible waitlisted students.
- Dropping a waitlisted enrollment rejection.
- Repeated drop prevention.
- Invalid enrollment ID.
- Enrollment retrieval.

The integration test verifies the enrollment, waitlist, drop, and FIFO promotion workflow using the Spring application and H2 persistence.

### Verification command

```bash
mvn clean test
```

**Latest supplied execution result:** 23 tests, 0 failures, 0 errors, 0 skipped.

## 11. Technical Limitations and Trade-offs

1. H2 is configured as an in-memory database. Data is not intended to persist across application restarts.
2. Explicit locking is not configured for enrollment capacity validation.
3. The student-course duplicate check is performed at the application service level; a database-level unique constraint for this pair was not identified in the supplied entity.
4. FIFO ordering uses createdAt ascending. Equal timestamps do not have an explicitly defined secondary ordering.
5. Concurrent enrollment behavior has not been independently verified.
6. The supplied test suite does not establish production database concurrency guarantees.

These limitations are documented without introducing additional infrastructure or expanding the assessment scope.

---

## 12. Conclusion

The current implementation covers the bounded course enrollment, prerequisite validation, waitlist, drop, and FIFO promotion functionality.

The supplied automated test execution confirms that all 23 tests pass.

The design uses the existing Spring Boot architecture, JPA entities, repositories, and H2 persistence.

The remaining submission work includes the AI usage record, setup instructions, limitations summary, and final assessment scorecard.

**Document status:** Initial design specification based on the supplied implementation and test evidence. REST endpoint mappings and any additional starter-project concurrency conventions should be verified before final submission.