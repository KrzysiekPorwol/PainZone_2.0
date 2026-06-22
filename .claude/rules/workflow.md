# Workflow — sesja nad kodem (Faza 8+)

Jedno zadanie z roadmap = jedna sesja. Pracuj samodzielnie: dostajesz task → robisz całość → meldujesz wynik. Bez bramek zatwierdzania w trakcie.

## Sesja

- 1 sesja = 1 task z `docs/07-roadmap.md` (np. `M1.3`).
- Na start: przeczytaj zadanie z `docs/07-roadmap.md` i powiązane pliki z `docs/`.
- Bez scope creep: napotkany inny problem → notatka w TODO/issue, nie fix w tej sesji.
- Cross-task refactor: wymaga jawnej zgody usera, nigdy oportunistycznie.

## Cykl pracy — dwa kroki

**Krok 1 (Claude, samodzielnie):**
1. Sprawdź założenia w `docs/` — buduj zgodnie z dokumentacją. Gdy potrzebujesz aktualnej dokumentacji biblioteki/wzorca/API → context7 MCP.
2. Implementuj od razu, małymi testowalnymi krokami. **Nie pokazuj planu z góry, nie czekaj na akceptację.** Pracuj cicho.
3. Po każdej zmianie uruchom testy/build. Nigdy nie mów „gotowe" bez dowodu.
4. Commit **lokalnie** przez skill `.claude/skills/commit/SKILL.md` (jeszcze bez push).
5. Finalne podsumowanie: kilka zdań *co wprowadziłem* (+ kluczowe decyzje techniczne) i jedno zdanie „**Twoja kolej — smoke test.**" + lista 1–5 rzeczy do sprawdzenia.

**Krok 2 (user → Claude):**
6. User robi smoke test golden path i potwierdza („OK" / zgłasza poprawki).
7. Po OK: `git push origin main`, oznacz task w `docs/07-roadmap.md`, zaktualizuj `docs/STATUS.md` jeśli task zamyka milestone.

## Decyzje w trakcie

- Decyzję techniczną/code-internal (naming, struktura folderów, edge case, wybór wzorca) **podejmij sam** rozsądnym defaultem i opisz w podsumowaniu — nie przerywaj pytaniem.
- Decyzja techniczna nietrywialna → napisz ADR w `docs/adr/` jako część tasku i zaznacz to w podsumowaniu (do ewentualnej rewizji).
- Stop i pytanie **tylko** gdy to decyzja produktowa/domenowa/UX, której nie da się rozsądnie założyć z `docs/`, glossary i wireframes.

## Standard jakości — MVP

- Unit testy invariantów domain (`docs/05-*.md`) — obowiązkowe (czysta Kotlin, brak Android deps).
- Compose `@Preview` dla każdego stanu ekranu (loading/empty/content/error) — obowiązkowe.
- Smoke test golden path — wykonuje user, nie Claude. Claude buduje APK i zatrzymuje się na podsumowaniu (Krok 1 pkt 5). Push dopiero po potwierdzeniu smoke testu.
- Integration testy (in-memory Room) — odroczone do v1.1 (patrz `docs/08-quality.md`).

## Definition of Done — task

1. Kod kompiluje, lint czysty, testy zielone.
2. Commit lokalny przez skill `.claude/skills/commit/SKILL.md`.
3. Smoke test golden path zaliczony (przez usera).
4. Push na `origin/main` (po smoke OK).
5. Zaktualizuj `docs/07-roadmap.md` — oznacz task jako zrobiony.
6. Zaktualizuj `docs/STATUS.md` jeśli task zamyka milestone.

## Anti-patterns

- Czekanie na akceptację planu zamiast działania — task ma być zrobiony w jednym przebiegu.
- Mówienie „gotowe" bez build/test jako dowodu.
- Push na main przed potwierdzeniem smoke testu.
- Praca nad 2 taskami w jednej sesji.
- Decyzja techniczna nietrywialna bez ADR.
- Pomijanie smoke testu — testy unit ≠ działająca ficza.