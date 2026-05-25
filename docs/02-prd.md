# PRD — PainZone 2.0

> Dokument Fazy 2 procesu designu. Sekcja MoSCoW zatwierdzona: 2026-05-25. OST i user stories — w trakcie. Aktualizuje się wraz z Fazą 3 (User Flows).
>
> Poprzedni dokument: `docs/01-vision.md` · Następny: `docs/03-flows.md`

---

## 1. Kontekst

Wizja (`docs/01-vision.md`) zdefiniowała **co** i **dla kogo**. PRD odpowiada na **jaki scope** wchodzi do MVP, **jaki do v1.1**, **jaki później**, i **co świadomie odpuszczamy na zawsze**.

Trzy decyzje odroczone w wizji §8 zostały zamknięte poniżej w MoSCoW (sekcja 2):
- **Statystyki:** Stats Lite (lista historyczna) → MVP. Pełne wykresy progresu → v1.1.
- **Konto:** brak w MVP. Opcjonalne konto + cloud backup → v1.1+.
- **RPE 3-stopniowe:** MVP.

---

## 2. MoSCoW — priorytetyzacja scope'u

### MUST — MVP, release na Google Play

| Feature | Krótko |
|---------|--------|
| **Biblioteka ćwiczeń (CRUD)** | Start pusta. User dodaje wyłącznie te ćwiczenia których realnie używa. Atrybuty: nazwa + grupa mięśniowa. |
| **Plany treningowe (CRUD)** | Plan = uporządkowana lista dni. Dzień = uporządkowana lista ćwiczeń z biblioteki, każde z: liczbą serii + timerem odpoczynku per ćwiczenie. |
| **Sesja treningu** | Wybór planu i dnia → ekran sesji. Logowanie serii: `reps × ciężar × RPE` (3-stopniowe: łatwa / normalna / ciężka). |
| **"Ile było ostatnio" (killer feature)** | W trakcie wykonywania serii widoczny inline: ostatnia zarejestrowana seria dla *tego samego ćwiczenia* z poprzedniej sesji (reps × ciężar × RPE). |
| **Timer odpoczynku** | Aktywny w sesji między seriami. Zapis rzeczywistego czasu odpoczynku do historii (kontekstualizuje wyniki). |
| **Stats Lite** | Ekran per-ćwiczenie: chronologiczna lista wszystkich serii z ostatnich 90 dni domyślnie + filtr okresu. Best set highlighted. Bez wykresów. |
| **Lokalna persystencja** | Wszystkie dane lokalnie (Room). Zero konta, zero internetu, zero onboardingu. Apka działa od pierwszej sekundy. |

### SHOULD — v1.1

| Feature | Krótko |
|---------|--------|
| **Pełne statystyki** | Wykresy progresu per grupa mięśniowa, skala miesięcy/lat. Decyzja o bibliotece wykresów (Vico / MPAndroidChart / custom Compose) → ADR w Fazie 6, gdy v1.1 wejdzie do scope'u. |
| **Opcjonalne konto + cloud backup** | Zakres do dopięcia w PRD v1.1: backup całości danych vs realny sync między urządzeniami. Decyzja architektoniczna (Firebase / własny backend / Drive API) odroczona. |

### COULD — v2

- Sync między urządzeniami (jeśli v1.1 zostało tylko przy backupie).
- **Wearables** — Garmin Fenix 7s priorytetowo, dalej Apple Watch / Galaxy Watch.
- Integracje z Apple Health / Google Fit.

### WON'T — kiedykolwiek

- ❌ Social feed, leaderboardy, followowanie znajomych.
- ❌ Gotowe plany od trenerów / influencerów / preset templates.
- ❌ Liczenie kalorii, makroskładników, dieta.
- ❌ Cardio, bieganie, joga, crossfit — apka jest **wyłącznie pod klasyczny trening siłowy**.
- ❌ Eksport CSV / PDF.
- ❌ Light mode i wybór motywu — sztywny dark theme.
- ❌ Wielojęzyczność — tylko PL.
- ❌ Wymuszony onboarding z kontem / e-mailem.

---

## 3. Opportunity Solution Tree

> _W trakcie. Tu trafi diagram Mermaid: outcome → opportunities (potrzeby usera) → solutions (Must z §2 + część Should) → assumption tests._

---

## 4. User Stories — MVP

> _W trakcie. Format: "Jako [persona], chcę [akcja], żeby [benefit]" + acceptance criteria. Po jednej grupie stories per feature z MUST §2._

---

## Referencje

- Wizja: `docs/01-vision.md`
- Proces: `docs/00-process.md`
- Glossary: `docs/glossary.md`
- Następny dokument: `docs/03-flows.md` (Faza 3 — User Flows + IA)
