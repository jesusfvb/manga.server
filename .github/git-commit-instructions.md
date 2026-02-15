# GitHub Copilot Commit Message Instructions

## Required Format

Use the following structure strictly:

<type> (<scope>): <short description>

Example:

feat (mangas): add pagination to manga endpoint

---

## Rules

- Always include a scope.
- Scope must represent the module or feature being modified.
- Use lowercase for type and scope.
- Use present tense.
- Do not end the message with a period.
- Keep description under 72 characters.
- Do not use emojis.
- Do not generate multiline commit messages unless explicitly requested.

---

## Allowed Types

- feat
- fix
- refactor
- chore
- test
- docs

---

## Scope Examples

- mangas
- chapters
- auth
- security
- users
- config
- frontend
- backend
- search

---

## Forbidden Messages

Do NOT generate generic messages like:

- update
- changes
- fixes
- minor improvements
- miscellaneous updates

---

## Important

Only describe changes that actually exist.
Do not invent modifications.
Do not generate summaries or reports.
