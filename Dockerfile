# Use a multi-stage build to build the JAR and then copy it into a slim runtime image

# --- Build stage ---
FROM gradle:8.13.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon -x test

# --- Runtime stage ---
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/JTodo.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/app/JTodo.jar"]
EXPOSE 8080
