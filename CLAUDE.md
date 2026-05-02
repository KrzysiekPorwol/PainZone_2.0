# Git workflow

## Automatyczne commitowanie i pushowanie
- Po skończeniu każdego zadania commituj zmiany **bez pytania**.
- Po commicie automatycznie pushuj na `origin`.
- Można pushować bezpośrednio na `main` — to jednoosobowy projekt.

## Format wiadomości commitów
- Używaj **konwencjonalnych commitów**: `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`, `style:`, `perf:`, `build:`, `ci:`.
- **Każdy commit musi mieć obie wersje językowe — angielską i polską.**
- Tytuł po angielsku (≤ 70 znaków), polskie tłumaczenie w body.
- Format:
  ```
  feat: add pain intensity slider to main screen

  PL: feat: dodaj suwak intensywności bólu na ekranie głównym

  <opcjonalny dłuższy opis po angielsku>

  <opcjonalny dłuższy opis po polsku>
  ```

## Grupowanie zmian
- Grupuj w **logiczne commity** — jeden commit = jedna spójna zmiana (feature / fix / refactor).
- Nie wrzucaj niezwiązanych zmian do jednego commita.
- Jeśli zadanie obejmuje kilka rzeczy — zrób kilka commitów po kolei.

## Wymaga jawnej zgody (NIGDY automatycznie)
Przed wykonaniem zawsze pytaj:
- `git push --force` / `--force-with-lease`
- `git reset --hard`
- `git branch -D` / usuwanie brancha
- `git rebase -i` na publicznych commitach
- Jakiekolwiek nieodwracalne operacje na historii
