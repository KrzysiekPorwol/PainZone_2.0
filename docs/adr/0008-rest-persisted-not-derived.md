# ADR-0008: Rest serii persistowany (`restBeforeSeconds`), nie derived

> TL;DR: `LoggedSet.restBeforeSeconds` przechowuje faktyczny odpoczynek przed serią; rezygnujemy z liczenia go z różnicy `completedAt`.

## Kontekst

`05-domain-session.md` zakładał, że rest między seriami liczymy w query: `completedAt[n] − completedAt[n-1]`, bez persistowania. M3.7 (Rest Timer) wymaga jednak, żeby kolejny `LoggedSet` trzymał faktyczny zmierzony odpoczynek (roadmap: „`restBeforeSeconds=actual` w kolejnym `LoggedSet`"). Wartość derived jest też mniej precyzyjna — różnica `completedAt` miesza odpoczynek z czasem wykonania serii.

## Decyzja

`LoggedSet` zyskuje pole `restBeforeSeconds: Int?` (null lub `>=0`). Przy `log()` repo liczy `now − completedAt` poprzedniej serii w tym samym `SessionExerciseSnapshot` i zapisuje jako sekundy. Pierwsza seria ćwiczenia → `null` (nic jej nie poprzedza). `edit()` nie tyka pola — edycja reps/weight/rpe nie zmienia odpoczynku. Schema v4, migracja addytywna `ALTER TABLE logged_set ADD COLUMN rest_before_seconds INTEGER`.

Banner timera (UI) liczy count-up z `completedAt` ostatniej serii aktywnego ćwiczenia — auto-start po zapisie, auto-reset przy kolejnym, odporny na śmierć procesu (przygotowuje M3.9).

## Konsekwencje

Historia trzyma realny odpoczynek bez joinów i bez zależności od sąsiednich rekordów przy edycji. Koszt: jedna kolumna + migracja. Wartość zapisana = ten sam czas, który widział user na bannerze (`now − completedAt[n-1]`), więc zawiera też czas wykonania serii n — akceptowalne dla logu treningowego (alert przekroczenia targetu: M3.8).
