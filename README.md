# Spring Cloud 微服务演示项目

这是一个基于 Spring Boot 3 和 Spring Cloud 的完整微服务演示项目，展示了主流微服务技术的使用。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Cloud Gateway** - API网关
- **Kubernetes** - 服务注册与配置中心（后续部署）
- **WebClient** - 服务间调用
- **Resilience4j** - 熔断降级
- **Redis** - 分布式锁/缓存
- **MySQL 8** - 数据存储
- **MyBatis** - ORM框架
- **Kafka** - 异步消息
- **Jackson** - 序列化反序列化

## 项目结构

```
spring-cloud-demo/
├── common/                    # 公共模块
│   ├── src/main/java/com/example/common/
│   │   ├── entity/           # 实体类
│   │   ├── dto/              # 数据传输对象
│   │   └── result/           # 统一响应结果
├── gateway-service/           # 网关服务 (端口: 8080)
├── user-service/             # 用户服务 (端口: 8081)
├── order-service/            # 订单服务 (端口: 8082)
├── inventory-service/        # 库存服务 (端口: 8083)
├── sql/                      # 数据库脚本
├── docker-compose.yml        # Docker Compose配置
└── README.md                 # 项目说明
```

## 服务说明

### 1. 网关服务 (Gateway Service)
- 统一入口，路由转发
- 静态路由配置
- 为Kubernetes部署做准备

### 2. 用户服务 (User Service)
- 用户管理 (CRUD)
- Redis缓存
- 用户信息查询

### 3. 订单服务 (Order Service)
- 订单管理
- 分布式事务处理
- 服务间调用 (用户服务、库存服务)
- Kafka消息发送
- Hystrix熔断降级

### 4. 库存服务 (Inventory Service)
- 商品管理
- 库存扣减/增加
- Redis分布式锁
- Kafka消息消费

## 分布式事务处理

项目实现了基于本地事务 + 补偿机制的分布式事务处理：

1. **订单创建流程**：
   - 创建订单 (本地事务)
   - 调用库存服务扣减库存
   - 发送Kafka消息
   - 失败时回滚订单

2. **订单取消流程**：
   - 更新订单状态为取消
   - 调用库存服务恢复库存

3. **库存管理**：
   - Redis分布式锁防止并发问题
   - 数据库乐观锁保证数据一致性

## 快速开始

### 1. 启动依赖服务

```bash
# 启动所有依赖服务 (MySQL, Redis, Nacos, Kafka)
docker-compose up -d
```

### 2. 初始化数据库

```bash
# 执行数据库初始化脚本
mysql -h localhost -P 3306 -u root -proot < sql/init.sql
```

### 3. 编译项目

```bash
# 编译整个项目
mvn clean compile
```

### 4. 启动微服务

```bash
# 启动各个服务 (按顺序启动)
mvn spring-boot:run -pl gateway-service
mvn spring-boot:run -pl user-service
mvn spring-boot:run -pl inventory-service
mvn spring-boot:run -pl order-service
```

### 5. 验证服务

访问以下地址验证服务是否正常：

- **测试页面**: http://localhost:8080 (推荐使用)
- 网关服务: http://localhost:8080
- 用户服务: http://localhost:8081
- 订单服务: http://localhost:8082
- 库存服务: http://localhost:8083
- 各服务健康检查: http://localhost:8080/actuator/health

### 6. 使用测试页面

启动网关服务后，访问 http://localhost:8080 即可看到测试页面，提供以下功能：

- 🟢 **服务状态监控** - 实时检查所有微服务状态
- 👤 **用户管理** - 创建、查询用户信息
- 📦 **商品管理** - 创建、查询商品和库存
- 🛒 **订单管理** - 创建订单、查询订单信息
- 🔄 **分布式事务测试** - 通过创建订单测试库存扣减

## API 接口

### 用户服务
- `POST /user-service/users` - 创建用户
- `GET /user-service/users/{id}` - 获取用户
- `GET /user-service/users` - 获取所有用户
- `PUT /user-service/users/{id}` - 更新用户
- `DELETE /user-service/users/{id}` - 删除用户

### 库存服务
- `POST /inventory-service/inventory/products` - 创建商品
- `GET /inventory-service/inventory/products/{id}` - 获取商品
- `GET /inventory-service/inventory/products` - 获取所有商品
- `PUT /inventory-service/inventory/products/{id}` - 更新商品
- `DELETE /inventory-service/inventory/products/{id}` - 删除商品
- `POST /inventory-service/inventory/decrease-stock` - 扣减库存
- `POST /inventory-service/inventory/increase-stock` - 增加库存
- `GET /inventory-service/inventory/check-stock` - 检查库存

### 订单服务
- `POST /order-service/orders` - 创建订单
- `GET /order-service/orders/{id}` - 获取订单
- `GET /order-service/orders` - 获取所有订单
- `GET /order-service/orders/user/{userId}` - 获取用户订单
- `PUT /order-service/orders/{id}/status` - 更新订单状态
- `POST /order-service/orders/{id}/cancel` - 取消订单

## 测试示例

### 创建订单测试

```bash
# 1. 创建用户
curl -X POST http://localhost:8080/user-service/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000",
    "address": "测试地址"
  }'

# 2. 创建订单
curl -X POST http://localhost:8080/order-service/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 2
  }'
```

## 配置说明

### 数据库配置
- MySQL: localhost:3306
- 数据库名: spring_cloud_demo
- 用户名: root
- 密码: root

### Redis配置
- 地址: localhost:6379
- 数据库: 0



### Kafka配置
- 地址: localhost:9092
- Topic: order-created

## 注意事项

1. 确保Java版本为17或以上
2. 确保Docker和Docker Compose已安装
3. 首次启动需要等待依赖服务完全启动
4. 如果遇到端口冲突，请修改application.yml中的端口配置

## 扩展功能

- 添加分布式链路追踪 (Sleuth + Zipkin)
- 添加服务监控 (Prometheus + Grafana)
- 添加API文档 (Swagger)
- 添加安全认证 (Spring Security + JWT)
- 添加分布式事务框架 (Seata) 