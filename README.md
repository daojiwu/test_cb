codebuddy创建的测试项目，有读取excel后处理excel后导出新excel的方法

--------------------------------------------------------------------------
## 项目功能

### Excel处理功能
- 读取Excel文件并转换为Java对象
- 将Java对象列表导出为Excel文件
- 复杂的Excel数据处理和转换

### 数据库工具类
- **Redis工具类**: 提供静态方法和实例方法两种调用方式
- **SQL Server工具类**: 独立运行的数据库访问工具，无需Spring环境
- **MongoDB工具类**: 独立运行的MongoDB访问工具，无需Spring环境

### 独立运行工具
项目中的以下工具类可以独立运行，无需启动Spring Boot应用：
- StandaloneRedisUtil: Redis操作工具
- StandaloneSqlServerUtils: SQL Server操作工具
- StandaloneMongoUtil: MongoDB操作工具

--------------------------------------------------------------------------
## 使用 Docker 部署
### 构建镜像
```bash
# 方法1：直接构建（需要本地已安装 Maven）
mvn clean package
docker build -t test-cb:latest .
# 方法2：使用多阶段构建（不需要本地 Maven）
docker build -f Dockerfile.optimized -t test-cb:latest .
```
### 运行容器
```bash
docker run -d -p 8081:8081 --name test-cb-app test-cb:latest
```
### 使用 Docker Compose
```bash
docker-compose up -d
```
## 访问应用
应用启动后可通过 http://localhost:8081 访问。

## 使用docker打包项目到其他电脑上运行
### 方法一：导出和导入 Docker 镜像（推荐）
```bash
a. 在当前电脑上导出镜像
1. 进入项目目录
cd E:\yinxq\(project\test_cb
2. 构建 Docker 镜像（如果尚未构建）
docker build -f Dockerfile.optimized -t test-cb:latest .
3. 将镜像导出为文件   执行完成后，会在当前目录生成一个 test-cb-latest.tar 文件。
docker save -o test-cb-latest.tar test-cb:latest

b. 在其他电脑上导入和运行
将 test-cb-latest.tar 文件复制到其他 Windows 10 电脑上，然后执行：
1. 导入镜像
docker load -i test-cb-latest.tar
2. 运行容器
docker run -d -p 8081:8081 --name test-cb-app test-cb:latest
3. 验证运行状态
docker ps
```


### 方法二：复制项目源码并重新构建（推荐用于长期使用）
```bash
这种方式更适合长期使用，因为包含了完整的构建信息：
1. 复制项目文件
将整个项目目录复制到其他电脑上，确保包含以下文件：
pom.xml
src/ 目录
Dockerfile 和 Dockerfile.optimized
docker-compose.yml
.dockerignore
2. 在目标电脑上运行
在目标电脑上执行以下步骤：
（这是最简单的方式，不需要本地预先安装 Maven）
1. 打开 PowerShell 或命令提示符，进入项目根目录
cd E:\yinxq\(project\test_cb
2. 使用优化的多阶段构建 Dockerfile 构建镜像
docker build -f Dockerfile.optimized -t test-cb:latest .
3. 运行容器
docker run -d -p 8081:8081 --name test-cb-app test-cb:latest
4. 查看容器运行状态
docker ps
5. 查看应用日志
docker logs test-cb-app
```
--------------------------------------------------------------------------
