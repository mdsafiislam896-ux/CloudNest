# NexVault Source Audit (honest status, Phase 0)
Method: static reading only. Nothing compiled or run (no SDK/network on build server).

## Found in base (TeleDrive 1.5.0) - still needs device verification
- Telegram via TDLib (TdLibTelegramClient), storage channel discovery by title/marker
- Transfer states: QUEUED/RUNNING/PAUSED/COMPLETED/FAILED/CANCELLED; transfer module in core/transfer
- Crypto module, key backup, recovery, Room DB with schema + migration test, DataStore prefs
- Media/thumbnail store, publish scheduler (WorkManager), proxy, network and power modules

## NOT found in base (must be built; spec Parts 4-7, 9-14, 17)
- Adaptive worker profiles Auto/8/16/32 (no profile model or segmented parallel transfer)
- Reliable Delete Engine state machine + tombstones; Universal multi-device Sync + conflicts
- Smart Storage Engine (auto-organization by type), Cloud Manager dashboard, Failure Center
- Notes, Scanner/OCR, Archive/Image tools, share links, Smart Search, Automation
- Bengali locale, brand splash/onboarding, AMOLED/accent settings, custom nav bar

## Design decisions (API-honest)
- Parallel upload: split into N parts -> N messages in parallel -> parts table in manifest -> SHA-256 verify. TDLib parallelism is bounded per account/DC; 32 workers = ceiling, not promise; Auto adapts to measured throughput and FLOOD_WAIT.
- Remote delete: state PENDING_REMOTE -> CONFIRMED only after API ack; failures retry, never silently drop.
- Keep wire markers (#teledrive-*) so old drives stay readable; add NexVault dual-prefix discovery.
- Client-side encryption before upload; keys in Keystore; passphrase never leaves device.

## CORRECTION (after deeper directory audit)
Earlier summary wrongly implied some UI modules were missing. The base already has presentation modules for:
notes, onboarding, search, trash, gallery, channels, collection, applock, proxy, settings (13 files), and a
floating nav bar with a single spring-animated sliding pill (NexVaultApp.kt). Folder tombstones and 512 MB
file-part splitting (FileParts.kt) also exist. These need restyling/verification, not rewriting.

## New in this round (pure logic + unit tests, NOT yet wired into DB/UI, NOT yet compiled)
core/engine: TransferProfile+WorkerPolicy, DeleteStateMachine, SmartOrganizer, ConflictResolver; tests in EngineTest.kt.
Note: FileParts (512 MB parts) and WorkerPolicy (parallel segments) are different layers; integration design pending.

## CORRECTION 2 (deeper read of data layer)
- Reliable delete ALREADY exists in the base: TrashRepositoryImpl persists PendingDelete rows before calling Telegram,
  clears them only after success, kicks the publish outbox to retry on failure, removes local metadata only after the remote
  delete, and records folder tombstones. My DeleteStateMachine duplicated this and was removed.
- Multi-device restore ALREADY exists via Telegram: the storage channel holds manifests + folder state; "Rebuild index" restores it.
  Firestore is therefore used for account/devices/activity only; a second (encrypted index) copy would add risk, not value.
- Per-file parallelism is limited by design: files are split into 512 MB parts (FileParts) and parts go sequentially.
  Real parallel parts would be possible only for >512 MB files; smaller segments would change the manifest format.
