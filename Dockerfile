FROM gradle:8.7-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle bootJar

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/acmeshop-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh /app/wait-for-it.sh
RUN chmod +x /app/wait-for-it.sh
EXPOSE 8080
ENTRYPOINT ["./wait-for-it.sh", "db:3306", "--", "java", "-jar", "app.jar"]
