# Vision — PainZone 2.0

> Dokument Fazy 1 procesu designu. Zatwierdzony: 2026-05-02. Aktualizuje się wraz z kolejnymi fazami (PRD, Domain, Architektura).

## 1. Pitch

> **PainZone 2.0** to apka mobilna która pozwala patrzeć i rejestrować wynik siłowy na treningu — żeby zajmowało to mało czasu, było wygodne i funkcjonalne.

## 2. Problem

Apki do trackowania treningów które dziś dominują (Hevy, Strong, Jefit i podobne) mają trzy istotne wady z perspektywy doświadczonego użytkownika siłowni:

1. **Są przeładowane.** Trzeba przebrnąć przez setki opcji żeby zacząć trenować.
2. **Wymagają logowania i onboardingu.** User musi „coś robić" zanim dotknie głównej fichy.
3. **Narzucają gotowe biblioteki ćwiczeń.** Nie pasują do realnego workflow osoby która ma swój sprawdzony zestaw ćwiczeń.

## 3. Persona

**Doświadczony amator siłowni, 18–30 lat.**

Trenuje samodzielnie 3–4 razy w tygodniu, zwykle realizuje wybrany plan przez ~3 miesiące zanim go wymieni. Nie korzysta z trenera — sam wie co chce robić, zna swoje ćwiczenia. Ceni swój czas: w trakcie treningu chce **szybko zarejestrować wynik bez ceremonii**, bez logowania, bez kombinowania, bez przebijania się przez biblioteki ćwiczeń których i tak nie używa.

## 4. Use Cases — 3 momenty użycia

| Moment | Co user robi | Priorytet UX |
|--------|-------------|--------------|
| **W trakcie treningu** (najważniejsze) | Rejestruje aktualne wyniki (powtórzenia, ciężar) i widzi „ile było ostatnio" w tej samej serii | **Maksymalna prostota i szybkość** — zero zbędnych kliknięć |
| **Przy planowaniu** | Tworzy/edytuje plan, dobiera ćwiczenia z własnej biblioteki, ustala liczbę serii i timer odpoczynku | Wygoda, czytelność |
| **Przy ocenie progresu** | Otwiera ekran statystyk — ogląda progres per grupa mięśniowa w skali miesięcy/lat | Czytelność wykresów, kontekst |

User jest na siłowni **codziennie lub prawie codziennie przez miesiące** — apka musi pozostać szybka i miła nawet po tysiącu uruchomień.

## 5. USP — czym się odróżniamy

PainZone 2.0 stawia na **ekstremalną prostotę i indywidualizm**:

- 🚫 **Pusta biblioteka na start** — user dodaje wyłącznie te ćwiczenia, których realnie używa. Nie zaśmiecamy wyboru.
- 🔓 **Zero logowania, zero onboardingu** — instaluje, otwiera, używa. Single-user, lokalnie na telefonie.
- 🏷 **Każde ćwiczenie ma przypisaną grupę mięśniową** — fundament pod statystyki progresu.
- ⭐ **Killer feature: podgląd „ile było ostatnio"** — w trakcie wykonywania serii widzisz swój ostatni wynik na tym samym ćwiczeniu. Pozwala szybko ocenić czy idziesz w górę.
- ⏱ **Timer odpoczynku zachowywany w historii** — kontekstualizuje osiągnięcia. *„Zrobiłem max na klatę pół roku temu, ale z 5-minutowymi przerwami"* — apka to wie i to pokazuje.
- 🎯 **Apka jest świadomie wąska** — pod klasyczny trening siłowy (plany → ćwiczenia → serie → powtórzenia × ciężar). Nie udajemy uniwersalnej apki fitness.

## 6. Success Metrics

| Wymiar | Cel |
|--------|-----|
| **Personal** | Używam apki na **każdym treningu** i nie wracam do notatek w telefonie |
| **Portfolio** | Repo na GitHubie samo z siebie pokazuje że umiem **myśleć o produkcie** (Vision, PRD, ADRs, czysta architektura) — nie tylko pisać kod. Pomaga w dostaniu rozmowy o pracę |
| **Google Play** | Apka jest **opublikowana i działa**. Popularność nice-to-have — bez celów liczbowych |

## 7. Non-Goals — czego apka świadomie NIE robi

- ❌ Social feed, followowanie znajomych, leaderboardy
- ❌ Gotowe plany od trenerów / influencerów / preset templates
- ❌ Liczenie kalorii, makroskładników, dieta
- ❌ Cardio, bieganie, joga, crossfit — apka jest **wyłącznie pod klasyczny trening siłowy**
- ❌ Eksport wyników do CSV / PDF (apka jest „dla siebie", nie do raportowania)
- ❌ Light mode i wybór motywu — **sztywny dark theme**
- ❌ Wielojęzyczność — **tylko PL**
- ❌ Logowanie i profile użytkownika (single-user, local-only)
- ❌ Cloud sync (może w przyszłości — nie warunek release)

## 8. Decyzje odroczone do PRD (Faza 2)

- **Statystyki w MVP czy v1.1?** Ekran istnieje na pewno — pytanie czy ląduje w pierwszym release na Google Play
- **RPE w uproszczonej formie 3-stopniowej** (łatwa / normalna / ciężka) — TAK / NIE / LATER?

> **Liczba serii** — locked: ustalana **wyłącznie w planie**, nie w definicji ćwiczenia. Biblioteka ćwiczeń trzyma „co" (nazwa, grupa mięśniowa), plan trzyma „jak" (ile serii, timer odpoczynku).

## 9. Roadmap kierunku — co po v1

- 🔮 **Integracja z Garmin Fenix 7s** (i być może innymi smartwatchami) — duża fika do wersji 2
- 🔮 *(potencjalnie)* Cloud sync, jeśli pojawi się realna potrzeba (np. zmiana telefonu)

---

## Referencje

- Plan procesu designu: `~/.claude/plans/zpoastanow-sie-jakie-zasady-async-floyd.md`
- Zasady projektu (git workflow, język): `CLAUDE.md`
- Następny dokument: `docs/02-prd.md` (Faza 2)
