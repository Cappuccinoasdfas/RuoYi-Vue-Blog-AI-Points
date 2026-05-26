# 配置文件说明

## 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.x+
- Node.js 14+ (前端)

## 配置文件位置

该配置文件包含新增功能，具体如下。其他项目运行方式与若依相同，可直接部署。

> **图片资源问题**：如果图片显示不了，请先将文件夹里的 SQL 打包进数据库运行，并把文件里的 uploadPath 资源全部放到 `D:/ruoyi/uploadPath` 位置（或在 yml 中搜索 `uploadPath` 自行修改路径）。

`ruoyi-admin/src/main/resources/application.yml`

在此文件内按 `Ctrl + F` 搜索填写，按规则配置。

## 功能配置

### 1. SQL地址填写（必填）

配置数据库连接地址。

### 2. Redis配置

可使用默认配置。

### 3. 邮箱配置

用于注册发送验证码功能，可自行网上或 AI 搜索配置方法。

### 4. AI地址配置

1. 下载 AI 服务项目：  
   `https://github.com/Cappuccinoasdfas/ai-service`

2. 按该项目教程部署后，填写 AI 服务地址。

## 运行与登录说明

配置完成后运行项目，使用以下账号登录：

- **管理端**（必须是本地地址访问）  
  账号：`admin`  
  密码：`admin123`

- **IP白名单配置**（在 yml 配置文件最下面）
  ```yaml
  admin:
    allowed-ips: 127.0.0.1,0:0:0:0:0:0:0:1
  只有配置了白名单的 IP 设备才能访问管理端。

博客端
账号：小猫
密码：admin123