# Demo03

`Demo03` 是一个基于 Kotlin Multiplatform 和 Compose Multiplatform 的多端示例工程，当前目标平台包括 Android、iOS、Desktop(JVM) 和 Web(WasmJs)。项目采用共享 UI + 平台宿主的结构，目前已经具备会话持久化、内容缓存回退、统一网络配置、共享层测试和基础工程校验链路。

## 项目概览

- 项目名称：`Demo03`
- 包名：
  - Android 应用：`com.example.demo_03`
  - 共享模块：`com.example.demo_03.shared`
- 支持平台：Android、iOS、Desktop(JVM)、Web(WasmJs)
- 桌面应用名称：`Demo03`
- 桌面 URL Scheme：`demo03://`
- Web 运行方式：浏览器 + WasmJs

## 当前已实现能力

- 启动页 `Splash`
- 登录页 `Login`
- 首页容器 `Home`
- 四个 Tab 页面：
  - `Feed`
  - `Discover`
  - `Messages`
  - `Profile`
- 基于平台存储的会话持久化与启动恢复
- 基于 `androidx.navigation` 的页面导航
- 基于 `Koin` 的依赖注入
- 基于 `Napier` 的日志初始化与页面生命周期日志
- 基于本地缓存的 Feed / Detail 回退展示
- 基于 `TODO.md`、`verify` 与 GitHub Actions 的基础工程化校验
- Deeplink 路由：
  - `demo03://app/login`
  - `demo03://app/home/feed`
  - `demo03://app/home/discover`
  - `demo03://app/home/messages`
  - `demo03://app/home/profile`
  - `demo03://app/feed/detail/{id}`
- Web 支持通过 URL hash 传入 deeplink，例如 `#demo03://app/home/feed`

## 技术栈

- Kotlin `2.3.10`
- Compose Multiplatform `1.10.1`
- Android Gradle Plugin `9.0.1`
- Android compileSdk / targetSdk `36`
- Android minSdk `29`
- Material 3
- AndroidX Lifecycle ViewModel
- AndroidX Navigation Compose
- Kotlin Coroutines `1.10.2`
- Koin `4.1.1`
- Napier `2.7.1`
- Ktor `3.3.3`
- Ktorfit `2.7.2`
- JVM 目标版本：`11`

## 工程结构

```text
.
├── androidApp/    Android 宿主应用
├── composeApp/    KMP 共享模块，包含公共 UI、导航、状态管理与 Desktop 入口
├── iosApp/        iOS 宿主应用（SwiftUI 入口 + Compose UIViewController）
├── docs/          补充文档
└── scripts/       桌面端 deeplink 注册脚本
```

### 关键目录说明

- `composeApp/src/commonMain/kotlin`
  - 共享 UI、导航、DI、ViewModel、会话状态和业务页面
- `composeApp/src/androidMain/kotlin`
  - Android 平台实现
- `composeApp/src/iosMain/kotlin`
  - iOS 平台实现与 `MainViewController`
- `composeApp/src/jvmMain/kotlin`
  - Desktop 入口与 URI 处理
- `composeApp/src/wasmJsMain/kotlin`
  - Web 平台实现、浏览器入口与本地存储适配
- `androidApp/src/main`
  - AndroidManifest、`MainActivity` 等 Android 宿主代码
- `iosApp/iosApp`
  - iOS SwiftUI App 入口与资源

## 架构说明

项目当前是一个轻量的共享 UI 架构：

- `androidApp` 和 `iosApp` 负责平台入口
- `composeApp` 承担主要业务逻辑和 Compose 页面
- 页面状态通过自定义 `MviViewModel<S, I>` 管理
- `SessionStore` 维护登录态并同步持久化存储
- `AppConfig` 统一管理环境和网络参数
- `PostRepository` 通过远端数据源 + 本地缓存数据源提供内容
- `AppRoute` 统一描述路由与 deeplink 映射
- `initKoin()` 在应用启动时初始化依赖容器
- `wasmJsMain` 提供 Web 平台的 `actual` 实现和浏览器入口

## 构建与运行

### 环境要求

- JDK 11+
- Android Studio / IntelliJ IDEA
- Xcode（运行 iOS 目标时）

### Android

构建 Debug 包：

```bash
./gradlew :androidApp:assembleDebug
```

安装到已连接设备：

```bash
./gradlew :androidApp:installDebug
```

使用 `adb + uri` 启动应用：

```bash
adb shell am start \
  -a android.intent.action.VIEW \
  -d "demo03://app/login" \
  com.example.demo_03
```

也可以直接验证其他 deeplink：

```bash
adb shell am start -a android.intent.action.VIEW -d "demo03://app/home/feed" com.example.demo_03
adb shell am start -a android.intent.action.VIEW -d "demo03://app/home/profile" com.example.demo_03
```

### Desktop

直接运行桌面版：

```bash
./gradlew :composeApp:run
```

打包桌面应用：

```bash
./gradlew :composeApp:packageDmg
./gradlew :composeApp:packageMsi
./gradlew :composeApp:packageDeb
```

### Web

本地启动 Web 版：

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
make web-run
```

构建 Web 发布产物：

```bash
./gradlew :composeApp:wasmJsBrowserDistribution
make web-dist
```

### iOS

在 Xcode 中打开 [`iosApp`](/Users/leon/ws/kmm/demo03/iosApp)，运行 `iosApp` target。

### Shared Checks

运行共享层测试：

```bash
./gradlew :composeApp:allTests
```

运行统一校验：

```bash
./gradlew verify
make verify
```

## Deeplink 说明

- Android 已在 `AndroidManifest.xml` 中注册 `demo03://app/...`
- Android 可通过 `adb shell am start -a android.intent.action.VIEW -d "<uri>" com.example.demo_03` 触发 deeplink 启动
- Desktop 支持两种方式处理 deeplink：
  - 启动参数传入 URI
  - 应用运行中通过 `Desktop.setOpenURIHandler()` 接收 URI
- Web 支持通过 URL hash 触发启动 deeplink：
  - `http://localhost:8080/#demo03://app/login`
  - `http://localhost:8080/#demo03://app/feed/detail/1`
- 受保护 deeplink 在未登录时会先进入 `Login`，登录成功后自动回跳目标页面
- Desktop 平台的 scheme 注册方式见 [`docs/desktop-deeplink.md`](/Users/leon/ws/kmm/demo03/docs/desktop-deeplink.md)

示例：

```bash
open "demo03://app/login"
open "demo03://app/home/profile"
```

## 依赖与仓库说明

- 依赖管理使用 `gradle/libs.versions.toml`
- 已配置 JetBrains Compose 仓库
- 已配置阿里云 Maven 镜像
- Gradle 已开启 configuration cache 与 build cache
- 根目录 [`TODO.md`](/Users/leon/ws/kmm/demo03/TODO.md) 用于维护阶段性任务状态
- GitHub Actions 已提供最小 CI：`verify`

## 当前限制

- 当前仍使用 `jsonplaceholder` 作为演示数据源
- `verify` 已覆盖共享层格式约束、编译和测试，但尚未包含发布签名与商店上架流程
- iOS / Desktop 的正式发布脚本仍需按目标环境继续补齐
- Web 当前使用浏览器 `localStorage` 作为持久化与缓存存储

## 后续可扩展方向

- 接入真实账号系统与 token 安全存储
- 将轻量缓存升级为更强的数据层方案
- 扩展埋点、崩溃上报和性能监控实现
- 为 Android/iOS/Desktop 补充更完整的 UI 自动化测试
