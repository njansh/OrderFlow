# Branching & Commit Convention

This project follows a lightweight Git workflow to keep `main` stable, trace work through GitHub Issues, and maintain a clean history.

---

## 1) Core Rules

- ✅ No direct pushes to `main`
- ✅ Every change must go through a Pull Request (PR)
- ✅ Every PR should be linked to a GitHub Issue
- ✅ Prefer small and focused PRs
- ✅ Use Squash and Merge
- ✅ One issue = one branch whenever possible

---

## 2) Branch Naming

Create branches from `main`.

Pattern:

```text
feature/<issue-number>-<short-description>
fix/<issue-number>-<short-description>
chore/<issue-number>-<short-description>
docs/<issue-number>-<short-description>
refactor/<issue-number>-<short-description>
```

Examples:

```text
feature/7-signup
feature/10-product-entity
feature/21-create-order

fix/27-status-transition

chore/2-postgresql
chore/3-flyway

docs/40-readme

refactor/42-remove-duplication
```

---

## 3) Commit Messages

Use short and consistent commits.

Pattern:

```text
feat: <description>
fix: <description>
chore: <description>
docs: <description>
refactor: <description>
test: <description>
```

Examples:

```text
feat: create user entity
feat: implement signup endpoint
feat: create order workflow

fix: validate status transition

chore: configure flyway
chore: add docker compose

test: add order service tests

docs: update project readme
```

---

## 4) Issue Workflow

Every feature starts with a GitHub Issue.

Example:

```text
#10 Create Product Entity
```

Create branch:

```bash
git checkout main
git pull
git checkout -b feature/10-product-entity
```

Work on the task.

Commit changes:

```bash
git add .
git commit -m "feat: create product entity"
```

Push branch:

```bash
git push -u origin feature/10-product-entity
```

Open Pull Request.

---

## 5) Pull Requests

PR title should follow commit conventions.

Examples:

```text
feat: create product entity
feat: implement signup
fix: validate status transitions
```

PR description must reference the issue:

```text
Closes #10
```

When the PR is merged, GitHub will automatically close the issue.

---

## 6) Daily Commands

Start work:

```bash
git checkout main
git pull
git checkout -b feature/<issue-number>-<description>
```

Commit:

```bash
git add .
git commit -m "feat: <description>"
```

Push:

```bash
git push -u origin <branch-name>
```

---

## 7) Merging Strategy

Recommended:

✅ Squash and Merge

Benefits:

- Cleaner history
- One commit per completed issue
- Easier project navigation
- Easier rollback if necessary

Example final commit after merge:

```text
feat: create product entity
```

---

## 8) Definition of Done

An issue is considered complete when:

- Acceptance criteria are satisfied
- Application builds successfully
- Relevant tests pass
- Pull Request is merged
- GitHub Issue is automatically closed