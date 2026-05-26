# Wireframes — PainZone 2.0

> TL;DR: Compact spec 11 ekranów + 5 modali. Format: Fields / Actions / States.

**Konwencja:** `→` = nawigacja · `·` = separator · `dirty` = niezapisane zmiany

---

## Checklist

| ID | Ekran | Status |
|----|-------|--------|
| S1 | Trenuj | ✅ |
| S2 | Plany | ✅ |
| S3 | Postęp | ✅ |
| S4 | Edycja planu | ✅ |
| S5 | Edycja dnia | ✅ |
| S6 | Picker ćwiczenia | ✅ |
| M1 | Picker plan/dzień | ✅ |
| M2 | Parametry ćwiczenia | ✅ |
| M3 | Nowe ćwiczenie | ✅ |
| S7 | Zarządzaj biblioteką | ⏳ |
| S8 | Edycja ćwiczenia | ⏳ |
| S9 | Sesja treningowa | ⏳ |
| D2 | Zakończ sesję | ⏳ |
| S10 | Stats Lite | ⏳ |
| S11 | Ustawienia | ⏳ |
| D1 | Dialog usunięcia | ⏳ |

---

## 2. Top-level — S1, S2, S3

**S1** Trenuj [PRD 4.3]
Fields: SmartCard(plan·dzień·"Zacznij"·"zmień"→M1) · PlanList(collapse/expand·tap-dzień→S9) · Banner(InProgress·global→S9)
Actions: [⋮]→S7/S11
States: loaded; empty→CTA"Stwórz plan"→S4; banner-gdy-InProgress

**S2** Plany [PRD 4.2]
Fields: PlanList(card:"N dni·ostatnio Xd"·tap→S4) · StickyСTA"+Nowy plan"→S4
Actions: [⋮]→S7/S11
States: loaded; empty→CTA→S4

**S3** Postęp [PRD 4.6]
Fields: ExerciseList(card:"Grupa·ostatnio Xd"·tap→S10)
Actions: [⋮]→S7/S11
States: loaded; empty→"Brak historii — zakończ pierwszą sesję"

---

## 3. CRUD planów — S4, S5, S6, M1, M2, M3

**S4** Edycja planu [PRD 4.2]
Fields: Nazwa(text·autofocus·TopBar-title) · Dni(list·⠿drag·tap→S5·[✕]delete)
Actions: [✓]→S2 · [🗑]→D1 · +Dzień→S5new · back-dirty→"Odrzucić zmiany?"
States: nowy=brak[🗑]·empty-list; istniejący=z-dniami

**S5** Edycja dnia [PRD 4.2]
Fields: Nazwa(text·autofocus) · Ćwiczenia(list·⠿drag·tap→M2·[✕]delete·subtitle:"N serie·T:00 odpocz.")
Actions: [✓]→S4 · [🗑]→D1 · +Ćwiczenie→S6 · back-dirty→"Odrzucić zmiany?"
States: nowy=brak[🗑]·empty-list; istniejący=z-ćwiczeniami

**S6** Picker ćwiczenia [PRD 4.1]
Fields: SearchBar(realtime-filter) · ExerciseList(grouped-by-muscle·tap→dodaj+back→S5)
Actions: StickyСTA"+Dodaj nowe"→M3
States: loaded=lista-pogrupowana; empty→CTA"Dodaj pierwsze"→M3

**M1** Picker plan/dzień [bottom sheet · Flow §4.2]
Fields: PlanList(▾/▸expand-collapse·tap-dzień→zamknij+update-S1-suggestion)

**M2** Parametry ćwiczenia [bottom sheet · PRD 4.2 AC2]
Fields: Serie(stepper·min1·step1) · Timer(stepper·min0:30·step0:30)
Actions: [Anuluj] · [✓]Zapisz→S5

**M3** Nowe ćwiczenie [bottom sheet · PRD 4.1 AC1–2]
Fields: Nazwa(text) · GrupaMięśniowa(dropdown)
Actions: [Anuluj]→S6 · [✓]Zapisz(disabled-gdy-puste)→S6+nowe-na-liście

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
🚧 Po ukończeniu §2–6: 2–3 warianty (labels, kształt, tło). Decyzja z Flow §1 O1.

---

## Referencje

`docs/03-flows.md` · `docs/02-prd.md` · `docs/glossary.md`
