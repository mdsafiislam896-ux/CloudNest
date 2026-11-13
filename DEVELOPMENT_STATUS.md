# NexVault - Development Status
Creator: Md. Safi Islam

## Environment (this build server)
- JDK 21 present. Android SDK, Gradle and network access: NOT available -> APK cannot be built here.
- Build path: Android Studio locally, or GitHub Actions (.github/workflows).

## Phase 1 - Foundation (done, not yet compiled)
- [x] Base project: TeleDrive 1.5.0 (Apache-2.0), credit kept in NOTICE / LICENSE
- [x] Package renamed com.drdisagree.teledrive -> com.safiislam.nexvault (sources, schemas, Room FQN)
- [x] Classes renamed: NexVaultApp / Theme / Database / Application
- [x] App name, channel titles, download folder rebranded to NexVault
- [x] About screen: "Created by Md. Safi Islam" + upstream credit
- [x] Upstream update-checker and donate/telegram links removed
- [ ] Wire-format markers (#teledrive-*, teledrive.db) intentionally kept for format compatibility

## Next
- [x] Premium palette + AMOLED scheme defined (NexVaultColors.kt)
- [ ] AMOLED/accent prefs + settings UI, donut storage ring, folder cards
- [ ] Splash with creator credit, new launcher icon
- [ ] Bengali locale (values-bn)
- [ ] Firebase auth, multi-account, replication, conflicts, device manager, health dashboard
- [ ] CI workflow rename + APK build

## Update - spec v2 received
- [x] Brand colors aligned to spec (Navy #101B35, Violet #7657F6, Aqua #37D6D0)
- [x] SOURCE_AUDIT.md written
- Order from spec: P0 build/audit -> P1 transfer+delete+manifest -> P2 sync -> P3 cloud manager -> P4 workspace -> P5 advanced

## Round 3
- [x] core/engine logic + tests written (unverified, unwired)
- [ ] Wire into Room/TransferScheduler/UI; Delete Engine remote ack; Sync; Cloud Manager; Bengali; UI restyle; remove desktop module

## Round 4 (static-checked only; brace balance verified on 21 edited files, NOT compiled)
- [x] AMOLED black theme option (AppTheme.AMOLED) wired: prefs, theme, Android + desktop entry points
- [x] Accent color picker (6 accents) end to end: prefs -> AppViewModel -> theme -> Settings > Appearance
- [x] Bengali language: AppLanguage.BENGALI + values-bn (187 UI strings; remaining ~590 fall back to English)
- [x] Home storage card: animated donut ring (StorageDonut.kt) with glow, replaces flat bar
- [ ] Still open: nav bar restyle + Transfers/Trash tabs, splash/onboarding/login restyle, folder cards, app icon,
      Firebase auth + Firestore rules, Sync engine, wiring core/engine into DB/transfers, Cloud Manager,
      replication/failover, Notes/Scanner/OCR extras, remove desktop module, full Bengali coverage, CI test run.

## Round 5
- [x] Bengali REMOVED per owner request (enum, strings, locale file)
- [x] Desktop module, upstream website, Windows/Linux/macOS CI and release/pages workflows removed -> Android only
- [x] CI: Android job now also runs :shared:desktopTest (engine unit tests)
- [x] Bottom bar: new Transfers tab (Home, Files, Gallery, Transfers, Settings); Trash stays in Home collections
- [x] Branded animated splash (BrandSplash) with tagline and "Created by Md. Safi Islam"
- [ ] STILL OPEN: login/register UI + Firebase (needs google-services.json), Sync engine, wiring core/engine into DB/transfers,
      Cloud Manager/replication/failover, new launcher icon, folder-card restyle, device manager, full test pass, release APK

## Round 6 - CloudNest (brand finalised by owner)
- [x] Brand: CloudNest; owner logo adopted (Telegram paper-plane replaced with a cloud-check glyph - Telegram marks must not be used as an app logo)
- [x] Adaptive launcher icon + monochrome + notification icon + in-app logo
- [x] Cloud account over Firebase REST (no google-services.json): sign up / in / out / reset / change password / edit name
- [x] Premium login/register/reset UI; app gated behind sign-in only when Firebase is configured
- [x] Device manager (list, rename, revoke, activity) on Firestore; firestore.rules (per-user isolation)
- [x] Cloud health dashboard (real counts only) with animated donut
- [x] Docs: FIREBASE_SETUP, SECURITY_AUDIT, TEST_REPORT, RELEASE_CHECKLIST
- [ ] OPEN: encrypted cloud-index sync + conflict UI, wiring core/engine into transfers/delete, multi-Telegram-account replication/failover,
      folder-card / Files / Home restyle to reference designs, context-menu restyle, signed release APK (needs keystore secrets)

## Round 7
- [x] Folder rows / folder grid / file rows restyled: rounded cards, hairline border, gradient folder badge, violet selection state
- [x] Removed redundant DeleteStateMachine (base has persistent PendingDelete + tombstones)
- [x] Audit corrected (see SOURCE_AUDIT.md CORRECTION 2)
- [ ] OPEN: Home/context-menu restyle, parallel part upload for >512 MB files, multi-account (TDLib), signed release APK

## Round 8 - desktop restored (Android + Windows/Linux/macOS)
- [x] desktop module restored from the original, renamed (package, CloudNest name, icons from the new logo, vendor Md. Safi Islam)
- [x] desktop DI: FirebaseConfig (from build-time secrets via BuildInfo) + DesktopDeviceInfoProvider
- [x] CI: Windows .msi, Linux .deb + AppImage, macOS .dmg jobs restored
- [x] Nav bar redesigned: glass bar, gradient glow pill, icon-only idle tabs, bouncing icon, haptic tick, press-scale
- [x] GradientButton (glow, press-scale, haptic) used on the sign-in screen; pressScale on folder/file cards
- [ ] OPEN: Home screen redesign, context-menu restyle, FAB restyle, multi-account/replication, signed release APK, full test pass

## Round 9 (user feedback from a real install)
- [x] Telegram: manifest caption now sent as a spoiler (text unchanged, so old/new manifests parse the same) - Android + desktop clients
- [x] Settings > Storage > "Tidy Telegram channel": re-publishes captions of existing files (rate-limit aware)
- [x] Nav bar rebuilt: one spring per tab, widths + pill derived in a single layout pass (no per-frame recomposition, no onGloballyPositioned feedback loop)
- [x] Tab transitions lightened (short fade + 1/14 drift instead of a full-width slide)
- [x] Home: greeting header + four gradient category tiles (photos/videos/audio/docs share of storage)
- [ ] OPEN: files auto-sorted into separate Telegram channels per type (needs multi-channel file listing), multi-account/replication,
      signed release APK, UI tests

## Round 10 (screenshots from the real install)
- [x] Donut ring size bug fixed (modifier order made it fill the card)
- [x] Design style setting: Colorful (default) / iPhone Glass / Warm Orange - palettes, shapes, typography per style
- [x] iPhone Glass floating tab bar (translucent capsule, specular rim, jelly-spring lens, icon + label tabs)
- [x] Default pill gets a glossy rim; default theme for NEW installs is Dark
- [x] Stable signing key committed (android/keystore) so every APK installs over the previous one; release build in CI
- [ ] OPEN: true backdrop blur (needs a library), iOS large-title screens, context-menu + FAB restyle, per-type Telegram channels,
      multi-account/replication, Play-store release key

## Round 11 - the "big" items
- [x] Real backdrop blur (GraphicsLayer + BlurEffect, no external library) behind the iPhone Glass tab bar; Android 12+ (older: tint only)
- [x] Telegram channel organised: friendly header (type, name, size, folder) + hashtags (#Photos, #Videos, #<folder>) above a spoilered manifest;
      stripped on read so the app only sees the manifest. "Tidy Telegram channel" refreshes old files.
- [x] Backup copy (replication) to a second drive of the same Telegram account: server-side copyDocument, read-back verification
      by Telegram unique file id + size, shown in Cloud Health. Files over 512 MB (multi-part) are not copied yet.
- [x] NexFab (glow / iPhone), Files FAB gradient, NexDropdownMenu (frosted iPhone card / soft rounded card) on all 6 menus
- [ ] NOT DONE: multiple Telegram ACCOUNTS (needs one TDLib instance per account - touches auth, DI and every client call),
      per-type separate CHANNELS (file list is scoped to one channel), iOS large-title screens, automatic failover on download

## Round 12 - design pass + ban protection
- [x] Settings: colourful gradient icon tiles (rounded squares / warm circles) for sections and rows; Home collection rows use them too
- [x] iPhone style: large bold titles via NexTopAppBar (Settings, Files, Gallery, Transfers)
- [x] Home: gradient "Cloud Storage" hero card with colour-segmented usage bar + "% safe" chip
- [x] Warm (Claude-like) style: solid orange pill, serif headings, ivory/charcoal palettes
- [x] Ban protection without multi-account: backup drive + rescue invite link + automatic download failover to the verified replica (RESCUE_GUIDE.md)
- [ ] NOT DONE: multiple Telegram sessions inside one app; per-type separate channels; Files Favorites/Browse/Recents tabs;
      scheduled replication; replication of >512 MB multi-part files

## Round 13 - the three remaining items
- [x] Multiple Telegram accounts (Android): AccountStore + AndroidAccountManager; each account has its own TDLib folder (first account keeps "tdlib"),
      its own Room file (first account keeps teledrive.db) and onboarding flag; shared Keystore key; switching restarts the app. UI: CloudNest account > Telegram accounts.
- [x] Sort uploads into drives by type (opt-in, Settings > Storage): TypeDriveRouter creates/uses Photos, Videos, Audio, Documents, Archives, Apps, Other drives; file goes to that drive's root.
- [x] Files: Browse / Recents / Favorites tabs + drive chips to jump between drives.
- [ ] Known limits: desktop has a single account; camera auto-backup is not type-sorted; replication still skips >512 MB parts.

## Round 14 - spec gaps closed
- [x] Found and fixed a real compile bug by static cross-checking imports (AuthScreen referenced a deleted CloudNestMark); all earlier patches re-verified by marker checks
- [x] Encrypted cloud index sync (Firestore) with 3-way merge, optimistic-concurrency writes, conflict screen (Keep this device / cloud / both / restore previous), auto-sync at start
- [x] Replication now mirrors whole drives, so files over 512 MB (multi-part) are copied and verified too; failover also works per part
- [x] Scheduled replication: daily WorkManager job (Wi-Fi, battery not low), switch in Cloud health
- [x] Tests: SyncMerge, CaptionStyler, Compose UI (3 styles), performance smoke (scroll + DB)
- [ ] Still true: nothing compiled or run here; trash state is not synced; sync covers items this device already knows (new ones arrive via Telegram);
      index snapshot limited to ~900 KB compressed; desktop has one account and no scheduled replication

## Round 15 - premium pass, design matched to the purple reference
- [x] Floating glass selection bar (Share / Favorite / Download / Move / Trash) + delete with Undo snackbar
- [x] Transfers overview card: live total speed, ETA, counts, Pause all / Resume all / Retry failed
- [x] Storage cleaner: duplicates (same content hash, "keep newest") + 50 largest files; trash-only, nothing automatic
- [x] Colorful style re-tuned to the reference: vivid purple, white rounded cards on lavender, orange/pink/teal/purple category tiles with big %,
      gradient hero card with slider-style safe% bar, swipeable drive cards with Open pill, gradient "Create folder" style dialog,
      purple welcome hero with highlighted word and ringed arrow button
- [ ] Not replicated from the reference: the 3D isometric cloud illustration (the CloudNest logo is used), Google Drive/OneDrive cards (CloudNest drives are Telegram channels)
