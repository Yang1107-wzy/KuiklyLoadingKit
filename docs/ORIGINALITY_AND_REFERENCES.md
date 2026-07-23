# Originality and references

This repository is an independent implementation for
`Tencent-TDS/KuiklyUI#1480`.

## References inspected

- Tencent-TDS/KuiklyUI tag `2.23.2`, commit
  `7afa0f74275211c2b5c8f4610484c2705dd286b0`
- Tencent-TDS/KuiklyUI main, commit
  `b396748db2818fa3beda4e00fa8b0daee19bbb2a`
- Kuikly-contrib/KuiklyChatUI main, commit
  `2bb10e88e9c8ab26c2f9d5f2d26f3317c8e376d8`
- Kuikly ComposeView, lifecycle, timeout, compilation, and ActivityIndicator
  documentation linked from Issue #1480.
- CocoaPods trunk metadata for `OpenKuiklyIOSRender` (version `2.23.2`).

The references were used to verify public dependency coordinates, Gradle/KMP
structure, lifecycle method names, timer cancellation, ActivityIndicator
constraints, animation APIs, and platform host integration patterns.

No participant loading component source was opened or copied. KuiklyChatUI
business component code is not present in this repository. KuiklyUI itself is
consumed through official Maven artifacts rather than vendored source.

## Licensing

Project-authored code is intended for Apache License 2.0. Kuikly artifacts
remain governed by the license distributed by Tencent in the KuiklyUI
repository. Platform and test dependencies retain their own licenses.

The minimal Android and iOS host code follows the public delegator integration
shape documented and demonstrated by Kuikly projects, but names, page wiring,
scope, and implementation were written for this repository. No KuiklyChatUI
business component was copied.
