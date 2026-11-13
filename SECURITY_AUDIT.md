# CloudNest - Security review (design-level, not a penetration test)

Implemented
- Cloud identity separate from Telegram sessions; Telegram sessions never leave the device.
- Refresh token encrypted with Android Keystore (CredentialCipher); passwords never persisted.
- Firestore rules: per-user isolation, default deny (firestore.rules).
- Activity log stores event type + short non-secret detail only.
- Device ID is a random per-install UUID (no hardware identifiers).

Known gaps / to verify
- Firestore rules are not yet covered by emulator tests.
- Device revocation is enforced client-side at app start (a cloned/modded client could ignore it).
- Encrypted cloud index sync is NOT implemented; only account, device registry and activity log use Firestore.
- No third-party penetration test or fuzzing has been done.

## Added since: cloud sync, replication, accounts
- Cloud index sync: names/folders/flags are gzip-compressed and sealed with AES-256-GCM (random 96-bit IV) using the
  content key BEFORE upload; Firestore only holds ciphertext, a version, a device id and a timestamp.
  Writes use an atomic precondition (updateTime / exists=false), so concurrent devices cannot silently overwrite each other.
- Merge never overwrites silently: changes on both sides become conflicts the person resolves.
- Replication copies inside Telegram (no key material involved); a replica counts as healthy only after read-back
  (same Telegram unique file id and size). Primary data is never modified or deleted.
- Multiple Telegram accounts: separate TDLib folder and Room database per account; sessions are never shared or copied.
  The content key (Android Keystore wrapped) is shared so replicas stay decryptable.
- Rescue invite link: grants read access to the drive to whoever opens it; treat the link as a secret and revoke it in Telegram if leaked.
- Known gaps: Firestore rules untested in the emulator; the committed signing key (android/keystore) is for personal builds only;
  device revocation is enforced client-side.
