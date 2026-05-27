# Rules — sposób pracy nad kodem (Faza 8+)

> TL;DR: Jedno zadanie z roadmap = jedna sesja Claude. Claude głośno myśli po polsku zanim pisze kod.

## Spec

### Sesja
- **1 sesja = 1 task z `07-roadmap.md`** (np. `M1.3`). Po skończeniu: commit + push + `/clear`.
- **Na start sesji:** Claude czyta zadanie ze `07-roadmap.md` + relevantne docs (`limit=60`). Nic więcej.
- **Bez scope creep:** napotkany inny problem → notatka w TODO/issue, nie fix w tej sesji.
- **Cross-task refactor:** wymaga jawnej zgody usera, nigdy oportunistycznie.

### Tryb planowania (plan mode) — obowiązkowy przed pisaniem kodu
- **Zawsze przed Edit/Write:** Claude wchodzi w plan mode (`ExitPlanMode` tool z draftem planu), pokazuje plan, czeka na akceptację. Dopiero po `approve` ruszają zmiany w plikach.
- **Co w planie:** lista plików do utworzenia/zmiany · krótka logika każdego pliku · zależności · jakie testy · co manualnie zweryfikuję.
- **Skip plan mode wolno tylko** dla trywialnych pojedynczych edycji (typo, 1-linijkowy fix) — i nawet wtedy 1 zdanie zapowiedzi.
- Źródło: [Claude Code docs — plan mode](https://docs.claude.com/en/docs/claude-code/common-workflows#use-plan-mode).

### Komunikacja podczas pisania kodu
- **Thinking-out-loud:** zanim utworzysz/edytujesz plik, w 2–4 zdaniach po polsku wyjaśnij:
  *co* piszesz · *jak* to działa · *dlaczego* tak (vs. alternatywa, jeśli istotna).
- **Wyjaśniaj na przykładzie:** abstrakcyjny opis („repository pattern abstrahuje źródło danych") → dorzuć konkret („tu: `ExerciseRepository.getAll()` zwraca `Flow<List<Exercise>>` — VM nie wie czy to Room czy mock w teście"). Mini-snippet > paragraf prozy.
- **Proaktywne alternatywy:** widzisz inny sposób → „Można też X — pro Y, kontra Z. Co wybierasz?".
- **Pytaj zamiast zakładać:** naming, struktura folderów, edge case → stop i pytanie.
- **Po pliku:** 1 zdanie statusu („Co dalej w tej sesji" / „Task zamknięty").

### Standard jakości — MVP
- Unit testy invariantów domain (`05-*.md`) — **obowiązkowe** (czysta Kotlin, brak Android deps).
- Compose `@Preview` dla każdego stanu ekranu (loading/empty/content/error) — **obowiązkowe**.
- Manual smoke test golden path przed commitem (skill `verify`) — **obowiązkowe**.
- Integration testy (in-memory Room) — **odroczone do v1.1** (patrz `08-quality.md`).
- Komentarze w kodzie tylko gdy non-obvious *why* (zgodnie z CLAUDE.md).

### Definition of Done — task
1. Kod kompiluje, lint czysty, testy zielone.
2. Manual smoke test golden path zaliczony.
3. Commit przez skill `commit` (EN+PL).
4. Push na `origin/main`.
5. `docs/STATUS.md` zaktualizowany jeśli task zamyka milestone.

### Anti-patterns
- Edit/Write nietrywialnych zmian bez wcześniejszego plan mode.
- Praca nad 2 taskami w jednej sesji „bo szybciej".
- Pisanie kodu bez wyjaśnienia *dlaczego* (silent edits) — łamie cel uczący.
- Wyjaśnianie tylko abstrakcyjne, bez konkretnego przykładu z kontekstu projektu.
- Decyzja techniczna nie-trywialna w trakcie sesji bez ADR (zatrzymaj się, ADR, potem kod).
- Pomijanie smoke testu — testy unit ≠ działająca ficza.

## Referencje

`CLAUDE.md` (styl ogólny, git, język) · `07-roadmap.md` (lista zadań) · `08-quality.md` (NFR + DoD ficzy)
