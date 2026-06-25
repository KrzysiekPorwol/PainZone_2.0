# User Flows + IA — PainZone 2.0

> TL;DR: 3 zakładki (Trenuj/Plany/Postęp), 14 ekranów, 3 modale (M1-M3) + 3 dialogi (D1-D3), 4 top flowy. Zatwierdzony 2026-05-26 (historia po planie: 2026-06-25).

## Spec

### IA decisions

| Element | Decyzja |
|---------|---------|
| Nav | Bottom navigation bar (3 tabs: **Trenuj · Plany · Postęp**) |
| Aktywna sesja | Persistent banner top gdy `WorkoutSession.state == InProgress` [PRD US-3] |
| Biblioteka ćwiczeń | Schowana — picker w Plany + overflow "Zarządzaj biblioteką" |
| Settings | Overflow ⋮ (MVP puste) |
| FAB | Brak (Trenuj = zakładka) |
| Zakładka Trenuj | Hybryd: smart suggestion top + lista planów/dni poniżej |
| Styl bottom bara | Odroczone do F4 (decyzja stylistyczna, [[project_phase4_bottom_bar_aesthetic_checkpoint]]) |

### Inwentarz ekranów

| ID | Ekran | Purpose | Entry → Exit | PRD |
|----|-------|---------|--------------|-----|
| S1 | Trenuj | Smart suggestion + lista | Tab → S9 / S4 (empty CTA) | US-3 |
| S2 | Plany | Lista planów + CRUD | Tab → S4 | US-2 |
| S3 | Postęp | Hub: 3 tryby historii | Tab → S10/S12/S13 | US-6 |
| S4 | Edycja planu | CRUD planu + dni | S2 → S5 · back S2 | US-2 |
| S5 | Edycja dnia | CRUD dnia + ćwiczenia | S4 → S6/M2 · back S4 | US-2 |
| S6 | Picker ćwiczenia | Wybór z biblioteki + inline add | S5 → S5 / M3 | US-1 |
| S7 | Zarządzaj biblioteką | CRUD ćwiczeń globalne | Overflow → S8 | US-1 |
| S8 | Edycja ćwiczenia | Nazwa + grupa mięśniowa | S7 / M3 → S7 | US-1 |
| S9 | Sesja treningowa | Log serii + Last Set + Rest Timer | S1 / banner → D2 → S1 | US-3, US-4, US-5 |
| S10 | Stats Lite | Lista + filtr + best set | S3 → back | US-6 |
| S11 | Ustawienia | About + reset | Overflow → back | — |
| S12 | Wybór planu | Picker planu do historii | S3 → S13 | US-6 |
| S13 | Historia sesji | Lista sesji + filtr planu | S3/S12 → S14 | US-6 |
| S14 | Szczegóły sesji | Read-only snapshot sesji | S13 → back | US-6 |

### Modale / dialogi

M1 Picker plan/dzień (S1) · M2 Parametry ćwiczenia w planie (S5) · M3 Nowe ćwiczenie (S6) · D1 Usuń obiekt (S4/S5/S7) · D2 Zakończ sesję (S9) · D3 Reset danych (S11)

### Empty states

S1 brak planów → CTA "Stwórz pierwszy plan" → S4 · S2 pusto → ten sam CTA · S3 pusto → "Brak historii — zakończ pierwszą sesję" · S6 pusta biblioteka → "[+ dodaj pierwsze ćwiczenie]" → M3 · S7 pusto → CTA → S8

### Top flowy (compact)

**F1 · Pierwsze uruchomienie:** Install → S1∅ → CTA → S4 (nazwa) → +dzień → S5 → +ćwiczenie → S6∅ → +nowe → M3 (nazwa+grupa) → S5 → M2 (parametry) → S5 → back → S4 → back → S2 → tap Trenuj → S1 gotowy.

**F2 · Zacznij trening:** Otwarcie → [InProgress? → banner → S9 resume] / [S1 → historia? → smart card (akceptuj → start) / lista (M1 picker → start)] → S9 → loop {Last Set Preview → input reps/ciężar/RPE → save ≤3s → Rest Timer auto → ostatnia seria? → następne ćwiczenie} → Zakończ → D2 → Completed → S1.

**F3 · Stwórz/edytuj plan:** S2 → +nowy/tap → S4 (nazwa · ⭐Aktywuj plan · dni: + / reorder / usuń / tap → S5) → S5 (ćwiczenie: + → S6 / tap → M2 / reorder / usuń) → S6 (z biblioteki → S5 / +nowe → M3 → S5) → back chain S5→S4→S2.

**F4 · Zobacz progres/historię:** S3 hub → [brak sesji: empty → S1/S2] / wybór trybu:
- *Po ćwiczeniu* → lista ćwiczeń → S10 (filtr 90d default + best set highlighted / 30d/90d/rok/wszystko / usunięte = read-only marker) → back.
- *Po planie* → S12 (wybór planu) → S13 (sesje tego planu, filtr planu przełączalny) → tap → S14 (read-only snapshot) → back.
- *Chronologicznie* → S13 (wszystkie sesje) → tap → S14 → back.

## Rationale

Mermaid diagramy (nav map + 4 flowy) wycięte do git history pre-2026-05-26. Compact strzałkowy format = wszystko czytelne w jednym scanie, zero renderowania, zero ASCII art per CLAUDE.md. Jeśli flow F2 zrobi się sporny przy implementacji — wtedy diagram do PR-a, nie do docs.

## Referencje

`02-prd.md` · `04-wireframes.md` · `glossary.md`