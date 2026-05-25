# Aktualny stan projektu

> Sekcja żywa — Claude utrzymuje ją aktualną. Edytowana przy każdej zmianie stanu (faza → faza, milestone → milestone). Krzysiek nie musi pamiętać o aktualizacji.

- **Etap:** Faza 0 — budujemy `CLAUDE.md` (kontrakt współpracy)
- **Następny krok:** w nowej sesji dopracowujemy `CLAUDE.md` — sekcje: (A) Kontekst projektu, (B) Sposób pracy, (C) Kiedy Claude działa vs pyta, (D) Twarde non-goals. Sekcję po sekcji, sokratejsko.
- **Proces designu:** `docs/00-process.md` — czytaj **gdy** dyskutujemy fazę designu, status fazy się zmienia, lub user pyta "gdzie jesteśmy"
- **Glossary domenowy:** `docs/glossary.md` — czytaj **gdy** dyskutujemy termin domenowy lub piszemy artefakt z domeną
- **Aktywny artefakt fazy:** `CLAUDE.md` (rewizja Vision odblokowana po zamknięciu Fazy 0)

**Odpowiedzialność Claude:** Aktualizuj tę sekcję gdy stan się zmienia. Aktualizuj `docs/00-process.md` status table równolegle. Nie pytaj usera o pozwolenie na te aktualizacje — to mechanika, nie decyzja merytoryczna.

---

# Konwencja językowa

## Artefakty projektu
- **Rozmowa Claude ↔ user:** po polsku.
- **Kod (identyfikatory, komentarze inline, nazwy klas/funkcji/plików):** po angielsku.
- **ADR-y i ważne dokumenty designu** (`docs/adr/`, `docs/01-vision.md`, PRD, architecture docs, threat model, glossary):
  **dwujęzyczne — EN + PL obok siebie**, w jednym pliku. Format: sekcja EN, potem `---`, potem sekcja PL (analogicznie jak commit messages).
  - **Why:** projekt jest portfolio dla rynku polskiego *i* potencjalnie międzynarodowego. EN daje czytelność dla rekrutera za granicą, PL ułatwia autorowi szybkie przeglądanie i jest naturalnym językiem myślenia.
- **README projektu:** EN (główny), opcjonalnie `README.pl.md` z linkiem.
- **Commit messages:** dwujęzyczne EN+PL (patrz skill `.claude/skills/commit/SKILL.md`).
- **PR descriptions:** dwujęzyczne EN+PL.

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
