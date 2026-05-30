# Contributing Guide

This project is intended to simulate a professional software development workflow while remaining focused on learning and engineering best practices.

---

## 1) Workflow Rules

- No direct pushes to `main`
- All changes must go through a Pull Request (PR)
- Every PR must be linked to a GitHub Issue
- Prefer small and focused PRs
- Use Squash and Merge

---

## 2) Branching & Commits

Follow the standards defined in:

```text
BRANCHING_AND_COMMITS.md
```

---

## 3) Code Review Checklist

### Correctness

- Does the implementation satisfy the issue requirements?
- Are validation rules enforced?
- Are edge cases handled properly?
- Are error responses consistent?

### Architecture

- Controllers only handle HTTP concerns
- Business rules remain in Use Cases / Services
- Persistence logic remains in Repositories
- Responsibilities are clearly separated
- Naming is clear and consistent

### Persistence

- Database changes include Flyway migrations
- Queries are appropriate for the use case
- Transactions are used when necessary

### Security

- No secrets committed
- Authentication works correctly
- Authorization rules are respected
- Users can only perform actions allowed by their role

### Testing

- Core business logic is covered by tests
- Tests are deterministic
- Existing tests continue to pass

### Documentation

- README updated when necessary
- Endpoints documented when applicable
- Public behavior changes are documented

---

## 4) Pull Request Checklist

Before requesting review:

- [ ] Project builds successfully
- [ ] Tests pass
- [ ] Acceptance criteria are satisfied
- [ ] No secrets were committed
- [ ] Documentation was updated if necessary

---

## 5) PR Etiquette

- Keep PRs focused on a single issue
- Avoid mixing unrelated changes
- Respond to review comments with fixes or justification
- Prefer multiple small PRs over one large PR

---

## 6) Definition of Done

A task is complete when:

- Acceptance criteria are satisfied
- Code builds successfully
- Relevant tests pass
- Pull Request is merged
- Related GitHub Issue is closed