# AGENTS.md

本文件供 AI 编码代理阅读，假设读者对本项目一无所知。

## 项目概览

`lightweight-rate-limiter` 是一个基于 Redis Lua 脚本实现的轻量级分布式限流组件，限流指标支持从分布式配置中心（Zookeeper、Nacos、Apollo、Consul、Etcd）动态下发与热更新。

- groupId：`io.redick01.ratelimiter`，版本：`1.0-SNAPSHOT`
- 支持的限流算法：令牌桶（`token_bucket_rate_limiter`）、漏桶（`leaky_bucket_rate_limiter`）、计数器滑动窗口（`sliding_window_rate_limiter`）、高并发（`concurrent_request_rate_limiter`）
- 支持 SpEL 表达式生成限流 key，实现多维度限流
- 通过 Micrometer + Spring Boot Actuator 暴露限流监控指标（`/actuator/prometheus`，可对接 Prometheus）
- 详细使用文档（含压测结果）见 `README.md`（中文），算法示意图在 `doc/` 目录

## 技术栈

- 语言：Java 17（`maven.compiler.source/target = 17`，Spring Boot 4 最低要求）
- 构建：Maven 多模块（根 `pom.xml`，packaging=pom；含 `mvnw`/`mvnw.cmd` wrapper，wrapper 配置在 `.mvn/wrapper/`）
- 框架：Spring Boot 4.1.0、Spring Cloud 2025.1.2、Spring Cloud Alibaba 2025.1.0.0（根 pom 的 `dependencyManagement` 统一导入）
- 关键依赖：spring-data-redis + lettuce（Redis 客户端，版本由 spring-boot-dependencies 管理）、AspectJ（AOP 拦截）、Micrometer（指标）、Lombok、Guava、hutool-all、commons-lang3
- 测试：JUnit 5（`spring-boot-starter-test`）、Mockito

## 模块结构

```
lightweight-rate-limiter-common    公共模块：配置属性（RtProperties）、枚举、常量、工具类
lightweight-rate-limiter-spi       自研 SPI 扩展加载框架（ExtensionLoader、ExtensionFactory、@SPI、@Join）
lightweight-rate-limiter-core      核心模块（依赖 spi 和 common）：
                                   - algorithm/   四种限流算法实现（RateLimiterAlgorithm SPI 接口）
                                   - annotation/  @RateLimiter 注解（key + 降级回调 clazz）
                                   - parser/      配置解析（properties/yml/json）与脚本解析（SpEL）
                                   - redis/       RedisTemplate 初始化
                                   - registry/    限流配置注册中心
                                   - refresh/     配置热刷新抽象（Refresher/AbstractRefresher）
                                   - monitor/     限流指标采集与注册（Micrometer）
                                   - src/main/resources/META-INF/scripts/  四个算法的 Lua 脚本
lightweight-rate-limiter-starter   Spring Boot Starter 集合：
                                   - ratelimiter-spring-boot-starter-common（基础自动装配，
                                     META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
                                     注册 BaseAutoConfiguration；含 AOP 拦截器 RateLimiterInterceptor）
                                   - zookeeper / nacos / apollo / etcd（各自配置中心接入，实现配置拉取与监听刷新；
                                     注意 zookeeper/etcd 的 EnvironmentPostProcessor 仍注册在 META-INF/spring.factories，
                                     但键为 Boot 4 新接口 org.springframework.boot.EnvironmentPostProcessor，
                                     旧的 org.springframework.boot.env.EnvironmentPostProcessor 将在 Boot 4.2 移除）
                                   - cloud-zookeeper / cloud-nacos / cloud-consul（Spring Cloud 方式接入）
lightweight-rate-limiter-example   各接入方式的示例工程（springboot / springboot3 / zookeeper / nacos /
                                   apollo / etcd / cloud-zookeeper / cloud-nacos / cloud-consul / common），
                                   是验证改动的手工测试入口
checkstyle/                        Checkstyle 规则文件（checkstyle-ali.xml 为实际启用规则）
```

## 扩展机制（改动前先理解）

- 项目自带一套 Dubbo 风格的 SPI（`io.redick01.spi.ExtensionLoader`），扩展点配置文件放在
  `META-INF/ratelimiter/` 下，格式为 `扩展名=实现类全限定名`。
- 现有扩展点：`RateLimiterAlgorithm`（限流算法）、`ConfigParser`（配置文件解析）、`ScriptParser`（表达式解析）。
- 新增算法 = 实现 `RateLimiterAlgorithm` + 在 `META-INF/ratelimiter/io.redick01.ratelimiter.algorithm.RateLimiterAlgorithm`
  中注册 + 在 `META-INF/scripts/` 放对应 Lua 脚本；算法名即配置中的 `algorithmName`。
- 限流核心逻辑在 Lua 脚本中（保证原子性），Java 侧只负责参数组装与结果处理，不要随意把逻辑挪回 Java。
- 配置热更新链路：配置中心监听 → `Refresher` → `ConfigParser` 解析 → `RateLimiterRegistry` 更新。

## 构建与测试命令

```bash
./mvnw clean install          # 全量构建（validate 阶段会先跑 Checkstyle，不通过则构建失败）
./mvnw test                   # 运行单元测试
./mvnw checkstyle:checkstyle  # 单独运行代码风格检查
./mvnw clean install -DskipTests   # 跳过测试构建
```

CI（`.github/workflows/ci.yml`）在 Java 17/21（Linux、macOS、Windows）矩阵上执行
`./mvnw -B clean install -Prelease`，随后上传 codecov。注意：pom 中并未定义名为 `release` 的 profile，
该参数目前不起实际作用；不要依赖它，也不要在未确认意图前删除 CI 中的该参数。

## 测试说明

- 单元测试极少：目前仅 core 模块的 `RateLimiterHandlerTest` 与 `RedisTemplateInitializationTest`。
- 这两个测试都是集成性质的：需要本机 `127.0.0.1` 有可用 Redis（脚本在 db 7 执行）。无 Redis 环境下会失败，
  本地验证可 `-DskipTests` 或单独跳过。
- 功能性验证主要靠 `lightweight-rate-limiter-example/` 下的示例工程手工/压测验证（README 中有压测方法），
  示例工程需要本地启动 Redis 及对应配置中心（Zookeeper/Nacos/Apollo/Consul/Etcd）。
- 新增代码如涉及算法或解析逻辑，应优先在 core 模块补充单元测试（JUnit 5，`spring-boot-starter-test` 已在根 pom 提供）。

## 代码风格规范

- 强制 Checkstyle：根 pom 为所有子模块启用 `maven-checkstyle-plugin`，规则文件为
  `checkstyle/checkstyle-ali.xml`（阿里巴巴规约），`violationSeverity=error`、`failsOnError=true`，
  绑定在 `validate` 阶段——风格不达标会直接构建失败。提交前务必本地跑通 `./mvnw clean install`。
- 仅检查 main 源码目录（`includeTestSourceDirectory=false`）。
- 使用 Lombok（provided scope），保持与现有代码一致的用法。
- 现有代码注释以中文和英文 Javadoc 混合存在；新代码遵循所在文件的既有风格，类头一般有 `@author` Javadoc。
- 配置键命名：`spring.ratelimiter.*`（如 `redis-config.url`、`rate-limiter-configs[i].algorithmName`）。

## 安全注意事项

- `RedisTemplateInitializationTest` 中硬编码了 Redis 密码（`Qrbrqj88`）。这是既有问题，
  修改该文件时不要扩散该凭据；如做安全清理可将其改为从环境读取，但需与维护者确认。
- 组件本身面向业务应用暴露 SpEL 表达式配置（`rateLimiterKey` 支持 SpEL），表达式来自配置中心——
  改动解析/执行链路（`SpelScriptParser`、`RateLimiterInterceptor`）时注意不要扩大表达式可访问的上下文范围。
- `doc/` 下的图片与示例配置含内网/本地地址，属正常文档内容。

## 提交与协作

- 主分支为 `master`，CI 在 PR 和 push 到 master 时触发。
- 不要在未获明确要求时执行 `git commit`/`push` 等变更操作。
- 仓库无 AGENTS.md 之外的代理约定文件；README.md 是唯一权威使用文档，改动对外行为（配置键、算法名、注解属性）时同步更新 README.md。
