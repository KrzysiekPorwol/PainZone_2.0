# Wireframes — CRUD planów (S4, S5, S6, M1, M2, M3)

> TL;DR: Edycja planu i dnia, picker ćwiczeń + 3 modały (picker plan/dzień, parametry, nowe ćwiczenie).

## Spec

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
Actions: StickyCTA"+Dodaj nowe"→M3
States: loaded=lista-pogrupowana; empty→CTA"Dodaj pierwsze"→M3

**M1** Picker plan/dzień [bottom sheet · Flow §4.2]
Fields: PlanList(▾/▸expand-collapse·tap-dzień→zamknij+update-S1-suggestion)

**M2** Parametry ćwiczenia [bottom sheet · PRD 4.2 AC2]
Fields: Serie(stepper·min1·step1) · Timer(stepper·min0:30·step0:30)
Actions: [Anuluj] · [✓]Zapisz→S5

**M3** Nowe ćwiczenie [bottom sheet · PRD 4.1 AC1–2]
Fields: Nazwa(text) · GrupaMięśniowa(dropdown)
Actions: [Anuluj]→S6 · [✓]Zapisz(disabled-gdy-puste)→S6+nowe-na-liście

## Referencje
`docs/04-wireframes.md`
