# Wireframes — Sesja (S9, D2)

> TL;DR: Ekran sesji treningowej z log serii ≤3s + Last Set Preview + Rest Timer, plus dialog zakończenia.

## Spec

**S9** Sesja treningowa [PRD 4.3, 4.4, 4.5]
Fields: TopBar(plan·dzień·"Ćw N/M"tap→sheet-jump-ćwiczeń-sesji) · ExerciseTitle("Nazwa · Seria K/L") · LastSetPreview("reps × kg / RPE — N dni temu" · brak→"Brak poprzedniej sesji") · InputRow(reps[stepper]·kg[stepper·step0.5·prefill-ostatnia-sesja]·RPE[chips:Łatwa/Normalna/Ciężka]·[✓]Zapisz) · LoggedList(odwr.chrono·tap-świeża→edycja-inline) · RestTimer(banner-bottom·mm:ss-countup·wibracja+dźwięk-po-Tplan)
Actions: [✓]Zapisz→log+auto-RestTimer+focus-reps · [⋮]→D2 · ostatnia-seria-ćwiczenia→auto-advance+CTA"Następne ćwiczenie →" · ostatnia-seria-ostatnie-ćwiczenie→CTA"Zakończ sesję"→D2
States: in-progress default; rest-active(timer+wibracja-po-Tplan); editing-fresh(tap-świeża→nadpisanie); paused(30min idle→banner "Wznów sesję"); empty-exercise(brak-serii→tylko-InputRow)

**D2** Zakończ sesję [dialog · PRD US-3]
Fields: Summary("N serii · czas Y · tonaż Z kg" · "Niezakończone ćwiczenia: K" gdy K>0)
Actions: [Anuluj]→S9 · [✓]Zakończ→Completed(read-only)→S1

## Rationale

**Auto-advance zamiast prev/next chips:** Flow F2 explicit "loop {…→następne ćwiczenie}" — sesja ma jednoznaczny kierunek. Stałe chips byłyby decyzyjnym noise w 95% serii. Cofnięcie/przeskok przez sheet w TopBar pokrywa edge-case bez kosztu tapów w głównej pętli.

## Referencje
`docs/04-wireframes.md` · `docs/02-prd.md#US-3` · `docs/03-flows.md#F2` · `docs/glossary.md#RPE`