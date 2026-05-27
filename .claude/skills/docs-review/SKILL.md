---
name: docs-review
description: Kompleksowy review dokumentacji projektowej PainZone 2.0 w `docs/` — wykrywa sprzeczności między plikami, weryfikuje założenia techniczne z aktualną dokumentacją bibliotek (przez context7-mcp) i generuje raport `docs/REVIEW.md`. Aktywuje się gdy user prosi o "review docs", "sprawdź dokumentację", "przejrzyj docs", "audit docs", "czy docs są spójne", "zrób review dokumentacji".
---

> Cel: znaleźć sprzeczności, nieaktualne API i braki w `docs/` zanim trafią do kodu. Raport jest po polsku, ale nazwy klas/API pozostają w angielskim.

## Zakres

- Skanuje **`docs/`** (rzeczywisty folder projektu — nie `.docs/`).
- **Tylko czyta** istniejące pliki. Jedyny zapis: `docs/REVIEW.md`.
- Działa dla dowolnej liczby i nazewnictwa plików `.md` w `docs/` (nie hardcoduj nazw).
- Jeśli `docs/` jest pusty lub nie istnieje → poinformuj usera jednym zdaniem i zakończ bez tworzenia raportu.

## Zależność: context7-mcp

Krok 3 (weryfikacja API z dokumentacją bibliotek) **wymaga** skilla `context7-mcp`.

- Jeśli dostępny → weryfikuj każde nietrywialne założenie techniczne (Compose, Room, Hilt, Navigation, Coroutines/Flow, DataStore, WorkManager, Paging, etc.).
- Jeśli niedostępny → pomiń krok 3, ale **w raporcie zaznacz**: "⚠️ Weryfikacja z dokumentacją bibliotek pominięta — context7-mcp niedostępny."

**Nie polegaj na własnej wiedzy o API.** Dokumentacje się zmieniają, API są deprecjonowane. Zawsze fetch przez context7-mcp.

## Workflow

### Krok 1 — Index

1. `ls docs/*.md` żeby zebrać listę plików.
2. Przeczytaj każdy plik w całości (review wymaga pełnego kontekstu — token-discipline tu nie obowiązuje).
3. Zbuduj mentalny indeks:
   - Numer + tytuł + TL;DR każdego pliku
   - Encje domenowe (nazwa, pola, typy, invarianty, relacje)
   - Ekrany (nazwa, ID, elementy UI, akcje)
   - Flow (kroki, decyzje, error states)
   - Stack techniczny + biblioteki + wersje
   - Reguły biznesowe i scope MVP vs later
4. Sprawdź `docs/STATUS.md` i `docs/00-process.md` — w której fazie jesteśmy (niektóre braki mogą być "by design" jeśli faza jeszcze nieotwarta).

### Krok 2 — Cross-reference check (NAJWAŻNIEJSZE)

Dla każdej pary istotnie powiązanych dokumentów sprawdź:

- **Encje:** czy pola/typy/invarianty są spójne między PRD, domain modelem, architekturą i schematem bazy?
- **Ekrany:** czy każdy element UI z wireframes ma pokrycie w PRD/flow/domain? Czy odwrotnie — funkcje z PRD mają ekrany?
- **Flow:** czy ścieżki użytkownika z PRD są reprezentowane w wireframes? Brak "martwych końców" w nawigacji?
- **Nazewnictwo:** ta sama rzecz = jedna nazwa we wszystkich plikach? (np. "Session" vs "TrainingSession" vs "Workout")
- **Scope:** to samo "MVP" / "later" / "out of scope" we wszystkich miejscach?
- **Linki:** wewnętrzne odnośniki (`patrz: 05-domain.md#Exercise`) wskazują na istniejące sekcje?

Cytuj sprzeczności konkretnie: `[plik:linia] mówi X, [plik:linia] mówi Y`.

### Krok 3 — Weryfikacja techniczna przez context7-mcp

Wyciągnij z `docs/` listę założeń technicznych: każdą wzmiankę o konkretnej bibliotece, API, klasie, adnotacji, wzorcu.

Dla każdego założenia:

1. `resolve-library-id` na nazwę biblioteki (np. "androidx.compose.navigation", "androidx.room", "hilt").
2. `query-docs` z konkretnym pytaniem ("czy Room obsługuje X przez Y?", "jak działa Navigation Compose typeSafe args?").
3. Porównaj odpowiedź z założeniem w docs.

Sprawdź też **przestarzałe podejścia** (lista w `references/android-standards.md` — przeczytaj **przed** rozpoczęciem kroku 3).

### Krok 4 — Kompletność

- Każdy ekran z wireframes → ma logikę w PRD?
- Każda encja z PRD → ma model w architekturze/domain?
- Error states i edge cases zdefiniowane dla głównych flow?
- Nawigacja kompletna (każdy ekran osiągalny, każda akcja prowadzi gdzieś)?
- Empty states / loading states zaadresowane?

### Krok 5 — Raport

Zapisz do `docs/REVIEW.md` (nadpisz jeśli istnieje). Struktura:

```markdown
# Docs Review — YYYY-MM-DD

## Podsumowanie
[1–3 zdania o stanie dokumentacji]

## Krytyczne sprzeczności
- **`plikA.md:NN` vs `plikB.md:MM`**: [opis] → sugestia: [jak naprawić]

## Niezgodności z dokumentacją bibliotek
[Lub: "⚠️ Pominięte — context7-mcp niedostępny."]
- **`plik.md:NN`**: założenie "X" → aktualna dokumentacja [biblioteka v.X]: "Y" → sugestia: [poprawka]

## Przestarzałe podejścia
- **`plik.md:NN`**: [co jest nie tak] → [co powinno być wg aktualnych rekomendacji]

## Braki w dokumentacji
- [Element, którego brakuje + który dokument powinien go zawierać]

## Drobne uwagi
- Niespójności nazewnictwa, literówki w API/klasach, martwe linki.

## Ocena ogólna
**Skala 1–10:** N/10
[Krótki komentarz: co działa, co wymaga uwagi w pierwszej kolejności]
```

## Zasady raportowania

- **Konkretnie z cytatami:** `plik:linia` zawsze gdy możliwe.
- **Sugestie, nie wyroki:** "rozważ X" / "można Y", a nie "źle, popraw na Z".
- **Nazwy klas/metod/API po angielsku** (zgodnie z konwencją projektu) — reszta po polsku.
- **Priorytetyzuj:** krytyczne sprzeczności i niezgodności z bibliotekami przed drobnymi uwagami.
- **Nie wymyślaj problemów** żeby zapełnić sekcje — pusta sekcja jest OK ("Brak uwag.").

## Anti-patterns (NIE rób)

- Modyfikacja istniejących plików w `docs/` — tylko `REVIEW.md` zapisujesz.
- Hardcodowanie nazw plików ("sprawdź 04-wireframes.md") — skanuj dynamicznie.
- Generic feedback bez cytatów ("dokumentacja mogłaby być bardziej szczegółowa") — wytnij.
- Pomijanie kroku 3 cicho — zawsze zaznacz w raporcie jeśli context7-mcp niedostępny.
- Powtarzanie tego samego problemu w kilku sekcjach — wybierz najbardziej pasującą.
- Zaufanie własnej wiedzy o Compose/Room/Hilt API — zawsze fetch przez context7-mcp.

## Edge cases

- **`docs/` pusty** → "Folder `docs/` jest pusty. Nic do review." Stop.
- **`docs/REVIEW.md` już istnieje** → nadpisz bez pytania (raport jest regenerowalny).
- **Faza projektu wczesna** (np. brak architektury, jesteśmy w F3) → nie raportuj braków z późniejszych faz jako "Braki w dokumentacji"; zaznacz w Podsumowaniu "Projekt w Fazie N, sekcje X/Y/Z nie są oczekiwane na tym etapie."
- **Plik przekracza 100 linii** (limit z `design-phase`) → flag w "Drobne uwagi".
