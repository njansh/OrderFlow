# OrderFlow Review Instructions

Review priorities:

- Validate layered architecture separation
- Controllers must not contain business rules
- Use cases must contain business logic
- Repositories should only handle persistence
- Validate authorization rules by role
- Verify status transition rules
- Verify DTO validation
- Suggest missing tests when applicable
- Detect duplicated code
- Detect security issues