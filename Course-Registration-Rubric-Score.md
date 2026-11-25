# Course Registration – Evidence-Based Assessment Scorecard

> **Important:** The original `Course-Registration-Rubric-Score.md` supplied by the assessment owner was not available. This is an independently prepared evidence-based scorecard mapped to the assessment requirements. It is **not the official assessment-owner rubric** and does not predict the assessor's score.

## 1. Scope and Evidence Reviewed

Assessment scope: Enrollment, duplicate prevention, prerequisite eligibility, FIFO waitlist handling, dropping an enrollment, and promotion from the waitlist for one course offering.

Evidence reviewed:
- Supplied Spring Boot / Java 21 project structure and implementation.
- `EnrollmentServiceImplTest.java` and the supplied controller/application tests.
- User-reported `mvn clean test` result: **23 tests passed, 0 failures, 0 errors, 0 skipped; BUILD SUCCESS**.
- `DESIGN-SPECIFICATION.md`
- `README.md`
- `LIMITATIONS-AND-RISKS.md`
- `AI-USAGE-RECORD.md`

The test result is recorded as a user-executed verification result. It is not represented as a test run performed by the AI reviewer.

## 2. Acceptance Criteria Scorecard

| # | Assessment Criterion | Status | Evidence / Observation |
|---|---|---|---|
| 1 | Enroll when capacity is available and prerequisite is satisfied | Pass | Service logic selects `ENROLLED` when the prerequisite is satisfied and enrolled count is below course capacity. Unit test covers available capacity. |
| 2 | When full, add an eligible learner to the waitlist once | Pass, with concurrency caveat | Service checks for an existing student-course enrollment and uses `WAITLISTED` when capacity is full. Tests cover full capacity and duplicate enrollment. No database uniqueness constraint on student/course is present, so simultaneous duplicate requests are not fully protected. |
| 3 | Drop an enrolled learner and promote the earliest eligible waitlisted learner | Pass for sequential flow | Drop flow marks the enrollment `DROPPED`, reads waitlisted records ordered by `createdAt ASC`, and promotes the first eligible learner. FIFO promotion tests are present. |
| 4 | Apply a consistent policy to prerequisite-ineligible waitlisted learners | Pass | The documented policy is to mark an ineligible waitlisted learner `INELIGIBLE` and continue scanning for the next eligible learner. A focused test covers skipping an ineligible learner. |
| 5 | Reject invalid requests and repeated/invalid drops without corrupting state | Pass in covered cases | Tests cover invalid course, invalid enrollment lookup, dropping a non-enrolled/waitlisted record, and repeated drop. |
| 6 | Provide focused automated tests for core behavior | Pass | Supplied test suite reports 23 passing tests across service, controller, and application tests. |
| 7 | Preserve concurrency/transaction behavior where applicable | Partial / limitation | `dropEnrollment()` is transactional, but enrollment does not have explicit transaction/locking protection for capacity checks. No database-level unique constraint protects the student-course pair. Concurrent enrollment/drop races remain a known risk; no concurrency test evidence was supplied. |

## 3. Design and Implementation Review

| Area | Assessment |
|---|---|
| Service boundary | Enrollment and drop behavior are implemented in the service layer and exposed through REST controllers. |
| Data model | Course and Enrollment entities are present; enrollment status and prerequisite completion are represented. |
| Capacity invariant | Sequential service flow checks enrolled count against course capacity. Concurrent requests can still race without locking or an equivalent atomic database strategy. |
| Waitlist ordering | Repository method requests waitlisted records ordered by `createdAt ASC`. |
| Ineligible learner policy | Mark the learner `INELIGIBLE` and continue to the next eligible waitlisted learner. |
| Persistence | Spring Data JPA with H2 in-memory configuration is used in the supplied project. |
| API surface | Course and enrollment endpoints are present; no frontend or out-of-scope modules are included. |

## 4. AI-Assisted Engineering Evidence

- AI assistance was used for requirement analysis, implementation review, test-coverage review, design documentation, and assessment evidence organization.
- The ineligible-at-front waitlist policy and concurrency limitations were reviewed and documented.
- The AI usage record does not claim that AI generated or modified application code.
- Final verification evidence is the user's reported Maven test run: 23 passed, with no failures or errors.

See `AI-USAGE-RECORD.md` for the detailed prompt/use record.

## 5. Known Limitations and Follow-up Risks

1. Add a database-level unique constraint for the student-course enrollment pair to protect against concurrent duplicate requests.
2. Add a concurrency-safe capacity reservation strategy, such as locking the course row or using an atomic database operation within a transaction.
3. Add focused integration/concurrency tests if concurrent enrollment requests are part of the starter's expected operating conditions.
4. Consider a deterministic secondary ordering key (for example, enrollment ID) if multiple waitlist records can share the same `createdAt` timestamp.
5. The supplied H2 in-memory configuration is suitable for local/test execution; production database behavior and migration strategy are not demonstrated.

## 6. Overall Evidence-Based Outcome

**Core sequential acceptance behavior: Demonstrated by implementation and supplied tests.**

**Concurrency robustness: Not fully demonstrated; known limitations are documented.**

**Documentation and AI-use evidence: Prepared**, based on the files listed above.

This scorecard intentionally avoids assigning numerical marks because the assessment owner's original scoring scale and category weights were not available. Use the official scorecard instead if it is later provided.