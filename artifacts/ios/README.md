# iOS screenshots

- 系统：iOS 26.0
- 设备：iPhone 17 Pro Simulator
- 分辨率：1206 × 2622

场景可通过启动参数选择：

```bash
xcrun simctl launch booted \
  io.github.yang1107.KuiklyLoadingDemo \
  --acceptance-scenario full-screen
```

`--acceptance-scenario` 支持 `full-screen`、`timeout`、`custom-theme` 和
`local`。不传该参数时进入完整 Demo 页面。

截图使用以下命令保存：

```bash
xcrun simctl io booted screenshot screenshot.png
```
