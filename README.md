# Demo03

`Demo03` 是一个基于 Kotlin Multiplatform 和 Compose Multiplatform 的多端示例工程，当前目标平台包括 Android、iOS 和 Desktop(JVM)。项目采用共享 UI + 平台宿主的结构，主要用于演示基础登录流、Tab 导航、会话状态管理以及 deeplink 接入。

## 项目概览

- 项目名称：`Demo03`
- 包名：
  - Android 应用：`com.example.demo_03`
  - 共享模块：`com.example.demo_03.shared`
- 支持平台：Android、iOS、Desktop(JVM)
- 桌面应用名称：`Demo03`
- 桌面 URL Scheme：`demo03://`

## 当前已实现能力

- 启动页 `Splash`
- 登录页 `Login`
- 首页容器 `Home`
- 四个 Tab 页面：
  - `Feed`
  - `Discover`
  - `Messages`
  - `Profile`
- 基于 `SessionStore` 的内存会话状态
- 基于 `androidx.navigation` 的页面导航
- 基于 `Koin` 的依赖注入
- 基于 `Napier` 的日志初始化与页面生命周期日志
- Deeplink 路由：
  - `demo03://app/login`
  - `demo03://app/home/feed`
  - `demo03://app/home/discover`
  - `demo03://app/home/messages`
  - `demo03://app/home/profile`

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
- `androidApp/src/main`
  - AndroidManifest、`MainActivity` 等 Android 宿主代码
- `iosApp/iosApp`
  - iOS SwiftUI App 入口与资源

## 架构说明

项目当前是一个轻量的共享 UI 架构：

- `androidApp` 和 `iosApp` 负责平台入口
- `composeApp` 承担主要业务逻辑和 Compose 页面
- 页面状态通过自定义 `MviViewModel<S, I>` 管理
- `SessionStore` 维护登录态
- `AppRoute` 统一描述路由与 deeplink 映射
- `initKoin()` 在应用启动时初始化依赖容器

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

### iOS

在 Xcode 中打开 [`iosApp`](/Users/leon/ws/kmm/demo03/iosApp)，运行 `iosApp` target。

## Deeplink 说明

- Android 已在 `AndroidManifest.xml` 中注册 `demo03://app/...`
- Desktop 支持两种方式处理 deeplink：
  - 启动参数传入 URI
  - 应用运行中通过 `Desktop.setOpenURIHandler()` 接收 URI
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

## 后续可扩展方向

- 接入持久化存储，替换当前内存登录态
- 补充网络层与真实数据源
- 为 Android/iOS/Desktop 补充统一的 deeplink 验证
- 增加单元测试和 UI 测试
