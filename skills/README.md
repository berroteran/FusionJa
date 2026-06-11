# Skills Workspace

Base structure for portable skills across IDEs in 2026.

## Recommended Structure

```
skills/
└── <skill-name>/
    ├── SKILL.md
    ├── agents/openai.yaml
    ├── scripts/
    ├── references/
    └── assets/
```

## Create a New Skill

```bash
python skills/cross-ide-skill-template/scripts/create_skill_structure.py --name my-skill --out skills
```

## Validate a Skill

```bash
python skills/cross-ide-skill-template/scripts/validate_skill_structure.py --skill-dir skills/my-skill
```
