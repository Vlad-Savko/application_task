LABEL authors="VladislavSavko"
FROM maven:3.8.4-eclipse-temurin-16 AS builder
COPY ../src /home/app/src
COPY ../pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

LABEL authors="VladislavSavko"
FROM adoptopenjdk/openjdk16:ubi
COPY --from=builder /home/app/target /usr/local/lib
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/application-jar-with-dependencies.jar"]