# ADR-0001: Navigation Compose typesafe

> TL;DR: Router = `androidx.navigation:navigation-compose` 2.9.8 z `@Serializable` route classes.

## Kontekst

Single-activity app, ~9 ekranów + 6 modali (`docs/04-wireframes.md`). Potrzebny router z bezpiecznymi argumentami (Plan id, Exercise id, Session id) bez ręcznego parsowania stringów.

## Decyzja

`androidx.navigation:navigation-compose:2.9.8` + `kotlinx.serialization` plugin. Destinacje jako `@Serializable data class`/`data object`, użycie `composable<Route> { it.toRoute<Route>() }`. Bez Voyager / Decompose / Compose Destinations.

## Konsekwencje

Compile-time typesafe args bez 3rd-party KSP. Wymóg: `org.jetbrains.kotlin.plugin.serialization`. Brak nested-graph slot API z Voyagera — wystarcza dla płaskiej IA MVP. Standard Android = wyższa wartość portfolio.
