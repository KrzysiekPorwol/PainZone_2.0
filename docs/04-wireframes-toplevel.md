# Wireframes — Top-level (S1, S2, S3)

> TL;DR: Trzy ekrany pod bottom barem — Trenuj / Plany / Postęp.

## Spec

**S1** Trenuj [PRD 4.3]
Fields: SmartCard(plan=`isActive`·dzień=next-po-MAX(startedAt)-modulo·"Zacznij"·"zmień"→M1·brak-aktywnego→ukryty) · PlanList(collapse/expand·tap-dzień→S9) · Banner(InProgress·global→S9)
Actions: [⋮]→S7/S11
States: loaded; empty→CTA"Stwórz plan"→S4; banner-gdy-InProgress; brak-aktywnego→tylko PlanList

**S2** Plany [PRD 4.2]
Fields: PlanList(card:"N dni·ostatnio Xd"·⭐-badge-gdy-aktywny·tap→S4) · StickyCTA"+Nowy plan"→S4
Actions: [⋮]→S7/S11
States: loaded; empty→CTA→S4

**S3** Postęp [PRD 4.6] — hub wyboru trybu
Fields: HubChoices(3 karty: "Po ćwiczeniu"→ExerciseList→S10 · "Po planie"→S12 · "Chronologicznie"→S13)
Actions: [⋮]→S7/S11
States: loaded; empty(brak zakończonych sesji)→"Brak historii — zakończ pierwszą sesję" (wszystkie 3 tryby puste)
Uwaga: ExerciseList ("Po ćwiczeniu", card:"Grupa") = lista ćwiczeń z M4.3 → S10; tu staje się podekranem huba.

## Referencje
`docs/04-wireframes.md`
