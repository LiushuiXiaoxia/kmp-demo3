# Desktop Deeplink

这个项目的桌面端 deeplink 支持分为两部分：

1. 应用可以从启动参数中接收 `demo03://...`
2. 当平台支持时，应用已经在运行中也可以通过 `Desktop.setOpenURIHandler()` 处理新的 URI 打开事件

## macOS

打包后的应用会在生成的 `Info.plist` 中声明 `demo03` URL Scheme。

构建：

```bash
./gradlew :composeApp:packageDmg
```

安装并运行打包后的应用后，执行：

```bash
open "demo03://app/login"
```

## Linux

把已安装的桌面可执行文件注册为 scheme handler：

```bash
./scripts/register-demo03-scheme-linux.sh /absolute/path/to/app-executable
```

然后测试：

```bash
xdg-open "demo03://app/login"
```

## Windows

把已安装的 `.exe` 注册为 scheme handler：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\register-demo03-scheme-windows.ps1 -AppExecutable "C:\Path\To\Demo03.exe"
```

然后测试：

```powershell
start "demo03://app/login"
```
