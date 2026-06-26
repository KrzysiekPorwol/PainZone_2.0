# Wireframes — Stats, ustawienia, dialogi (S10, S11, D1, D3)

> TL;DR: Stats Lite (lista per sesja + best set top), Ustawienia (about + reset), dialog usunięcia obiektu i dialog resetu danych.

## Spec

**S10** Stats Lite [PRD 4.6 · US-6]
Fields: TopBar(nazwa-ćwiczenia·subtitle:grupa-mięśniowa) · FilterChips(30d·**90d**·Rok·Wszystko·segmented) · BestSetCard(top·"Best: reps × kg · 1RM≈Y kg · N dni temu"·formuła→glossary) · SessionList(grouped-by-data·sekcja-header:"DD.MM · Plan · Dzień"·row:"reps × kg · RPE · po Xs odpocz."·pierwsza-seria-rest="—")
Actions: tap-filtr→re-render(<200ms) · back→S3
States: loaded; empty-filter("Brak serii w tym okresie · zmień filtr"); ćwiczenie-usunięte(banner-top "Ćwiczenie usunięte — read-only")

**S11** Ustawienia [Flow IA · overflow ⋮]
Fields: AboutSection(wersja·autor·link-repo·licencje-OSS) · DataSection([Usuń wszystkie dane](destructive)→D3)
Actions: back→poprzedni-ekran
States: MVP minimal — sztywny **dark theme** Material 3 (bez theme toggle, bez Material You / dynamic color — wizja §7 Non-Goals)

**D1** Usuń obiekt [dialog · S4/S5/S7]
Fields: Title("Usunąć [obiekt]?") · Body(warning · gdy-ćwiczenie-z-historią: "Historia M sesji zostanie zachowana jako read-only")
Actions: [Anuluj] · [Usuń](destructive)→back+snackbar"Usunięto"
States: simple (plan/dzień bez countera); confirm-z-historią (ćwiczenie, M>0); **blocked** (ćwiczenie w ≥1 planie → Title"Nie można usunąć „X"" · Body"używane w planach: … · usuń je z tych planów" · jedyna akcja [Rozumiem], brak destructive)

**D3** Reset danych [dialog · S11]
Fields: Title("Usunąć wszystkie dane?") · Body("Plany, ćwiczenia, sesje — bezpowrotnie. Operacji nie da się cofnąć.")
Actions: [Anuluj] · [Usuń wszystko](destructive)→wipe Room→S1∅
States: idle; wiping (progress · disabled actions)

## Rationale

**BestSetCard top zamiast ★ w liście:** US-6 wymaga odpowiedzi "czy idę w górę" w ≤10s. Card na górze = zero scrollów. Ikona w rzędach byłaby noise w 90 dniach historii.

**Sekcje per data sesji:** Stats czyta się rzadko i kontemplacyjnie (nie szybkie skanowanie jak session log) — czytelność > gęstość. Sesja = naturalna jednostka.

**Osobny D3 zamiast reuse D1:** D1 to "usuń obiekt" z soft-delete fallbackiem (historia zostaje). D3 to hard wipe całej bazy — inny ciężar, inny copy, inna ścieżka odzyskiwania (zero). Reuse byłby false economy.

## Referencje
`docs/04-wireframes.md` · `docs/02-prd.md#US-6` · `docs/03-flows.md#F4` · `docs/glossary.md`