# Android screenshots

- 系统：Android 14 / API 34
- ABI：`arm64-v8a`
- 设备配置：Pixel 7
- 分辨率：1080 × 2400

场景可通过 Activity extra 启动：

```bash
adb shell am start -W \
  -n io.github.yang1107.kuikly.loading.demo/.MainActivity \
  --es acceptanceScenario full-screen
```

`acceptanceScenario` 支持 `full-screen`、`timeout`、`custom-theme` 和
`local`。不传该参数时进入完整 Demo 页面。

截图使用以下命令保存：

```bash
adb exec-out screencap -p > screenshot.png
```
