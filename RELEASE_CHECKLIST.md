# CloudNest - Release checklist
- [ ] CI build green (Actions) and :shared:desktopTest green
- [ ] Firebase secrets set; rules published; API key restricted
- [ ] Signing keystore created and stored ONLY in GitHub Secrets (KEYSTORE, KEY_ALIAS, KEY_PASSWORD, KEYSTORE_PASSWORD)
- [ ] Install on a real phone: sign up, sign in, sign out, device list, revoke from second phone
- [ ] Telegram login + upload + download of a large file verified
- [ ] Release APK built with signing; version bumped in gradle/libs.versions.toml
