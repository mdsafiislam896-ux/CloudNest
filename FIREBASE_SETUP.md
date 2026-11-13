# CloudNest - Firebase setup (no google-services.json needed)

CloudNest talks to Firebase through its public REST APIs, so you only need two values.

1. console.firebase.google.com -> Add project (e.g. "cloudnest").
2. Build -> Authentication -> Get started -> enable **Email/Password**.
3. Build -> Firestore Database -> Create database (production mode).
4. Firestore -> Rules -> paste the contents of `firestore.rules` -> Publish.
5. Project settings (gear) -> General:
   - copy **Project ID**
   - copy **Web API Key** (under "Your apps", or Project settings -> General).
6. GitHub repo -> Settings -> Secrets and variables -> Actions -> New secret:
   - `FIREBASE_API_KEY` = the Web API key
   - `FIREBASE_PROJECT_ID` = the project ID
7. Re-run the workflow. With both secrets empty, the app builds with the cloud account switched off.

Security notes
- The Web API key is public by design; protection comes from `firestore.rules`
  (each user can only read/write `users/{their uid}/...`; everything else is denied).
- In Google Cloud Console -> APIs & Services -> Credentials, restrict the key to
  "Identity Toolkit API", "Token Service API" and "Cloud Firestore API".
- Passwords are never stored. The refresh token is encrypted with the Android Keystore.
- Revoking a device signs it out at its next app start; it can sign in again with the password.
  If a device is lost, also change your password.
