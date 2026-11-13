# CloudNest - performance notes

- Judge smoothness on a RELEASE build (`assembleRelease`, R8 on). Debug builds disable optimisation and Compose runs several times slower.
- Navigation bar: one spring per tab; widths, pill position and pill width come from one layout pass (no per-frame recomposition).
- Glass blur: GraphicsLayer + BlurEffect (Android 12+, GPU). On older phones only the tint is drawn.
- Measure on device: Android Studio Profiler (CPU/Memory), or a Macrobenchmark with FrameTimingMetric on the release build.
- Smoke budgets live in android/src/androidTest .../perf/PerformanceSmokeTest.kt.
