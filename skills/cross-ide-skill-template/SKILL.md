---
name: cross-ide-skill-template
description: Scaffold and maintain Codex skills using an IDE-agnostic layout compatible with IntelliJ IDEA, VS Code, Eclipse, NetBeans, and LSP-based editors. Use when creating a new skill folder, validating required skill files, or enforcing cross-platform conventions for scripts, paths, and formatting.
---

# Cross Ide Skill Template

## Overview

Create reusable skills with a stable folder contract and deterministic automation scripts.
Use this template as the default starting point for new skills in this repository.

## Skill Contract

```
<skill-name>/
├── SKILL.md
├── agents/
│   └── openai.yaml
├── scripts/
├── references/
└── assets/
```

## Workflow

1. Create a new skill folder with `scripts/create_skill_structure.py`.
2. Rename skill using lowercase hyphen-case only.
3. Update SKILL.md frontmatter (`name`, `description`) with precise trigger conditions.
4. Update `agents/openai.yaml` (`display_name`, `short_description`, `default_prompt`).
5. Add deterministic automations under `scripts/` and usage docs under `references/`.
6. Validate the folder with `scripts/validate_skill_structure.py`.

## IDE Compatibility Rules

- Use UTF-8 and LF line endings.
- Use relative paths only.
- Avoid IDE metadata in skill folders.
- Keep scripts cross-platform (Python preferred for shared logic).
- Keep SKILL.md concise and move detailed docs to `references/`.

## Validation

Run from repo root:

```bash
python skills/cross-ide-skill-template/scripts/validate_skill_structure.py --skill-dir skills/<skill-name>
```

For PowerShell:

```powershell
python .\skills\cross-ide-skill-template\scripts\validate_skill_structure.py --skill-dir .\skills\<skill-name>
```
