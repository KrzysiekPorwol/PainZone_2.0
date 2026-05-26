# Wireframes Lo-Fi — PainZone 2.0

> TL;DR: Lo-fi frames 11 ekranów + 5 modali z Fazy 3. ASCII (tu) → Figma (po zatwierdzeniu).
>
> Status: 🟡 w toku (start: 2026-05-26). Link Figma: _🚧 po migracji._

**Konwencja:** `╭─╮ │ ╰─╯` = Card · `▶` = primary button · `[ ]` = secondary · `①②` = interakcje pod frame'em · `🚧` = placeholder.

---

## Mapowanie (checklist)

| ID | Ekran | Status |
|----|-------|--------|
| S1 | Trenuj | ✅ §2.1 |
| S2 | Plany | ✅ §2.2 |
| S3 | Postęp | ✅ §2.3 |
| S4 | Edycja planu | ✅ §3.1 |
| S5 | Edycja dnia | ✅ §3.2 |
| S6 | Picker ćwiczenia | ✅ §3.3 |
| M1 | Picker plan/dzień | ✅ §3.4 |
| M2 | Parametry ćwiczenia | ✅ §3.5 |
| M3 | Nowe ćwiczenie | ✅ §3.6 |
| S7 | Zarządzaj biblioteką | ⏳ §4.1 |
| S8 | Edycja ćwiczenia | ⏳ §4.2 |
| S9 | Sesja treningowa | ⏳ §5.1 |
| D2 | Zakończ sesję | ⏳ §5.2 |
| S10 | Stats Lite | ⏳ §6.1 |
| S11 | Ustawienia | ⏳ §6.2 |
| D1 | Dialog usunięcia | ⏳ §6.3 |

---

## 2. Top-level — S1, S2, S3

### 2.1 S1 — Trenuj → [PRD 4.3 · Flow §4.2]

**[loaded]**
```
┌──────────────────────────────────────┐
│ Trenuj                          [⋮]  │
├──────────────────────────────────────┤
│ ╭──────────────────────────────────╮ │
│ │ 🔁 Kontynuuj Push/Pull/Nogi      │ │
│ │ Następny: Pull · Ostatnio 3d temu│ │
│ │ ② zmień plan/dzień               │ │
│ │              ① ▶ Zacznij Pull    │ │
│ ╰──────────────────────────────────╯ │
│  Albo wybierz ręcznie:               │
│  ▾ Push/Pull/Nogi          ③         │
│      · Push · Pull · Nogi            │
│  ▾ Upper/Lower 4-day                 │
│      · Upper A · Lower A · ...       │
├──────────────────────────────────────┤
│  [● Trenuj] [ Plany ] [ Postęp ]     │
└──────────────────────────────────────┘
```
→ ① ▶ Zacznij Pull → S9 · ② zmień plan/dzień → M1 · ③ tap nazwę = expand/collapse · tap dzień → S9 · [⋮] → S7/S11

**[empty]**
```
┌──────────────────────────────────────┐
│ Trenuj                          [⋮]  │
├──────────────────────────────────────┤
│         ╳ Brak planów                │
│   Stwórz pierwszy plan żeby          │
│   zacząć trening.                    │
│        ① ▶ Stwórz pierwszy plan      │
├──────────────────────────────────────┤
│  [● Trenuj] [ Plany ] [ Postęp ]     │
└──────────────────────────────────────┘
```
→ ① → S4 (nowy plan)

**[banner — aktywna sesja]**
```
┌──────────────────────────────────────┐
│ ⏺ Aktywna: Push 3/12 serii      → ① │
├──────────────────────────────────────┤
│ (zawartość jak [loaded])             │
└──────────────────────────────────────┘
```
→ ① → S9 resume · Banner globalny na wszystkich ekranach gdy InProgress.

### 2.2 S2 — Plany → [PRD 4.2 · Flow §4.3]

**[loaded]** _(lista scrolluje; CTA sticky nad bottom barem)_
```
┌──────────────────────────────────────┐
│ Plany                           [⋮]  │
├──────────────────────────────────────┤
│ ╭──────────────────────────────────╮ │  ↑
│ │ Push/Pull/Nogi                ①  │ │  │
│ │ 3 dni · ostatnio 3d temu         │ │  │ scroll
│ ╰──────────────────────────────────╯ │  │
│ ╭──────────────────────────────────╮ │  │
│ │ Upper/Lower 4-day             ①  │ │  │
│ │ 4 dni · ostatnio 12d temu        │ │  ↓
├──────────────────────────────────────┤
│ ╭─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─╮ │  ← sticky
│ │          ② + Nowy plan           │ │
│ ╰─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─╯ │
├──────────────────────────────────────┤
│  [ Trenuj ] [● Plany ] [ Postęp ]    │
└──────────────────────────────────────┘
```
→ ① tap karty → S4 · ② sticky CTA → S4 nowy · [⋮] → S7/S11

**[empty]**
```
┌──────────────────────────────────────┐
│ Plany                           [⋮]  │
├──────────────────────────────────────┤
│                                      │
│          ╳ Brak planów               │
│    Stwórz pierwszy plan żeby         │
│    śledzić swoje treningi.           │
│        ① ▶ Stwórz pierwszy plan      │
│                                      │
├──────────────────────────────────────┤
│  [ Trenuj ] [● Plany ] [ Postęp ]    │
└──────────────────────────────────────┘
```
→ ① → S4 nowy

### 2.3 S3 — Postęp → [PRD 4.6 · Flow §4.4]

**[loaded]**
```
┌──────────────────────────────────────┐
│ Postęp                          [⋮]  │
├──────────────────────────────────────┤
│ ╭──────────────────────────────────╮ │
│ │ Wyciskanie sztangi            ①  │ │
│ │ Klatka · ostatnio 2d temu        │ │
│ ╰──────────────────────────────────╯ │
│ ╭──────────────────────────────────╮ │
│ │ Martwy ciąg                   ①  │ │
│ │ Plecy · ostatnio 5d temu         │ │
│ ╰──────────────────────────────────╯ │
│ ╭──────────────────────────────────╮ │
│ │ Przysiad                      ①  │ │
│ │ Nogi · ostatnio 5d temu          │ │
│ ╰──────────────────────────────────╯ │
│ ╭──────────────────────────────────╮ │
│ │ Wiosłowanie sztangą           ①  │ │
│ │ Plecy · ostatnio 12d temu        │ │
│ ╰──────────────────────────────────╯ │
├──────────────────────────────────────┤
│  [ Trenuj ] [ Plany ] [● Postęp ]    │
└──────────────────────────────────────┘
```
→ ① tap → S10

**[empty]**
```
┌──────────────────────────────────────┐
│ Postęp                          [⋮]  │
├──────────────────────────────────────┤
│                                      │
│          ╳ Brak historii             │
│    Zakończ pierwszą sesję żeby       │
│    zobaczyć swój postęp.             │
│                                      │
├──────────────────────────────────────┤
│  [ Trenuj ] [ Plany ] [● Postęp ]    │
└──────────────────────────────────────┘
```

---

## 3. CRUD planów — S4, S5, S6, M1, M2, M3

### 3.1 S4 — Edycja planu → [PRD 4.2 · Flow §4.3]

**[nowy]**
```
┌──────────────────────────────────────┐
│ ←  Nowy plan                    [✓]  │
├──────────────────────────────────────┤
│  Nazwa                               │
│  ╭──────────────────────────────────╮│
│  │ Push/Pull/Nogi          ← focus  ││
│  ╰──────────────────────────────────╯│
│                                      │
│  Dni                                 │
│  ╳ Brak dni                          │
│                                      │
│         ① + Dodaj dzień              │
└──────────────────────────────────────┘
```
→ ① → S5 nowy dzień

**[z dniami]**
```
┌──────────────────────────────────────┐
│ ← Push/Pull/Nogi          [🗑]  [✓]  │
├──────────────────────────────────────┤
│  Nazwa                               │
│  ╭──────────────────────────────────╮│
│  │ Push/Pull/Nogi                   ││
│  ╰──────────────────────────────────╯│
│                                      │
│  Dni                                 │
│  ╭──────────────────────────────────╮│
│  │ ⠿  Push                    ① ②  ││
│  ╰──────────────────────────────────╯│
│  ╭──────────────────────────────────╮│
│  │ ⠿  Pull                    ① ②  ││
│  ╰──────────────────────────────────╯│
│  ╭──────────────────────────────────╮│
│  │ ⠿  Nogi                    ① ②  ││
│  ╰──────────────────────────────────╯│
│         ③ + Dodaj dzień              │
└──────────────────────────────────────┘
```
→ ① tap → S5 · ② [✕] usuń dzień · ③ → S5 nowy · ⠿ drag reorder · [🗑] → D1 usuń plan · [✓] Zapisz → S2 · back bez zapisu → dialog "Odrzucić zmiany?"

---

### 3.2 S5 — Edycja dnia → [PRD 4.2 · Flow §4.3]

**[nowy dzień]**
```
┌──────────────────────────────────────┐
│ ← Nowy dzień                    [✓]  │
├──────────────────────────────────────┤
│  Nazwa dnia                          │
│  ╭──────────────────────────────────╮│
│  │ Push                    ← focus  ││
│  ╰──────────────────────────────────╯│
│                                      │
│  Ćwiczenia                           │
│  ╳ Brak ćwiczeń                      │
│                                      │
│       ① + Dodaj ćwiczenie            │
└──────────────────────────────────────┘
```
→ ① → S6

**[z ćwiczeniami]**
```
┌──────────────────────────────────────┐
│ ← Push                    [🗑]  [✓]  │
├──────────────────────────────────────┤
│  Nazwa dnia                          │
│  ╭──────────────────────────────────╮│
│  │ Push                             ││
│  ╰──────────────────────────────────╯│
│                                      │
│  Ćwiczenia                           │
│  ╭──────────────────────────────────╮│
│  │ ⠿  Wyciskanie sztangi      ① ②  ││
│  │    4 serie · 3:00 odpocz.        ││
│  ╰──────────────────────────────────╯│
│  ╭──────────────────────────────────╮│
│  │ ⠿  Rozpiętki               ① ②  ││
│  │    3 serie · 2:00 odpocz.        ││
│  ╰──────────────────────────────────╯│
│  ╭──────────────────────────────────╮│
│  │ ⠿  Tricep                  ① ②  ││
│  │    3 serie · 2:00 odpocz.        ││
│  ╰──────────────────────────────────╯│
│       ③ + Dodaj ćwiczenie            │
└──────────────────────────────────────┘
```
→ ① tap → M2 parametry · ② [✕] usuń · ③ → S6 · ⠿ drag reorder · [🗑] → D1 usuń dzień · [✓] Zapisz → S4 · back bez zapisu → dialog "Odrzucić zmiany?"

---

### 3.3 S6 — Picker ćwiczenia → [PRD 4.1 · Flow §4.3]

**[loaded]**
```
┌──────────────────────────────────────┐
│ ← Wybierz ćwiczenie                  │
├──────────────────────────────────────┤
│  🔍 Szukaj ćwiczenia...              │
├──────────────────────────────────────┤
│  Klatka                              │
│  ╭──────────────────────────────────╮│
│  │ Wyciskanie sztangi             ① ││
│  ╰──────────────────────────────────╯│
│  ╭──────────────────────────────────╮│
│  │ Rozpiętki                      ① ││
│  ╰──────────────────────────────────╯│
│  Plecy                               │
│  ╭──────────────────────────────────╮│
│  │ Martwy ciąg                    ① ││
│  ╰──────────────────────────────────╯│
├──────────────────────────────────────┤
│      ② + Dodaj nowe ćwiczenie        │
└──────────────────────────────────────┘
```
→ ① tap → dodaje do dnia + wraca S5 · ② → M3 · sticky CTA nad dolną krawędzią · szukaj filtruje realtime

**[empty]**
```
┌──────────────────────────────────────┐
│ ← Wybierz ćwiczenie                  │
├──────────────────────────────────────┤
│  🔍 Szukaj ćwiczenia...              │
├──────────────────────────────────────┤
│                                      │
│        ╳ Biblioteka pusta            │
│   Dodaj pierwsze ćwiczenie           │
│   żeby zacząć planować.              │
│     ① ▶ Dodaj pierwsze ćwiczenie     │
│                                      │
└──────────────────────────────────────┘
```
→ ① → M3

---

### 3.4 M1 — Picker plan/dzień _(bottom sheet)_ → [Flow §4.2]

```
╭──────────────────────────────────────╮
│          Wybierz plan i dzień        │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─   │
│  ▾ Push/Pull/Nogi               ①   │
│      · Push                    ②    │
│      · Pull                    ②    │
│      · Nogi                    ②    │
│  ▸ Upper/Lower 4-day            ①   │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─   │
│             [ Anuluj ]               │
╰──────────────────────────────────────╯
```
→ ① tap planu = expand/collapse · ② tap dnia → zamknij + zaktualizuj sugestię S1

---

### 3.5 M2 — Parametry ćwiczenia _(bottom sheet)_ → [PRD 4.2 AC2]

```
╭──────────────────────────────────────╮
│  Wyciskanie sztangi                  │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─   │
│  Serie                               │
│  ╭──────────────────────────────────╮│
│  │   [−]          4          [+]    ││
│  ╰──────────────────────────────────╯│
│  Timer odpoczynku                    │
│  ╭──────────────────────────────────╮│
│  │   [−]        3:00         [+]    ││
│  ╰──────────────────────────────────╯│
│                                      │
│    [ Anuluj ]        ① ▶ Zapisz     │
╰──────────────────────────────────────╯
```
→ ① Zapisz → S5 · [−]/[+] stepper · kroki: serie co 1 (min 1), timer co 0:30 (min 0:30)

---

### 3.6 M3 — Nowe ćwiczenie _(bottom sheet)_ → [PRD 4.1 AC1–AC2]

```
╭──────────────────────────────────────╮
│  Nowe ćwiczenie                      │
│  ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─   │
│  Nazwa                               │
│  ╭──────────────────────────────────╮│
│  │ Wyciskanie sztangi...            ││
│  ╰──────────────────────────────────╯│
│  Grupa mięśniowa                     │
│  ╭──────────────────────────────────╮│
│  │ Klatka                        ▾  ││
│  ╰──────────────────────────────────╯│
│                                      │
│   [ Anuluj ]    ① ▶ Zapisz ⬦        │
│                 ⬦ disabled gdy puste │
╰──────────────────────────────────────╯
```
→ ① Zapisz → S6 (nowe ćwiczenie na liście, user tapuje żeby dodać do dnia) · [Anuluj] → S6 · AC2: Zapisz disabled dopóki oba pola niepuste

---

## 4. Biblioteka — S7, S8
🚧

---

## 5. Sesja — S9, D2
🚧

---

## 6. Postęp, ustawienia, dialogi — S10, S11, D1
🚧

---

## 7. Bottom bar — checkpoint stylowania
🚧 Po S1–S3: 2-3 warianty (labels, kształt, tło). Decyzja z Fazy 3 §1 O1.

---

## Referencje

`docs/03-flows.md` · `docs/02-prd.md` · : 🚧