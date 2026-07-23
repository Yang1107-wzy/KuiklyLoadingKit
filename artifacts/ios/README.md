# iOS evidence status

No iOS runtime evidence has been captured. Kotlin/Native compilation and the
static framework link pass, but CocoaPods is not installed and the host has not
run.

After the real simulator run, use `xcrun simctl io booted screenshot` and
record the simulator model and iOS version in `docs/VALIDATION.md`.
