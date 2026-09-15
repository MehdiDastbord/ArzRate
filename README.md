# ArzRates

Enhanced Android currency app with English + Persian, responsive live widget, calculator, drag-to-reorder currencies, theme selection, and branded app icon.

## Main features
- English and Persian UI with RTL support.
- Currency names translated to Persian where supported by the API/data model.
- Light / Dark / System appearance setting.
- Home dashboard with long-press drag-and-drop currency reordering.
- Currency picker with country/currency flags.
- Calculator tab for converting between any two loaded rates.
- Responsive home-screen widget:
  - 1–3 currencies: large one-per-row layout.
  - More currencies: compact multi-column layout.
  - Adapts when the widget is resized.
  - Light phone mode: soft white background + dark text.
  - Dark phone mode: soft near-black background + white text.
  - Flags/symbols for supported currencies, with a generic fallback.
  - Opens the app when tapped.
  - Fetches data when the widget cache is empty and refreshes in the background.
- App logo is installed as the launcher icon and shown in Settings.
- GitHub Actions updated to current action runtimes and Java 17.

## Build
GitHub Actions runs `gradle :app:assembleDebug` and uploads `app-debug.apk` as an artifact.
