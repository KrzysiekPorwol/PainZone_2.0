# ADR-0006: Local-only persistence, brak kont

> TL;DR: Pojedyncza baza Room na urządzeniu, brak `user_id`, brak sync, brak eksportu w MVP.

## Kontekst

PRD US-7 + Vision §5: zero kont, zero sync w MVP. Wszystkie dane lokalnie. Unique constraints określane jako "per user" w F5 — niespójne z brakiem konta (REVIEW.md).

## Decyzja

Jedna baza Room (`pz_db`), brak kolumny `user_id` w żadnej tabeli. Unique constraints sformułowane jako "globalne wśród aktywnych rekordów" (np. `Exercise.name` unique gdzie `deletedAt IS NULL`). Brak warstwy sync/auth. Eksport JSON odłożony do v1.x.

## Konsekwencje

Utrata urządzenia = utrata historii (akceptowalne MVP, doc'owane w PRD US-7). Zero backendu = zero kosztów infra/RODO-data-controller. Backup: Android Auto Backup default — bez kontroli formatu. v1.x: dodać export/import (niezależnie od kont).
