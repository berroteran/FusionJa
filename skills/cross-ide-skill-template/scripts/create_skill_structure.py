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

"""Create a portable skill folder structure compatible with modern IDEs."""

from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

SKILL_TEMPLATE = """---
name: {skill_name}
description: Describe precisely what this skill does and when it should be used.
---

# {skill_title}

## Overview

Describe the skill outcome in 1-2 lines.

## Workflow

1. Define the task boundary.
2. Use scripts from `scripts/` for deterministic work.
3. Keep deep docs under `references/`.
4. Keep reusable output files under `assets/`.
"""

OPENAI_YAML_TEMPLATE = """interface:
  display_name: "{display_name}"
  short_description: "{short_description}"
  default_prompt: "Use ${skill_name} to solve this request."
policy:
  allow_implicit_invocation: false
"""

ALLOWED_RESOURCES = ("scripts", "references", "assets")
MAX_SKILL_NAME_LENGTH = 64


def normalize_skill_name(raw_name: str) -> str:
    cleaned = raw_name.strip().lower()
    cleaned = re.sub(r"[^a-z0-9]+", "-", cleaned)
    cleaned = re.sub(r"-{2,}", "-", cleaned).strip("-")
    return cleaned


def to_title_case(skill_name: str) -> str:
    return " ".join(word.capitalize() for word in skill_name.split("-"))


def parse_resources(raw_resources: str) -> list[str]:
    resources = [item.strip() for item in raw_resources.split(",") if item.strip()]
    if not resources:
        return list(ALLOWED_RESOURCES)

    invalid = sorted(set(resources) - set(ALLOWED_RESOURCES))
    if invalid:
        raise ValueError(f"Unknown resource(s): {', '.join(invalid)}")
    return list(dict.fromkeys(resources))


def write_file(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8", newline="\n")


def create_structure(base_dir: Path, skill_name: str, resources: list[str]) -> Path:
    skill_dir = base_dir / skill_name
    if skill_dir.exists():
        raise FileExistsError(f"Skill directory already exists: {skill_dir}")

    skill_dir.mkdir(parents=True, exist_ok=False)
    write_file(
        skill_dir / "SKILL.md",
        SKILL_TEMPLATE.format(skill_name=skill_name, skill_title=to_title_case(skill_name)),
    )

    short_description = "Portable skill scaffold for cross-IDE usage."
    openai_yaml = OPENAI_YAML_TEMPLATE.format(
        display_name=to_title_case(skill_name),
        short_description=short_description,
        skill_name=skill_name,
    )
    write_file(skill_dir / "agents" / "openai.yaml", openai_yaml)

    for resource in resources:
        resource_dir = skill_dir / resource
        resource_dir.mkdir(parents=True, exist_ok=True)
        if resource == "assets":
            write_file(resource_dir / ".gitkeep", "")

    return skill_dir


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Create skill scaffolds compatible with modern IDE workflows."
    )
    parser.add_argument("--name", required=True, help="Skill name (any case, normalized to hyphen-case)")
    parser.add_argument("--out", default="skills", help="Output directory (default: skills)")
    parser.add_argument(
        "--resources",
        default="scripts,references,assets",
        help="Comma-separated resources: scripts,references,assets",
    )
    return parser


def main() -> int:
    parser = build_parser()
    args = parser.parse_args()

    skill_name = normalize_skill_name(args.name)
    if not skill_name:
        print("[ERROR] Skill name must include alphanumeric characters.")
        return 1
    if len(skill_name) > MAX_SKILL_NAME_LENGTH:
        print(f"[ERROR] Skill name too long ({len(skill_name)}). Max: {MAX_SKILL_NAME_LENGTH}.")
        return 1

    try:
        resources = parse_resources(args.resources)
    except ValueError as exc:
        print(f"[ERROR] {exc}")
        return 1

    output_dir = Path(args.out).resolve()
    output_dir.mkdir(parents=True, exist_ok=True)

    try:
        created_path = create_structure(output_dir, skill_name, resources)
    except FileExistsError as exc:
        print(f"[ERROR] {exc}")
        return 1

    print(f"[OK] Skill scaffold created at: {created_path}")
    print("[NEXT] Edit SKILL.md and agents/openai.yaml.")
    print("[NEXT] Validate with validate_skill_structure.py.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
