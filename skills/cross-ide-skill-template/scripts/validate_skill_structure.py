#!/usr/bin/env python3
#
# Copyright 2026 Omar Berroterán Silva
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Validate minimum required structure for a Codex skill folder."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

REQUIRED_FILES = (
    "SKILL.md",
    "agents/openai.yaml",
)

OPTIONAL_DIRS = (
    "scripts",
    "references",
    "assets",
)


def validate_frontmatter(skill_md: Path) -> list[str]:
    errors: list[str] = []
    content = skill_md.read_text(encoding="utf-8")

    if not content.startswith("---\n"):
        errors.append("SKILL.md must start with YAML frontmatter '---'.")
        return errors

    parts = content.split("---", 2)
    if len(parts) < 3:
        errors.append("SKILL.md frontmatter block is incomplete.")
        return errors

    frontmatter = parts[1]
    if not re.search(r"(?m)^name:\s*[a-z0-9][a-z0-9-]*\s*$", frontmatter):
        errors.append("Frontmatter 'name' is missing or invalid (use hyphen-case).")
    if not re.search(r"(?m)^description:\s*.+$", frontmatter):
        errors.append("Frontmatter 'description' is missing.")
    return errors


def validate_openai_yaml(openai_yaml: Path) -> list[str]:
    errors: list[str] = []
    content = openai_yaml.read_text(encoding="utf-8")

    required_keys = (
        "interface:",
        "display_name:",
        "short_description:",
        "default_prompt:",
    )
    for key in required_keys:
        if key not in content:
            errors.append(f"agents/openai.yaml missing key: {key}")
    return errors


def validate_structure(skill_dir: Path) -> list[str]:
    errors: list[str] = []

    if not skill_dir.exists():
        return [f"Skill directory does not exist: {skill_dir}"]
    if not skill_dir.is_dir():
        return [f"Skill path is not a directory: {skill_dir}"]

    for rel in REQUIRED_FILES:
        required = skill_dir / rel
        if not required.exists():
            errors.append(f"Missing required file: {rel}")

    for optional_dir in OPTIONAL_DIRS:
        target = skill_dir / optional_dir
        if target.exists() and not target.is_dir():
            errors.append(f"Expected directory but found file: {optional_dir}")

    skill_md = skill_dir / "SKILL.md"
    openai_yaml = skill_dir / "agents/openai.yaml"
    if skill_md.exists():
        errors.extend(validate_frontmatter(skill_md))
    if openai_yaml.exists():
        errors.extend(validate_openai_yaml(openai_yaml))

    return errors


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description="Validate skill folder structure.")
    parser.add_argument(
        "--skill-dir",
        required=True,
        help="Path to the skill directory (e.g., skills/my-skill)",
    )
    return parser


def main() -> int:
    parser = build_parser()
    args = parser.parse_args()

    skill_dir = Path(args.skill_dir).resolve()
    errors = validate_structure(skill_dir)

    if errors:
        print("[ERROR] Skill validation failed:")
        for error in errors:
            print(f" - {error}")
        return 1

    print(f"[OK] Skill structure is valid: {skill_dir}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
