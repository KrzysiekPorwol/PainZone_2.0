# PainZone 2.0

Android app do śledzenia postępów i prowadzenia dzienniczka treningów na siłowni. Stack: **Kotlin + Jetpack Compose**.

---

# Aktualny stan projektu

> Sekcja żywa — Claude utrzymuje ją aktualną. Edytowana przy każdej zmianie stanu (faza → faza, milestone → milestone). Krzysiek nie musi pamiętać o aktualizacji.

- **Etap:** Faza 1.5 — ryzyka i konkurencja (`docs/risks-and-assumptions.md`, `docs/competitive-analysis.md`)
- **Proces designu:** `docs/00-process.md` — czytaj **gdy** dyskutujemy fazę designu, status fazy się zmienia, lub user pyta "gdzie jesteśmy"
  - **Glossary domenowy:** `docs/glossary.md` — czytaj **gdy** dyskutujemy termin domenowy lub piszemy artefakt z domen.
  **Odpowiedzialność Claude:** Aktualizuj tę sekcję gdy stan się zmienia. Aktualizuj `docs/00-process.md` status table równolegle. Nie pytaj usera o pozwolenie na te aktualizacje — to mechanika, nie decyzja merytoryczna.

---

# Konwencja językowa

## Artefakty projektu
- **Rozmowa Claude ↔ user:** po polsku.
- **Kod (identyfikatory, komentarze inline, nazwy klas/funkcji/plików):** po angielsku.
- **ADR-y i ważne dokumenty designu** (`docs/adr/`, `docs/01-vision.md`, PRD, architecture docs, threat model, glossary): po polsku.
- **Commit messages:** dwujęzyczne EN+PL (patrz skill `.claude/skills/commit/SKILL.md`).
- **PR descriptions:** dwujęzyczne EN+PL.
# Styl komunikacji

## Pytaj często
- Przy każdej niejednoznaczności (scope, intencja, wybór biblioteki, nazwa, kierunek) **zatrzymaj się i zapytaj**. Lepiej dwa razy potwierdzić niż źle zgadnąć i odkręcać.
- Dotyczy zarówno decyzji merytorycznych jak i drobnych wyborów które mają konsekwencje (nazwa pakietu, struktura folderów, etc.).
- **Why:** projekt jest uczący i portfoliowy — chcę być świadomy każdej decyzji, nie odkrywać jej po fakcie w diffie.

## Niezgoda — mów wprost
- Gdy widzisz że mój pomysł jest słaby — **powiedz to wprost i uzasadnij** ("to zły kierunek bo X, lepiej Y bo Z"). Nie owijaj w bawełnę, nie sygnalizuj subtelnie.
- Po argumentach decyzja należy do mnie. Ale chcę usłyszeć Twoją wersję, nie grzeczne "OK, zrobię".
- **Why:** uczę się architektury — soft no jest bezużyteczne, mocna kontra z argumentem uczy.

## Proaktywne alternatywy
- Gdy widzisz lepszą opcję niż to o co proszę — **powiedz, nawet jeśli moja prośba była jasna.** Format: "Można też tak: X — pro Y, kontra Z. Co wybierasz?"
- **Why:** nie znam wszystkich wzorców Kotlin/Compose — nie chcę żebyś milcząco realizował moją gorszą wersję, gdy widzisz lepszą.

## Długość odpowiedzi
- **Zwięźle + krótkie uzasadnienie.** Powiedz co zrobiłeś i dlaczego w 2–4 zdaniach. Bez podsumowań na końcu, bez nagłówków sekcji gdy nie trzeba.
- Przy decyzjach merytorycznych — możesz rozwinąć. Przy mechanicznych krokach — jedno zdanie.

# Git workflow

## Kiedy commitować
- Po skończeniu każdego zadania **automatycznie zrób commita i wypushuj** na `origin/main` — bez pytania.
- Jeden commit = jedna spójna zmiana (feature / fix / refactor). Jeśli zadanie obejmuje kilka niezwiązanych rzeczy → zrób kilka osobnych commitów po kolei.
- Push bezpośrednio na `main` jest OK — to jednoosobowy projekt.

## Jak commitować
**Zawsze gdy tworzysz commit w tym projekcie** — proaktywnie po tasku, na żądanie użytkownika, czy przez `/commit` — użyj skilla `.claude/skills/commit/SKILL.md`. Tam są wszystkie szczegóły: format wiadomości, typy, reguły dwujęzyczności (EN + PL), przykłady i workflow.

## Wymaga jawnej zgody (NIGDY automatycznie)
Przed wykonaniem zawsze pytaj:
- `git push --force` / `--force-with-lease`
- `git reset --hard`
- `git branch -D` / usuwanie brancha
- `git rebase -i` na publicznych commitach
- `git commit --amend` na już wypushowanych commitach
- Jakiekolwiek nieodwracalne operacje na historii
