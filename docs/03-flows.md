# User Flows + Information Architecture — PainZone 2.0

> TL;DR: 3 top-level tabs (Trenuj/Plany/Postęp) + 11 ekranów + 5 modali — tabele, navigation map, flowy 4 scenariuszy.
>
> Zatwierdzony: 2026-05-26.

---

## 1. Decyzja IA

| Element | Decyzja | Uzasadnienie |
|---------|---------|--------------|
| **Wzorzec nav** | Bottom navigation bar | Branżowy default (Hevy, Strong), thumb-friendly |
| **Liczba zakładek** | **3: Trenuj / Plany / Postęp** | Bez social/preset/profile nie ma czym zapełnić więcej |
| **Aktywna sesja** | Persistent banner u góry gdy `WorkoutSession.state == InProgress` | PRD 4.3 AC4 |
| **Biblioteka ćwiczeń** | Schowana — picker w Plany + overflow "Zarządzaj biblioteką" | Rzadka aktywność, ~20 pozycji |
| **Settings** | Overflow ⋮ | MVP Settings praktycznie puste |
| **FAB** | Brak | "Trenuj" = zakładka, 1-tap z każdego miejsca |
| **Zakładka Trenuj** | Hybryd: smart sugestia u góry + pełna lista poniżej | USP "min. kliknięć" + manual pick |
| **Wizualny styl bottom bara** | Odroczone do Fazy 4 | Decyzja stylistyczna |

```
┌──────────────────────────────────┐
│ ⏺ Aktywna: Push 3/12  →         │  ← persistent banner (tylko gdy InProgress)
├──────────────────────────────────┤
│   PainZone               [⋮]     │  ← TopAppBar + overflow
├──────────────────────────────────┤
│   (content per zakładka)         │
├──────────────────────────────────┤
│  [Trenuj]  [Plany]  [Postęp]    │  ← bottom navigation bar (3 tabs)
└──────────────────────────────────┘
```

---

## 2. Inwentarz ekranów

### Top-level

| ID | Ekran | Purpose | Mapowanie |
|----|-------|---------|-----------|
| **S1** | Trenuj | Smart suggestion + lista planów/dni | PRD 4.3 |
| **S2** | Plany | Lista planów + CRUD | PRD 4.2 |
| **S3** | Postęp | Lista ćwiczeń z historią | PRD 4.6 |

### Sub-screens

| ID | Ekran | Purpose | Entry | Exit | Mapowanie |
|----|-------|---------|-------|------|-----------|
| **S4** | Edycja planu | CRUD planu + dni | S2 | tap dnia → S5 · back → S2 | PRD 4.2 |
| **S5** | Edycja dnia | CRUD dnia + ćwiczenia | S4 | + ćwiczenie → S6 · back → S4 | PRD 4.2 |
| **S6** | Picker ćwiczenia | Wybór z biblioteki + inline dodanie | S5 | wybór → S5 · + nowe → M3 → S5 | PRD 4.1 |
| **S7** | Zarządzaj biblioteką | CRUD ćwiczeń (globalne) | Overflow ⋮ | tap → S8 · back | PRD 4.1 |
| **S8** | Edycja ćwiczenia | Nazwa + grupa mięśniowa | S7 / M3 | zapisz → S7 | PRD 4.1 |
| **S9** | Sesja treningowa | Logowanie serii + Last Set Preview + Rest Timer | S1 / banner | "Zakończ" → D2 → S1 | PRD 4.3–4.5 |
| **S10** | Stats Lite | Lista serii + filtr + best set | S3 | back → S3 | PRD 4.6 |
| **S11** | Ustawienia | O aplikacji + reset | Overflow ⋮ | back | — |

### Modale / dialogi

| ID | Element | Trigger |
|----|---------|---------|
| **M1** | Picker plan/dzień | S1 "zmień plan/dzień" |
| **M2** | Parametry ćwiczenia w planie | S5 → tap ćwiczenia |
| **M3** | Nowe ćwiczenie | S6 "+ dodaj nowe" |
| **D1** | Dialog usunięcia | S4 / S7 "Usuń" |
| **D2** | Dialog "Zakończ sesję" | S9 "Zakończ sesję" |

### Empty states

- **S1** brak planów → CTA "Stwórz pierwszy plan" → S4
- **S2** pusto → CTA "Stwórz pierwszy plan" → S4
- **S3** pusto → "Brak historii — zakończ pierwszą sesję"
- **S6** pusta biblioteka → "Biblioteka pusta — [+ dodaj pierwsze ćwiczenie]"
- **S7** pusto → CTA "Dodaj pierwsze ćwiczenie" → S8

---

## 3. Navigation map

```mermaid
graph TD
    subgraph BBar["Bottom Navigation Bar"]
        S1[S1 Trenuj]
        S2[S2 Plany]
        S3[S3 Postęp]
    end

    subgraph Overflow["Overflow Menu ⋮"]
        S7[S7 Zarządzaj biblioteką]
        S11[S11 Ustawienia]
    end

    S1 -->|Start sesji| S9[S9 Sesja treningowa]
    S9 -.banner.-> BBar

    S2 --> S4[S4 Edycja planu]
    S4 --> S5[S5 Edycja dnia]
    S5 --> S6[S6 Picker ćwiczenia]
    S6 -.+ nowe.-> S8[S8 Edycja ćwiczenia]
    S6 -->|wybór| S5

    S3 --> S10[S10 Stats Lite]

    S7 --> S8

    BBar -.overflow.-> Overflow
```

---

## 4. Flow diagrams

### 4.1 Pierwsze uruchomienie

```mermaid
flowchart TD
    Start((Instalacja)) --> S1Empty[S1 EMPTY]
    S1Empty -->|CTA Stwórz plan| S4New[S4 nowy]
    S4New --> A1([Nazwa planu]) --> A2([+ dzień])
    A2 --> S5New[S5 nowy dzień] --> A4([+ ćwiczenie])
    A4 --> S6Empty[S6 PUSTA BIBLIOTEKA]
    S6Empty -->|+ dodaj pierwsze| M3[M3 nowe ćwiczenie]
    M3 --> A5([Nazwa + grupa]) --> S5WithEx[S5 z ćwiczeniem]
    S5WithEx --> M2[M2 parametry] --> S5WithEx
    S5WithEx -.kolejne.-> A4
    S5WithEx -->|Back| S4WithDay[S4 z dniem] -->|Back| S2[S2 Plany]
    S2 -->|Tap Trenuj| S1Ready[S1 gotowy] --> End((Gotowy))

    classDef errorState stroke:#f00,stroke-width:2px
    class S1Empty,S6Empty errorState
```

### 4.2 Zacznij trening

```mermaid
flowchart TD
    Start((Otwarcie)) --> D1{Sesja InProgress?}
    D1 -->|TAK| Banner[Banner widoczny] -->|tap| S9Resume[S9 resume]
    D1 -->|NIE| S1[S1 Trenuj]
    S1 --> D2{Historia?}
    D2 -->|TAK| SmartCard[Smart suggestion] --> D3{Akceptuje?}
    D2 -->|NIE| ManualList[Lista planów]
    D3 -->|TAK| Start2([Tap Zacznij])
    D3 -->|NIE| M1([M1 picker]) --> ManualList
    ManualList --> Start2
    S9Resume --> S9[S9 Sesja InProgress]
    Start2 --> S9
    S9 --> Loop{Pętla serii}
    Loop --> Preview[Last Set Preview inline]
    Preview --> Input([reps + ciężar + RPE])
    Input --> Save[(LoggedSet saved ≤ 3s)]
    Save --> Timer[Rest Timer auto]
    Timer --> D4{Ostatnia seria?}
    D4 -->|NIE| Loop
    D4 -->|TAK| D5{Ostatnie ćwiczenie?}
    D5 -->|NIE| Next([Następne]) --> Loop
    D5 -->|TAK| End([Zakończ]) --> D2b[D2 Dialog]
    D2b -->|Potwierdź| Done[(Completed)] --> S1Done[S1]

    classDef criticalNode fill:#fff4e6,stroke:#f59e0b,stroke-width:2px
    class Save,Preview,Timer criticalNode
```

### 4.3 Stwórz / edytuj plan

```mermaid
flowchart TD
    Start((Edycja planu)) --> S2[S2 Plany]
    S2 --> D1{Nowy?}
    D1 -->|+ nowy| S4N[S4 nowy]
    D1 -->|Tap planu| S4E[S4 istniejący]
    S4N --> Name([Nazwa]) --> S4[S4]
    S4E --> D2{Co?}
    D2 -->|Nazwa| Name
    D2 -->|Dni| Days{Akcja}
    D2 -->|Usuń| Del[D1 → S2]
    S4 --> Days
    Days -->|+ / Tap| S5[S5 Edycja dnia]
    Days -->|Reorder / Usuń| S4
    S5 --> D3{Ćwiczenie?}
    D3 -->|+| S6[S6 Picker]
    D3 -->|Tap| M2[M2 parametry] --> S5
    D3 -->|Reorder / Usuń| S5
    S6 --> D4{Z biblioteki?}
    D4 -->|TAK| S5
    D4 -->|+ nowe| M3[M3] --> S5
    S5 -->|Back| S4 -->|Back| S2
```

### 4.4 Zobacz progres

```mermaid
flowchart TD
    Start((Progres)) --> S3[S3 Postęp]
    S3 --> D1{Historia?}
    D1 -->|NIE| Empty[Empty state] --> End((S1/S2))
    D1 -->|TAK| List[Lista ćwiczeń]
    List --> Tap([Tap ćwiczenia]) --> S10[S10 Stats Lite]
    S10 --> Render[Lista + filtr 90d + best set]
    Render --> D2{Akcja?}
    D2 -->|Filtr| Filter([30d/90d/rok/wszystko]) --> Render
    D2 -->|Back| S3
    D2 -->|Usunięte| RO[Read-only] --> D2

    classDef errorState stroke:#f00,stroke-width:2px
    class Empty,RO errorState
```

---

## Referencje

`docs/02-prd.md` · `docs/04-wireframes.md` · `docs/glossary.md`