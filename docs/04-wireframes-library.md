# Wireframes — Biblioteka (S7, S8)

> TL;DR: Globalny CRUD ćwiczeń + edycja pojedynczego ćwiczenia z licznikiem użycia.

## Spec

**S7** Zarządzaj biblioteką [PRD 4.1]
Fields: SearchBar(realtime-filter) · ExerciseList(sort:A-Z·subtitle:grupa-mięśniowa·tap→S8·trailing[✕]→D1)
Actions: StickyCTA"+Nowe ćwiczenie"→S8new · back→poprzedni-ekran
States: loaded; empty→CTA"Dodaj pierwsze ćwiczenie"→S8new

**S8** Edycja ćwiczenia [PRD 4.1]
Fields: Nazwa(text·autofocus·TopBar-title) · GrupaMięśniowa(dropdown·wartości→F5) · UsageInfo("Używane w N planach · M sesjach"·tylko-istniejący)
Actions: [✓]Zapisz(disabled-gdy-puste)→S7 · [🗑]→D1(z-counterem) · back-dirty→"Odrzucić zmiany?"
States: nowy=brak[🗑]·brak-UsageInfo; istniejący=z-[🗑]+UsageInfo

## Referencje
`docs/04-wireframes.md` · `docs/02-prd.md#US-1` · [[project_phase5_muscle_group_invariant]]