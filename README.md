# AgroChain Android App

AgroChain is a Kotlin + Jetpack Compose Android application that demonstrates a role-aware oilseed by-product marketplace with a foundational smart-contract and escrow simulation. Farmers can publish inventory, buyers can negotiate in real time, transporters handle logistics, and admins can supervise verification and system health. The base APK already exposes placeholders for future AI/ML price forecasting and IoT-driven quality feeds so the platform can scale to advanced capabilities later on.

## Feature Highlights
- **Secure role-based login** for farmers, transporters, buyers, and admins with credential hints for quick testing.
- **Role dashboards** tailored to each persona (listing management, contract oversight, verification console, etc.).
- **Marketplace listings** with filters, sorting, and real-time offer workflows.
- **Smart contract + escrow simulation**: buyers place offers, producers lock escrow upon acceptance, and parties can advance/release funds with notifications and audit logs.
- **Notification center & activity ledger** capturing every listing, offer, and contract state change.
- **Admin console** for user verification toggles and live KPIs.
- **Future-tech placeholders** for AI/ML price forecasting and IoT quality telemetry to show roadmap considerations.

## Project Structure
```
AgroChain/
├─ app/
│  ├─ build.gradle            # Android module configuration (Compose, lifecycle, coroutines)
│  └─ src/main/
│     ├─ java/com/example/agrochain/
│     │  ├─ model/            # Data classes & enums
│     │  ├─ ui/               # Compose screens & navigation
│     │  ├─ ui/theme/         # Material theme setup
│     │  ├─ AgroChainViewModel.kt
│     │  └─ MainActivity.kt
│     └─ res/                 # Manifest, themes, icons, strings
├─ build.gradle               # Root plugins
├─ gradle.properties          # AndroidX/Compose switches
└─ settings.gradle            # Repository configuration
```

## Getting Started
1. **Open in Android Studio**  
   - `File > Open...` → choose this folder.  
   - Let Gradle sync; ensure Android Gradle Plugin 8.5+ and JDK 17.

2. **Run on device or emulator**  
   - Select the `app` configuration and click Run, or build an APK with `./gradlew assembleDebug` and deploy to your phone.

3. **Sample credentials**
   - Farmer: `farmer@agrochain.com` / `farmer123`
   - Transporter: `transporter@agrochain.com` / `trans123`
   - Buyer: `buyer@agrochain.com` / `buyer123`
   - Admin: `admin@agrochain.com` / `admin123`
   - (A quick-fill chip on the login screen will populate these automatically.)

4. **Testing flows**
   - Log in as a farmer, publish a listing, and verify the activity log.
   - Switch to buyer, filter & sort listings, place offers, and watch notifications propagate.
   - Return as the listing owner to accept offers, trigger escrow states, or simulate release.
   - Use the admin panel to toggle verification flags and observe notifications/logs.

## Testing & Verification
- **Unit/UI tests**: Compose UI tests are wired (`androidTestImplementation androidx.compose.ui:ui-test-junit4`). Add targeted tests under `app/src/androidTest` as you expand critical journeys.
- **Static analysis**: Run `./gradlew lint` for Android lint and `./gradlew ktlintCheck` (if ktlint is later added) to keep Kotlin style consistent.
- **Manual smoke**: Validate the login flow, listing lifecycle, smart-contract actions, notifications, and admin toggles on real devices/emulators running API 26+.

## Extensibility Roadmap
- Plug real authentication/identity providers in place of the seeded credential vault.
- Back the repositories with a remote data source (REST, GraphQL, or Web3 provider) and persist local state with Room or DataStore.
- Replace the escrow simulation with on-chain smart-contract calls once the Solidity backend is ready; the `Contract` model can map 1:1 to chain events.
- Implement AI/ML services for price intelligence (connect to the placeholder Compose cards) and stream IoT sensor data for automated quality scoring.
- Integrate Firebase Cloud Messaging or WorkManager-backed alerts for production-ready notifications.

## Helpful Commands
- `./gradlew assembleDebug` – build the debug APK.
- `./gradlew test` – JVM unit tests (extend as needed).
- `./gradlew lint` – run Android lint checks.

## Support
Open an issue or send feedback through the in-app “record roadmap note” widget (captured in the activity log) to keep track of upcoming AI/IoT enhancements.

