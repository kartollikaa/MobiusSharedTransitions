# MobiusSharedTransitions

Демо-приложение к докладу [«Когда Shared Transition — это требование дизайнеров: необычные кейсы на Compose»](https://mobiusconf.com/talks/6efbf5637ee3439b85042a41002c0937/) на конференции [Mobius](https://mobiusconf.com/).

Вырезанный из продакшена раздел «Комбо» приложения [Drinkit](https://play.google.com/store/apps/details?id=ru.dodopizza.drinkit) — кейс, на котором в докладе разбирается, как делать shared element transitions в нетривиальных условиях: с блюром, видеофоном и затемнениями. Приложение одноэкранное, без бэкенда, запускается «как есть».

В репозитории показано:

- shared element transition между списком слотов и деталями — одновременно на нескольких элементах;
- многослойный frosted-glass blur поверх видеофона на [Haze](https://github.com/chrisbanes/haze), который не ломает transition;
- интеграция с Navigation 3 (`androidx.navigation3`) и predictive back;
- Macrobenchmark с A/B-замером блюра.

## Shared elements

Два экрана: `combo/slots/ComboSlots.kt` (список слотов) и `combo/details/ComboDetails.kt` (детали с pager'ом продуктов). Transition настроен сразу на семи элементах — список в `combo/sharedtransition/ComboSharedElementKey.kt`:

```
Background  Image  CustomizePanel  CustomizeButton  SizeText  NameText  StoppedBadge
```

Ключ — `ComboSharedElementKey(slotId, productId, type)`. `slotId` нужен, чтобы одна и та же картинка продукта в разных слотах не схлопывалась в общий transition.

Поверх обычного fade-перехода (`tween(700ms)`) прописан predictive back со своим `transitionSpec`: `tween(200ms)` на enter и `tween(100ms)` на exit.

## Многослойный блюр на Haze

На фоне крутится видео, поверх — полупрозрачные карточки с блюром, поверх них кнопки и бейджи тоже с блюром. Если включить `hazeEffect` везде, эффекты блюрят друг друга, и картинка превращается в кашу.

Решение — z-index'ы и фильтрация через `canDrawArea`. В `combo/blur/ComboBlurZIndex.kt`:

```kotlin
object ComboBlurZIndex {
    const val Video = 0f
    const val BackgroundTint = 1f
    const val SlotBackground = 2f
    const val Image = 3f
}
```

Каждый блюр-приёмник фильтрует источники по `area.zIndex < <свой слой>`. Модификаторы в `combo/blur/BlurEffectModifier.kt`:

- `Modifier.backgroundBlurSource(provider, zIndex, key)` — то, что показывается **сквозь** блюр.
- `Modifier.backgroundBlurEffect(...)` — поверхность, которая **сама блюрит**. `noiseFactor = 0f`, `inputScale = HazeInputScale.Fixed(0.5f)` (блюр в половинном разрешении), `fallbackTint` для режима без блюра.

`BlurProvider` раздаётся через `staticCompositionLocalOf`. Когда `hazeState == null` (превью, тесты, A/B-режим без блюра) — модификаторы тихо схлопываются в обычный `background(color)`.

## Видеофон через ExoPlayer

`combo/video/ComboVideo.kt` рендерит видео через `PlayerView` **обязательно на `TextureView`** (`res/layout/combo_player_view.xml`). `SurfaceView` рисуется отдельной surface'ой композитора и в захват Haze не попадает — блюр получается «дырявый».

## Бенчмарки

`MainActivity` принимает Intent extra `blur_enabled` — флаг прокидывается в `ComboScreen` и `rememberHazeState`. A/B-тумблер: один и тот же UI с блюром и без.

- `ColdStartupBenchmark` — холодный старт.
- `ComboTransitionBenchmark` — «открыть слот → дождаться идла → назад» с `FrameTimingMetric`, `TraceSectionMetric("Compose:draw", Sum)`, `MemoryUsageMetric`. Два варианта: `transitionBlurEnabled` / `transitionBlurDisabled`.

UI цепляется по `testTagsAsResourceId` — `combo_slot_0`, `combo_back`.

### Метрики

- **`StartupTimingMetric`** — TTID/TTFD холодного старта. Захватывает первый кадр приложения и время до полной отрисовки.
- **`FrameTimingMetric`** — длительность каждого кадра (`frameDurationCpuMs`). В отчёт идут перцентили P50/P90/P95/P99 — лежат в `sampledMetrics` отдельно от агрегированных метрик. Бюджет на 60 Гц — 16.67 мс/кадр.
- **`MemoryUsageMetric(Max)`** — пиковые `GPU`, `HeapSize`, `RssAnon`, `RssFile` за прогон.
- **`TraceSectionMetric("Compose:draw", Sum)`** — суммарное время в фазе draw Compose. На текущей версии Compose секция с этим именем не эмитится (отдаёт нули) — название нужно сверить с реальным трейсом в `ui.perfetto.dev`.

### Результаты (Pixel 4a, Android 13, 60 Гц, 7 итераций)

Cold startup, TTID (медиана):

| Вариант  | TTID    | Δ                |
| -------- | ------- | ---------------- |
| Blur ON  | 918 мс  |                  |
| Blur OFF | 847 мс  | −71 мс (~8%)     |

Длительность кадра в переходе list ↔ detail, мс:

| Вариант  | P50   | P90   | P95   | P99   |
| -------- | ----- | ----- | ----- | ----- |
| Blur ON  | 37.85 | 50.13 | 55.74 | 85.52 |
| Blur OFF | 30.06 | 35.76 | 39.05 | 62.51 |
| Δ        | +7.79 (+26%) | +14.37 (+40%) | +16.69 (+43%) | +23.01 (+37%) |

Оба варианта в переходе вылезают за бюджет 16.67 мс/кадр, но Blur ON значимо хуже на всех перцентилях; хвост (P99) проседает сильнее всего.

Память за прогон (медиана пиков):

| Вариант  | GPU      | Heap     | RSS anon |
| -------- | -------- | -------- | -------- |
| Blur ON  | 125 МБ   | 117 МБ   | 165 МБ   |
| Blur OFF | 94 МБ    | 162 МБ   | 204 МБ   |

+31 МБ GPU с блюром ожидаемы — Haze кеширует снапшоты поверхностей. Heap/RSS у варианта без блюра выше — вероятно шум на 7 итерациях, GPU — единственная достоверная метрика памяти здесь.

Трейсы каждого прогона лежат рядом с `*-benchmarkData.json` в `benchmark/build/outputs/connected_android_test_additional_output/.../<device>/` — открываются в `ui.perfetto.dev` для разбора отдельных кадров.

## Стек

- Kotlin 2.0.21, AGP 9.0.1, minSdk 26, targetSdk 36
- Compose BOM 2026.01.01 (Compose 1.10.3)
- [Haze](https://github.com/chrisbanes/haze) 1.6.0
- [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) 1.0.1
- [Media3 ExoPlayer](https://developer.android.com/media/media3) 1.6.0
- Macrobenchmark 1.3.4

## Структура

- `app/combo/` — экран и всё, что с ним связано
  - `ComboScreen.kt` — корень: Haze + `BlurProvider` + `NavDisplay` + `SharedTransitionLayout`
  - `slots/`, `details/`, `components/` — UI
  - `blur/` — обвязка вокруг Haze (`BlurProvider`, модификаторы, z-index'ы)
  - `sharedtransition/` — ключи shared-элементов
  - `video/` — `PlayerView` на `TextureView`
- `benchmark/` — Macrobenchmark на `:app`

## Запуск

```bash
./gradlew :app:installDebug
./gradlew :benchmark:connectedBenchmarkAndroidTest   # нужен физический девайс
```
