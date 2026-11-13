# CloudNest
**Your Personal Cloud Universe** - a personal cloud drive that stores your files in your own private Telegram channel, with client-side encryption. Android and desktop (Windows, Linux, macOS).

Created by **Md. Safi Islam**. Built on the open-source [TeleDrive](https://github.com/Mahmud0808/TeleDrive) (Apache License 2.0); see `NOTICE` and `LICENSE`.

## Platforms
| Platform | Module | Build |
|---|---|---|
| Android | `android` | `./gradlew :android:assembleDebug` |
| Windows (.msi) | `desktop` | `./gradlew :desktop:packageMsi` |
| Linux (.deb / AppImage) | `desktop` | `./gradlew :desktop:packageDebUser` |
| macOS (.dmg) | `desktop` | `./gradlew :desktop:packageDmg` |

GitHub Actions (`.github/workflows/build.yml`) builds all of them and uploads the artifacts.

## First run
1. Get a Telegram `api_id` / `api_hash` at https://my.telegram.org (entered inside the app, stored encrypted).
2. Optional cloud account (sign-in, device manager): see `FIREBASE_SETUP.md`.

## Docs
`FIREBASE_SETUP.md` · `SECURITY_AUDIT.md` · `TEST_REPORT.md` · `RELEASE_CHECKLIST.md` · `SOURCE_AUDIT.md` · `DEVELOPMENT_STATUS.md`
