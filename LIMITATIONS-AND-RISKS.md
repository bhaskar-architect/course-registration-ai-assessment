# Limitations and Risks

## 1. In-Memory Database

The application currently uses an H2 in-memory database.

- Database records are stored only while the application is running.
- Data is lost when the application shuts down or restarts.
- H2 is suitable for local development and testing but is not intended as a production persistence solution.

**Potential improvement:** Use a persistent relational database such as PostgreSQL or MySQL if the application needs long-term data storage.

## 2. Concurrent Enrollment Requests

The enrollment process checks the current number of enrolled learners before saving a new enrollment.

If multiple enrollment requests are processed simultaneously when only one seat remains, more than one request could potentially observe the same available capacity.

This may result in the course exceeding its configured capacity.

The current test execution verifies the functional enrollment and waitlist scenarios, but it does not establish protection against simultaneous enrollment requests.

**Potential improvement:** Introduce an appropriate database locking or concurrency-control strategy and add a focused concurrency test.

## 3. Duplicate Enrollment Protection

The application checks whether a learner already has an enrollment record for the same course before creating another record.

This prevents duplicate requests during normal sequential processing.

However, an application-level check alone may not prevent duplicate records if two identical requests are processed simultaneously. A database-level unique constraint on the learner and course combination could provide additional protection.

**Potential improvement:** Consider adding a suitable unique database constraint and handling constraint violations gracefully.

## 4. FIFO Waitlist Ordering

Waitlisted learners are retrieved in ascending order of their creation timestamp.

This supports the First-In, First-Out (FIFO) promotion policy.

However, if multiple waitlist records have exactly the same creation timestamp, their relative ordering may not be deterministic.

**Potential improvement:** Add a unique secondary ordering field, such as the enrollment ID, to make queue ordering deterministic.

## 5. Prerequisite Eligibility

The application uses the prerequisite-completion information supplied with an enrollment request to determine learner eligibility.

An ineligible learner is assigned the `INELIGIBLE` status and is not enrolled.

During waitlist promotion, an ineligible learner is skipped and marked `INELIGIBLE`, allowing the next eligible learner to be considered.

The current implementation does not demonstrate integration with an external academic system to independently verify prerequisite completion.

**Potential improvement:** Integrate with an authoritative prerequisite-validation source if this becomes a business requirement.

## 6. Persistence and Schema Management

The current configuration uses Hibernate schema generation with `ddl-auto: update`.

This is convenient for development because Hibernate can update the database schema based on entity changes.

For production environments, schema changes should be managed through controlled database migration scripts.

**Potential improvement:** Consider using a database migration tool such as Flyway or Liquibase when introducing a persistent production database.

## 7. Test Coverage Limitations

The current test execution completed successfully:

- Total tests executed: 23
- Failures: 0
- Errors: 0
- Skipped: 0
- Build status: SUCCESS

The tests cover the main functional scenarios, including enrollment, waitlisting, duplicate prevention, prerequisite failure, FIFO promotion, and invalid or repeated drops.

However, the current test results do not demonstrate production-scale load handling or concurrent enrollment safety.

**Potential improvement:** Add focused concurrency tests and performance tests if the application is extended beyond the current assessment scope.

## 8. Summary

The current implementation focuses on the bounded course-registration feature required by the assessment: enrollment, dropping, prerequisite eligibility, and FIFO waitlist promotion.

The identified limitations are documented as follow-up considerations rather than additional features implemented as part of this assessment.

The current solution is intended as a focused starter-project implementation and has been verified through the available automated test suite.