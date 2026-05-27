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

**S3** Postęp [PRD 4.6]
Fields: ExerciseList(card:"Grupa·ostatnio Xd"·tap→S10)
Actions: [⋮]→S7/S11
States: loaded; empty→"Brak historii — zakończ pierwszą sesję"

## Referencje
`docs/04-wireframes.md`
