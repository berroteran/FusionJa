# IDE Compatibility (2026)

## Objective

Keep skill folders editable and executable from IntelliJ IDEA, VS Code, Eclipse, NetBeans, and plain terminal workflows.

## Contract

- Store source files as UTF-8 text.
- Use LF line endings by default.
- Use relative paths only.
- Keep folder names lowercase-hyphen-case.
- Avoid IDE-specific metadata files inside skill folders.

## Recommended Stack

- `SKILL.md` for activation logic and workflow.
- `agents/openai.yaml` for UI metadata.
- `scripts/` for deterministic automation scripts (Python preferred).
- `references/` for long docs.
- `assets/` for templates and binary artifacts.

## Validation Routine

1. Run `validate_skill_structure.py`.
2. Ensure `SKILL.md` has `name` and `description` in frontmatter.
3. Ensure `agents/openai.yaml` defines interface metadata.
4. Ensure optional folders are directories, not files.
