# -----------------------------
# Stage 1: Build
# -----------------------------
FROM maven:3.9.4-amazoncorretto-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
ARG SKIP_TESTS=true
RUN mvn clean package -DskipTests=${SKIP_TESTS}

# -----------------------------
# Stage 2: Runtime
# -----------------------------
FROM amazoncorretto:21-alpine
WORKDIR /app

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

# Default environment variables (can be overridden at runtime)
ENV APP_PORT=8082
ENV SPRING_PROFILES_ACTIVE=local

# Expose application port
EXPOSE $APP_PORT

# Entrypoint
ENTRYPOINT ["java","-Xms256m","-Xmx512m","-jar","app.jar"]
