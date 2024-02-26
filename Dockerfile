FROM openjdk:21-slim
# TODO: use real build name
COPY build/libs/JTodo-0.0.1-SNAPSHOT.jar /app/JTodo.jar
ENTRYPOINT ["java"]
CMD ["-jar", "/app/JTodo.jar"]
EXPOSE 8080
