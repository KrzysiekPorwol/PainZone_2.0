# Vision — PainZone 2.0

> TL;DR: Dziennik siłowni dla doświadczonego amatora — pusta biblioteka, zero konta, logowanie serii w sekundy z podglądem "ile było ostatnio".
>
> Zatwierdzony: 2026-05-25.

## 1. Pitch

> **PainZone 2.0** to dziennik treningów siłowych dla doświadczonych amatorów którzy wiedzą co chcą trenować. W kilka sekund dodajesz swoje ćwiczenia, składasz z nich plan, a na siłowni logujesz serie bez ceremonii — widząc w trakcie wykonywania "ile było ostatnio na tym samym ćwiczeniu". Wszystko zaprojektowane pod jedno: nie tracić czasu na obsługę apki kiedy mógłbyś trenować.

## 2. Problem

Apki takie jak Hevy, Strong, Jefit projektowane są pod osobę zaczynającą. Doświadczony amator uderza w trzy bariery:
1. **Przeciążone interfejsy.** Kreatory, gotowe plany, social — zanim zalogujesz pierwszą serię.
2. **Logowanie wymuszone na starcie.** Konto, e-mail — zanim zobaczysz główny ekran.
3. **Przeciążone biblioteki.** Setki gotowych pozycji, używasz 20.

## 3. Persona

**Doświadczony amator siłowni, 18–30 lat.** Trenuje 3–4× w tygodniu, realizuje własny plan ~3 miesiące. Nie korzysta z trenera. Ceni czas — szybka rejestracja między seriami, bez ceremonii.

## 4. Use Cases — 3 momenty użycia

| Moment | Co user robi | Priorytet UX |
|--------|-------------|--------------|
| **W trakcie treningu** (najważniejsze) | Rejestruje reps × ciężar, widzi "ile było ostatnio" | Maksymalna prostota — zero zbędnych kliknięć |
| **Przy planowaniu** | Tworzy plan z ćwiczeń, ustala serie i timery | Wygoda, czytelność |
| **Przy ocenie progresu** | Ogląda progres per ćwiczenie w skali miesięcy | Czytelność, kontekst |

## 5. USP

- 📬 **Pusta biblioteka na start** — tylko twoje ćwiczenia.
- 🔓 **Zero konta w MVP** — pełnowartościowa od pierwszej sekundy.
- ⚡ **Logowanie bez ceremonii** — minimum kliknięć.
- ⭐ **Killer feature: Last Set Preview** — inline podgląd ostatniego wyniku podczas serii.
- ⏱ **Timer odpoczynku w historii** — kontekstualizuje osiągnięcia.
- 🎯 **Świadomie wąska** — wyłącznie klasyczny trening siłowy.

## 6. Success Metrics

| Wymiar | Cel |
|--------|-----|
| **Personal** | Używam na każdym treningu, nie wracam do notatek |
| **Portfolio** | `docs/` pokazuje product thinking, nie tylko kod |
| **Google Play** | Opublikowana i działa |

## 7. Non-Goals

- ❌ Social feed, leaderboardy, followowanie
- ❌ Gotowe plany / preset templates
- ❌ Kalorie, makro, dieta
- ❌ Cardio, bieganie, joga, crossfit
- ❌ Eksport CSV / PDF
- ❌ Light mode / wybór motywu — sztywny dark theme
- ❌ Wielojęzyczność — tylko PL
- ❌ Wearables i integracje zdrowotne — post-v1