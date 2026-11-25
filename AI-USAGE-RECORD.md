# AI Usage Record – Course Waitlist Assessment

## 1. Purpose

AI was used as an engineering assistant during the Course Waitlist assessment to support requirement analysis, implementation review, test coverage review, technical design documentation, and verification.

The candidate independently reviewed the suggestions against the existing starter project, source code, assessment acceptance criteria, and test execution results.

## 2. AI-Assisted Activities

### Activity 1: Assessment Requirement Analysis

**Prompt summary:**

Review the Course Waitlist assessment and identify the required functionality, acceptance criteria, scope boundaries, and deliverables. Keep all recommendations strictly within the assessment scope.

**AI response summary:**

- Identified enrollment, dropping, and FIFO waitlist promotion as the core workflow.
- Identified prerequisite validation and duplicate enrollment prevention.
- Identified required automated tests and documentation deliverables.
- Recommended avoiding unrelated features and infrastructure.

**Decision:** Accepted.

**Files and decisions informed:**
- Overall implementation review.
- Test coverage checklist.
- Design specification structure.

### Activity 2: Existing Implementation Review

**Prompt summary:**

Review the existing CourseServiceImpl, EnrollmentServiceImpl, entities, and repositories against the assessment acceptance criteria. Identify gaps without introducing unnecessary features.

**AI response summary:**

- Reviewed enrollment capacity checks.
- Reviewed duplicate enrollment prevention.
- Reviewed prerequisite handling.
- Reviewed FIFO waitlist promotion.
- Identified the ineligible learner policy: mark the learner INELIGIBLE and continue to the next eligible learner.
- Identified concurrency as a limitation requiring documentation.

**Decision:** Accepted after reviewing the existing Java implementation.

**Files reviewed:**
- CourseServiceImpl.java
- EnrollmentServiceImpl.java
- Course.java
- Enrollment.java
- CourseRepository.java
- EnrollmentRepository.java

### Activity 3: Automated Test Review

**Prompt summary:**

Review EnrollmentServiceImplTest.java and verify whether the test scenarios cover the assessment acceptance criteria.

**AI response summary:**

Reviewed tests for:
- Enrollment when capacity is available.
- Waitlisting when the course is full.
- Duplicate enrollment.
- Prerequisite failure.
- Invalid course ID.
- FIFO promotion.
- Skipping ineligible waitlisted learners.
- Invalid and repeated drops.
- Enrollment retrieval.

**Decision:** Accepted after comparing the test source with the acceptance criteria.

**File reviewed:**
- EnrollmentServiceImplTest.java

### Activity 4: Configuration and Concurrency Review

**Prompt summary:**

Review application configuration and Maven dependencies. Identify persistence and concurrency considerations relevant to the assessment.

**AI response summary:**

- Identified H2 in-memory database configuration.
- Reviewed Spring Data JPA and Hibernate usage.
- Identified that explicit locking was not present in the supplied enrollment service and repository code.
- Documented the risk of concurrent enrollment requests exceeding course capacity.

**Decision:** Accepted as a documented limitation. No additional infrastructure or concurrency implementation was claimed.

**Files reviewed:**
- application.yaml
- pom.xml
- EnrollmentServiceImpl.java
- EnrollmentRepository.java

### Activity 5: Design Specification Preparation

**Prompt summary:**

Prepare a proportionate design specification based on the existing implementation, data model, persistence configuration, and assessment requirements.

**AI response summary:**

Prepared documentation covering:
- Service boundaries.
- Entity and repository responsibilities.
- Enrollment workflow.
- Capacity invariants.
- FIFO promotion.
- Prerequisite and waitlist assumptions.
- Persistence and concurrency approach.
- Testing evidence.
- Limitations and trade-offs.

**Decision:** Accepted after reviewing the document against the supplied implementation.

**File created:**
- DESIGN-SPECIFICATION.md

## 3. AI Suggestions – Acceptance Summary

| AI-assisted activity | Decision | Reason |
|---|---|---|
| Assessment requirement breakdown | Accepted | Matches assessment scope |
| Existing service implementation review | Accepted | Compared with supplied source |
| Test coverage review | Accepted | Compared with existing tests |
| Concurrency risk identification | Accepted | Supported by supplied code |
| Design specification preparation | Accepted after review | Based on existing implementation |

No AI-generated application code changes are claimed in this record. The AI assistance documented here was primarily requirement analysis, code and test review, technical assessment, and documentation preparation.

## 4. Independent Verification

The following verification evidence was reviewed:

**Command executed:**

```bash
mvn clean test
```

**Reported test execution:**

| Test class | Tests |
|---|---:|
| CourseControllerTest | 4 |
| EnrollmentControllerTest | 4 |
| CourseRegistrationApplicationTests | 2 |
| EnrollmentServiceImplTest | 13 |
| Total | 23 |

**Execution result:**

- Tests run: 23
- Failures: 0
- Errors: 0
- Skipped: 0
- Build result: BUILD SUCCESS

The test execution output was reviewed independently of the AI-generated recommendations.

## 5. Limitations

- No dedicated concurrent enrollment test is included in the reported test execution.
- Explicit locking for enrollment capacity validation is not implemented.
- The current test evidence does not establish production-level concurrency guarantees.
- AI recommendations were reviewed against the supplied source and assessment requirements rather than accepted without verification.

## 6. Conclusion

AI was used as an engineering assistant for requirement decomposition, implementation review, test coverage analysis, technical risk identification, and design documentation.

The candidate reviewed the recommendations against the existing Java implementation, assessment acceptance criteria, and Maven test execution output.

The goal was to use AI responsibly while retaining independent engineering judgment and keeping the solution within the defined assessment scope.