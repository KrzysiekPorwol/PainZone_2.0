# Architektura — PainZone 2.0

> TL;DR: Single-activity Compose + Room + Hilt; 3 warstwy `ui/` · `domain/` · `data/`, feature-first.

## Spec

### Stack

| Warstwa | Biblioteka | Wersja |
|---------|------------|--------|
| Język | Kotlin + `kotlinx.serialization` plugin | latest stable |
| UI | `androidx.compose:compose-bom` | 2026.03.00 |
| UI | `androidx.compose.material3:material3` | (z BOM) |
| UI | `androidx.activity:activity-compose` | 1.13.0 |
| UI | `androidx.lifecycle:lifecycle-viewmodel-compose` | 2.10.0 |
| Nawigacja | `androidx.navigation:navigation-compose` | 2.9.8 |
| DI | `com.google.dagger:hilt-android` | 2.59.2 |
| DB | `androidx.room:room-runtime` + `room-compiler` (KSP) | 2.8.4 |

Uwaga: od Room 2.7 `room-ktx` jest scalony z `room-runtime` — nie dodawać osobno.

### Package layout (root `com.painzone`)

```
PainZoneApp.kt              @HiltAndroidApp
MainActivity.kt             @AndroidEntryPoint
ui/
  theme/                    Color · Type · Theme (dark only)
  navigation/               NavGraph · Routes (@Serializable)
  common/                   shared composables (SmartCard, RpeChips, …)
  library/  plans/  session/  history/  stats/  settings/
    └─ {Feature}Screen.kt + {Feature}ViewModel.kt + ui-state
domain/
  exercise/  plan/  session/  common/
    └─ Encje · invarianty · Repository (interface)
data/
  db/                       PainZoneDatabase · TypeConverters · migrations
  exercise/  plan/  session/
    └─ {Feature}Entity · {Feature}Dao · {Feature}RepositoryImpl
  di/                       DatabaseModule · RepositoryModule
```

### Reguły warstw

- `ui/` → `domain/` (encje + interfejsy repo). `ui/` **nie** zna `data/`.
- `data/` → `domain/` (implementuje interfejsy repo, mapuje `Entity` ↔ encja).
- `domain/` → nic Android-specific (pure Kotlin). Brak Room/Compose imports.
- ViewModel woła repo bezpośrednio — brak UseCase layer (ADR-0004).
- State: `StateFlow` w VM, `collectAsStateWithLifecycle()` w Compose.

### Persistence

- Jedna baza `pz_db` (ADR-0006), schema versioning Room od `version = 1`.
- TypeConverters: `Instant ↔ Long (epochMillis)`, `MuscleGroup ↔ String`, `Rpe ↔ Int?`.
- Migracje: schema export `room.schemaLocation` w `build.gradle`, każda zmiana = `Migration(N, N+1)` lub auto-migration.

### Decyzje — linki do ADR

- [ADR-0001 Navigation Compose typesafe](adr/0001-nav-compose-typesafe.md)
- [ADR-0002 DI — Hilt](adr/0002-di-hilt.md)
- [ADR-0003 Snapshot pattern w WorkoutSession](adr/0003-session-snapshot-pattern.md)
- [ADR-0004 Layered architecture — UI/Domain/Data](adr/0004-layered-architecture.md)
- [ADR-0005 Theme — sticky dark Material 3](adr/0005-theme-sticky-dark.md)
- [ADR-0006 Local-only persistence](adr/0006-local-only-persistence.md)

## Rationale

[on-demand — uzasadnienia w plikach ADR]
