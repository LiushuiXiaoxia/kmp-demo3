# Demo03 TODO

状态说明：`TODO / DOING / DONE / BLOCKED`

## P0 基础完善

- [x] `DONE` 建立工程 TODO 文档并整理分阶段路线图
- [x] `DONE` 为共享层引入 `AppConfig` / `NetworkConfig` 统一管理基础配置
- [x] `DONE` 将登录态升级为“内存状态 + 持久化存储”
- [x] `DONE` 为 Feed / Detail 建立共享层本地缓存接口与默认实现
- [x] `DONE` 统一远端访问方式，移除仓库内分散的硬编码 URL
- [x] `DONE` 为 `HttpClient` 增加统一超时、日志和默认 headers 配置
- [x] `DONE` 为共享层补充基础单元测试，保证 `:composeApp:allTests` 有实际测试源码
- [x] `DONE` 接入基础静态检查并补充统一校验命令

## P1 产品化增强

- [x] `DONE` 为 Feed / Detail 实现缓存回退与缓存提示
- [x] `DONE` 补齐 `feed/detail/{id}` deeplink
- [x] `DONE` 增加 deeplink 鉴权与登录后回跳目标页面
- [x] `DONE` 补充关键页面的错误、重试和离线提示
- [x] `DONE` 更新 README 为可开发工程说明

## P2 上线准备

- [x] `DONE` 预留 `AnalyticsTracker` / `CrashReporter` 接口并提供 no-op 默认实现
- [x] `DONE` 为日志初始化增加 debug/release 开关策略
- [x] `DONE` 增加 dev/test/prod 环境配置抽象与默认解析逻辑
- [x] `DONE` 增加最小 CI 工作流，覆盖编译、测试和静态检查
- [ ] `TODO` 平台签名、正式发布脚本与商店上架准备
