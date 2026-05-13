# Skills Workspace

Estructura base para skills portable entre IDEs en 2026.

## Estructura recomendada

```
skills/
└── <skill-name>/
    ├── SKILL.md
    ├── agents/openai.yaml
    ├── scripts/
    ├── references/
    └── assets/
```

## Crear un skill nuevo

```bash
python skills/cross-ide-skill-template/scripts/create_skill_structure.py --name my-skill --out skills
```

## Validar un skill

```bash
python skills/cross-ide-skill-template/scripts/validate_skill_structure.py --skill-dir skills/my-skill
```
