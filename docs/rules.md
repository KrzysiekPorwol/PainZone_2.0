# Rules — sposób pracy nad kodem (Faza 8+)

> TL;DR: Jedno zadanie z roadmap = jedna sesja Claude. Claude głośno myśli po polsku zanim pisze kod.

## Spec

### Sesja
- **1 sesja = 1 task z `07-roadmap.md`** (np. `M1.3`). Po skończeniu: commit + push + `/clear`.
- **Na start sesji:** Claude czyta zadanie ze `07-roadmap.md` + relevantne docs (`limit=60`). Nic więcej.
- **Bez scope creep:** napotkany inny problem → notatka w TODO/issue, nie fix w tej sesji.
- **Cross-task refactor:** wymaga jawnej zgody usera, nigdy oportunistycznie.

## Zasady pracy

   **Sprawdzenie zalożeń w dokumentacji /docs** - budujemy rozwiązania zgodnie z zalożeniami w dokumentacji znajdującej się w folderze docs.
   **Najpierw dokumentacja** — przed rozpoczęciem pracy użyj /context7-mvp, żeby sprawdzić aktualną dokumentację używanego rozwiązania.
   **Aktualizowanie pliku 07-roadmap.md** - po skonczonym zadaniu w roadmap.md aktualizuj co juz jest zrobione a co jest do zrobienia.
   **Plan** — przed jakąkolwiek zmianą w kodzie napisz plan. NIE implementuj dopóki plan nie zostanie zatwierdzony.
   **Weryfikacja planu** — przedstaw plan, czekaj na uwagi, uwzględnij je. Powtarzaj aż do zatwierdzenia.
   **Implementacja** — dopiero po zatwierdzeniu planu. Wprowadzaj zmiany małymi, testowalnymi krokami.
   **Weryfikacja** — po każdej zmianie uruchom testy/build. Nigdy nie mów „gotowe" bez dowodu że działa.
   **Podsumowanie** — po każdym kroku krótko opisz co się zmieniło i dlaczego.

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
- Manual smoke test golden path przed commitem (skill `verify`) — **obowiązkowe**. **Smoke test wykonuje Krzysiek**, nie Claude. Claude buduje, instaluje APK, a następnie zatrzymuje się ze zdaniem „**Twoja kolej — smoke test.**" i listą konkretnych rzeczy do sprawdzenia (1–5 punktów). Czeka na potwierdzenie („ok"/„działa") lub feedback przed commitem.
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
