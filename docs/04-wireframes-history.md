# Wireframes — Historia (S12, S13, S14)

> TL;DR: Hub Postęp (S3) rozgałęzia się na historię po planie i chronologiczną; lista sesji z filtrem planu + read-only podgląd sesji.

## Spec

**S12** Wybór planu [tryb "Po planie" · z S3]
Fields: PlanList(plany które mają ≥1 zakończoną sesję · nazwa bieżąca · tap→S13 z filtrem=ten plan)
Actions: back→S3
States: loaded; empty→"Żaden plan nie ma jeszcze sesji"

**S13** Historia sesji [PRD US-6/US-7 · z S3 lub S12]
Fields: PlanFilter(dropdown: Wszystkie · Plan A · … · przełączalny) · SessionList(card:"DD.MM · Plan · Dzień · N serii · tonaż Z kg" · sort najnowsza→najstarsza · tap→S14)
Actions: zmień-filtr→re-render · back→S3
States: loaded; filtr-start = Wszystkie (wejście "Chronologicznie") lub preselekcja planu (wejście "Po planie" z S12); empty-filter→"Brak sesji dla tego planu"

**S14** Szczegóły sesji [read-only · z S13]
Fields: Header(data · plan · dzień · czas trwania · tonaż) · ExerciseList(per ćwiczenie snapshot: nazwa + serie "reps × kg · RPE · po Xs odpocz." · 1. seria rest="—") · marker"usunięte"(gdy ćwiczenie soft-deleted w momencie podglądu)
Actions: back→S13
States: loaded (snapshot z momentu sesji — przeżywa rename/usunięcie planu/ćwiczenia)

## Rationale

**Plan = nazwa-snapshot, nie ID:** sesja trzyma `plan_name_snapshot` (nie FK do planu — plan może zniknąć). Filtr S13 i grupowanie S12 działają po nazwie planu z momentu sesji, więc historia jest stabilna nawet po usunięciu/zmianie planu. Konsekwencja: dwa plany o tej samej nazwie w różnym czasie zlewają się w filtrze — akceptowalne dla solo usera.

**S12 osobny picker zamiast filtra od razu:** decyzja UX — "najpierw wybieramy plan z listy" daje czysty mental model (wybór trybu → wybór planu → sesje), kosztem jednego tapnięcia. Filtr na S13 pozwala potem przełączać bez cofania.

**S14 read-only reuse layoutu S9:** ten sam render serii co sesja w toku (`04-wireframes-session.md`), bez inputów. Snapshot, nie live query — zero ryzyka, że edycja planu zmieni historię.

## Referencje
`docs/04-wireframes-toplevel.md#S3` · `docs/04-wireframes-session.md` · `docs/04-wireframes-misc.md#S10` · `docs/02-prd.md#US-6`
