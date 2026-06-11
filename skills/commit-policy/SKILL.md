---
name: commit-policy
description: Draft and validate kernel-style commit messages with 50/72 formatting, an imperative title, a required blank separator line, body text focused on what and why, Fixes references with SHA-1 of at least 12 characters, and Assisted-by attribution. Use when Codex must write, review, or correct commit messages, including history cleanup before PRs.
---

# Commit Policy

Author: Omar Berroterán Silva.

## Objective

Generate self-contained, scannable, and auditable commit messages.
Keep one functional change per commit.

## Workflow

1. Keep a single scope per commit.
2. Write an imperative title with a maximum of 50 characters.
3. Leave the second line blank.
4. Write a technical body with lines capped at 72 characters.
5. Explain problem, impact, and rationale for the change.
6. Skip implementation details that are already obvious in the code.
7. Add `Fixes:` references when applicable.
8. Always end with `Assisted-by:`.
9. Do not use AI-generated `Signed-off-by:`.

## Mandatory Rules

- Use this title format: `[Component/Module]: Imperative summary`.
- Capitalize the first letter of the title.
- Do not end the title with a period.
- Keep line 2 completely blank.
- Limit every body line to 72 characters.
- Include `Assisted-by: <Model or Tool>` as the last non-empty line.
- If `Fixes:` is present, use `Fixes: <sha12+> ("<commit title>")`.
- Do not include `Signed-off-by:` in AI-generated commit messages.

## Plantilla

```text
[Component/Module]: Imperative summary under 50 chars

Explain the original problem context and user or system impact.
Describe what changed and why this approach is safer or clearer.
Mention design tradeoffs only when they affect maintenance.

Fixes: a1b2c3d4e5f6 ("Original commit title")
Assisted-by: OpenAI GPT-5
```

## Validation Checklist

- Title <= 50 characters.
- Imperative title, using `[Component/Module]: ...`.
- Blank line 2.
- Body lines <= 72 characters.
- Valid `Fixes:` reference when applicable.
- `Assisted-by:` present as the last non-empty line.
- No `Signed-off-by:`.

## Minimal Questions if Context Is Missing

- What was the observable problem before the change.
- What user or technical impact is resolved.
- Whether there is a previous commit to reference with `Fixes:`.

