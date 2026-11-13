# CloudNest - If a Telegram account is banned

Goal: never lose files because one Telegram account is banned or deleted.

CloudNest does this with TWO Telegram accounts, without running two accounts inside one app:

1. **Account A (main)** - your normal CloudNest drive.
2. **Backup drive** - create a second drive on account A (Home > Drives). Open Cloud health > *Backup copy*,
   choose it as the backup drive, tap *Replicate now*. Copies are made inside Telegram (no re-upload) and only
   count as safe after they are read back and match (same Telegram unique file id and size).
3. **Rescue account B** - Cloud health > *Rescue account* > *Create rescue invite link*. Open the link on account B
   (it joins your drive). Install CloudNest on any phone, sign in with account B, open the shared drive, set a drive of B's
   own as the backup drive and tap *Replicate now*. B now owns a full copy.
4. Keep your **Recovery Passphrase** safe: encrypted files need it, on every account.

Automatic failover: if a download from the main drive fails (file or channel gone), CloudNest looks the file up in the
backup drive by its unique file id and downloads that copy instead.

Limits (honest):
- Files over 512 MB (multi-part) are not replicated yet.
- Replication runs when you tap the button; it is not scheduled.
- Replication is inside Telegram. If Telegram itself is unreachable, no copy helps; also keep a local copy of
  anything critical (Offline files).
