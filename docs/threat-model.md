# Threat Model — PainZone 2.0

> TL;DR: Lokalna apka bez sieci/kont — powierzchnia ataku minimalna; chronimy integralność lokalnej DB i UX przed pomyłkami usera.

## Spec

### Scope

- **Assets:** historia treningów (`WorkoutSession` + `LoggedSet`), plany (`TrainingPlan`), biblioteka ćwiczeń. Wartość: portfolio-grade danych nawykowych, nie wrażliwych (brak PII, brak finansów, brak zdrowia w sensie RODO szczególnym).
- **Trust boundaries:** Android sandbox apki (jedyna). Brak IPC export'owanego, brak sieci, brak kont (ADR-0006).
- **Out of scope:** ataki na OS, ataki na Google Play Services, ataki łańcucha dostaw Gradle/KSP (przyjmujemy wiarygodne źródła AndroidX/Hilt/Room).

### STRIDE

| Kategoria | Wektor | Ryzyko | Mitigacja |
|-----------|--------|--------|-----------|
| **Spoofing** | Inna apka udaje PainZone via intent | Niskie | Brak `exported=true` na aktywnościach/receiverach poza `MainActivity` (launcher). Bez deep-linków w MVP. |
| **Tampering** | Root/ADB modyfikuje `pz_db` | Niskie | Akceptowane — local-only, user's own device. Brak weryfikacji integralności (overkill MVP). |
| **Tampering** | Niedokończona migracja Room korumpuje schema | Średnie | Schema export do VCS (`room.schemaLocation`), unit-testy migracji od v2 wzwyż. `fallbackToDestructiveMigration` **wyłączony** w release. |
| **Repudiation** | N/A | — | Single-user, brak audytu — nie projektujemy logów. |
| **Information disclosure** | Android Auto Backup → Google Drive użytkownika | Niskie | Akceptowane (to dane usera, jego konto Drive). Dokumentuj w Settings/About. |
| **Information disclosure** | Logi (`Log.d` z danymi treningu) | Niskie | Tylko `Log.w`/`Log.e` z błędami; brak danych usera w logach. ProGuard strip `Log.d`/`Log.v` w release. |
| **Denial of Service** | Eksplozja danych (1000 sesji × 50 setów) | Niskie | Indeksy Room na `startedAt`, `exerciseId`. Paging odłożony do v1.x (MVP < 10k setów). |
| **Elevation of privilege** | KSP/Hilt-generated code wykonuje obcy kod | Bardzo niskie | Tylko źródła AndroidX/Google + Hilt official. Brak custom `@TypeConverter` z reflekcją. |

### Privacy / RODO

- **Dane osobowe:** brak (nazwy ćwiczeń + liczby ≠ PII).
- **Dane szczególne RODO art. 9 (zdrowie):** trening to lifestyle data, nie diagnostyka — nie kwalifikuje się jako "zdrowie" w sensie RODO. Confirmed, nie wymaga zgody.
- **Data controller:** użytkownik (dane na jego urządzeniu, brak transferu).

### Co flagujemy do v1.x

- Eksport JSON → szyfrowanie przy "Udostępnij" (Android Sharesheet).
- Jeśli dodamy sync/cloud → re-do threat modelu (nowe trust boundaries).

## Rationale

[on-demand]
