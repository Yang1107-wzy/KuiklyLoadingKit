# Android runtime evidence

Captured from a real API 34 Google APIs ARM64 emulator using a Pixel 7 profile.
Guest: Android 14, `arm64-v8a`, 1080 × 2400.

Files:

- `android-gallery.png`
- `android-full-screen.png`
- `android-timeout.png`
- `android-timeout-dismissed.png`
- `android-custom-theme.png`
- `android-local.png`

The acceptance states are reproducible with:

```bash
adb shell am start -W \
  -n io.github.yang1107.kuikly.loading.demo/.MainActivity \
  --es acceptanceScenario full-screen
```

Replace `full-screen` with `timeout`, `custom-theme`, or `local`. Omitting the
extra opens the interactive gallery. Screenshots were captured with
`adb exec-out screencap -p`; all files were opened and visually checked.
