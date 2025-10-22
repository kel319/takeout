FROM openjdk:17-jdk-slim


# 环境变量
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xmx512m -Xms256m -Dspring.profiles.active=dev"

# 设置时区
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 复制应用
COPY target/student.jar app.jar

# 端口暴露
EXPOSE 9090

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]