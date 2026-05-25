# Vision — PainZone 2.0

> Dokument Fazy 1 procesu designu. Zatwierdzony: 2026-05-25. Aktualizuje się wraz z kolejnymi fazami (PRD, Domain, Architektura).

## 1. Pitch

> **PainZone 2.0** to dziennik treningów siłowych dla doświadczonych amatorów którzy wiedzą co chcą trenować. W kilka sekund dodajesz swoje ćwiczenia, składasz z nich plan, a na siłowni logujesz serie bez ceremonii — widząc w trakcie wykonywania "ile było ostatnio na tym samym ćwiczeniu". Wszystko zaprojektowane pod jedno: nie tracić czasu na obsługę apki kiedy mógłbyś trenować.

## 2. Problem

Apki do trackowania treningów które dziś dominują (Hevy, Strong, Jefit) zaprojektowane są pod osobę zaczynającą lub szukającą inspiracji. Doświadczony amator siłowni — mający swój sprawdzony zestaw ćwiczeń — uderza w trzy bariery:

1. **Przeciążone interfejsy.** Kreatory, gotowe plany, sekcje społecznościowe, leaderboardy — zanim zalogujesz pierwszą serię.
2. **Logowanie wymuszone na starcie.** Konto, e-mail, password — zanim zobaczysz główny ekran apki.
3. **Przeciążone biblioteki ćwiczeń.** Setki gotowych pozycji z których realnie używasz 20 — twój zestaw ginie w szumie.

## 3. Persona

**Doświadczony amator siłowni, 18–30 lat.**

Trenuje samodzielnie 3–4 razy w tygodniu, zwykle realizuje wybrany plan przez ~3 miesiące zanim go wymieni. Nie korzysta z trenera — sam wie co chce robić, zna swoje ćwiczenia. Ceni swój czas: w trakcie treningu chce **szybko zarejestrować wynik bez ceremonii**, bez wymuszonego onboardingu, bez przebijania się przez biblioteki ćwiczeń których i tak nie używa.

## 4. Use Cases — 3 momenty użycia

| Moment | Co user robi | Priorytet UX |
|--------|-------------|--------------|
| **W trakcie treningu** (najważniejsze) | Rejestruje aktualne wyniki (powtórzenia, ciężar) i widzi „ile było ostatnio" w tej samej serii | **Maksymalna prostota i szybkość** — zero zbędnych kliknięć |
| **Przy planowaniu** | Tworzy/edytuje plan, dobiera ćwiczenia z własnej biblioteki, ustala liczbę serii i timer odpoczynku | Wygoda, czytelność |
| **Przy ocenie progresu** | Otwiera ekran statystyk — ogląda progres per grupa mięśniowa w skali miesięcy/lat | Czytelność wykresów, kontekst |

User trafia na siłownię **3–4 razy w tygodniu przez miesiące i lata** — apka musi pozostać szybka i miła nawet po tysiącu uruchomień.

## 5. USP — czym się odróżniamy

PainZone 2.0 stawia na **ekstremalną prostotę i indywidualizm**:

- 📬 **Pusta biblioteka na start** — dodajesz wyłącznie ćwiczenia których realnie używasz. Twój katalog odzwierciedla *twój* trening, nie inspirację z internetu.
- 🔓 **Konto opcjonalne** — apka pełnowartościowa od pierwszej sekundy bez logowania. Konto odblokowuje backup/synchronizację (dokładny zakres odroczony do PRD).
- ⚡ **Logowanie serii bez ceremonii** — minimum kliknięć między ekranem treningu a zapisanym wynikiem. Apka jest pod używanie *między seriami*, nie na kanapie.
- ⭐ **Killer feature: podgląd „ile było ostatnio"** — w trakcie wykonywania serii widzisz swój ostatni wynik na tym samym ćwiczeniu. Pozwala szybko ocenić czy idziesz w górę.
- ⏱ **Timer odpoczynku zachowywany w historii** — kontekstualizuje osiągnięcia. *„Zrobiłem max na klatę pół roku temu, ale z 5-minutowymi przerwami"* — apka to wie i to pokazuje.
- 🎯 **Apka jest świadomie wąska** — pod klasyczny trening siłowy (plany → ćwiczenia → serie → powtórzenia × ciężar). Nie udajemy uniwersalnej apki fitness.

## 6. Success Metrics

| Wymiar | Cel |
|--------|-----|
| **Personal** | Używam apki na **każdym treningu** i nie wracam do notatek w telefonie |
| **Portfolio** | Repo na GitHubie samo z siebie pokazuje że umiem **myśleć o produkcie** (Vision, PRD, ADRs, czysta architektura) — nie tylko pisać kod.
| **Google Play** | Apka jest **opublikowana i działa**. Popularność nice-to-have — bez celów liczbowych |

## 7. Non-Goals — czego apka świadomie NIE robi

- ❌ Social feed, followowanie znajomych, leaderboardy
- ❌ Gotowe plany od trenerów / influencerów / preset templates
- ❌ Liczenie kalorii, makroskładników, dieta
- ❌ Cardio, bieganie, joga, crossfit — apka jest **wyłącznie pod klasyczny trening siłowy**
- ❌ Eksport wyników do CSV / PDF (apka jest „dla siebie", nie do raportowania)
- ❌ Light mode i wybór motywu — **sztywny dark theme**
- ❌ Wielojęzyczność — **tylko PL**
- ❌ Integracje z Apple Health / Google Fit / Samsung Health
- ❌ Wearables (Garmin / Apple Watch / Galaxy Watch) w MVP — zaplanowane post-v1, patrz §9

## 8. Decyzje odroczone do PRD (Faza 2)

- **Co konkretnie daje konto (po zalogowaniu)?** Backup ćwiczeń / planów / historii, sync między urządzeniami, eksport danych, ewentualne social-like rzeczy. Co wchodzi do MVP, co do v1.1, co później.
- **Statystyki w MVP czy v1.1?** Ekran istnieje na pewno — pytanie czy ląduje w pierwszym release na Google Play. Konsekwencja: jeśli v1.1, use case „ocena progresu" z §4 też się tam przesuwa.
- **RPE w uproszczonej formie 3-stopniowej** (łatwa / normalna / ciężka) — TAK / NIE / LATER?

> **Liczba serii** — locked: ustalana **wyłącznie w planie**, nie w definicji ćwiczenia. Biblioteka ćwiczeń trzyma „co" (nazwa, grupa mięśniowa), plan trzyma „jak" (ile serii, timer odpoczynku).

## 9. Roadmap kierunku — co po v1

**Pewne kierunki na wersję 2:**

- 🔮 **Wearables** (Garmin Fenix 7s priorytetowo, dalej Apple Watch / Galaxy Watch) — duży moduł zaplanowany na wersję 2.
- 🔮 **Cloud sync / backup / eksport** — powiązane z funkcjonalnością opcjonalnego konta. Co dokładnie wchodzi i kiedy — decyzja w PRD, patrz §8.

**Możliwe pomysły:**

- 🔮 Integracje z Apple Health / Google Fit

---

## Referencje

- Zasady projektu (git workflow, język): `CLAUDE.md`
- Następny dokument: `docs/02-prd.md` (Faza 2)
