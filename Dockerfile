# 使用官方 OpenJDK 运行环境作为基础镜像
FROM openjdk:8-jre-slim

# 设置维护者信息
LABEL maintainer="developer@example.com"

# 设置工作目录
WORKDIR /app

# 将 jar 文件复制到容器中
COPY target/*.jar app.jar

# 暴露端口（项目配置端口）
EXPOSE 8081

# 设置 JVM 参数
ENV JAVA_OPTS=""

# 运行应用程序
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]