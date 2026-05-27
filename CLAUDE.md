# PainZone 2.0

Android app do śledzenia postępów i prowadzenia dzienniczka treningów na siłowni. Stack: **Kotlin + Jetpack Compose**.

**Aktualny stan:** @docs/STATUS.md

---

# Konwencja językowa

## Artefakty projektu
- **Rozmowa Claude ↔ user:** po polsku.
- **Komentarze inline w kodzie:** po angielsku (mimo że projekt PL).
- **ADR-y i docs designu** (`docs/adr/`, `docs/01-*.md`, PRD, architecture, threat model, glossary): po polsku.
- **Commit messages i PR descriptions:** dwujęzyczne EN+PL (skill: `.claude/skills/commit/SKILL.md`).

---

# Dokumentacja — dyscyplina tokenów

- Każdy `docs/*.md` zaczyna się od `> TL;DR: [1 zdanie]` tuż pod nagłówkiem.
- Nie duplikuj między docs — linkuj do źródła.
- Każde zdanie zarabia: wartość dla Claude-context, portfolio lub nauki. 0/3 → wytnij.
- **Docs designu (`docs/0X-*.md`, ADR-y, threat model, roadmap, quality):** prowadzi skill `.claude/skills/design-phase/SKILL.md` — struktura Spec/Rationale, hard limit 100 linii/plik, reading rules (`limit=60` default), phase output contracts, anti-patterns (ASCII art, mermaid w Spec, długie why-paragraphs). Aktywuje się automatycznie przy edycji/dyskusji docs designu.

---

# Styl komunikacji

> Projekt uczący/portfoliowy — Krzysiek chce być świadomy każdej decyzji i uczyć się architektury. Stąd: dużo pytań, mocna kontra, proaktywne alternatywy.

- **IMPORTANT — Pytaj często:** przy każdej niejednoznaczności (scope, intencja, biblioteka, nazwa, kierunek, struktura folderów) **zatrzymaj się i zapytaj**.
- **IMPORTANT — Niezgoda wprost:** gdy widzisz słaby pomysł — powiedz to z uzasadnieniem ("zły kierunek bo X, lepiej Y bo Z"). Bez owijania. Decyzja należy do Krzyśka, ale chcesz usłyszeć kontra.
- **Proaktywne alternatywy:** gdy widzisz lepszą opcję niż to o co proszę — powiedz, nawet jeśli prośba była jasna. Format: „Można też tak: X — pro Y, kontra Z. Co wybierasz?"
- **Długość odpowiedzi:** zwięźle + krótkie uzasadnienie (2–4 zdania). Bez podsumowań na końcu. Przy decyzjach merytorycznych możesz rozwinąć.

---

# Git workflow

## Kiedy commitować
- Po skończeniu każdego zadania **automatycznie zrób commita i wypushuj** na `origin/main` — bez pytania.
- Jeden commit = jedna spójna zmiana. Niezwiązane rzeczy → kilka osobnych commitów.
- Push bezpośrednio na `main` jest OK — to jednoosobowy projekt.

## Jak commitować
**Zawsze przy commitcie w tym projekcie** użyj skilla `.claude/skills/commit/SKILL.md` (format, typy, dwujęzyczność EN+PL, przykłady).

## Wymaga jawnej zgody
Przed jakąkolwiek nieodwracalną operacją git (force push, reset --hard, branch -D, rebase/amend na wypushowanych commitach) — **zawsze pytaj**.

---

# Sposób pracy nad kodem (Faza 8+)

Gdy pracujemy nad faza 8, zawsze stosuj się do zasad zawartych w docs/rules.md