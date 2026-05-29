# ADR-0007: Reorder drag&drop — biblioteka `sh.calvin.reorderable`

> TL;DR: Drag&drop ćwiczeń w planie (S5) realizujemy biblioteką `sh.calvin.reorderable`, nie ręcznie.

## Kontekst

M2.7 wymaga reorderu ćwiczeń przez uchwyt ⠿ w liście (wireframe S5). Compose nie ma wbudowanego komponentu do drag&drop w `LazyColumn` — trzeba albo ręcznie liczyć offsety przeciągania, granice i autoscroll, albo użyć biblioteki. Ręczna implementacja to dużo kodu i edge case'ów (autoscroll przy krawędziach, animacja przesunięć, klucze itemów) — łatwo o subtelne bugi.

## Decyzja

Dodajemy `sh.calvin.reorderable:reorderable` (3.1.0). API: `rememberReorderableLazyListState(lazyListState) { from, to -> }`, `ReorderableItem(state, key) { isDragging -> }`, `Modifier.draggableHandle()` na uchwycie. Drag startuje **tylko z uchwytu** ⠿ — tap na wierszu dalej otwiera parametry, kosz dalej usuwa.

## Konsekwencje

Mniej własnego kodu, gładki drag + autoscroll + haptyka out-of-the-box. Trade-off: zewnętrzna zależność — ale dojrzała, mała, de-facto standard dla Compose, aktywnie utrzymywana. Persist kolejności jest niezależny od biblioteki (repo `reorderExercises(dayId, orderedIds)`), więc wymiana/usunięcie biblioteki nie dotyka warstwy danych. Pierwsza zewnętrzna zależność UI poza Compose/Hilt — świadomie, bo koszt ręcznej wersji > korzyść z zero-deps.