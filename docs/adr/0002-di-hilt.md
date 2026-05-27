# ADR-0002: DI — Hilt

> TL;DR: Hilt 2.57.2 (KSP) jako jedyny mechanizm DI.

## Kontekst

Solo projekt, prosty graf (DB → DAO → Repo → ViewModel). Wymagana integracja z `hiltViewModel()` w Compose. Alternatywy: Koin (runtime), manualne DI.

## Decyzja

`com.google.dagger:hilt-android:2.57.2` + plugin `com.google.dagger.hilt.android` + KSP `hilt-compiler`. `enableAggregatingTask = true` dla incremental build. `@HiltViewModel` w `ui/`, `@Module @InstallIn(SingletonComponent::class)` w `data/`. Wersja 2.57.2 — ostatnia kompatybilna z AGP 8.x (2.59+ wymaga AGP 9.0+).

## Konsekwencje

Compile-time graf (vs runtime Koin) — błędy w buildzie, nie w czasie wykonania. Hilt = standard Android (portfolio +). Annotation processing wydłuża build; `enableAggregatingTask` ogranicza. `hilt-android-testing` daje rule do testów instrumented.
