# MobiusSharedTransitions

Демо-приложение к докладу [«Когда Shared Transition — это требование дизайнеров: необычные кейсы на Compose»](https://mobiusconf.com/talks/6efbf5637ee3439b85042a41002c0937/) на конференции [Mobius](https://mobiusconf.com/).

Это вырезанный из прод приложения, но упрощенный раздел «Комбо» приложения [Drinkit](https://play.google.com/store/apps/details?id=ru.dodopizza.drinkit). На нём в докладе разбираю shared element transitions в неудобных условиях: блюр, видео, затемнения

В репозитории показано:

- shared element transition между списком слотов и деталями;
- многослойный blur поверх видео на [Haze](https://github.com/chrisbanes/haze), который работает совместно с Shared Transitions;
- Navigation 3 (`androidx.navigation3`) с predictive back;
- Macrobenchmark с A/B-замером блюра.

## Shared elements

Экранов два: `combo/slots/ComboSlots.kt` со списком и `combo/details/ComboDetails.kt` с pager'ом продуктов. В переходе участвуют семь элементов, ключи лежат в `combo/sharedtransition/ComboSharedElementKey.kt`:

```
Background  Image  CustomizePanel  CustomizeButton  SizeText  NameText  StoppedBadge
```

Ключ выглядит как `ComboSharedElementKey(slotId, productId, type)`. `slotId` нужен, чтобы одинаковые продукты в разных слотах не схлопывались в общий transition.

## Многослойный блюр на Haze

На фоне крутится видео, над ним полупрозрачные карточки с блюром, над ними кнопки и бейджи — тоже с блюром

Чтобы реализовать такой послойный блюр – у каждого слоя свой z-index, а блюр фильтрует источники через `canDrawArea`. Иерархия лежит в `combo/blur/ComboBlurZIndex.kt`:

```kotlin
object ComboBlurZIndex {
    const val Video = 0f
    const val BackgroundTint = 1f
    const val SlotBackground = 2f
    const val Image = 3f
}
```

Каждый слой поверхности с блюром берёт только источники с меньшим z-index'ом. Сами модификаторы блюров лежат в `combo/blur/BlurEffectModifier.kt`:

- `Modifier.backgroundBlurSource(provider, zIndex, key)` — поверхность-источник для вычисления блюр.
- `Modifier.backgroundBlurEffect(...)` — поверхность блюра, реализующая сам визуальный эффект. `noiseFactor = 0f`, `inputScale = HazeInputScale.Fixed(0.5f)` (блюрим в половинном разрешении), `fallbackTint` на случай отключённого блюра.

`BlurProvider` пробрасывается через `staticCompositionLocalOf`. Если `hazeState == null` (превью, тесты, A/B без блюра), модификаторы тихо превращаются в обычный `background(color)`.

## Видео через ExoPlayer

`combo/video/ComboVideo.kt` рендерит видео через `PlayerView` поверх `TextureView` (`res/layout/combo_player_view.xml`), и это важно!. `SurfaceView` рендерится в отдельном потоке, отдельностоящим механизмом, и Haze её не видит – блюр не захватывается

## Бенчмарки

`MainActivity` читает Intent extra `blur_enabled`, флаг передается в `ComboScreen` и `rememberHazeState`. Получается A/B: один и тот же UI с блюром и без него.

- `ColdStartupBenchmark` — холодный старт.
- `ComboTransitionBenchmark` — «открыть слот, подождать Idle, нажать назад». Метрики: `FrameTimingMetric`, `TraceSectionMetric("Compose:draw", Sum)`, `MemoryUsageMetric`. Два варианта: `transitionBlurEnabled` и `transitionBlurDisabled`.

Тесты цепляются за UI через `testTagsAsResourceId`: `combo_slot_0`, `combo_back`.

### Метрики

- **`StartupTimingMetric`** — TTID/TTFD холодного старта.
- **`FrameTimingMetric`** — длительность каждого кадра (`frameDurationCpuMs`). В отчёте есть P50/P90/P95/P99, лежат в `sampledMetrics` отдельно от агрегатов. На 60 Гц бюджет — 16.67 мс на кадр.
- **`MemoryUsageMetric(Max)`** — пиковые `GPU`, `HeapSize`, `RssAnon`, `RssFile`.
- **`TraceSectionMetric("Compose:draw", Sum)`** — суммарное время в фазе draw Compose. На текущей версии Compose секция с этим именем не эмитится, метрика всегда нулевая. Имя надо сверить с тем, что реально пишется в трейс на `ui.perfetto.dev`.

### Результаты (Pixel 4a, Android 13, 60 Гц, 7 итераций)

Cold startup, TTID, медиана:

| Вариант  | TTID    | Δ                |
| -------- | ------- | ---------------- |
| Blur ON  | 918 мс  |                  |
| Blur OFF | 847 мс  | −71 мс (~8%)     |

Длительность кадра на переходе list ↔ detail, мс:

| Вариант  | P50   | P90   | P95   | P99   |
| -------- | ----- | ----- | ----- | ----- |
| Blur ON  | 37.85 | 50.13 | 55.74 | 85.52 |
| Blur OFF | 30.06 | 35.76 | 39.05 | 62.51 |
| Δ        | +7.79 (+26%) | +14.37 (+40%) | +16.69 (+43%) | +23.01 (+37%) |

Оба варианта во время перехода не укладываются в 16.67 мс на кадр, но с блюром заметно хуже на всех перцентилях, и хвост (P99) проседает сильнее всего.

Память, медиана пиков за прогон:

| Вариант  | GPU      | Heap     | RSS anon |
| -------- | -------- | -------- | -------- |
| Blur ON  | 125 МБ   | 117 МБ   | 165 МБ   |
| Blur OFF | 94 МБ    | 162 МБ   | 204 МБ   |

+31 МБ GPU с блюром ожидаемы: Haze кеширует снапшоты поверхностей. Heap и RSS у варианта без блюра почему-то выше — похоже на шум на 7 итерациях. Из метрик памяти тут можно доверять только GPU.

Трейсы каждого прогона лежат рядом с `*-benchmarkData.json` в `benchmark/build/outputs/connected_android_test_additional_output/.../<device>/`. Открываются в `ui.perfetto.dev`, там можно покопаться в отдельных кадрах.

## Стек

- Kotlin 2.0.21, AGP 9.0.1, minSdk 26, targetSdk 36
- Compose BOM 2026.01.01 (Compose 1.10.3)
- [Haze](https://github.com/chrisbanes/haze) 1.6.0
- [Navigation 3](https://developer.android.com/guide/navigation/navigation-3) 1.0.1
- [Media3 ExoPlayer](https://developer.android.com/media/media3) 1.6.0
- Macrobenchmark 1.3.4

## Структура

- `app/combo/` — экран и всё к нему
  - `ComboScreen.kt` — корень: Haze + `BlurProvider` + `NavDisplay` + `SharedTransitionLayout`
  - `slots/`, `details/`, `components/` — UI
  - `blur/` — абстракции вокруг Haze (`BlurProvider`, модификаторы, z-index'ы)
  - `sharedtransition/` — ключи shared-элементов
  - `video/` — `PlayerView`
- `benchmark/` — Macrobenchmark на `:app`

## Запуск

```bash
./gradlew :app:installDebug
./gradlew :benchmark:connectedBenchmarkAndroidTest   # нужен физический девайс
```
