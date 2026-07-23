# iOS runtime evidence

Captured from a real iPhone 17 Pro simulator running iOS 26.0 at
1206 × 2622.

Files:

- `ios-gallery.png`
- `ios-full-screen.png`
- `ios-timeout.png`
- `ios-timeout-dismissed.png`
- `ios-custom-theme.png`
- `ios-local.png`

The acceptance states are reproducible with:

```bash
xcrun simctl launch booted \
  io.github.yang1107.KuiklyLoadingDemo \
  --acceptance-scenario full-screen
```

Replace `full-screen` with `timeout`, `custom-theme`, or `local`. Omitting the
arguments opens the interactive gallery. Screenshots were captured with
`xcrun simctl io booted screenshot`; all files were opened and visually
checked.
