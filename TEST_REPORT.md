# CloudNest - Test report (honest status)

Nothing has been executed on the authoring server (no Android SDK / Gradle / network). Results must come from CI or a device.

## Written, to be run
| Suite | Where | Count / scope |
|---|---|---|
| Engine logic (worker policy, organizer, conflicts) | shared/jvmCommonTest .../core/engine | 9 |
| Sync merge (3-way, conflicts, first sync) | .../core/sync/SyncMergeTest | 6 |
| Telegram caption header/spoiler round-trip | .../core/sync/CaptionStylerTest | 5 |
| Cloud models / Firebase config | .../domain/CloudModelsTest | 2 |
| Koin graph verification (existing) | android/src/test AppModulesTest | covers new bindings |
| Compose UI tests: buttons, donut, tiles in all 3 design styles | android/src/androidTest .../ui | 5 |
| Performance smoke: 10k-row scroll, 10k-file DB insert | android/src/androidTest .../perf | 2 (generous budgets) |

CI: `:shared:desktopTest` and `testDebugUnitTest` run in .github/workflows/build.yml. Instrumented tests need an emulator:
`./gradlew :android:connectedDebugAndroidTest`.

## NOT covered
- Firestore security-rules emulator tests
- Firebase auth / sync end-to-end against a real project
- Multi-account switching, replication and failover against real Telegram (needs real accounts)
- Real-device frame-rate / memory profiling (use Android Studio Profiler or a Macrobenchmark on a release build)
