# PRD — PainZone 2.0

> TL;DR: MoSCoW + Outcome + 7 User Stories (compact AC) dla MVP. Zatwierdzony 2026-05-26.

## Spec

### MoSCoW

| Scope | Items |
|-------|-------|
| **MUST** | Biblioteka ćwiczeń (CRUD) · Plany (CRUD) · Sesja (reps×ciężar×RPE) · Last Set Preview · Rest Timer · Stats Lite · Lokalna persystencja (Room) |
| **SHOULD v1.1** | Pełne statystyki (wykresy) · Konto + cloud backup |
| **COULD v2** | Sync · Wearables (Garmin Fenix 7s) · Apple Health / Google Fit |
| **WON'T** | Social · Gotowe plany · Kalorie · Cardio · Eksport · Light mode · i18n |

### Outcome + assumption tests

**Outcome:** Krzysiek loguje 100% treningów siłowych w PainZone przez ≥3 mies. z rzędu, bez notatek.

| ID | Assumption | Falsyfikacja |
|----|------------|--------------|
| A1 | Stats Lite wystarczy do oceny progresu | Po 3 mies: nie wiem czy idę w górę w ≤10s → wykresy do v1.05 |
| A2 | Logowanie serii ≤3s | Mediana z 10 serii >4s → refactor UX sesji P0 |
| A3 | Last Set Preview wystarczy | ≥3 wyjścia do Stats z sesji w 20 sesjach → preview rozbuduj |

### User Stories (MVP)

**US-1 · Biblioteka ćwiczeń** [4.1]
Story: Zarządzam ~20 własnymi ćwiczeniami.
AC: dodaj z pustej (nazwa+grupa) · zapis disabled bez wymaganych pól · edycja nazwy propaguje wszędzie bez utraty serii · usuń = ostrzeżenie ile planów/sesji używa; soft/hard delete → F5

**US-2 · Plany treningowe** [4.2]
Story: Składam plan = dni × ćwiczenia żeby sesja miała kontekst.
AC: stwórz plan z dniami (kolejność zachowana) · parametry (serie, timer) per ćwiczenie w planie, nie globalnie · reorder ćwiczeń aktywny od następnej sesji · edycja po Completed sesjach nie tyka historii

**US-3 · Sesja treningu** [4.3]
Story: Loguję serię w ≤3s żeby nie tracić flow.
AC: start z planu z pre-fill (serie z planu, ciężar z ostatniej sesji) · log ≤3s + auto-start Rest Timer · edycja świeżej serii nadpisuje · pauza 30min → InProgress zachowany · "Zakończ" → Completed (read-only)

**US-4 · Last Set Preview** [4.4]
Story: Widzę inline poprzedni wynik w 2s.
AC: inline "reps × ciężar / RPE — N dni temu" przy aktywacji ćwiczenia · brak historii = explicit "Brak poprzedniej sesji" · "ostatnia" = chronologicznie, nie best · widoczny stale, bez tap/dropdown

**US-5 · Timer odpoczynku** [4.5]
Story: Auto-timer + zapis czasu żeby za 6 mies. wiedzieć "max z 2 czy 5 min".
AC: auto-start z planu po zapisie serii · `restBeforeSeconds=actual` w kolejnym LoggedSet · po przekroczeniu wibracja+dźwięk, timer dalej liczy · pierwsza seria = `null`, Stats pokazuje "—"

**US-6 · Stats Lite** [4.6]
Story: Otwieram ćwiczenie i w ≤10s wiem czy idę w górę vs 3 mies.
AC: lista LoggedSet 90d default (data, reps×ciężar×RPE, rest) · filtr 30d/90d/rok/wszystko, re-render <200ms · best set (1RM est., formuła → F5) wyróżniony · zero wykresów · usunięte ćwiczenia = marker "usunięte", read-only

**US-7 · Zero-friction onboarding** [4.7]
Story: Instaluję i jestem w UI od razu.
AC: pierwsze uruchomienie wprost do głównego ekranu (zero login/welcome/coach marks) · pełna funkcjonalność offline (airplane mode) · zero pytań o e-mail/hasło/ID · persystencja po restarcie telefonu

## Rationale

OST i mermaid diagram wycięte do historii — outcome jest jednoliniowy w Spec. Pełne AC z given/when/then w git history pre-2026-05-26 jeśli potrzebne. Format compact wymusza dyscyplinę: AC = lista zachowań, nie scenariuszy.

## Referencje

`01-vision.md` · `03-flows.md` · `glossary.md`