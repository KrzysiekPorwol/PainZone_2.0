# Wireframes — PainZone 2.0

> TL;DR: Master index — checklist 14 ekranów + 6 modali, linki do modułów per sekcja.

**Konwencja:** `→` = nawigacja · `·` = separator · `dirty` = niezapisane zmiany

## Checklist

| ID | Ekran | Status | Moduł |
|----|-------|--------|-------|
| S1 | Trenuj | ✅ | [toplevel](04-wireframes-toplevel.md) |
| S2 | Plany | ✅ | [toplevel](04-wireframes-toplevel.md) |
| S3 | Postęp (hub) | ✅ | [toplevel](04-wireframes-toplevel.md) |
| S4 | Edycja planu | ✅ | [crud](04-wireframes-crud.md) |
| S5 | Edycja dnia | ✅ | [crud](04-wireframes-crud.md) |
| S6 | Picker ćwiczenia | ✅ | [crud](04-wireframes-crud.md) |
| M1 | Picker plan/dzień | ✅ | [crud](04-wireframes-crud.md) |
| M2 | Parametry ćwiczenia | ✅ | [crud](04-wireframes-crud.md) |
| M3 | Nowe ćwiczenie | ✅ | [crud](04-wireframes-crud.md) |
| S7 | Zarządzaj biblioteką | ✅ | [library](04-wireframes-library.md) |
| S8 | Edycja ćwiczenia | ✅ | [library](04-wireframes-library.md) |
| S9 | Sesja treningowa | ✅ | [session](04-wireframes-session.md) |
| D2 | Zakończ sesję | ✅ | [session](04-wireframes-session.md) |
| S10 | Stats Lite | ✅ | [misc](04-wireframes-misc.md) |
| S11 | Ustawienia | ✅ | [misc](04-wireframes-misc.md) |
| D1 | Dialog usunięcia | ✅ | [misc](04-wireframes-misc.md) |
| D3 | Reset danych | ✅ | [misc](04-wireframes-misc.md) |
| S12 | Wybór planu | ✅ | [history](04-wireframes-history.md) |
| S13 | Historia sesji | ✅ | [history](04-wireframes-history.md) |
| S14 | Szczegóły sesji | ✅ | [history](04-wireframes-history.md) |

## Bottom bar — spec stylowania

> Decyzja: **Wariant B+ (YouTube-style)** — ikony Material Symbols + label pod, active = Filled + `primary` color. Brak pill/dot/underline. Zamyka Flow §1 O1.

| Element | Spec |
|---|---|
| Height | 64dp |
| Ikony | Material Symbols 24dp · inactive=Outlined `onSurfaceVariant` (60%) · active=Filled `primary` (100%) |
| Label | 12sp · inactive=Regular `onSurfaceVariant` · active=Medium `primary` |
| Tab → ikona | Trenuj=`fitness_center` · Plany=`list_alt` · Postęp=`trending_up` |
| Background | `surface` solid · brak elevation · divider-top 1dp `outlineVariant` |
| Touch target | 64dp × 1/3 width per tab |
| Accessibility | `contentDescription` = nazwa zakładki · "wybrane, X, 1 z 3" w TalkBack |

**Rationale:** 16dp mniej chrome niż M3 default (80dp→64dp) = więcej miejsca dla treści sesji. Labele zostają dla first-time discoverability i a11y. Filled-vs-Outlined wystarcza jako active signal — pill byłby double signal. `list_alt` bo Plan = lista dni, nie kalendarz dat.

## Referencje

`docs/03-flows.md` · `docs/02-prd.md` · `docs/glossary.md`