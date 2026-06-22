# PainZone 2.0

Android app do śledzenia postępów i prowadzenia dzienniczka treningów na siłowni.

## Stack

Kotlin · Jetpack Compose · Room · Hilt · MVVM / Clean Architecture

## Build & test

- Build: `./gradlew assembleDebug`
- Unit tests: `./gradlew test`
- Lint: `./gradlew lint`

## Project layout

- Aktualny stan projektu: @docs/STATUS.md
- Roadmap zadań: @docs/07-roadmap.md
- Wymagania jakościowe: @docs/08-quality.md

## Konwencje językowe

- Rozmowa Claude ↔ user: po polsku
- Komentarze inline w kodzie: po angielsku
- ADR-y i docs designu (`docs/adr/`, `docs/0X-*.md`): po polsku
- Commit messages: dwujęzyczne EN+PL (skill: `.claude/skills/commit/SKILL.md`)

## Konwencje kodu

- Komentarze w kodzie tylko gdy non-obvious *why*
- Każdy `docs/*.md` zaczyna się od `> TL;DR: [1 zdanie]`
- Nie duplikuj między docs — linkuj do źródła

## Docs designu

Prowadzi skill `.claude/skills/design-phase/SKILL.md` — struktura Spec/Rationale, hard limit 100 linii/plik.

## Git

- Push bezpośrednio na `main` (jednoosobowy projekt)
- Commit format: skill `.claude/skills/commit/SKILL.md`
- Nieodwracalne operacje git (force push, reset --hard, rebase na wypushowanych) — **zawsze pytaj**

## Sposób pracy

Pracuj samodzielnie: dostajesz task → robisz całość (kod + build + commit lokalny) → meldujesz co wprowadziłeś i jak zrobić smoke test. Bez pokazywania planu i czekania na akceptację w trakcie. Push na `main` dopiero po potwierdzeniu smoke testu przez usera. Szczegóły cyklu: `.claude/rules/workflow.md`.