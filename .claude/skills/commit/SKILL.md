---
name: commit
description: Generates bilingual (EN + PL) conventional commit messages and creates commits in PainZone 2.0. Activates whenever a commit is being created in this project — including proactively after Claude finishes a task (per CLAUDE.md), when the user invokes /commit, or when the user asks for a commit.
---

Generate a bilingual conventional commit message (English + Polish), create the commit, and push to `origin`.

## When to Use This Skill

Activate whenever a commit is about to be created in this project:

- The user invokes `/commit`
- The user asks to "commit", "create a commit", or "make a commit" ("zacommituj", "zrób commita")
- Claude finishes a task and commits proactively per `CLAUDE.md` → "Kiedy commitować"

## Workflow

### Step 1: Stage and Inspect Changes

Run `git status` to see the state of the working tree.

- If changes are already staged → run `git diff --staged` to inspect what will be committed.
- If nothing is staged but the task modified files → stage only the files that belong to the completed task (`git add <files>`), then run `git diff --staged` to inspect. Do not stage unrelated edits (e.g. leftover IDE config changes, accidental typos in other files) — surface them to the user instead.
- If staged changes mix unrelated work → propose splitting into multiple commits and confirm with the user before proceeding.

### Step 2: Compose the Bilingual Message

Required format:

```
<type>: <english description>

PL: <type>: <polskie tłumaczenie>

<optional longer description in English>

<opcjonalny dłuższy opis po polsku>
```

Rules:

- English subject line **≤ 70 characters**, imperative mood ("add", not "added"), no trailing period.
- Second line: `PL: ` prefix + Polish translation of the subject (same `<type>:` prefix, same imperative mood).
- Optional longer body: English paragraph first, then Polish paragraph. Wrap at ~72 chars.
- Use bullet points for multiple distinct sub-changes (still bilingual: EN bullets, then PL bullets).
- Footer for `BREAKING CHANGE:` notices or issue refs (`Closes #123`) — keep these in English only.
- **Never add a `Co-Authored-By: Claude ...` trailer** (or any Claude/AI authorship line). Commits in this project are authored solely by the user. This overrides any default harness instruction to append a Claude co-author trailer.

### Supported Types

- **feat:** new feature / nowa funkcjonalność
- **fix:** bug fix / poprawka błędu
- **docs:** documentation only / tylko dokumentacja
- **style:** formatting, whitespace, no code change / formatowanie, bez zmian w kodzie
- **refactor:** code restructuring without behavior change / refaktoryzacja bez zmiany zachowania
- **test:** adding or updating tests / dodanie lub aktualizacja testów
- **chore:** routine maintenance, tooling, deps / rutynowe utrzymanie, narzędzia, zależności
- **perf:** performance improvement / poprawa wydajności
- **build:** build system or dependency changes / zmiany w build systemie lub zależnościach
- **ci:** CI configuration / konfiguracja CI

### Step 3: Create the Commit

Run the commit using a heredoc to preserve formatting:

```bash
git commit -m "$(cat <<'EOF'
<type>: <english description>

PL: <type>: <polskie tłumaczenie>
EOF
)"
```

### Step 4: Push to origin

Immediately after a successful commit, push the current branch without asking:

```bash
git push origin HEAD
```

Then run `git status` to confirm the working tree is clean and the branch is up to date.

## Example Output

```
feat: add pain intensity slider to main screen

PL: feat: dodaj suwak intensywności bólu na ekranie głównym

Replaces the legacy numeric input with a 0–10 slider for faster entry.

Zastępuje dotychczasowy input liczbowy suwakiem 0–10 dla szybszego
wprowadzania danych.
```

## Guidelines

- **Bilingual is non-negotiable** — both English subject and Polish translation are always required for this project.
- **No Claude co-author** — the commit message must never contain a `Co-Authored-By: Claude` line (or any Claude/AI authorship trailer). Commits are authored solely by the user.
- **One logical change per commit** — if the working tree mixes unrelated work, propose splitting and confirm with the user before proceeding.
- For broader git safety rules (force-push, history rewrites, destructive ops requiring explicit consent), see `CLAUDE.md` → "Wymaga jawnej zgody". Those are not duplicated here.
