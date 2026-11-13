# NexVault Roadmap (by Md. Safi Islam)

## A. Premium UX ("smooth") - planned
1. Design language: navy/violet/aqua glow, glass cards, 28dp shapes, spring motion (Material 3 Expressive already in base).
2. Home: greeting + animated donut storage ring, category chips (Docs/Images/Videos/Audio), recent files, health badge.
3. Splash with logo animation + "by Md. Safi Islam"; 3-step onboarding; login/register (Cosmos-style).
4. Shared-element transitions, haptics, skeleton loaders, pull-to-refresh, predictive back.
5. Themes: Light / Dark / AMOLED / System + accent picker; Bengali + English.

## B. Spec features - planned order
Auth (Firebase) -> multi-Telegram accounts -> replication/failover -> conflict UI -> device manager -> health dashboard.

## C. Extra features worth adding
- Biometric vault (hidden/locked folders) - base has app lock, extend per-folder
- Smart search (name, type, date, OCR-free tags) and saved filters
- Auto photo backup rules per account; Wi-Fi/charging policy (base has this)
- Storage cleaner: duplicates, large files, old trash (never auto-delete)
- Share links with expiry via encrypted bundle
- Offline pinning, home-screen widget, quick-upload tile
- Weekly integrity-check report notification
- Recovery Passphrase strength meter + printable recovery sheet

## Constraints
Telegram API: real parallelism is limited per account; speeds shown are measured, never faked.
