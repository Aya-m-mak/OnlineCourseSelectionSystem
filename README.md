# 在线选课系统 (Online Course Selection System)

## 项目简介
本项目是基于 Spring Boot、Thymeleaf 和 PostgreSQL 开发的在线选课管理系统，旨在实现学生选课和管理员课程管理功能。

## 技术栈
- Java
- Spring Boot
- Spring Data JPA
- Thymeleaf
- PostgreSQL
- HTML / CSS
- Maven

## 主要功能

### 学生功能
- 学生登录
- 查看和搜索课程
- 选择和取消课程
- 设置课程优先级
- 添加备选课程
- 查看每周课表
- 提交选课志愿
- 查看选课结果

### 管理员功能
- 管理员登录
- 添加、修改和删除课程
- 管理课程容量
- 执行选课分配
- 查看课程剩余名额

## 数据库
系统使用 PostgreSQL 保存课程和学生选课相关数据。

## 项目运行
1. 安装 Java、Maven 和 PostgreSQL。
2. 创建 PostgreSQL 数据库 `course_selection_db`。
3. 配置数据库连接和环境变量 `DB_PASSWORD`、`ADMIN_PASSWORD`。
4. 启动 Spring Boot 项目。
5. 在浏览器访问 `http://localhost:8080`。

## 项目状态
核心选课功能已实现，后续将完善 Linux 服务器部署和 HTTPS 配置。