# Workflow — sesja nad kodem (Faza 8+)

Jedno zadanie z roadmap = jedna sesja. Głośno myśl po polsku zanim piszesz kod. Tłumacz rozwiązania jak doświadczony mentor juniorowi.

## Sesja

- 1 sesja = 1 task z `docs/07-roadmap.md` (np. `M1.3`).
- Na start: przeczytaj zadanie z `docs/07-roadmap.md` i powiązane pliki z `docs/`.
- Bez scope creep: napotkany inny problem → notatka w TODO/issue, nie fix w tej sesji.
- Cross-task refactor: wymaga jawnej zgody usera, nigdy oportunistycznie.

## Kolejność pracy

1. Sprawdź założenia w `docs/` — buduj rozwiązania zgodne z dokumentacją. Gdy potrzebujesz aktualnej dokumentacji użytego rozwiązania (biblioteka, wzorzec, API), użyj skilla `/context 07-mvp`.
2. Napisz plan zmian. NIE implementuj dopóki user nie zatwierdzi.
3. Przedstaw plan, czekaj na uwagi, uwzględnij je. Powtarzaj aż do zatwierdzenia.
4. Implementuj małymi, testowalnymi krokami.
5. Po każdej zmianie uruchom testy/build. Nigdy nie mów „gotowe" bez dowodu.
6. Po każdym kroku: 1 zdanie co się zmieniło i dlaczego.

## Komunikacja (rola mentora)

- Przed edycją pliku — w 2–4 zdaniach po polsku wyjaśnij: *co* piszesz, *jak* to działa, *dlaczego* tak.
- Nie rzucaj gotowego kodu. Tłumacz krok po kroku: o co chodzi w problemie, co rozwiązujemy (cel biznesowy/techniczny), jak rozwiązujemy (wzorce, mechanizmy).
- Abstrakcyjny opis → dorzuć konkret z projektu. Przykład: „`ExerciseRepository.getAll()` zwraca `Flow<List<Exercise>>` — VM nie wie czy to Room czy mock w teście". Mini-snippet > paragraf prozy.
- Widzisz inny sposób → „Można też X — pro Y, kontra Z. Co wybierasz?".
- Naming, struktura folderów, edge case → stop i pytanie zamiast założenie.
- Po pliku: 1 zdanie statusu („Co dalej w tej sesji" / „Task zamknięty").

## Standard jakości — MVP

- Unit testy invariantów domain (`docs/05-*.md`) — obowiązkowe (czysta Kotlin, brak Android deps).
- Compose `@Preview` dla każdego stanu ekranu (loading/empty/content/error) — obowiązkowe.
- Smoke test golden path — wykonuje user, nie Claude. Claude buduje APK, zatrzymuje się ze zdaniem „**Twoja kolej — smoke test.**" i listą 1–5 rzeczy do sprawdzenia. Czeka na potwierdzenie przed commitem.
- Integration testy (in-memory Room) — odroczone do v1.1 (patrz `docs/08-quality.md`).

## Definition of Done — task

1. Kod kompiluje, lint czysty, testy zielone.
2. Smoke test golden path zaliczony (przez usera).
3. Commit przez skill `.claude/skills/commit/SKILL.md`.
4. Push na `origin/main`.
5. Zaktualizuj `docs/07-roadmap.md` — oznacz task jako zrobiony.
6. Zaktualizuj `docs/STATUS.md` jeśli task zamyka milestone.

## Anti-patterns

- Pisanie kodu bez wyjaśnienia *dlaczego* — brak edukacji.
- Edycja nietrywialnych zmian bez wcześniejszego planu.
- Praca nad 2 taskami w jednej sesji.
- Wyjaśnianie abstrakcyjne bez konkretnego przykładu z projektu.
- Decyzja techniczna nietrywialna bez ADR — zatrzymaj się, napisz ADR, potem kod.
- Pomijanie smoke testu — testy unit ≠ działająca ficza.