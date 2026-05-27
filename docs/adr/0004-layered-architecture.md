# ADR-0004: Layered architecture — UI / Domain / Data

> TL;DR: 3 warstwy bez UseCase layer; ViewModel woła repo bezpośrednio.

## Kontekst

MVP solo + projekt portfoliowy. Chcemy granice testowe i DIP, ale full Clean (UseCase per akcja) = overkill przy ~9 ekranach i prostym CRUD + logowanie sesji.

## Decyzja

Pakiety: `ui/` (Compose + `HiltViewModel` + `StateFlow`), `domain/` (encje + invarianty + repo interfaces), `data/` (Room entities + DAOs + repo implementations). Repo interfejs w `domain`, implementacja w `data` (DIP). Brak warstwy UseCase.

## Konsekwencje

ViewModel testowalny z fake repo (bez Robolectric). Mniej ceremonii vs Clean. Jeśli logika ViewModel urośnie (>3 metody z gałęziami) — można dodać UseCase post-MVP bez rewrite. Ryzyko: VM stanie się "fat" — kontrolować review-em.
