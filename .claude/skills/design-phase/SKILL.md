---
name: design-phase
description: Prowadzi fazy designu PainZone 2.0 (5-7 wg `docs/00-process.md`) z dyscypliną tokenów. Aktywuje się gdy piszemy/edytujemy dowolny `docs/0X-*.md`, dyskutujemy domain model, architekturę, ADR, threat model, roadmap, jakość — lub user pyta "jak prowadzimy tę fazę".
---

> Cel: docs są **code-ready** (Claude może zacząć Fazę 8 bez dopytywania) i **token-cheap** (Spec ≤60 linii czytane domyślnie, Rationale on-demand).

## Hard limity

- **Spec ≤ 60 linii** per plik. Powyżej = stop, tnij zanim dalej.
- **Plik łącznie ≤ 100 linii** (Spec + Rationale + TL;DR + nagłówki).
- Powyżej 100 → rozbij na moduły (np. `05-domain-exercise.md`, `05-domain-session.md`) albo wytnij.
- Każde zdanie zarabia (wartość dla code-context / portfolio / nauki). 0/3 → wytnij.

## Struktura każdego doc fazy

```markdown
# <Tytuł>
> TL;DR: [1 zdanie]

## Spec
[≤60 linii — sama prawda: fakty, decyzje, kontrakty]

## Rationale
[opcjonalna — dlaczego, alternatywy, learning. Claude czyta tylko on-demand]
```

Marker `## Rationale` jest **wymagany** jeśli plik ma jakikolwiek "dlaczego" — żeby Claude wiedział od której linii ciąć przy domyślnym czytaniu.

## Reading rules dla Claude

- **Default:** `Read` z `limit=60` → łapiesz tylko Spec, nie spalasz tokenów.
- **Pełny plik** czytasz tylko gdy: user pyta "dlaczego X?", podejmujesz decyzję ADR-grade, robisz cross-review faz.
- **Nigdy nie ładuj 3+ docs jednocześnie** — czytaj sekwencyjnie, po Spec na raz.

## Phase output contracts

Co MUSI być w Spec żeby Faza 8 (kod) ruszyła bez dopytywania:

- **F5 `05-domain.md`** → encje · atrybuty (typowane) · invarianty · relacje · stany. ≤60 linii Spec. Jeśli >5 encji → rozbij per encja.
- **F6 `06-architecture.md`** → stack (Compose · Room · Hilt · etc.) · package layout · linki do ADR per decyzja. ≤60 linii. Diagramy → tylko w Rationale.
- **F6 `docs/adr/NNNN-*.md`** → kontekst (≤3 linie) · decyzja (≤3 linie) · konsekwencje (≤3 linie). Format MADR ale tnij.
- **F6 `threat-model.md`** → 1 strona. STRIDE table lub bullety, bez prozy.
- **F6.5 `walking-skeleton.md`** → 1 ekran end-to-end który działa. Lista plików + co robią.
- **F7 `07-roadmap.md`** → milestones jako tabela. ≤30 linii.
- **F7 `08-quality.md`** → DoD checklist + scenariusze jakości jako bullety.

## Anti-patterns (NIE rób)

- ASCII art ekranów (mamy compact spec w CLAUDE.md).
- "Alternatives considered" inline w Spec → idą do Rationale albo do ADR.
- Długie why-paragraphs w Spec → 1 linia max, reszta do Rationale.
- Duplikacja między docs → linkuj do źródła (`patrz: 05-domain.md#Exercise`).
- Pisanie Rationale "na zapas" gdy decyzja oczywista → zostaw puste.
- Otwieranie nowej fazy bez DoD poprzedniej.

## Workflow per faza

1. **Przeczytaj `docs/00-process.md`** (1x na start sesji fazy) — sprawdź cel fazy i deliverable.
2. **Przeczytaj Spec poprzedniej fazy** (`limit=60`) — kontekst wejściowy.
3. **Draft Spec inline w rozmowie** — bullety, nie proza. User akceptuje → piszesz do pliku.
4. **Rationale tylko jeśli decyzja non-obvious** — pytaj usera: "Dodać Rationale dla X? (Y/N)".
5. **Po zapisie:** update `docs/STATUS.md` + `docs/00-process.md` status table. Bez pytania.
6. **Commit** przez skill `commit`.

## Edge cases

- **Plik istniejący przekracza limit** → flag userowi przed dalszą pracą, zaproponuj refaktor.
- **User chce ASCII/diagram** → propose: "Diagram w Rationale + compact spec w Spec. OK?"
- **Decyzja techniczna w docs fazy** → przenieś do ADR, w Spec link.