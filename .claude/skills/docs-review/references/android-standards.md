# Android / Kotlin / Compose — standards checklist

> Przeczytaj **przed** krokiem 3 (weryfikacja techniczna). Lista przestarzałych podejść do wyłapania w `docs/` + obszary, w których weryfikacja przez context7-mcp jest obowiązkowa.

## Przestarzałe podejścia (red flags w docs)

Jeśli dokumentacja projektu wspomina którekolwiek z poniższych → flag w sekcji **Przestarzałe podejścia** w raporcie.

### UI
- **XML layouts / `findViewById`** → projekt jest Compose-only.
- **`Fragment` / `FragmentManager`** → zastąpione przez composables + Navigation Compose.
- **`DataBinding` / `ViewBinding`** → niepotrzebne w Compose.
- **`RecyclerView` + `Adapter`** → `LazyColumn` / `LazyRow` / `LazyVerticalGrid`.
- **`ConstraintLayout` XML** → composables z `Modifier` (lub `ConstraintLayout` compose jeśli naprawdę potrzebny).

### Async / state
- **`AsyncTask`** → deprecated od API 30. Coroutines + Flow.
- **`Loader` / `LoaderManager`** → deprecated. Coroutines + Flow / Room Flow queries.
- **`LiveData` jako primary state** → w nowych projektach Compose preferuj `StateFlow` / `MutableStateFlow`. `LiveData` OK jeśli interop z Java/starszym kodem, ale dla nowego projektu Compose-first to anti-pattern.
- **`runBlocking` w UI / viewModelScope** → blokuje główny wątek.
- **GlobalScope** → leaki, brak strukturalnej współbieżności.
- **Callbacks zamiast suspend** w nowym kodzie Kotlin.

### Architektura
- **MVP / MVC** → preferowane MVVM/MVI z Compose.
- **`Activity`-based navigation z `Intent`** dla wewnętrznej nawigacji → Navigation Compose.
- **Wiele `Activity` jako ekrany** → single-activity + composables.
- **Manualny DI / Service Locator** dla nietrywialnych grafów → Hilt (lub Koin świadomie).

### Persistence
- **`SharedPreferences`** dla nowych preferencji → DataStore (Preferences lub Proto).
- **Raw SQLite (`SQLiteOpenHelper`)** → Room.
- **Synchroniczne queries Room z main threada** → suspend / Flow.

### Inne
- **`enqueueUniquePeriodicWork` bez constraints** → flag jeśli docs sugerują background work bez WorkManager / niepotrzebnie częste.
- **`startForegroundService` bez Android 14+ foregroundServiceType** → wymagane od API 34.
- **Brak wzmianki o `R8`/ProGuard rules** dla bibliotek wymagających (jeśli docs mówią o release build).

## Obszary OBOWIĄZKOWEJ weryfikacji przez context7-mcp

Dla każdego z poniższych założeń w `docs/` → **musisz** zfetchować aktualną dokumentację:

### Jetpack Compose
- API stabilności / `@Stable`, `@Immutable`
- `remember`, `rememberSaveable`, `derivedStateOf` — kiedy używać
- `LaunchedEffect`, `DisposableEffect`, `SideEffect` — semantyka
- Modifier order, `Modifier.composed` (deprecated w nowszych wersjach)
- Material 3 vs Material 2 — komponenty, theming, color schemes
- Animations API (`animate*AsState`, `updateTransition`, `AnimatedContent`)

### Navigation Compose
- Type-safe navigation (od 2.8+) z `@Serializable` route classes
- `NavHost`, `composable` builder, `navigation` graph
- Argumenty: typy, nullability, defaults
- Deep links
- BackStack handling, `popUpTo`, `inclusive`

### Room
- `@Entity`, `@PrimaryKey`, `@ColumnInfo`
- Relacje: `@Relation`, `@Embedded`, junction tables dla M:N
- `@TypeConverter` dla custom types
- Flow / suspend queries
- Migracje: `Migration` vs `AutoMigration`
- Multi-module schematy

### Hilt
- `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`
- Scopes: `@Singleton`, `@ViewModelScoped`, `@ActivityRetainedScoped`
- `@Provides` vs `@Binds`
- `@Qualifier` dla wielu implementacji
- Hilt z Compose: `hiltViewModel()`

### Coroutines / Flow
- `viewModelScope`, `lifecycleScope`, `repeatOnLifecycle`
- `collectAsStateWithLifecycle` (preferowane nad `collectAsState` w Compose)
- `SharedFlow` vs `StateFlow` — kiedy które
- `flowOn`, `Dispatchers.IO/Default/Main` — gdzie i kiedy
- `combine`, `flatMapLatest`, `stateIn`

### DataStore / Preferences
- Preferences DataStore vs Proto DataStore
- Migracja z SharedPreferences

### Inne często używane
- WorkManager: constraints, unique work, expedited work
- Paging 3 z Compose
- Coil / Glide dla obrazków
- KSP vs KAPT (KSP preferowane dla Room, Hilt — szybsze)

## Versioning awareness

Jeśli `docs/` podają wersje bibliotek (np. "Compose 1.5", "Room 2.6"):

- Sprawdź przez context7-mcp czy wersja nie jest **zbyt stara** (>1 major version behind).
- Sprawdź czy wzmianki o API są zgodne **z tą wersją** (nie z najnowszą).
- Jeśli docs nie podają wersji → flag w **Braki w dokumentacji**: "Brak deklaracji wersji bibliotek — utrudnia weryfikację API."

## Architektura — oficjalne rekomendacje Google

Sprawdź czy opisana architektura jest zgodna z [Modern Android Architecture Guidelines](https://developer.android.com/topic/architecture) (fetch przez context7-mcp):

- Warstwy: UI → Domain (opcjonalnie) → Data
- Unidirectional Data Flow w UI
- Repository pattern dla data layer
- UseCase / Interactor w domain (opcjonalnie, dla złożonej logiki)
- ViewModel jako state holder, **nie** Android-aware logic
- Single Source of Truth per dane
