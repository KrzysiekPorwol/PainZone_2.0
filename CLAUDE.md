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
