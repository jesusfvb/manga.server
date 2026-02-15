# GitHub Copilot Instructions

## Comments Policy

- Do NOT generate Javadoc comments.
- Do NOT generate comments above classes.
- Do NOT generate comments above methods.
- Do NOT add decorative or redundant comments.

- Only add comments INSIDE methods when:
    - The logic is non-obvious
    - A business rule is being applied
    - A security-related validation is performed
    - There is a potential edge case

- Comments must be short and practical.
- No emojis.
- No tutorial-style explanations.

Example of allowed comment inside a method:

    // Validate ownership before updating resource
    if (!resource.getUserId().equals(currentUserId)) {
        throw new AccessDeniedException("Not allowed");
    }

---

## File Generation Policy

- Do NOT generate new files unless explicitly requested.
- Do NOT generate:
    - change logs
    - migration reports
    - documentation files
    - summaries of modifications
    - markdown reports

- Only generate the exact class, method, or snippet requested.

---

## Code Style

- Keep implementations minimal and production-ready.
- Avoid unnecessary abstraction.
- Do not overengineer.
- Do not explain obvious code.
