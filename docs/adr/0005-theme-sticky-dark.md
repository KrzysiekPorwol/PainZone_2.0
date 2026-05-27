# ADR-0005: Theme — sticky dark Material 3, bez Material You

> TL;DR: Hard-coded M3 dark color scheme. Brak light, brak dynamic color, brak togglea.

## Kontekst

Vision §7 non-goals: brak wyboru motywu. Wireframe S11 wzmiankował "Material You system default" — REVIEW (`docs/REVIEW.md`) flagował sprzeczność i rozwiązał na korzyść stałego dark (low-light gym environment).

## Decyzja

`MaterialTheme(colorScheme = darkColorScheme(...))` z brand palette. Brak `dynamicDarkColorScheme()`. `isSystemInDarkTheme()` ignorowany. Brak preference w UI, brak persisted theme setting.

## Konsekwencje

Spójny brand (kontrolujemy kolory, nie OEM). Mniej testów theme combinations. Trade-off: user traci wybór — non-goal MVP. Łatwy do cofnięcia w v1.1 (podmiana statycznego scheme na dynamic + Settings).
