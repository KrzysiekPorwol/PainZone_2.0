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
| S2 | Plany | ⏳ §2.2 |
| S3 | Postęp | ⏳ §2.3 |
| S4 | Edycja planu | ⏳ §3.1 |
| S5 | Edycja dnia | ⏳ §3.2 |
| S6 | Picker ćwiczenia | ⏳ §3.3 |
| M1 | Picker plan/dzień | ⏳ §3.4 |
| M2 | Parametry ćwiczenia | ⏳ §3.5 |
| M3 | Nowe ćwiczenie | ⏳ §3.6 |
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

### 2.2 S2 — Plany
🚧

### 2.3 S3 — Postęp
🚧

---

## 3. CRUD planów — S4, S5, S6, M1, M2, M3
🚧

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

`docs/03-flows.md` · `docs/02-prd.md` · Figma: 🚧