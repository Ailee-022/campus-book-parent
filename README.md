# 校园二手书交易平台（微服务架构）

## 📖 项目简介
本项目是一个基于 Spring Boot + Spring Cloud 微服务架构的校园二手书交易平台。系统实现了用户管理、图书发布与检索、订单生成与防超卖、以及 AI 智能助手等核心功能
## 🛠️ 技术选型
- **核心框架**：Spring Boot 2.7.18
- **微服务通信**：RestTemplate (HTTP 调用)
- **持久层**：MyBatis-Plus 3.5.5
- **数据库**：MySQL 8.0
- **鉴权与安全**：JWT (JSON Web Token)、BCrypt 密码加密
- **AI 接入**：智谱AI (GLM-4-Flash 模型，兼容 OpenAI 格式)
- **工具库**：Hutool、Lombok

## 🏗️ 系统架构
本项目采用轻量级多模块微服务架构，分为以下几个服务：

| 服务名称 | 端口 | 职责 | 数据库表 |
| :--- | :--- | :--- | :--- |
| `student-service` | 8081 | 学生/管理员登录注册、信息管理、鉴权 | `tb_student`, `tb_admin` |
| `book-service` | 8082 | 图书发布、分页搜索、乐观锁防超卖扣减 | `tb_book` |
| `order-service` | 8083 | 订单生成、跨服务调用、订单查询 | `tb_order`, `tb_order_item` |
| `ai-service` | 8084 | AI 助手，基于数据库内容的智能问答 | 无 (调用 API) |
| `common` | - | 公共模块，统一返回结果、JWT工具类、全局异常 | - |
graph TD
%% 定义客户端
Client[用户/Postman/前端]

    %% 定义微服务
    StudentService[student-service :8081]
    BookService[book-service :8082]
    OrderService[order-service :8083]
    AiService[ai-service :8084]

    %% 定义数据库
    DB[(MySQL 数据库)]

    %% 定义外部服务
    ExternalAI[智谱 AI 开放平台]

    %% 客户端请求流向
    Client --> StudentService
    Client --> BookService
    Client --> OrderService
    Client --> AiService

    %% 微服务内部调用
    OrderService -- RestTemplate 扣减库存 --> BookService
    AiService -- 查库拼装Prompt --> BookService

    %% 数据库连接
    StudentService --> DB
    BookService --> DB
    OrderService --> DB

    %% 外部AI调用
    AiService -- 调用大模型API --> ExternalAI


## ⚡ 核心技术难点与解决方案
1. **高并发防超卖（乐观锁）**：
   在 `tb_book` 表中加入 `version` 字段。扣减库存时使用 `UPDATE tb_book SET stock = stock - ?, version = version + 1 WHERE id = ? AND stock >= ? AND version = ?`。若更新受影响行数为 0，则说明库存不足或版本冲突，直接抛出异常回滚事务。
2. **无状态鉴权（JWT 拦截器）**：
   采用 JJWT 生成 Token，在 `order-service` 和 `student-service` 中编写 `JwtInterceptor` 拦截器。从请求头 `Authorization: Bearer xxx` 中解析 Token，并通过 `ThreadLocal` 将 `userId` 传递给 Controller，保证不同线程间的数据隔离。
3. **跨服务调用与数据一致性**：
   订单服务（8083）通过 `RestTemplate` 调用图书服务（8082）的扣减库存接口。只有库存扣减成功后，才会在订单库中生成订单数据。
4. **AI 助手的简易 RAG（检索增强生成）**：
   用户发起提问 -> `ai-service` 调用 `book-service` 查询数据库相关图书 -> 拼接提示词（Prompt） -> 调用大模型 API -> 返回自然语言回答。

## 🚀 启动指南

### 1. 环境准备
- JDK 11、Maven、MySQL 8.0

### 2. 数据库初始化
使用 Navicat 连接本地 MySQL，新建数据库 `campus_book_db`，并执行根目录下的 `init_db.sql` 文件。

### 3. 修改配置
修改 `student-service`、`book-service`、`order-service` 中 `application.properties` 的 MySQL 密码。

### 4. AI 服务配置
前往智谱AI开放平台申请 API Key，并替换 `ai-service` 模块中 `AiController.java` 的 `API_KEY` 常量。

### 5. 启动微服务
依次启动 `BookServiceApplication` (8082)、`StudentServiceApplication` (8081)、`OrderServiceApplication` (8083)、`AiServiceApplication` (8084)。

### 6. 接口测试说明
本项目未使用额外的 Postman 客户端，而是使用了 **IDEA 内置的 HTTP Client**。
所有接口测试用例均保存在根目录的 `api-docs.http` 文件中。
该文件完全兼容 Postman 格式，可以直接在 IDEA 中一键运行，也支持一键导入至 Postman 中使用。

## 🤝 开发者
- 作者：Ailee
- 考核项目：2025领沃大二后端考核